package com.example.zabitadenetim.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
// YENİ EKLENDİ: İkonlar, coroutine ve diyalog yönetimi için gerekli kütüphaneler
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.launch
import com.example.zabitadenetim.data.ApiClient
// YENİ EKLENDİ BİTTİ

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.InspectionResponse

@Composable
fun InspectionScreen(viewModel: InspectionViewModel = viewModel(),
                     onNavigateToDetail: (Int) -> Unit = {}
) {

    // ARKA PLANDA SİLME İSTEĞİ ATMAK İÇİN (30.07.2026)
    val coroutineScope = rememberCoroutineScope()


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
                    // on delete fonksiyonu ekledik (30.07.2026)
                    InspectionCard(
                        inspection = inspection,
                        onDelete = { id ->
                            coroutineScope.launch {
                                try {
                                    val response = ApiClient.apiService.deleteInspection(id)
                                    if (response.isSuccessful) {
                                        viewModel.fetchInspections()  // silme gerçekleştiyse listeyi yenile

                                    }

                                } catch (e: Exception) {

                                }
                            }
                        },
                        onClick = { id ->
                            onNavigateToDetail(id)
                        }

                    )
                    }
                }
            }
        }
    }


// kartlar tasarımı
@Composable
fun InspectionCard(
    inspection: InspectionResponse,
    onDelete: (Int) -> Unit = {},
    onClick: (Int) -> Unit = {} // Tıklama parametresi eklendi
) {

    // Emin misiniz diyalogunu açıp kapatacak değişken
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Denetimi Sil") },
            text = { Text("Bu denetim kaydını silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDelete(inspection.id)
                }) {
                    Text("Evet, Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(inspection.id) }, // KART TIKLANABİLİR YAPILDI
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Metinleri ve silme ikonunu yan yana getirmek için Row eklendi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {

            // Sol taraftaki bilgiler
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(text = "İşletme ID: ${inspection.businessName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Durum: ${inspection.status}")

                // Eğer AI raporu varsa ekranda göster
                if (inspection.aiSummary != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "AI Analizi:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = inspection.aiSummary, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Sağ taraftaki çöp tenekesi ikonu (SİLME BUTONU)
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}