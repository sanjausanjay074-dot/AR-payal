package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.CartEntity
import com.example.data.local.CommunityListingEntity
import com.example.data.local.CustomDesignEntity
import com.example.data.local.JewelleryDatabase
import com.example.data.local.WishlistEntity
import com.example.data.local.ReviewEntity
import com.example.data.repository.AiBraceletResponse
import com.example.data.repository.JewelleryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AiDesignState {
    object Idle : AiDesignState
    object Loading : AiDesignState
    data class Success(val response: AiBraceletResponse) : AiDesignState
    data class Error(val message: String) : AiDesignState
}

class JewelleryViewModel(private val repository: JewelleryRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedDefaultReviewsIfEmpty()
        }
    }

    // Filtering category state
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Screen navigation route helper inside a Single-Screen tab/sheet experience
    private val _currentSection = MutableStateFlow("catalog") // "catalog", "customizer", "wishlist", "cart"
    val currentSection: StateFlow<String> = _currentSection.asStateFlow()

    // Flow items from Room DB
    val cartItems: StateFlow<List<CartEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistEntity>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customDesigns: StateFlow<List<CustomDesignEntity>> = repository.restoredDesigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communityListings: StateFlow<List<CommunityListingEntity>> = repository.communityListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI customized design session state
    private val _aiDesignState = MutableStateFlow<AiDesignState>(AiDesignState.Idle)
    val aiDesignState: StateFlow<AiDesignState> = _aiDesignState.asStateFlow()

    // Notification toast state
    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun navigateTo(section: String) {
        _currentSection.value = section
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    // Cart actions
    fun addToCart(productId: Int, title: String, category: String, price: Double, imageSpec: String, metadata: String = "") {
        viewModelScope.launch {
            // Check if item already exists in cart, if yes increment qty
            val curCart = cartItems.value
            val match = curCart.find { it.productId == productId && it.title == title && it.imageSpec == imageSpec }
            if (match != null) {
                repository.updateCartQuantity(match, 1)
            } else {
                repository.addToCart(productId, title, category, price, imageSpec, metadata)
            }
            _notificationMessage.value = "Added '$title' to your bag."
        }
    }

    fun incrementCartQuantity(item: CartEntity) {
        viewModelScope.launch {
            repository.updateCartQuantity(item, 1)
        }
    }

    fun decrementCartQuantity(item: CartEntity) {
        viewModelScope.launch {
            repository.updateCartQuantity(item, -1)
        }
    }

    fun removeFromCart(item: CartEntity) {
        viewModelScope.launch {
            repository.removeFromCart(item)
            _notificationMessage.value = "Removed item from bag."
        }
    }

    fun checkout() {
        viewModelScope.launch {
            repository.checkout()
            _notificationMessage.value = "Order placed successfully! Thank you for purchasing sterling silver art."
        }
    }

    // Wishlist actions
    fun toggleWishlist(productId: Int, title: String, category: String, price: Double, imageSpec: String, metadata: String = "") {
        viewModelScope.launch {
            repository.toggleWishlist(productId, title, category, price, imageSpec, metadata)
            val isFavorite = repository.isWishlisted(productId, title)
            _notificationMessage.value = if (isFavorite) "Removed '$title' from favorites." else "Favorited '$title'!"
        }
    }

    fun isItemWishlisted(productId: Int, title: String): Flow<Boolean> {
        return wishlistItems.map { list ->
            list.any { it.productId == productId && it.title == title }
        }
    }

    // Custom Design Builder - manual additions
    fun saveCustomBracelet(title: String, style: String, finish: String, gemstone: String, engraving: String, price: Double, weight: Double, aiPrompt: String = "", aiDesc: String = "") {
        viewModelScope.launch {
            val entity = CustomDesignEntity(
                title = title.ifBlank { "Bespoke Silver Bracelet" },
                style = style,
                finish = finish,
                gemstone = gemstone,
                engraving = engraving,
                price = price,
                silverWeight = weight,
                aiPrompt = aiPrompt,
                aiDescription = aiDesc
            )
            repository.addCustomDesign(entity)
            _notificationMessage.value = "Saved design '$title' to your Custom Studio!"
        }
    }

    fun deleteCustomDesign(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomDesign(id)
            _notificationMessage.value = "Deleted custom design sketch."
        }
    }

    // Gemini AI bracelet designer service integration
    fun generateAiBracelet(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _aiDesignState.value = AiDesignState.Loading
            try {
                val response = repository.getAiDesignedBracelet(prompt)
                if (response != null) {
                    _aiDesignState.value = AiDesignState.Success(response)
                } else {
                    _aiDesignState.value = AiDesignState.Error("Crafting failed. Please refine your design prompt.")
                }
            } catch (e: Exception) {
                _aiDesignState.value = AiDesignState.Error(e.message ?: "Metal casting error. Please check network.")
            }
        }
    }

    fun resetAiDesign() {
        _aiDesignState.value = AiDesignState.Idle
    }

    fun postPayalForSale(
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
        viewModelScope.launch {
            repository.postCommunityListing(
                title = title.ifBlank { "Unbranded Silver Payal" },
                description = description.ifBlank { "Traditional artisan-crafted silver payal." },
                price = if (price <= 0.0) 120.0 else price,
                silverWeight = if (silverWeight <= 0.0) 18.0 else silverWeight,
                bellsType = bellsType,
                purity = purity,
                contactInfo = contactInfo.ifBlank { "artisan.seller@example.com" },
                sellerName = sellerName.ifBlank { "Silver Artisan" },
                imageSpec = imageSpec
            )
            _notificationMessage.value = "Your bespoke Payal '$title' is now posted for sale!"
        }
    }

    fun removePayalFromSale(listing: CommunityListingEntity) {
        viewModelScope.launch {
            repository.removeCommunityListing(listing)
            _notificationMessage.value = "Removed Payal Listing '${listing.title}'."
        }
    }

    fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> {
        return repository.getReviewsForProduct(productId)
    }

    fun addReview(productId: Int, rating: Int, author: String, comment: String) {
        viewModelScope.launch {
            repository.insertReview(
                ReviewEntity(
                    productId = productId,
                    rating = rating,
                    author = author.ifBlank { "Silver Enthusiast" },
                    comment = comment
                )
            )
            _notificationMessage.value = "Review submitted! Thank you for rating this masterpiece."
        }
    }
}

class JewelleryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JewelleryViewModel::class.java)) {
            val db = Room.databaseBuilder(
                application.applicationContext,
                JewelleryDatabase::class.java, "jewellery_db"
            ).fallbackToDestructiveMigration().build()
            val repository = JewelleryRepository(db.jewelleryDao())
            @Suppress("UNCHECKED_CAST")
            return JewelleryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
