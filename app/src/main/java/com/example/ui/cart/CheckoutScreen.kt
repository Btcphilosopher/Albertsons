package com.example.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FulfillmentMethod
import com.example.data.model.Store
import com.example.ui.theme.*
import com.example.ui.viewmodel.CartSummary

@Composable
fun CheckoutScreen(
    cartSummary: CartSummary,
    currentStore: Store,
    fulfillmentMethod: FulfillmentMethod,
    onPlaceOrder: () -> String, // Returns orderId
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTimeSlot by remember { mutableStateOf("Today, 4:00 PM - 5:00 PM") }
    var selectedSubstitution by remember { mutableStateOf("BEST MATCH") }
    var selectedPaymentMethod by remember { mutableStateOf("Visa •••• 4920") }
    var isOrderPlaced by remember { mutableStateOf(false) }
    var confirmedOrderId by remember { mutableStateOf("") }

    if (isOrderPlaced) {
        OrderConfirmationView(
            orderId = confirmedOrderId,
            currentStore = currentStore,
            fulfillmentMethod = fulfillmentMethod,
            totalAmount = cartSummary.finalTotal,
            totalSaved = cartSummary.totalSavings,
            pointsEarned = cartSummary.pointsToEarn,
            timeSlot = selectedTimeSlot,
            onDone = onBack
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("checkout_screen")
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("CHECKOUT", fontWeight = FontWeight.Black, fontSize = 18.sp)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Fulfillment & Time Window
                item {
                    CheckoutSectionCard(title = "1. FULFILLMENT & TIME SLOT") {
                        Text("${fulfillmentMethod.displayName} from ${currentStore.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        val slots = listOf("Today, 4:00 PM - 5:00 PM", "Today, 6:00 PM - 7:00 PM", "Tomorrow, 9:00 AM - 10:00 AM")
                        slots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTimeSlot = slot }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedTimeSlot == slot, onClick = { selectedTimeSlot = slot })
                                Text(slot, fontSize = 13.sp, fontWeight = if (selectedTimeSlot == slot) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // 2. Substitutions Preference
                item {
                    CheckoutSectionCard(title = "2. ITEM SUBSTITUTION PREFERENCE") {
                        Text("If an item becomes unavailable in store:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))

                        val subOptions = listOf(
                            "BEST MATCH" to "Shopper picks similar brand/size",
                            "SAME BRAND" to "Keep brand, match size",
                            "NO SUBSTITUTE" to "Refund item if unavailable"
                        )

                        subOptions.forEach { (option, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSubstitution = option }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedSubstitution == option, onClick = { selectedSubstitution = option })
                                Column {
                                    Text(option, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(desc, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // 3. Payment Method Selection
                item {
                    CheckoutSectionCard(title = "3. PAYMENT METHOD") {
                        val payments = listOf("Visa •••• 4920", "Apple Pay / Google Pay", "FreshMarket Store Wallet")
                        payments.forEach { pay ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaymentMethod = pay }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedPaymentMethod == pay, onClick = { selectedPaymentMethod = pay })
                                Text(pay, fontSize = 13.sp, fontWeight = if (selectedPaymentMethod == pay) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // 4. Final Review Breakdown
                item {
                    CheckoutSectionCard(title = "4. ORDER TOTAL & SAVINGS") {
                        SummaryRow("Subtotal (${cartSummary.items.sumOf { it.quantity }} items)", "$${String.format("%.2f", cartSummary.subtotal)}")
                        if (cartSummary.totalSavings > 0) {
                            SummaryRow("Total Savings", "-$${String.format("%.2f", cartSummary.totalSavings)}", isDiscount = true)
                        }
                        SummaryRow("Estimated Tax", "$${String.format("%.2f", cartSummary.estimatedTax)}")
                        SummaryRow("Total", "$${String.format("%.2f", cartSummary.finalTotal)}")
                    }
                }
            }

            // Bottom Place Order Bar
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        confirmedOrderId = onPlaceOrder()
                        isOrderPlaced = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("place_order_btn")
                ) {
                    Text("PLACE ORDER • $${String.format("%.2f", cartSummary.finalTotal)}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun CheckoutSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = DeepRed)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun OrderConfirmationView(
    orderId: String,
    currentStore: Store,
    fulfillmentMethod: FulfillmentMethod,
    totalAmount: Double,
    totalSaved: Double,
    pointsEarned: Int,
    timeSlot: String,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(FreshGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("ORDER CONFIRMED!", fontWeight = FontWeight.Black, fontSize = 22.sp, color = DarkGreen)
        Text("Order Number: $orderId", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepRed)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("FULFILLMENT DETAILS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Store: ${currentStore.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Method: ${fulfillmentMethod.displayName}", fontSize = 12.sp)
                Text("Time Slot: $timeSlot", fontSize = 12.sp, color = FreshGreen, fontWeight = FontWeight.Bold)

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                SummaryRow("Total Paid", "$${String.format("%.2f", totalAmount)}")
                SummaryRow("You Saved", "$${String.format("%.2f", totalSaved)}", isDiscount = true)
                SummaryRow("Points Earned", "+$pointsEarned PTS")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("DONE & BACK TO SHOPPING", fontWeight = FontWeight.Bold)
        }
    }
}
