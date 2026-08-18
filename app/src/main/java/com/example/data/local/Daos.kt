package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list_items ORDER BY isChecked ASC, estimatedAisle ASC")
    fun getAllListItems(): Flow<List<ShoppingListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListItem(item: ShoppingListItemEntity)

    @Update
    suspend fun updateListItem(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE id = :id")
    suspend fun deleteListItem(id: String)

    @Query("DELETE FROM shopping_list_items WHERE isChecked = 1")
    suspend fun clearCheckedItems()

    @Query("DELETE FROM shopping_list_items")
    suspend fun clearAllList()
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM clipped_coupons")
    fun getClippedCoupons(): Flow<List<ClippedCouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun clipCoupon(coupon: ClippedCouponEntity)

    @Query("DELETE FROM clipped_coupons WHERE couponId = :couponId")
    suspend fun unclipCoupon(couponId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders_history ORDER BY timestamp DESC")
    fun getOrdersHistory(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
}

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY status ASC")
    fun getPantryItems(): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePantryItem(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deletePantryItem(id: String)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM scheduled_groceries")
    fun getScheduledItems(): Flow<List<ScheduledGroceryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSchedule(item: ScheduledGroceryEntity)

    @Query("DELETE FROM scheduled_groceries WHERE id = :id")
    suspend fun deleteSchedule(id: String)
}
