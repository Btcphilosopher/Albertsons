package com.example.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    departments: List<String>,
    allProducts: List<Product>,
    selectedDepartment: String?,
    searchQuery: String,
    cartQuantityMap: Map<String, Int>,
    onSelectDepartment: (String?) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (Product, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var organicOnlyFilter by remember { mutableStateOf(false) }
    var glutenFreeFilter by remember { mutableStateOf(false) }
    var dealsOnlyFilter by remember { mutableStateOf(false) }

    // Filter Logic
    val filteredProducts = remember(allProducts, selectedDepartment, searchQuery, organicOnlyFilter, glutenFreeFilter, dealsOnlyFilter) {
        allProducts.filter { product ->
            val matchesDept = selectedDepartment == null || product.department.equals(selectedDepartment, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.department.contains(searchQuery, ignoreCase = true)
            val matchesOrganic = !organicOnlyFilter || product.isOrganic
            val matchesGluten = !glutenFreeFilter || product.isGlutenFree
            val matchesDeals = !dealsOnlyFilter || product.dealTag != null

            matchesDept && matchesSearch && matchesOrganic && matchesGluten && matchesDeals
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("shop_screen")
    ) {
        // Department Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedDepartment == null,
                    onClick = { onSelectDepartment(null) },
                    label = { Text("ALL DEPARTMENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepRed, selectedLabelColor = Color.White)
                )
            }
            items(departments) { dept ->
                FilterChip(
                    selected = selectedDepartment == dept,
                    onClick = { onSelectDepartment(if (selectedDepartment == dept) null else dept) },
                    label = { Text(dept.uppercase(), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepRed, selectedLabelColor = Color.White)
                )
            }
        }

        // Secondary Filters Row (Organic, Gluten-Free, Deals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters", tint = Color.Gray, modifier = Modifier.size(18.dp))

            FilterChip(
                selected = dealsOnlyFilter,
                onClick = { dealsOnlyFilter = !dealsOnlyFilter },
                label = { Text("ON SALE / DEALS", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepRed, selectedLabelColor = Color.White)
            )

            FilterChip(
                selected = organicOnlyFilter,
                onClick = { organicOnlyFilter = !organicOnlyFilter },
                label = { Text("ORGANIC", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FreshGreen, selectedLabelColor = Color.White)
            )

            FilterChip(
                selected = glutenFreeFilter,
                onClick = { glutenFreeFilter = !glutenFreeFilter },
                label = { Text("GLUTEN-FREE", fontSize = 11.sp) }
            )
        }

        // Results count banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} PRODUCTS AVAILABLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (selectedDepartment != null) {
                Text(
                    text = "Clear Dept",
                    fontSize = 11.sp,
                    color = DeepRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSelectDepartment(null) }
                )
            }
        }

        // Product Grid
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No products found matching your search.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSelectDepartment(null)
                            organicOnlyFilter = false
                            glutenFreeFilter = false
                            dealsOnlyFilter = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepRed)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        cartQuantity = cartQuantityMap[product.id] ?: 0,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onQuantityChange = onQuantityChange
                    )
                }
            }
        }
    }
}
