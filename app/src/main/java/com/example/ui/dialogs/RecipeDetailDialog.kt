package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Recipe
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen

@Composable
fun RecipeDetailDialog(
    recipe: Recipe,
    onAddIngredientsToCart: (Recipe) -> Unit,
    onDismiss: () -> Unit
) {
    var servingsMultiplier by remember { mutableStateOf(1.0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RECIPE MEAL KIT", fontSize = 11.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(recipe.title, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("${recipe.prepTimeMinutes} mins • ${recipe.caloriesPerServing} Cal/serving", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Text(recipe.description, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("INGREDIENTS LIST (${recipe.ingredients.size})", fontWeight = FontWeight.Black, fontSize = 12.sp, color = DeepRed)
                    Text("Est: ~$${String.format("%.2f", recipe.estimatedCostForFour)}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = FreshGreen)
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    items(recipe.ingredients) { ing ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${ing.productName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(ing.quantityStr, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddIngredientsToCart(recipe)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add Kit")
                Spacer(modifier = Modifier.width(6.dp))
                Text("ADD ALL INGREDIENTS TO CART")
            }
        }
    )
}
