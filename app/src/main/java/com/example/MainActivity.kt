package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.Product
import com.example.data.model.Recipe
import com.example.ui.account.AccountScreen
import com.example.ui.ai.AiAssistantBottomSheet
import com.example.ui.cart.CartScreen
import com.example.ui.cart.CheckoutScreen
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.components.TopHeaderBar
import com.example.ui.deals.DealsScreen
import com.example.ui.dialogs.ProductDetailDialog
import com.example.ui.dialogs.RecipeDetailDialog
import com.example.ui.fuel.FuelScreen
import com.example.ui.home.HomeScreen
import com.example.ui.instore.InStoreModeScreen
import com.example.ui.list.ListScreen
import com.example.ui.pharmacy.PharmacyScreen
import com.example.ui.shop.ShopScreen
import com.example.ui.theme.FreshMarketTheme
import com.example.ui.viewmodel.FreshMarketViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FreshMarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FreshMarketTheme {
                FreshMarketSuperApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreshMarketSuperApp(viewModel: FreshMarketViewModel) {
    // Collect Flow States
    val currentStore by viewModel.selectedStore.collectAsState()
    val stores = viewModel.stores
    val fulfillmentMethod by viewModel.fulfillmentMethod.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDepartment by viewModel.selectedDepartment.collectAsState()
    val selectedProductDetail by viewModel.selectedProductDetail.collectAsState()
    val rewardPoints by viewModel.rewardPoints.collectAsState()

    val allProducts = viewModel.allProducts
    val departments = viewModel.departments
    val coupons = viewModel.coupons
    val rewardOffers = viewModel.rewardOffers
    val recipes = viewModel.recipes
    val prescriptions = viewModel.prescriptions
    val fuelStations = viewModel.fuelStations

    val shoppingList by viewModel.shoppingList.collectAsState()
    val rawCartItems by viewModel.rawCartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val clippedCoupons by viewModel.clippedCoupons.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val pantryItems by viewModel.pantryItems.collectAsState()
    val scheduledItems by viewModel.scheduledGroceries.collectAsState()

    val isInStoreMode by viewModel.isInStoreMode.collectAsState()
    val scannedProduct by viewModel.scannedProduct.collectAsState()
    val assistantMessages by viewModel.assistantMessages.collectAsState()
    val isAssistantLoading by viewModel.isAssistantLoading.collectAsState()

    // Navigation state
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var currentSubView by remember { mutableStateOf<String?>(null) } // "CART", "CHECKOUT", "PHARMACY", "FUEL"
    var showAiAssistantSheet by remember { mutableStateOf(false) }
    var selectedRecipeDetail by remember { mutableStateOf<Recipe?>(null) }

    val clippedCouponIds = remember(clippedCoupons) { clippedCoupons.map { it.couponId }.toSet() }
    val cartQuantityMap = remember(rawCartItems) { rawCartItems.associate { it.productId to it.quantity } }

    Scaffold(
        topBar = {
            if (currentSubView == null && !isInStoreMode) {
                TopHeaderBar(
                    currentStore = currentStore,
                    fulfillmentMethod = fulfillmentMethod,
                    rewardPoints = rewardPoints,
                    cartCount = cartSummary.items.sumOf { it.quantity },
                    searchQuery = searchQuery,
                    isInStoreMode = isInStoreMode,
                    onStoreClick = { currentTab = NavTab.ACCOUNT },
                    onFulfillmentChange = { viewModel.setFulfillmentMethod(it) },
                    onSearchChange = {
                        viewModel.setSearchQuery(it)
                        if (it.isNotEmpty() && currentTab != NavTab.SHOP) {
                            currentTab = NavTab.SHOP
                        }
                    },
                    onCartClick = { currentSubView = "CART" },
                    onAiAssistantClick = { showAiAssistantSheet = true },
                    onInStoreModeToggle = { viewModel.setInStoreMode(it) }
                )
            }
        },
        bottomBar = {
            if (currentSubView == null && !isInStoreMode) {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        currentTab = tab
                        currentSubView = null
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .testTag("fresh_market_super_app")
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isInStoreMode) {
                InStoreModeScreen(
                    shoppingList = shoppingList,
                    allProducts = allProducts,
                    scannedProduct = scannedProduct,
                    onScanSimulate = { viewModel.setScannedProduct(it) },
                    onClearScan = { viewModel.setScannedProduct(null) },
                    onAddToCart = { viewModel.addProductToCart(it.id) },
                    onToggleCheck = { viewModel.toggleListItemCheck(it) },
                    onCloseInStore = { viewModel.setInStoreMode(false) }
                )
            } else {
                when (currentSubView) {
                    "CART" -> CartScreen(
                        cartSummary = cartSummary,
                        fulfillmentMethod = fulfillmentMethod,
                        onQuantityChange = { pid, qty -> viewModel.updateCartQuantity(pid, qty) },
                        onRemoveItem = { pid -> viewModel.updateCartQuantity(pid, 0) },
                        onClearCart = { viewModel.clearCart() },
                        onProceedToCheckout = { currentSubView = "CHECKOUT" },
                        onBack = { currentSubView = null }
                    )

                    "CHECKOUT" -> CheckoutScreen(
                        cartSummary = cartSummary,
                        currentStore = currentStore,
                        fulfillmentMethod = fulfillmentMethod,
                        onPlaceOrder = { viewModel.checkoutAndCreateOrder() },
                        onBack = { currentSubView = null }
                    )

                    "PHARMACY" -> PharmacyScreen(
                        prescriptions = prescriptions,
                        onBack = { currentSubView = null }
                    )

                    "FUEL" -> FuelScreen(
                        fuelStations = fuelStations,
                        rewardPoints = rewardPoints,
                        onBack = { currentSubView = null }
                    )

                    else -> when (currentTab) {
                        NavTab.HOME -> HomeScreen(
                            allProducts = allProducts,
                            coupons = coupons,
                            recipes = recipes,
                            clippedCouponIds = clippedCouponIds,
                            cartQuantityMap = cartQuantityMap,
                            onTabSelect = { currentTab = it },
                            onProductClick = { viewModel.selectProductDetail(it) },
                            onAddToCart = { viewModel.addProductToCart(it.id) },
                            onQuantityChange = { prod, qty -> viewModel.updateCartQuantity(prod.id, qty) },
                            onCouponClipToggle = { viewModel.toggleClipCoupon(it.id) },
                            onRecipeClick = { selectedRecipeDetail = it },
                            onPharmacyClick = { currentSubView = "PHARMACY" },
                            onFuelClick = { currentSubView = "FUEL" }
                        )

                        NavTab.SHOP -> ShopScreen(
                            departments = departments,
                            allProducts = allProducts,
                            selectedDepartment = selectedDepartment,
                            searchQuery = searchQuery,
                            cartQuantityMap = cartQuantityMap,
                            onSelectDepartment = { viewModel.selectDepartment(it) },
                            onProductClick = { viewModel.selectProductDetail(it) },
                            onAddToCart = { viewModel.addProductToCart(it.id) },
                            onQuantityChange = { prod, qty -> viewModel.updateCartQuantity(prod.id, qty) }
                        )

                        NavTab.LIST -> ListScreen(
                            shoppingList = shoppingList,
                            pantryItems = pantryItems,
                            onAddListItem = { name, cat, aisle -> viewModel.addCustomListItem(name, cat, aisle) },
                            onToggleCheck = { viewModel.toggleListItemCheck(it) },
                            onRemoveItem = { viewModel.removeListItem(it) },
                            onClearChecked = { viewModel.clearCheckedListItems() },
                            onMoveCheckedToCart = { /* Move checked items */ },
                            onUpdatePantryItem = { viewModel.updatePantryItem(it) }
                        )

                        NavTab.DEALS -> DealsScreen(
                            coupons = coupons,
                            rewardOffers = rewardOffers,
                            dealProducts = allProducts.filter { it.dealTag != null },
                            rewardPoints = rewardPoints,
                            clippedCouponIds = clippedCouponIds,
                            cartQuantityMap = cartQuantityMap,
                            onCouponClipToggle = { viewModel.toggleClipCoupon(it.id) },
                            onProductClick = { viewModel.selectProductDetail(it) },
                            onAddToCart = { viewModel.addProductToCart(it.id) },
                            onQuantityChange = { prod, qty -> viewModel.updateCartQuantity(prod.id, qty) }
                        )

                        NavTab.ACCOUNT -> AccountScreen(
                            currentStore = currentStore,
                            stores = stores,
                            orderHistory = orderHistory,
                            scheduledItems = scheduledItems,
                            onSelectStore = { viewModel.selectStore(it) },
                            onPharmacyClick = { currentSubView = "PHARMACY" },
                            onFuelClick = { currentSubView = "FUEL" }
                        )
                    }
                }
            }
        }
    }

    // AI Assistant Bottom Sheet
    if (showAiAssistantSheet) {
        AiAssistantBottomSheet(
            messages = assistantMessages,
            isLoading = isAssistantLoading,
            onSendMessage = { viewModel.sendAiAssistantQuery(it) },
            onDismiss = { showAiAssistantSheet = false }
        )
    }

    // Product Detail Dialog
    selectedProductDetail?.let { product ->
        ProductDetailDialog(
            product = product,
            cartQuantity = cartQuantityMap[product.id] ?: 0,
            onAddToCart = { viewModel.addProductToCart(it.id) },
            onDismiss = { viewModel.selectProductDetail(null) }
        )
    }

    // Recipe Detail Dialog
    selectedRecipeDetail?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            onAddIngredientsToCart = { viewModel.addRecipeIngredientsToCart(it) },
            onDismiss = { selectedRecipeDetail = null }
        )
    }
}
