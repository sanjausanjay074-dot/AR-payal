package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int, // -1 if custom
    val title: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val imageSpec: String, // image key or custom specification
    val metadata: String = "" // custom detail specs (JSON or formatted text)
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int, // -1 if custom
    val title: String,
    val category: String,
    val price: Double,
    val imageSpec: String,
    val metadata: String = ""
)

@Entity(tableName = "custom_designs")
data class CustomDesignEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val style: String,
    val finish: String,
    val gemstone: String,
    val engraving: String,
    val price: Double,
    val silverWeight: Double,
    val aiPrompt: String = "",
    val aiDescription: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_listings")
data class CommunityListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val silverWeight: Double,
    val purity: String = "925 Pure Silver",
    val bellsType: String = "Ghungroo Bells",
    val contactInfo: String,
    val sellerName: String,
    val imageSpec: String = "custom_payal:Heavy Chain:Polished:Ghungroo Bells:None",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "product_reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val rating: Int, // 1 to 5
    val author: String,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface JewelleryDao {
    // Cart operations
    @Query("SELECT * FROM cart_items ORDER BY id ASC")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Update
    suspend fun updateCartItem(item: CartEntity)

    @Delete
    suspend fun deleteCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Wishlist operations
    @Query("SELECT * FROM wishlist_items ORDER BY id DESC")
    fun getWishlistItems(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId AND (:productId != -1 OR title = :title)")
    suspend fun deleteWishlistItem(productId: Int, title: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId AND (:productId != -1 OR title = :title))")
    suspend fun isWishlisted(productId: Int, title: String): Boolean

    // Custom designs
    @Query("SELECT * FROM custom_designs ORDER BY createdAt DESC")
    fun getCustomDesigns(): Flow<List<CustomDesignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomDesign(design: CustomDesignEntity): Long

    @Query("DELETE FROM custom_designs WHERE id = :id")
    suspend fun deleteCustomDesignById(id: Int)

    // Community / User posted listings for sell
    @Query("SELECT * FROM community_listings ORDER BY createdAt DESC")
    fun getCommunityListings(): Flow<List<CommunityListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityListing(listing: CommunityListingEntity): Long

    @Delete
    suspend fun deleteCommunityListing(listing: CommunityListingEntity)

    // Review operations
    @Query("SELECT * FROM product_reviews WHERE productId = :productId ORDER BY timestamp DESC")
    fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT COUNT(*) FROM product_reviews")
    suspend fun getReviewsCount(): Int
}

@Database(entities = [CartEntity::class, WishlistEntity::class, CustomDesignEntity::class, CommunityListingEntity::class, ReviewEntity::class], version = 3, exportSchema = false)
abstract class JewelleryDatabase : RoomDatabase() {
    abstract fun jewelleryDao(): JewelleryDao
}
