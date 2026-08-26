package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.presentation.TrafficInspectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficRecordsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrafficInspectionViewModel = viewModel()
) {

    //26.08.2026
    // Trafik kayıt listesini ve ekran durumlarını takip et
    val trafficInspections by viewModel.trafficInspections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // ekran ilk açıldığında trafik kayıtlarını backend üzerinden getir
    LaunchedEffect(Unit) {
        viewModel.fetchTrafficInspections()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Trafik Kayıtları")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onNavigateBack
                    ) {
                        Text("Geri")
                    }
                }
            )
        }
    ) { paddingValues ->

        when {

            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Bilinmeyen bir hata oluştu.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            trafficInspections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kayıtlı trafik işlemi bulunamadı.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(trafficInspections) { traffic ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = traffic.violationType,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "Kayıt No: ${traffic.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Plaka: ${traffic.plate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Araç Türü: ${traffic.vehicleType ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Adres: ${traffic.address ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Yapılan İşlem: ${traffic.actionTaken ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Durum: ${traffic.status ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Tarih: ${traffic.createdAt}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}