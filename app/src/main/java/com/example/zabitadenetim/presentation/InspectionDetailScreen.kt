package com.example.zabitadenetim.ui.screens.login.inspection // Kendi paket yoluna göre kontrol etmeyi unutma

import androidx.compose.foundation.layout.*
// 14.52 YENİ EKLENDİ: Sayfa kaydırma için gerekli importlar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// 14.52 YENİ EKLENDİ: Font ve ViewModel importları
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.presentation.InspectionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    inspectionId: Int,
    onNavigateBack: () -> Unit,
    viewModel: InspectionViewModel = viewModel()

) {
    // tıklanan denetimin idsini listede arayıp bulduk
    val inspection = viewModel.inspections.value.find { it.id == inspectionId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Denetim Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        // 14.52 GÜNCELLENDİ: Ortadaki o geçici iki Text silindi, yerine veri kontrolü (if-else) yapısı kuruldu
        if (inspection == null) {
            // Eğer veri henüz yüklenmediyse veya bulunamadıysa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Denetim bilgisi bulunamadı veya yükleniyor...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // 14.52 YENİ EKLENDİ: Sayfa aşağı kaydırılabilir yapıldı
                    .padding(16.dp)
            ) {

                // 14.52 YENİ EKLENDİ: 1. KART - İŞLETME BİLGİLERİ
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = inspection.businessName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adres: ${inspection.notes ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Durum: ${inspection.status}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 14.52 YENİ EKLENDİ: 2. KART - YAPAY ZEKA BAŞMÜFETTİŞ RAPORU
                Text(
                    text = "Yapay Zeka Çapraz Analiz Raporu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = inspection.aiSummary ?: "Bu denetim için henüz yapay zeka raporu oluşturulmamış veya analiz ediliyor.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}