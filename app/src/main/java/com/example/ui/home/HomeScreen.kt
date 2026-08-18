package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Coupon
import com.example.data.model.Product
import com.example.data.model.Recipe
import com.example.ui.components.CouponCard
import com.example.ui.components.NavTab
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    allProducts: List<Product>,
    coupons: List<Coupon>,
    recipes: List<Recipe>,
    clippedCouponIds: Set<String>,
    cartQuantityMap: Map<String, Int>,
    onTabSelect: (NavTab) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (Product, Int) -> Unit,
    onCouponClipToggle: (Coupon) -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onPharmacyClick: () -> Unit,
    onFuelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Greeting Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                AlbertsonsDarkBlue
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    Text(
                        text = "GOOD MORNING, ALEX!",
                        color = ForUGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "WHAT ARE YOU SHOPPING FOR?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Action Buttons Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickActionButton("SHOP NOW", Icons.Default.ShoppingCart, ForUGold) { onTabSelect(NavTab.SHOP) }
                        QuickActionButton("MY LIST", Icons.Default.FormatListNumbered, Color.White) { onTabSelect(NavTab.LIST) }
                        QuickActionButton("DEALS", Icons.Default.LocalOffer, Color.White) { onTabSelect(NavTab.DEALS) }
                        QuickActionButton("FOR U®", Icons.Default.Star, ForUGold) { onTabSelect(NavTab.DEALS) }
                        QuickActionButton("RECIPES", Icons.Default.RestaurantMenu, Color.White) { onTabSelect(NavTab.SHOP) }
                    }
                }
            }
        }

        // Savings Engine Callout Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Savings",
                        tint = FreshGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "YOU COULD SAVE UP TO $18.72 TODAY",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = DarkGreen
                        )
                        Text(
                            text = "6 Coupons & 2 Member Offers ready to clip for your basket.",
                            fontSize = 11.sp,
                            color = FreshGreen
                        )
                    }
                    Button(
                        onClick = { onTabSelect(NavTab.DEALS) },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("CLIP ALL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // BUY AGAIN (Frequents)
        item {
            SectionHeader(title = "BUY AGAIN", subtitle = "Your weekly staple groceries")
            val buyAgainProducts = allProducts.take(6)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(buyAgainProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        cartQuantity = cartQuantityMap[product.id] ?: 0,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onQuantityChange = onQuantityChange,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }

        // PERSONALISED DEALS
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "PERSONALISED DEALS FOR U", subtitle = "Selected based on your favorites")
            val dealProducts = allProducts.filter { it.dealTag != null }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dealProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        cartQuantity = cartQuantityMap[product.id] ?: 0,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onQuantityChange = onQuantityChange,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }

        // DIGITAL COUPONS HIGHLIGHT
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "TOP DIGITAL COUPONS", subtitle = "Clip to automatically apply at checkout")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                coupons.take(3).forEach { coupon ->
                    CouponCard(
                        coupon = coupon,
                        isClipped = coupon.id in clippedCouponIds,
                        onClipToggle = onCouponClipToggle
                    )
                }
            }
        }

        // POPULAR NEAR YOU
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "POPULAR NEAR YOU", subtitle = "Top trending items at Market Street store")
            val popularProducts = allProducts.sortedByDescending { it.rating }.take(6)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        cartQuantity = cartQuantityMap[product.id] ?: 0,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onQuantityChange = onQuantityChange,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }

        // RECIPE MEAL INSPIRATION
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "MEAL PLANNING & RECIPES", subtitle = "Select recipe to auto-add missing ingredients to cart")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe) }
                    )
                }
            }
        }

        // PHARMACY & FUEL SUPER-APP MODULE CARDS
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pharmacy Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPharmacyClick() }
                        .testTag("home_pharmacy_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.MedicalServices, contentDescription = "Pharmacy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ALBERTSONS RX", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("1 Refill Ready • Rx Transfer", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Manage Rx →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Fuel Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFuelClick() }
                        .testTag("home_fuel_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = ForUGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("EXPRESS FUEL", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("20¢/gal for U® discount", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Find Station →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = accentColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Restaurant, contentDescription = recipe.title, tint = DeepRed, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${recipe.prepTimeMinutes} mins • ${recipe.caloriesPerServing} cal", fontSize = 10.sp, color = Color.Gray)
                Text("~$${recipe.estimatedCostForFour}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = FreshGreen)
            }
        }
    }
}
