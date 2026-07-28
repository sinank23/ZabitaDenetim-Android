package com.example.zabitadenetim.ui.screens.login.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.InspectionResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit, onNavigateToNewInspection: () ->  Unit) {

    // veri tutucu (state) ekliyoruz
    // sunucudan gelecek liste
    var inspections by remember { mutableStateOf<List<InspectionResponse>>(emptyList()) }

    // veri çekilirken ekranda yükleniyor ikonu
    var isLoading by remember { mutableStateOf(true) }

    // eğer hata olursa ekranda görmek için
    var errorMessage by remember { mutableStateOf("") }

    // ekran açıldığında çalışacak modül
    LaunchedEffect(Unit) {
        try {
            // retrofit ile get isteği attık
            inspections = ApiClient.apiService.getInspections()
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Veriler alınamadı: ${e.localizedMessage}"
            isLoading = false
        }
    }

    Scaffold(
        // üst menü (topbar kısm) oluşturuluyor
        topBar = {
            TopAppBar(
                title = { Text("Zabıta Denetim Sistemi")},
                actions = {
                    // sağ üst köşeeye eklenecek çıkış butonu
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                },
                // üst menü ve arka plan ve yazı renkleri
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // yeni denetim sayfasına yönlendirecek olan butonu oluşturalım
            Button(
                onClick = { onNavigateToNewInspection()  },
                modifier = Modifier.fillMaxWidth() // Butonu tam genişlik yaptık ki yukarıda sabit ve şık dursun
            ) {
                Text("Yeni Denetim Başlat")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // verilerin ekranda göstermke için
            if (isLoading) {
                // yuvarlak yükleme animasyonu
                CircularProgressIndicator()

            } else if (errorMessage.isNotEmpty()) {
                // HATA VARSA
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)

            } else if (inspections.isEmpty()) {
                // liste boşsa eski yazı ortaya çıksın
                Text(
                    text = "Kayıtlı bir denetim yok",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // veri varsa lazycolumn yapısı ile alt alta dizdir.
                LazyColumn (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inspections) { inspection ->

                        // Tamamen güvenli ve güncel kart tasarımı
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Üst Kısım: İşletme Adı ve Tarih yan yana
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = inspection.businessName,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Tarih boş gelirse çökmeyi engelleyen güvenli yapı
                                    Text(
                                        text = if (!inspection.inspectionDate.isNullOrEmpty()) inspection.inspectionDate.take(10) else "Tarih Yok",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Durum boş gelirse çökmeyi engelleyen güvenli yapı
                                Text(
                                    text = "Durum: ${if (!inspection.status.isNullOrEmpty()) inspection.status else "Bekliyor"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}