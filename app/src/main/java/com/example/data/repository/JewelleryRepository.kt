package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.example.data.api.Part
import com.example.data.api.ResponseFormat
import com.example.data.api.ResponseFormatText
import com.example.data.api.GeminiClient
import com.example.data.local.CartEntity
import com.example.data.local.CommunityListingEntity
import com.example.data.local.CustomDesignEntity
import com.example.data.local.JewelleryDao
import com.example.data.local.WishlistEntity
import com.example.data.local.ReviewEntity
import com.example.data.model.JewelleryCatalog
import com.example.data.model.JewelleryProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class AiBraceletResponse(
    val title: String,
    val style: String,      // "Cuff", "Chain", "Braided", "Bangle"
    val finish: String,     // "Polished", "Oxidized", "Hammered", "Brushed"
    val gemstone: String,   // "Turquoise", "Amethyst", "Onyx", "Garnet", "Sterling Bead", "None"
    val engraving: String,  // Recommended short engraving
    val description: String,// Brief description from the jeweller perspective
    val silverWeight: Double, // suggested silver weight
    val recommendedPrice: Double // estimated price
)

class JewelleryRepository(private val dao: JewelleryDao) {

    val cartItems: Flow<List<CartEntity>> = dao.getCartItems()
    val wishlistItems: Flow<List<WishlistEntity>> = dao.getWishlistItems()
    val restoredDesigns: Flow<List<CustomDesignEntity>> = dao.getCustomDesigns()
    val communityListings: Flow<List<CommunityListingEntity>> = dao.getCommunityListings()

    // Database: Community listings operations
    suspend fun postCommunityListing(
        title: String,
        description: String,
        price: Double,
        silverWeight: Double,
        bellsType: String,
        purity: String,
        contactInfo: String,
        sellerName: String,
        imageSpec: String
    ) {
        withContext(Dispatchers.IO) {
            dao.insertCommunityListing(
                CommunityListingEntity(
                    title = title,
                    description = description,
                    price = price,
                    silverWeight = silverWeight,
                    purity = purity,
                    bellsType = bellsType,
                    contactInfo = contactInfo,
                    sellerName = sellerName,
                    imageSpec = imageSpec
                )
            )
        }
    }

    suspend fun removeCommunityListing(listing: CommunityListingEntity) {
        withContext(Dispatchers.IO) {
            dao.deleteCommunityListing(listing)
        }
    }

    // Database: Cart operations
    suspend fun addToCart(productId: Int, title: String, category: String, price: Double, imageSpec: String, metadata: String = "") {
        withContext(Dispatchers.IO) {
            dao.insertCartItem(
                CartEntity(
                    productId = productId,
                    title = title,
                    category = category,
                    quantity = 1,
                    price = price,
                    imageSpec = imageSpec,
                    metadata = metadata
                )
            )
        }
    }

    suspend fun updateCartQuantity(item: CartEntity, change: Int) {
        withContext(Dispatchers.IO) {
            val newQty = item.quantity + change
            if (newQty <= 0) {
                dao.deleteCartItem(item)
            } else {
                dao.updateCartItem(item.copy(quantity = newQty))
            }
        }
    }

    suspend fun removeFromCart(item: CartEntity) {
        withContext(Dispatchers.IO) {
            dao.deleteCartItem(item)
        }
    }

    suspend fun checkout() {
        withContext(Dispatchers.IO) {
            dao.clearCart()
        }
    }

    // Database: Wishlist operations
    suspend fun toggleWishlist(productId: Int, title: String, category: String, price: Double, imageSpec: String, metadata: String = "") {
        withContext(Dispatchers.IO) {
            val exists = dao.isWishlisted(productId, title)
            if (exists) {
                dao.deleteWishlistItem(productId, title)
            } else {
                dao.insertWishlistItem(
                    WishlistEntity(
                        productId = productId,
                        title = title,
                        category = category,
                        price = price,
                        imageSpec = imageSpec,
                        metadata = metadata
                    )
                )
            }
        }
    }

    suspend fun isWishlisted(productId: Int, title: String): Boolean {
        return withContext(Dispatchers.IO) {
            dao.isWishlisted(productId, title)
        }
    }

    // Database: Custom design local archiving
    suspend fun addCustomDesign(design: CustomDesignEntity): Long {
        return withContext(Dispatchers.IO) {
            dao.insertCustomDesign(design)
        }
    }

    suspend fun deleteCustomDesign(id: Int) {
        withContext(Dispatchers.IO) {
            dao.deleteCustomDesignById(id)
        }
    }

    // AI logic: call Gemini model to generate a custom-designed artisan bracelet
    suspend fun getAiDesignedBracelet(userPrompt: String): AiBraceletResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getMockAiBraceletResponse(userPrompt)
        }

        val systemPrompt = """
            You are a master silversmith. Generate an exquisite, custom-designed artisan silver bracelet based on the user's dream draft prompt.
            You MUST return a JSON object with this exact JSON structure:
            {
              "title": "Bespoke bracelet name",
              "style": "Cuff" or "Chain" or "Braided" or "Bangle",
              "finish": "Polished" or "Oxidized" or "Hammered" or "Brushed",
              "gemstone": "Turquoise" or "Amethyst" or "Onyx" or "Garnet" or "Sterling Bead" or "None",
              "engraving": "Short custom engraving text suitable for silver",
              "description": "Indulgent artisan jeweller's visual description explaining how your silversmithing techniques match their dream.",
              "silverWeight": 15.0,
              "recommendedPrice": 140.0
            }
            Substitute with appropriate custom values for weights and pricing (Weight between 15 and 35g, Pricing between 120 and 420 dollars).
            Do not output any introductory prose or markdown code blocks except raw JSON. It must be valid JSON matching the schema.
        """.trimIndent()

        val apiRequest = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = "Dream description: $userPrompt")))),
            generationConfig = GenerationConfig(
                responseFormat = ResponseFormat(text = ResponseFormatText(mimeType = "application/json")),
                temperature = 0.9f
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        try {
            val response = GeminiClient.service.generateContent(apiKey, apiRequest)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!jsonText.isNullOrEmpty()) {
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(AiBraceletResponse::class.java)
                adapter.fromJson(jsonText)
            } else {
                getMockAiBraceletResponse(userPrompt)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getMockAiBraceletResponse(userPrompt)
        }
    }

    private fun getMockAiBraceletResponse(userPrompt: String): AiBraceletResponse {
        val title = when {
            userPrompt.contains("moon", true) || userPrompt.contains("star", true) || userPrompt.contains("celestial", true) -> "Selene's Galactic Cuff"
            userPrompt.contains("sea", true) || userPrompt.contains("ocean", true) || userPrompt.contains("serpent", true) -> "Poseidon Tides Braided"
            userPrompt.contains("vintage", true) || userPrompt.contains("antique", true) -> "Aethelgard Heirloom Chain"
            else -> "Argent Boutique Classic Bangle"
        }
        val style = when {
            userPrompt.contains("cuff", true) -> "Cuff"
            userPrompt.contains("chain", true) -> "Chain"
            userPrompt.contains("braid", true) -> "Braided"
            else -> "Cuff"
        }
        val finish = when {
            userPrompt.contains("antique", true) || userPrompt.contains("dark", true) -> "Oxidized"
            userPrompt.contains("hammer", true) -> "Hammered"
            userPrompt.contains("brushed", true) -> "Brushed"
            else -> "Polished"
        }
        val gemstone = when {
            userPrompt.contains("blue", true) || userPrompt.contains("turquoise", true) -> "Turquoise"
            userPrompt.contains("purple", true) || userPrompt.contains("amethyst", true) -> "Amethyst"
            userPrompt.contains("black", true) || userPrompt.contains("onyx", true) -> "Onyx"
            userPrompt.contains("garnet", true) || userPrompt.contains("red", true) -> "Garnet"
            else -> "Sterling Bead"
        }
        val engraving = if (userPrompt.length in 1..25) userPrompt.uppercase() else "WISDOM & PATHS"
        val desc = "Lovingly designed using traditional silversmithing techniques. Features organic textures echoing '$userPrompt', with heavy links or cuff edges finished to order."
        val weight = 16.5 + (userPrompt.length % 15)
        val price = 140.0 + (weight * 6.5)

        return AiBraceletResponse(
            title = title,
            style = style,
            finish = finish,
            gemstone = gemstone,
            engraving = engraving,
            description = desc,
            silverWeight = Math.round(weight * 10.0) / 10.0,
            recommendedPrice = Math.round(price) + 0.00
        )
    }

    fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> {
        return dao.getReviewsForProduct(productId)
    }

    suspend fun insertReview(review: ReviewEntity) {
        withContext(Dispatchers.IO) {
            dao.insertReview(review)
        }
    }

    suspend fun seedDefaultReviewsIfEmpty() {
        withContext(Dispatchers.IO) {
            val count = dao.getReviewsCount()
            if (count == 0) {
                val defaults = listOf(
                    ReviewEntity(productId = 1, rating = 5, author = "Aria G.", comment = "Absolutely gorgeous! The Celtic grooves have a beautiful dark oxidized silver detailing."),
                    ReviewEntity(productId = 1, rating = 4, author = "Brian D.", comment = "Very heavy, premium sterling silver feel. The detailing is perfect."),
                    ReviewEntity(productId = 2, rating = 5, author = "Liam M.", comment = "Imposing and flat out beautiful square Onyx, very high quality finish."),
                    ReviewEntity(productId = 3, rating = 5, author = "Seraphina K.", comment = "Stunning moonstone solitaire, the blue sparkles are amazing under any light."),
                    ReviewEntity(productId = 4, rating = 5, author = "Evelyn T.", comment = "Beautiful delicate crescent moon. Drapes elegantly and the sapphires really shimmer!"),
                    ReviewEntity(productId = 7, rating = 5, author = "Karan S.", comment = "The turquoise stone is top-shelf quality, hand hammered marks make it unique.")
                )
                for (rev in defaults) {
                    dao.insertReview(rev)
                }
            }
        }
    }
}
