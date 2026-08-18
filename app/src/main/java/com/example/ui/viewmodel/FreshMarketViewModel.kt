package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.GeminiAssistantRepository
import com.example.data.repository.FreshMarketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItemWithProduct(
    val product: Product,
    val quantity: Int,
    val substitutionPreference: String,
    val itemTotal: Double
)

data class CartSummary(
    val items: List<CartItemWithProduct> = emptyList(),
    val subtotal: Double = 0.0,
    val memberSavings: Double = 0.0,
    val couponSavings: Double = 0.0,
    val estimatedTax: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val finalTotal: Double = 0.0,
    val totalSavings: Double = 0.0,
    val pointsToEarn: Int = 0
)

class FreshMarketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = FreshMarketRepository(db)
    private val assistantRepo = GeminiAssistantRepository()

    // -----------------------------------------------------------------
    // STATE FLOWS
    // -----------------------------------------------------------------
    val stores = repository.getStores()
    val allProducts = repository.getAllProducts()
    val departments = repository.getDepartments()
    val coupons = repository.getDigitalCoupons()
    val rewardOffers = repository.getRewardOffers()
    val recipes = repository.getRecipes()
    val prescriptions = repository.getPrescriptions()
    val fuelStations = repository.getFuelStations()

    private val _selectedStore = MutableStateFlow(stores.first())
    val selectedStore: StateFlow<Store> = _selectedStore.asStateFlow()

    private val _fulfillmentMethod = MutableStateFlow(FulfillmentMethod.DELIVERY)
    val fulfillmentMethod: StateFlow<FulfillmentMethod> = _fulfillmentMethod.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDepartment = MutableStateFlow<String?>(null)
    val selectedDepartment: StateFlow<String?> = _selectedDepartment.asStateFlow()

    private val _selectedProductDetail = MutableStateFlow<Product?>(null)
    val selectedProductDetail: StateFlow<Product?> = _selectedProductDetail.asStateFlow()

    private val _rewardPoints = MutableStateFlow(850)
    val rewardPoints: StateFlow<Int> = _rewardPoints.asStateFlow()

    val shoppingList: StateFlow<List<ShoppingListItemEntity>> = repository.shoppingList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawCartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clippedCoupons: StateFlow<List<ClippedCouponEntity>> = repository.clippedCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderHistory: StateFlow<List<OrderEntity>> = repository.orderHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pantryItems: StateFlow<List<PantryItemEntity>> = repository.pantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getInitialPantryItems())

    val scheduledGroceries: StateFlow<List<ScheduledGroceryEntity>> = repository.scheduledItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-Store Mode
    private val _isInStoreMode = MutableStateFlow(false)
    val isInStoreMode: StateFlow<Boolean> = _isInStoreMode.asStateFlow()

    private val _scannedProduct = MutableStateFlow<Product?>(null)
    val scannedProduct: StateFlow<Product?> = _scannedProduct.asStateFlow()

    // AI Assistant Messages
    private val _assistantMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "👋 Hello! I'm your FreshMarket AI Assistant. Ask me to plan meals, calculate budget recipes, locate store aisles, or find clipping deals!" to false
        )
    )
    val assistantMessages: StateFlow<List<Pair<String, Boolean>>> = _assistantMessages.asStateFlow()

    private val _isAssistantLoading = MutableStateFlow(false)
    val isAssistantLoading: StateFlow<Boolean> = _isAssistantLoading.asStateFlow()

    // -----------------------------------------------------------------
    // COMPUTED CART & SMART SAVINGS
    // -----------------------------------------------------------------
    val cartSummary: StateFlow<CartSummary> = combine(
        rawCartItems,
        clippedCoupons,
        _fulfillmentMethod
    ) { cartList, clippedList, fulfillment ->
        val clippedIds = clippedList.map { it.couponId }.toSet()
        val allProds = repository.getAllProducts().associateBy { it.id }

        var subtotal = 0.0
        var memberSavings = 0.0
        var couponSavings = 0.0

        val itemsWithProducts = cartList.mapNotNull { cartEntity ->
            val product = allProds[cartEntity.productId] ?: return@mapNotNull null
            val regularPrice = product.price
            val effectivePrice = product.salePrice ?: product.memberPrice ?: product.price

            if (product.salePrice != null) {
                memberSavings += (regularPrice - product.salePrice) * cartEntity.quantity
            }

            subtotal += regularPrice * cartEntity.quantity

            CartItemWithProduct(
                product = product,
                quantity = cartEntity.quantity,
                substitutionPreference = cartEntity.substitutionPreference,
                itemTotal = effectivePrice * cartEntity.quantity
            )
        }

        // Apply Clipped Coupons
        coupons.filter { it.id in clippedIds }.forEach { coupon ->
            couponSavings += coupon.discountAmount
        }

        val deliveryFee = if (fulfillment == FulfillmentMethod.DELIVERY) 3.95 else 0.0
        val discountedSubtotal = (subtotal - memberSavings - couponSavings).coerceAtLeast(0.0)
        val estimatedTax = discountedSubtotal * 0.0825
        val finalTotal = discountedSubtotal + estimatedTax + deliveryFee
        val totalSavings = memberSavings + couponSavings
        val pointsToEarn = finalTotal.toInt()

        CartSummary(
            items = itemsWithProducts,
            subtotal = subtotal,
            memberSavings = memberSavings,
            couponSavings = couponSavings,
            estimatedTax = estimatedTax,
            deliveryFee = deliveryFee,
            finalTotal = finalTotal,
            totalSavings = totalSavings,
            pointsToEarn = pointsToEarn
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

    // -----------------------------------------------------------------
    // ACTIONS
    // -----------------------------------------------------------------
    fun selectStore(store: Store) {
        _selectedStore.value = store
    }

    fun setFulfillmentMethod(method: FulfillmentMethod) {
        _fulfillmentMethod.value = method
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectDepartment(department: String?) {
        _selectedDepartment.value = department
    }

    fun selectProductDetail(product: Product?) {
        _selectedProductDetail.value = product
    }

    fun setInStoreMode(enabled: Boolean) {
        _isInStoreMode.value = enabled
    }

    fun setScannedProduct(product: Product?) {
        _scannedProduct.value = product
    }

    fun addProductToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, quantity)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun toggleClipCoupon(couponId: String) {
        viewModelScope.launch {
            val isCurrentlyClipped = clippedCoupons.value.any { it.couponId == couponId }
            if (isCurrentlyClipped) {
                repository.unclipCoupon(couponId)
            } else {
                repository.clipCoupon(couponId)
            }
        }
    }

    fun addCustomListItem(name: String, category: String = "Produce", aisle: String = "Aisle 1") {
        viewModelScope.launch {
            repository.addToList(
                ShoppingListItemEntity(
                    id = "list_${System.currentTimeMillis()}",
                    name = name,
                    category = category,
                    estimatedAisle = aisle,
                    quantity = 1
                )
            )
        }
    }

    fun toggleListItemCheck(item: ShoppingListItemEntity) {
        viewModelScope.launch {
            repository.updateListItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun removeListItem(id: String) {
        viewModelScope.launch {
            repository.removeFromList(id)
        }
    }

    fun clearCheckedListItems() {
        viewModelScope.launch {
            repository.clearCheckedFromList()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun updatePantryItem(item: PantryItemEntity) {
        viewModelScope.launch {
            repository.updatePantryItem(item)
        }
    }

    fun addRecipeIngredientsToCart(recipe: Recipe, servingsMultiplier: Float = 1.0f) {
        viewModelScope.launch {
            recipe.ingredients.forEach { ingredient ->
                repository.addToCart(ingredient.productId, 1)
            }
        }
    }

    fun checkoutAndCreateOrder(): String {
        val summary = cartSummary.value
        val orderId = "ORD-${(100000..999999).random()}"

        viewModelScope.launch {
            val order = OrderEntity(
                orderId = orderId,
                timestamp = System.currentTimeMillis(),
                storeName = selectedStore.value.name,
                fulfillmentMethod = fulfillmentMethod.value.displayName,
                subtotal = summary.subtotal,
                discount = summary.totalSavings,
                tax = summary.estimatedTax,
                total = summary.finalTotal,
                status = "CONFIRMED",
                itemCount = summary.items.sumOf { it.quantity },
                itemsSummaryJson = summary.items.joinToString(", ") { "${it.product.name} x${it.quantity}" }
            )
            repository.recordOrder(order)
            repository.clearCart()
            _rewardPoints.value += summary.pointsToEarn
        }
        return orderId
    }

    fun sendAiAssistantQuery(userText: String) {
        if (userText.isBlank()) return
        val currentMsgs = _assistantMessages.value.toMutableList()
        currentMsgs.add(userText to true)
        _assistantMessages.value = currentMsgs
        _isAssistantLoading.value = true

        viewModelScope.launch {
            val cartProductNames = cartSummary.value.items.map { it.product.name }
            val clipped = clippedCoupons.value.map { it.couponId }

            val responseText = assistantRepo.queryAssistant(
                userQuery = userText,
                availableProducts = allProducts,
                cartProducts = cartProductNames,
                clippedCoupons = clipped
            )

            val updatedMsgs = _assistantMessages.value.toMutableList()
            updatedMsgs.add(responseText to false)
            _assistantMessages.value = updatedMsgs
            _isAssistantLoading.value = false
        }
    }
}
