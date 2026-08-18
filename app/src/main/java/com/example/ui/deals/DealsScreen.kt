package com.example.ui.deals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.Coupon
import com.example.data.model.Product
import com.example.data.model.RewardOffer
import com.example.ui.components.CouponCard
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun DealsScreen(
    coupons: List<Coupon>,
    rewardOffers: List<RewardOffer>,
    dealProducts: List<Product>,
    rewardPoints: Int,
    clippedCouponIds: Set<String>,
    cartQuantityMap: Map<String, Int>,
    onCouponClipToggle: (Coupon) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (Product, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val categories = remember(coupons) {
        listOf("ALL") + coupons.map { it.category }.distinct()
    }

    val filteredCoupons = remember(coupons, selectedCategoryFilter) {
        if (selectedCategoryFilter == null || selectedCategoryFilter == "ALL") {
            coupons
        } else {
            coupons.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("deals_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Points Balance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Points", tint = ForUGold, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("for U® REWARDS (ALBERTSONS)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                Text("Member ID: 8829-4019-33", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }

                        Surface(
                            color = ForUGold,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$rewardPoints PTS",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Points Progress bar to next reward ($5 off at 500pts)
                    val pointsProgress = (rewardPoints % 500) / 500f
                    LinearProgressIndicator(
                        progress = { pointsProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ForUGold,
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${500 - (rewardPoints % 500)} points until your next $5.00 Grocery Reward!",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Available Reward Redemptions Carousel
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("REDEEM YOUR REWARDS", fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("Convert points into fuel discounts or free groceries", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rewardOffers, key = { it.id }) { offer ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Surface(
                                color = GoldRewards.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${offer.pointsRequired} POINTS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(offer.description, fontSize = 10.sp, color = Color.Gray, maxLines = 2)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { /* Redeem simulation */ },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldRewards),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REDEEM NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Digital Coupons Header & Category Chips
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("DIGITAL COUPONS (${coupons.size})", fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("Clip to save automatically at checkout or in-store", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCategoryFilter ?: "ALL") == cat,
                        onClick = { selectedCategoryFilter = if (cat == "ALL") null else cat },
                        label = { Text(cat, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DeepRed, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        // Coupons List
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredCoupons.forEach { coupon ->
                    CouponCard(
                        coupon = coupon,
                        isClipped = coupon.id in clippedCouponIds,
                        onClipToggle = onCouponClipToggle
                    )
                }
            }
        }

        // On Sale Products Carousel
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("FEATURED WEEKLY SPECIALS", fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("Lowest member prices this week", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

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
    }
}
