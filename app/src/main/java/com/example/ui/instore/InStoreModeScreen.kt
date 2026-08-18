package com.example.ui.instore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
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
import com.example.data.local.ShoppingListItemEntity
import com.example.data.model.Product
import com.example.ui.theme.*

@Composable
fun InStoreModeScreen(
    shoppingList: List<ShoppingListItemEntity>,
    allProducts: List<Product>,
    scannedProduct: Product?,
    onScanSimulate: (Product) -> Unit,
    onClearScan: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleCheck: (ShoppingListItemEntity) -> Unit,
    onCloseInStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var locatorQuery by remember { mutableStateOf("") }
    var showBarcodeModal by remember { mutableStateOf(false) }

    val searchedProducts = remember(allProducts, locatorQuery) {
        if (locatorQuery.isBlank()) emptyList()
        else allProducts.filter { it.name.contains(locatorQuery, ignoreCase = true) || it.category.contains(locatorQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("in_store_mode_screen")
    ) {
        // Top In-Store Mode Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = "Store", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHOPPING IN STORE • ALBERTSONS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    IconButton(onClick = onCloseInStore) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit In-Store", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barcode Scanner & Product Locator Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showBarcodeModal = true
                            onScanSimulate(allProducts.random())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scan_barcode_btn")
                    ) {
                        Icon(imageVector = Icons.Outlined.QrCodeScanner, contentDescription = "Scan", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SCAN BARCODE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        // Product Locator Search Input
        OutlinedTextField(
            value = locatorQuery,
            onValueChange = { locatorQuery = it },
            placeholder = { Text("Product Locator (e.g. Milk, Avocados)...", fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = DeepRed) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("product_locator_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        if (locatorQuery.isNotBlank()) {
            // Product Locator Search Results
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchedProducts) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Aisle: ${product.aisle}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = DeepRed)
                            }

                            Button(
                                onClick = { onAddToCart(product) },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepRed)
                            ) {
                                Text("ADD", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // In-Store Shopping Route List
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("OPTIMIZED SHOPPING ROUTE", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Follow this aisle path to complete your list efficiently", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shoppingList) { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { onToggleCheck(item) },
                                    colors = CheckboxDefaults.colors(checkedColor = FreshGreen)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(item.estimatedAisle, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DeepRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Barcode Scanner Dialog Simulator
    if (showBarcodeModal && scannedProduct != null) {
        AlertDialog(
            onDismissRequest = {
                showBarcodeModal = false
                onClearScan()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = FreshGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BARCODE SCANNED ✓", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(scannedProduct.brand, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(scannedProduct.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Price: $${String.format("%.2f", scannedProduct.price)} (${scannedProduct.unitPriceStr})", fontWeight = FontWeight.Black, color = DeepRed)
                    Text("Location: ${scannedProduct.aisle}", fontSize = 12.sp, color = FreshGreen, fontWeight = FontWeight.Bold)

                    if (scannedProduct.dealTag != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(color = DeepRed, shape = RoundedCornerShape(4.dp)) {
                            Text(scannedProduct.dealTag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddToCart(scannedProduct)
                        showBarcodeModal = false
                        onClearScan()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Text("ADD TO CART")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBarcodeModal = false
                    onClearScan()
                }) {
                    Text("CLOSE")
                }
            }
        )
    }
}
