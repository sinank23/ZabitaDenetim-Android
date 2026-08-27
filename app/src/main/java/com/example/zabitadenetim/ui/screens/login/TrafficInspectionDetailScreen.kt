package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TrafficInspectionDetailScreen(
    trafficInspectionId: Int,
    onNavigateBack: () -> Unit,
    viewModel: TrafficInspectionViewModel = viewModel()
) {

    //27.08.2026
    // trafik kayıt listesini ve ekran durumlarını takip et
    val trafficInspections by viewModel.trafficInspections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // detay ekranı açıldığında trafik kayıtlarını backend üzerinden getir
    LaunchedEffect(trafficInspectionId) {
        viewModel.fetchTrafficInspections()
    }

    // route üzerinden gelen ID'ye ait trafik kaydını bul
    val trafficInspection =
        trafficInspections.firstOrNull {
            it.id == trafficInspectionId
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Trafik Kayıt Detayı")
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

            trafficInspection == null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Trafik kaydı bulunamadı.")
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = trafficInspection.violationType,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Kayıt No: ${trafficInspection.id}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Plaka: ${trafficInspection.plate}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Araç Türü: ${trafficInspection.vehicleType ?: "Belirtilmemiş"}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Adres:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = trafficInspection.address ?: "Belirtilmemiş",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Açıklama:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = trafficInspection.description ?: "Belirtilmemiş",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Yapılan İşlem:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = trafficInspection.actionTaken ?: "Belirtilmemiş",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Durum: ${trafficInspection.status ?: "Belirtilmemiş"}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Tarih: ${trafficInspection.createdAt}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    HorizontalDivider()

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Konum Bilgisi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Enlem: ${trafficInspection.latitude ?: "Belirtilmemiş"}"
                    )

                    Text(
                        text = "Boylam: ${trafficInspection.longitude ?: "Belirtilmemiş"}"
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}