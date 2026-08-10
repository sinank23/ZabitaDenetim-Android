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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    inspectionId: Int,
    onNavigateBack: () -> Unit,
    viewModel: InspectionViewModel = viewModel()

) {
    // tıklanan denetimin idsini listede arayıp bulduk
    val inspection = viewModel.inspections.value.find { it.id == inspectionId }

    // 03.08.2026 eklendi
    val reviews by viewModel.reviews.collectAsState()
    val isLoadingReviews by viewModel.isLoadingReviews.collectAsState()
    val inspectionAnswers by viewModel.inspectionAnswers.collectAsState()

    // 05.08.2026
    // yapay zeka raporu yeniden oluşturulurken yüklenme durumunu takip et
    val isRetryingAi by viewModel.isRetryingAi.collectAsState()

    // yeniden deneme sonucunda oluşsan mesaj
    val retryAiMessage by viewModel.retryAiMessage.collectAsState()

    LaunchedEffect(inspectionId) {
        viewModel.fetchInspections()
    }

    LaunchedEffect(inspectionId) {
        viewModel.fetchInspectionAnswers(inspectionId)
    }

    // sayfa açıldığında o işletmenin yorumlarını getir
    LaunchedEffect(key1 = inspection?.businessId) {
        inspection?.businessId?.let { businessId ->
            viewModel.fetchBusinessReviews(businessId)
        }
    }

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
                            text = inspection.businessName ?: "İşletme adı belirtilmemiş",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adres: ${inspection.address ?: "Belirtilmemiş"}",                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Durum: ${inspection.status ?: "Bekliyor"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 14.52 YENİ EKLENDİ: 2. KART - YAPAY ZEKA BAŞMÜFETTİŞ RAPORU


                // 03.08.2026 YENİ EKLENDİ: 3. KART - GOOGLE YORUMLARI
                Text(
                    text =  "Google Müşteri Yorumları",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoadingReviews) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (reviews.isEmpty()) {
                    Text(
                        text = "Bu işletme için yorum bulunmuyor.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // yorumları döngüye sok ve alt alta yaz
                    reviews.forEach { review ->
                        Card (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

                        ) {
                            Column (modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${review.authorName} - Puan: ${review.rating}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.text ?: "Kullanıcı metin girmeden sadece puan bırakmış.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


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
                            text = inspection.aiSummary
                                ?: "Bu denetim için henüz yapay zeka raporu oluşturulmamış.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                // 05.08.2026
                // yeniden deneme butonu
                if(
                    inspection.aiSummary.isNullOrBlank() ||
                    inspection.status == "AI Analizi bekleniyor"
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.retryAiReport(inspection.id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isRetryingAi
                    ) {
                        if(isRetryingAi) {
                            CircularProgressIndicator(
                                modifier =  Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yapay zeka yeniden deniyor...")
                        } else {
                            Text("Yapay Zeka Raporunu Yeniden Dene")
                        }
                    }
                }
                

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}