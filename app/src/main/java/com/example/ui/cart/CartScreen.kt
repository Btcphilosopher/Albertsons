package com.example.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.CartItemWithProduct
import com.example.ui.viewmodel.CartSummary

@Composable
fun CartScreen(
    cartSummary: CartSummary,
    fulfillmentMethod: FulfillmentMethod,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("cart_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("YOUR CART (${cartSummary.items.sumOf { it.quantity }})", fontWeight = FontWeight.Black, fontSize = 18.sp)
            }

            if (cartSummary.items.isNotEmpty()) {
                Text(
                    text = "Clear Cart",
                    fontSize = 12.sp,
                    color = DeepRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClearCart() }
                )
            }
        }

        if (cartSummary.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart Empty", tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your Cart is Empty", fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add fresh groceries, deals, and recipes to start your order.", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRed)
                    ) {
                        Text("START SHOPPING")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Smart Savings Callout Banner
                if (cartSummary.totalSavings > 0) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = LightGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Savings, contentDescription = "Savings", tint = FreshGreen, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "YOU ARE SAVING $${String.format("%.2f", cartSummary.totalSavings)} ON THIS ORDER!",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = DarkGreen
                                    )
                                    Text("Member deals and clipped coupons applied.", fontSize = 11.sp, color = FreshGreen)
                                }
                            }
                        }
                    }
                }

                // Cart Items
                items(cartSummary.items, key = { it.product.id }) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { qty -> onQuantityChange(item.product.id, qty) },
                        onRemove = { onRemoveItem(item.product.id) }
                    )
                }

                // Smart Savings Summary Breakdown Card
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("ORDER SUMMARY", fontWeight = FontWeight.Black, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            SummaryRow("Subtotal", "$${String.format("%.2f", cartSummary.subtotal)}")

                            if (cartSummary.memberSavings > 0) {
                                SummaryRow("Member Savings", "-$${String.format("%.2f", cartSummary.memberSavings)}", isDiscount = true)
                            }

                            if (cartSummary.couponSavings > 0) {
                                SummaryRow("Digital Coupons", "-$${String.format("%.2f", cartSummary.couponSavings)}", isDiscount = true)
                            }

                            SummaryRow("Estimated Tax", "$${String.format("%.2f", cartSummary.estimatedTax)}")
                            SummaryRow("${fulfillmentMethod.displayName} Fee", if (cartSummary.deliveryFee > 0) "$${String.format("%.2f", cartSummary.deliveryFee)}" else "FREE")

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("TOTAL ESTIMATE", fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    Text("+${cartSummary.pointsToEarn} Reward Points Earned", fontSize = 11.sp, color = GoldRewards, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "$${String.format("%.2f", cartSummary.finalTotal)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = DeepRed
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Checkout Bar
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("$${String.format("%.2f", cartSummary.finalTotal)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DeepRed)
                    }

                    Button(
                        onClick = onProceedToCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("checkout_btn")
                    ) {
                        Text("CHECKOUT NOW →", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemWithProduct,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = item.product.name, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.brand, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                Text("Aisle: ${item.product.aisle}", fontSize = 10.sp, color = DeepRed)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${String.format("%.2f", item.itemTotal)}", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = DeepRed)
                }

                Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                IconButton(onClick = { onQuantityChange(item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = DeepRed)
                }

                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Remove", tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = if (isDiscount) FreshGreen else Color.DarkGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isDiscount) FreshGreen else Color.Black)
    }
}
