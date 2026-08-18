package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FulfillmentMethod
import com.example.data.model.Store
import com.example.ui.theme.AlbertsonsBlue
import com.example.ui.theme.ForUGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
    currentStore: Store,
    fulfillmentMethod: FulfillmentMethod,
    rewardPoints: Int,
    cartCount: Int,
    searchQuery: String,
    isInStoreMode: Boolean,
    onStoreClick: () -> Unit,
    onFulfillmentChange: (FulfillmentMethod) -> Unit,
    onSearchChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onInStoreModeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFulfillmentMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(bottom = 8.dp)
    ) {
        // Top Location & Services Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Store location chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable { onStoreClick() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("store_selector_chip")
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = currentStore.name.removePrefix("Albertsons - "),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Fulfillment Dropdown Selector
            Box {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    onClick = { showFulfillmentMenu = true },
                    modifier = Modifier.testTag("fulfillment_selector")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = when (fulfillmentMethod) {
                                FulfillmentMethod.DRIVEUP_AND_GO -> Icons.Outlined.DirectionsCar
                                FulfillmentMethod.DELIVERY -> Icons.Outlined.LocalShipping
                                FulfillmentMethod.FLASH_DELIVERY -> Icons.Outlined.ElectricBolt
                                FulfillmentMethod.IN_STORE -> Icons.Outlined.Storefront
                            },
                            contentDescription = fulfillmentMethod.displayName,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = fulfillmentMethod.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = showFulfillmentMenu,
                    onDismissRequest = { showFulfillmentMenu = false }
                ) {
                    FulfillmentMethod.values().forEach { method ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(method.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${method.eta} • Fee: ${method.fee}", fontSize = 11.sp, color = Color.Gray)
                                }
                            },
                            onClick = {
                                onFulfillmentChange(method)
                                showFulfillmentMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Main Header Title, Points & Cart Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Albertsons",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                // In-Store Mode Chip
                Surface(
                    color = if (isInStoreMode) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { onInStoreModeToggle(!isInStoreMode) },
                    modifier = Modifier.testTag("in_store_mode_toggle")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "In Store",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isInStoreMode) "IN-STORE ON" else "IN-STORE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // for U Rewards Points Widget
                Surface(
                    color = ForUGold,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "for U Points",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "$rewardPoints pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }

                // AI Assistant Trigger Button
                IconButton(
                    onClick = onAiAssistantClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("ai_assistant_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Cart Icon with Badge
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                Text("$cartCount", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("cart_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search 2,000+ items, for U® deals, or 'taco ingredients'...", fontSize = 12.sp, color = Color.Gray) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                } else {
                    IconButton(onClick = onAiAssistantClick) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice/AI Search", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .testTag("global_search_input")
        )
    }
}

