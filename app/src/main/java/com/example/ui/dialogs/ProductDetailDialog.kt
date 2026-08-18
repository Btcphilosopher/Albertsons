package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen

@Composable
fun ProductDetailDialog(
    product: Product,
    cartQuantity: Int,
    onAddToCart: (Product) -> Unit,
    onDismiss: () -> Unit
) {
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
                    Text(product.brand, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(product.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(product.size, fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = product.name, tint = Color.Gray, modifier = Modifier.size(56.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val effPrice = product.salePrice ?: product.memberPrice ?: product.price
                        Text("$${String.format("%.2f", effPrice)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DeepRed)
                        Text(product.unitPriceStr, fontSize = 11.sp, color = Color.Gray)
                    }

                    Text("Aisle: ${product.aisle}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FreshGreen)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("PRODUCT DETAILS & INGREDIENTS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.description, fontSize = 12.sp)

                if (product.allergens.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Allergens: ${product.allergens.joinToString(", ")}", fontSize = 11.sp, color = DeepRed, fontWeight = FontWeight.Bold)
                }

                if (product.nutritionFacts != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("NUTRITION FACTS (Per Serving)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            val n = product.nutritionFacts
                            Text("${n.calories} Cal | Protein: ${n.protein} | Fat: ${n.fat} | Carbs: ${n.carbs} | Sodium: ${n.sodium}", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddToCart(product)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD TO CART ($${String.format("%.2f", product.salePrice ?: product.price)})")
            }
        }
    )
}
