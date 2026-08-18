package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey val id: String,
    val productId: String? = null,
    val name: String,
    val category: String,
    val quantity: Int = 1,
    val isChecked: Boolean = false,
    val estimatedAisle: String = "Aisle 1",
    val estimatedPrice: Double = 0.0,
    val note: String = ""
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val substitutionPreference: String = "BEST_MATCH", // BEST_MATCH, SAME_BRAND, SAME_SIZE, LOWER_PRICE, NO_SUB
    val note: String = ""
)

@Entity(tableName = "clipped_coupons")
data class ClippedCouponEntity(
    @PrimaryKey val couponId: String,
    val clippedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders_history")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val timestamp: Long,
    val storeName: String,
    val fulfillmentMethod: String,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val total: Double,
    val status: String,
    val itemCount: Int,
    val itemsSummaryJson: String
)

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val status: String, // HAVE, RUNNING_LOW, NEED
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scheduled_groceries")
data class ScheduledGroceryEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val productName: String,
    val frequency: String,
    val nextDeliveryDate: String,
    val quantity: Int,
    val isActive: Boolean = true
)
