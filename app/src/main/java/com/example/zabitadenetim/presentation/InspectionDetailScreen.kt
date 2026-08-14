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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    inspectionId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToPdf: (Int) -> Unit,
    onNavigateToBusinessHistory: (Int, Int) -> Unit,
    viewModel: InspectionViewModel = viewModel()
) {
    val context = LocalContext.current

    // tıklanan denetimin idsini listede arayıp bulduk
    val inspection = viewModel.inspections.value.find { it.id == inspectionId }

    val formattedInspectionDate = inspection?.inspectionDate?.let { date ->
        try {
            val datePart = date.substringBefore("T")
            val timePart = date.substringAfter("T").take(5)

            val dateParts = datePart.split("-")

            if (dateParts.size == 3) {
                "${dateParts[2]}.${dateParts[1]}.${dateParts[0]} $timePart"
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }

    //12.08.2026 eklendi
    // yapay zeka analizini ayrı kartlara bölme işlemi için

    val aiReportSections = inspection?.aiSummary?.let { report ->

        val sectionTitles = listOf(
            "GENEL_DEGERLENDIRME",
            "DENETIM_KRITERLERI",
            "FOTOGRAF_BULGULARI",
            "GOOGLE_YORUMLARI",
            "ZABITA_NOTU_KARSILASTIRMASI",
            "TUTARLILIK_VE_RISKLER",
            "ONERILER"
        )

        sectionTitles.associateWith { currentTitle ->
            val startTag = "[$currentTitle]"
            val startIndex = report.indexOf(startTag)

            if (startIndex == -1) {
                ""
            } else {
                val contentStart = startIndex + startTag.length

                val nextSectionIndex = sectionTitles
                    .map { "[$it]" }
                    .map { report.indexOf(it, contentStart)}
                    .filter { it != -1 }
                    .minOrNull()

                if (nextSectionIndex != null) {
                    report.substring(contentStart, nextSectionIndex).trim()
                } else {
                    report.substring(contentStart).trim()
                }
            }
        }
    }

    // 03.08.2026 eklendi
    val reviews by viewModel.reviews.collectAsState()
    val isLoadingReviews by viewModel.isLoadingReviews.collectAsState()
    val inspectionAnswers by viewModel.inspectionAnswers.collectAsState()

    // 05.08.2026
    // yapay zeka raporu yeniden oluşturulurken yüklenme durumunu takip et
    val isRetryingAi by viewModel.isRetryingAi.collectAsState()

    // yeniden deneme sonucunda oluşsan mesaj
    val retryAiMessage by viewModel.retryAiMessage.collectAsState()

    //12.08.2026 eklendi
    // zabıta memurunun notunu detay sayfasında görmek için state

    var showInspectorNoteDialog by remember { mutableStateOf(false) }



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
                            text = "Denetim No: ${inspection.id}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Denetim Tarihi: ${formattedInspectionDate ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Adres: ${inspection.address ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // denetimin konusu
                        Text(
                            text = "Faaliyet Konusu: ${inspection.categoryName ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Durum: ${inspection.status ?: "Bekliyor"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "İşletme Sahibi: ${inspection.ownerName ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "İletişim: ${inspection.contactInfo ?: "Belirtilmemiş"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (inspection.latitude != null && inspection.longitude != null) {
                            OutlinedButton(
                                onClick = {
                                    val latitude = inspection.latitude
                                    val longitude = inspection.longitude

                                    val mapUri = Uri.parse(
                                        "geo:$latitude,$longitude?q=$latitude,$longitude"
                                    )

                                    val mapIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        mapUri
                                    )

                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val browseUri = Uri.parse(
                                            "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                                        )

                                        val browserIntent = Intent(
                                            Intent.ACTION_VIEW,
                                            browseUri
                                        )

                                        context.startActivity(browserIntent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Konumu Haritada Aç")
                            }
                        }

                        //14.08.2026 eklendi
                        // denetime ait PDF raporunu açma modülü ekledik.
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                onNavigateToPdf(inspection.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PDF Raporunu Aç")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (inspection.businessId != null) {
                            OutlinedButton(
                                onClick = {
                                    onNavigateToBusinessHistory(
                                        inspection.businessId,
                                        inspection.id
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Bu İşletmenin Geçmiş Denetimleri")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showInspectorNoteDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zabıta Personel Notu")
                }

                Spacer(modifier = Modifier.height(24.dp))

                //11.08.2026
                // denetime ait spru ve cevapları göster
                Text(
                    text = "Denetim Soruları ve Cevapları",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (inspectionAnswers.isEmpty()) {
                    Text(
                        text = "Bu denetime ait soru-cevap kaydı bulunamadı.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    inspectionAnswers.forEach{ answer ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        )  {
                            Column (
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = answer.questionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (answer.isYes) "Cevap: Evet" else "Cevap: Hayır",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
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

                if (inspection.aiSummary.isNullOrBlank()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bu denetim için henüz yapay zeka raporu oluşturulmamış.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                } else {

                    val reportSections = listOf(
                        "GENEL_DEGERLENDIRME" to "Genel Değerlendirme",
                        "DENETIM_KRITERLERI" to "Denetim Kriterleri",
                        "FOTOGRAF_BULGULARI" to "Fotoğraf Bulguları",
                        "GOOGLE_YORUMLARI" to "Google Yorumları",
                        "ZABITA_NOTU_KARSILASTIRMASI" to "Zabıta Notu Karşılaştırması",
                        "TUTARLILIK_VE_RISKLER" to "Tutarlılık ve Riskler",
                        "ONERILER" to "Öneriler"
                    )

                    reportSections.forEach { (key, title) ->

                        val content = aiReportSections?.get(key)

                        if (!content.isNullOrBlank()) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = content,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
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

                if (showInspectorNoteDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showInspectorNoteDialog = false
                        },
                        title = {
                            Text("Zabıta Personel Notu")
                        },
                        text = {
                            Text(
                                text = inspection.notes
                                    ?.takeIf { it.isNotBlank() }
                                    ?: "Bu denetim için personel notu girilmemiş."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showInspectorNoteDialog = false
                                }
                            ) {
                                Text("Kapat")
                            }
                        }
                    )
                }
            }
        }
    }
}