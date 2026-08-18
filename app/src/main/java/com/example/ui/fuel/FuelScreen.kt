package com.example.ui.fuel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelStation
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ForUGold

@Composable
fun FuelScreen(
    fuelStations: List<FuelStation>,
    rewardPoints: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val centsDiscount = (rewardPoints / 100) * 10 // 10 cents per 100 points

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("fuel_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("ALBERTSONS EXPRESS FUEL & REWARDS", fontWeight = FontWeight.Black, fontSize = 17.sp)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Points to Fuel Discount Converter Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocalGasStation, contentDescription = "Fuel", tint = ForUGold, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("YOUR for U® FUEL DISCOUNT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Text("Based on $rewardPoints points", color = Color.LightGray, fontSize = 11.sp)
                                }
                            }

                            Text("${centsDiscount}¢/GAL OFF", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ForUGold)
                        }
                    }
                }
            }

            // Fuel Stations List
            item {
                Text("NEAREST EXPRESS FUEL STATIONS", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            items(fuelStations, key = { it.id }) { station ->
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
                            Text(station.name, fontWeight = FontWeight.Black, fontSize = 15.sp)
                            Text("${station.distanceMiles} mi", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(station.address, fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Regular: $${station.priceRegular}/gal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("With Member Discount: $${String.format("%.2f", station.priceRegular - (centsDiscount / 100.0))}/gal", fontWeight = FontWeight.Black, color = ForUGold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { /* Pay at pump simulation */ },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("PAY AT PUMP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
