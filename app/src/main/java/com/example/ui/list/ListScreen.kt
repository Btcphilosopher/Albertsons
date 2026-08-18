package com.example.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PantryItemEntity
import com.example.data.local.ShoppingListItemEntity
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LightGreen

@Composable
fun ListScreen(
    shoppingList: List<ShoppingListItemEntity>,
    pantryItems: List<PantryItemEntity>,
    onAddListItem: (String, String, String) -> Unit,
    onToggleCheck: (ShoppingListItemEntity) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearChecked: () -> Unit,
    onMoveCheckedToCart: () -> Unit,
    onUpdatePantryItem: (PantryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Store Route List, 1 = Pantry Tracker
    var newItemText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Produce") }

    val categoryAisleMap = mapOf(
        "Produce" to "1 PRODUCE",
        "Bakery" to "2 BAKERY",
        "Deli" to "3 DELI",
        "Meat" to "4 MEAT & SEAFOOD",
        "Dairy" to "5 DAIRY & EGGS",
        "Pantry" to "6 PANTRY",
        "Frozen" to "7 FROZEN",
        "Household" to "8 HOUSEHOLD & CLEANING"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("list_screen")
    ) {
        // Tab Selector Row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("INTELLIGENT LIST (${shoppingList.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("HOUSEHOLD PANTRY", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        if (selectedTab == 0) {
            // Add Item Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = newItemText,
                            onValueChange = { newItemText = it },
                            placeholder = { Text("Add item (e.g. Avocado, Whole Milk)...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("add_list_item_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newItemText.isNotBlank()) {
                                    onAddListItem(
                                        newItemText.trim(),
                                        selectedCategory,
                                        categoryAisleMap[selectedCategory] ?: "1 PRODUCE"
                                    )
                                    newItemText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                            modifier = Modifier.testTag("add_list_item_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Department category picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Category:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        categoryAisleMap.keys.take(4).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }

            // Route Banner Callout
            Surface(
                color = LightGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Navigation, contentDescription = "Route", tint = FreshGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STORE ROUTE OPTIMIZED (Aisle 1 -> 8)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreshGreen
                        )
                    }

                    if (shoppingList.any { it.isChecked }) {
                        Text(
                            text = "Clear Checked",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepRed,
                            modifier = Modifier.clickable { onClearChecked() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Intelligent List Grouped by Aisle
            val groupedByAisle = shoppingList.groupBy { it.estimatedAisle }.toSortedMap()

            if (shoppingList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "List", tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your Shopping List is Empty", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("Add items above or click 'Add to List' on products.", fontSize = 12.sp, color = Color.LightGray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedByAisle.forEach { (aisle, itemsInAisle) ->
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text(
                                    text = aisle.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepRed,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        items(itemsInAisle, key = { it.id }) { listItem ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (listItem.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = listItem.isChecked,
                                        onCheckedChange = { onToggleCheck(listItem) },
                                        colors = CheckboxDefaults.colors(checkedColor = FreshGreen)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = listItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            textDecoration = if (listItem.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (listItem.isChecked) Color.Gray else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Qty: ${listItem.quantity} • Category: ${listItem.category}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    IconButton(onClick = { onRemoveItem(listItem.id) }) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Remove", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Pantry Tracker View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "HOUSEHOLD PANTRY INVENTORY",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = DeepRed
                )
                Text(
                    text = "Track what you have at home so you never overbuy or run out.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pantryItems, key = { it.id }) { pantry ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(pantry.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Category: ${pantry.category}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    PantryStatusChip("HAVE", pantry.status == "HAVE", FreshGreen) {
                                        onUpdatePantryItem(pantry.copy(status = "HAVE"))
                                    }
                                    PantryStatusChip("LOW", pantry.status == "RUNNING_LOW", Color(0xFFF59E0B)) {
                                        onUpdatePantryItem(pantry.copy(status = "RUNNING_LOW"))
                                    }
                                    PantryStatusChip("NEED", pantry.status == "NEED", DeepRed) {
                                        onUpdatePantryItem(pantry.copy(status = "NEED"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantryStatusChip(label: String, isSelected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
