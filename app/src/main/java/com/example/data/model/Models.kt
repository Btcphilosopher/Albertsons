package com.example.data.model

import androidx.annotation.DrawableRes

enum class FulfillmentMethod(val displayName: String, val eta: String, val fee: String) {
    DRIVEUP_AND_GO("DriveUp & Go™", "Ready in 2 hrs", "FREE"),
    DELIVERY("Delivery", "30-45 min", "$3.95"),
    FLASH_DELIVERY("Flash 30-Min", "Under 30 min", "$5.95"),
    IN_STORE("In-Store Mode", "Aisle Navigator", "N/A")
}

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}

data class NutritionFacts(
    val calories: Int = 120,
    val protein: String = "4g",
    val fat: String = "2g",
    val carbs: String = "18g",
    val sodium: String = "150mg"
)

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val department: String,
    val category: String,
    val price: Double,
    val salePrice: Double? = null,
    val memberPrice: Double? = null,
    val unitPriceStr: String, // e.g. "$0.24 / oz" or "$1.49 / lb"
    val size: String,
    val isOrganic: Boolean = false,
    val isGlutenFree: Boolean = false,
    val dealTag: String? = null, // e.g. "BUY 1 GET 1 FREE" or "SAVE $1.50"
    val aisle: String, // e.g. "Aisle 4 - Dairy"
    val description: String = "Freshly sourced high quality grocery item from local partners.",
    val allergensList: List<String> = emptyList(),
    val nutritionFacts: NutritionFacts? = NutritionFacts(),
    val nutritionInfo: Map<String, String> = emptyMap(),
    val calories: Int? = null,
    val ingredients: String = "",
    val origin: String = "USA",
    val rating: Float = 4.8f,
    val reviewCount: Int = 124,
    val stockStatus: StockStatus = StockStatus.IN_STOCK,
    val imageUrl: String = "",
    val weightLbEstimate: Double? = null
) {
    val allergens: List<String>
        get() = if (allergensList.isNotEmpty()) allergensList else listOf("None")
}

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val distanceMiles: Double,
    val hours: String,
    val isOpen: Boolean,
    val phone: String,
    val services: List<String>,
    val deliveryEtaMinutes: Int = 35,
    val pickupEtaMinutes: Int = 45
)

data class Coupon(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val discountAmount: Double,
    val originalRequirement: String,
    val expiresDate: String,
    val isClipped: Boolean = false,
    val code: String = "CLIP100"
)

enum class RewardType {
    GROCERY_OFF,
    GAS_DISCOUNT,
    CASH_BACK,
    FREE_ITEM
}

data class RewardOffer(
    val id: String,
    val title: String,
    val pointsRequired: Int,
    val rewardType: RewardType,
    val valueDisplay: String,
    val description: String
)

data class RecipeIngredient(
    val productId: String,
    val name: String,
    val amountDisplay: String,
    val isOptional: Boolean = false
) {
    val productName: String get() = name
    val quantityStr: String get() = amountDisplay
}

data class Recipe(
    val id: String,
    val title: String,
    val category: String, // Quick Dinners, Family, Healthy, Budget, High Protein
    val prepTimeMinutes: Int,
    val servings: Int,
    val caloriesPerServing: Int,
    val estimatedCostForFour: Double,
    val description: String = "Delicious chef-crafted recipe using fresh supermarket ingredients.",
    val imageUrl: String = "",
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val dealsCount: Int = 0
)

data class Prescription(
    val id: String,
    val rxNumber: String,
    val medicationName: String,
    val dosage: String,
    val quantity: Int,
    val doctorName: String,
    val refillsRemaining: Int,
    val lastRefillDate: String,
    val status: String, // "Ready for Pickup", "Refill Due", "Processing"
    val copayAmount: Double
) {
    val prescriber: String get() = doctorName
}

data class FuelStation(
    val id: String,
    val name: String,
    val address: String,
    val distanceMiles: Double,
    val regularPrice: Double,
    val pointsDiscountAvailable: Double, // e.g., $0.20 off per gallon
    val isOpen24Hours: Boolean = true
) {
    val priceRegular: Double get() = regularPrice
}

data class PantryItem(
    val id: String,
    val name: String,
    val category: String,
    val status: String, // "HAVE", "RUNNING_LOW", "NEED"
    val lastUpdated: String
)

data class ScheduledItem(
    val id: String,
    val productId: String,
    val productName: String,
    val frequency: String, // "Every Week", "Every 2 Weeks", "Every 4 Weeks"
    val nextDeliveryDate: String,
    val quantity: Int,
    val isActive: Boolean = true
)

data class DigitalReceipt(
    val orderId: String,
    val date: String,
    val storeName: String,
    val totalAmount: Double,
    val totalSaved: Double,
    val pointsEarned: Int,
    val itemCount: Int,
    val fulfillmentType: String,
    val items: List<String>
)
