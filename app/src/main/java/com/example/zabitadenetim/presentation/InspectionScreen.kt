package com.example.zabitadenetim.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.InspectionResponse

@Composable
fun InspectionScreen(viewModel: InspectionViewModel = viewModel()) {
        // ekran ilk açıldığında ekrana denetimler gelsim
    LaunchedEffect(key1 = true) {
        viewModel.fetchInspections()
    }


    val inspections = viewModel.inspections.value  // apiden gelen denetim listesi
    val isLoading = viewModel.isLoading.value      // yükleniyor mu
    val error = viewModel.errorMessage.value       // hata mesajı

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Zabıta Denetim Kayıtları", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))  // boşluk bıraktı

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        } else {
            // Kaydırılabilir  Liste Oluşturma
            LazyColumn {      // her kayıt için bir kart oluşuyor ve inspection carda gönderiliyor
                items(inspections) { inspection ->
                    InspectionCard(inspection)
                }
            }
        }
    }
}

// kartlar tasarımı
@Composable
fun InspectionCard(inspection: InspectionResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "İşletme ID: ${inspection.businessName}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Durum: ${inspection.status}")

            // Eğer AI raporu varsa ekranda göster
            if (inspection.aiSummary != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "AI Analizi:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(text = inspection.aiSummary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}