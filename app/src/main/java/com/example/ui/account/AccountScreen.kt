package com.example.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.data.local.ScheduledGroceryEntity
import com.example.data.model.Store
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.GoldRewards

@Composable
fun AccountScreen(
    currentStore: Store,
    stores: List<Store>,
    orderHistory: List<OrderEntity>,
    scheduledItems: List<ScheduledGroceryEntity>,
    onSelectStore: (Store) -> Unit,
    onPharmacyClick: () -> Unit,
    onFuelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubSection by remember { mutableStateOf("MAIN") } // MAIN, ORDERS, STORES, SCHEDULE, WALLET

    when (activeSubSection) {
        "ORDERS" -> OrderHistoryView(orderHistory) { activeSubSection = "MAIN" }
        "STORES" -> StoreLocatorView(stores, currentStore, onSelectStore) { activeSubSection = "MAIN" }
        "SCHEDULE" -> ScheduleSaveView(scheduledItems) { activeSubSection = "MAIN" }
        "WALLET" -> WalletView { activeSubSection = "MAIN" }
        else -> MainAccountView(
            currentStore = currentStore,
            orderCount = orderHistory.size,
            scheduleCount = scheduledItems.size,
            onNavigateSection = { activeSubSection = it },
            onPharmacyClick = onPharmacyClick,
            onFuelClick = onFuelClick,
            modifier = modifier
        )
    }
}

@Composable
fun MainAccountView(
    currentStore: Store,
    orderCount: Int,
    scheduleCount: Int,
    onNavigateSection: (String) -> Unit,
    onPharmacyClick: () -> Unit,
    onFuelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(DeepRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alex Taylor", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("alex.taylor@example.com", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("FreshMarket Member • Gold Status", fontSize = 11.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Navigation Tiles
        item {
            AccountTile("Order History & Digital Receipts", "$orderCount past orders", Icons.Outlined.ReceiptLong) {
                onNavigateSection("ORDERS")
            }
        }

        item {
            AccountTile("Store Locator", "${currentStore.name} selected", Icons.Outlined.Storefront) {
                onNavigateSection("STORES")
            }
        }

        item {
            AccountTile("Schedule & Save", "$scheduleCount active subscriptions", Icons.Outlined.Autorenew) {
                onNavigateSection("SCHEDULE")
            }
        }

        item {
            AccountTile("Digital Wallet & Member Barcode", "Payment cards & scan ID", Icons.Outlined.QrCodeScanner) {
                onNavigateSection("WALLET")
            }
        }

        item {
            AccountTile("Pharmacy & Health Hub", "Prescriptions & Vaccines", Icons.Outlined.MedicalServices) {
                onPharmacyClick()
            }
        }

        item {
            AccountTile("Fuel Rewards Station Finder", "Gas discounts & nearest pumps", Icons.Outlined.LocalGasStation) {
                onFuelClick()
            }
        }
    }
}

@Composable
fun AccountTile(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = DeepRed, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.Gray)
        }
    }
}

@Composable
fun OrderHistoryView(orders: List<OrderEntity>, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Order History & Receipts", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No past orders found.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders, key = { it.orderId }) { order ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(order.orderId, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Surface(
                                    color = FreshGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        order.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreshGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${order.storeName} • ${order.fulfillmentMethod}", fontSize = 12.sp, color = Color.Gray)
                            Text("Items: ${order.itemCount} | Total: $${String.format("%.2f", order.total)} (Saved $${String.format("%.2f", order.discount)})", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(order.itemsSummaryJson, fontSize = 11.sp, color = Color.DarkGray, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreLocatorView(
    stores: List<Store>,
    currentStore: Store,
    onSelectStore: (Store) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Store Locator", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stores, key = { it.id }) { store ->
                val isSelected = store.id == currentStore.id
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, DeepRed) else null,
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(store.name, fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Text("${store.distanceMiles} mi", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepRed)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(store.address, fontSize = 12.sp, color = Color.Gray)
                        Text("Hours: ${store.hours}", fontSize = 11.sp, color = FreshGreen)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Services: ${store.services.joinToString(" • ")}", fontSize = 11.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isSelected) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("CURRENT STORE SELECTED ✓", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onSelectStore(store) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SET AS MY STORE", color = DeepRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleSaveView(items: List<ScheduledGroceryEntity>, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Schedule & Save", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Automatically reorder staple groceries on a set schedule and save 5% on every delivery.", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        val defaultSchedules = listOf(
            "Organic Whole Milk 1 Gal" to "Every Week",
            "Starbucks Pike Place Coffee 12oz" to "Every 2 Weeks",
            "Bounty Paper Towels 6pk" to "Every 4 Weeks"
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(defaultSchedules) { (name, freq) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Frequency: $freq", fontSize = 12.sp, color = DeepRed, fontWeight = FontWeight.Bold)
                            Text("Next order: Tuesday, Sep 1", fontSize = 11.sp, color = Color.Gray)
                        }

                        Switch(checked = true, onCheckedChange = {})
                    }
                }
            }
        }
    }
}

@Composable
fun WalletView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Digital Wallet & Member ID", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barcode / QR Code Scanner Card for In-Store Scan
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SCAN AT CHECKOUT FOR MEMBER SAVINGS", color = GoldRewards, fontWeight = FontWeight.Black, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.QrCode2, contentDescription = "QR Code", tint = Color.Black, modifier = Modifier.size(80.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("FreshMarket ID: 8829-4019-33", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
