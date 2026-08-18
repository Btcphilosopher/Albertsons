package com.example.ui.pharmacy

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
import com.example.data.model.Prescription
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DeepRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LightGreen

@Composable
fun PharmacyScreen(
    prescriptions: List<Prescription>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var refilledPrescriptionIds by remember { mutableStateOf(setOf<String>()) }
    var showVaccineModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("pharmacy_screen")
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
            Text("ALBERTSONS PHARMACY HUB", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Schedule Vaccine Callout Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Vaccines, contentDescription = "Vaccine", tint = FreshGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ANNUAL FLU & COVID VACCINES AVAILABLE", fontWeight = FontWeight.Black, fontSize = 13.sp, color = DarkGreen)
                            Text("Walk-ins welcome or schedule an appointment online.", fontSize = 11.sp, color = FreshGreen)
                        }
                        Button(
                            onClick = { showVaccineModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                        ) {
                            Text("BOOK", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Prescription List
            item {
                Text("YOUR ACTIVE PRESCRIPTIONS", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            items(prescriptions, key = { it.rxNumber }) { rx ->
                val isRefilled = rx.rxNumber in refilledPrescriptionIds
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
                            Text(rx.medicationName, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Rx #${rx.rxNumber}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${rx.dosage} • Prescribed by ${rx.prescriber}", fontSize = 12.sp, color = Color.DarkGray)
                        Text("Refills remaining: ${rx.refillsRemaining}", fontSize = 11.sp, color = DeepRed, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isRefilled) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REFILL REQUESTED ✓", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { refilledPrescriptionIds = refilledPrescriptionIds + rx.rxNumber },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REQUEST QUICK REFILL", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showVaccineModal) {
        AlertDialog(
            onDismissRequest = { showVaccineModal = false },
            title = { Text("Vaccine Scheduled ✓", fontWeight = FontWeight.Bold) },
            text = { Text("Your Flu Shot appointment has been scheduled at FreshMarket Pharmacy for tomorrow at 10:30 AM.") },
            confirmButton = {
                Button(
                    onClick = { showVaccineModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                ) {
                    Text("DONE")
                }
            }
        )
    }
}
