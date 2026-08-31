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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

import coil.compose.AsyncImage

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficInspectionDetailScreen(
    trafficInspectionId: Int,
    onNavigateBack: () -> Unit,
    isAdmin: Boolean = false,
    viewModel: TrafficInspectionViewModel = viewModel()
) {

    //27.08.2026
    // trafik kayıt listesini ve ekran durumlarını takip et
    val trafficInspections by viewModel.trafficInspections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    //31.08.2026
// seçilen trafik işlemine ait fotoğrafları takip etmek için
    val trafficPhotos by viewModel.trafficPhotos.collectAsState()

    //31.08.2026
// süper adminin seçtiği trafik işlem durumunu tutmak için
    var selectedStatus by remember {
        mutableStateOf("")
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    val trafficStatusOptions = listOf(
        "Kaydedildi",
        "İnceleniyor",
        "Tamamlandı",
        "İptal Edildi"
    )

    //31.08.2026
// süper admin trafik kaydını silmeden önce onay penceresini kontrol etmek için
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    // detay ekranı açıldığında trafik kayıtlarını backend üzerinden getir
    LaunchedEffect(trafficInspectionId) {
        viewModel.fetchTrafficInspections()

        //31.08.2026
        // seçilen trafik kaydına ait fotoğrafları getir
        viewModel.fetchTrafficInspectionPhotos(
            trafficInspectionId
        )
    }


    // route üzerinden gelen ID'ye ait trafik kaydını bul
    val trafficInspection =
        trafficInspections.firstOrNull {
            it.id == trafficInspectionId
        }

    //31.08.2026
// kayıt yüklendiğinde mevcut durumu seçim alanına aktar
    LaunchedEffect(trafficInspection?.status) {

        if (
            selectedStatus.isBlank()
            && trafficInspection?.status != null
        ) {
            selectedStatus =
                trafficInspection.status
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },

            title = {
                Text("Trafik Kaydını Sil")
            },

            text = {
                Text(
                    "Bu trafik işlem kaydını silmek istediğinize emin misiniz? Bu işlem geri alınamaz."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteTrafficInspection(
                            trafficInspectionId = trafficInspectionId,
                            onSuccess = {

                                showDeleteDialog = false

                                //31.08.2026
                                // kayıt silindikten sonra trafik kayıt listesine geri dön
                                onNavigateBack()
                            }
                        )
                    }
                ) {
                    Text(
                        text = "Sil",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Vazgeç")
                }
            }
        )
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

                    if (isAdmin) {

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Text(
                            text = "Durum Yönetimi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            OutlinedButton(
                                onClick = {
                                    statusMenuExpanded = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedStatus.ifBlank {
                                        trafficInspection.status ?: "Durum Seç"
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = statusMenuExpanded,
                                onDismissRequest = {
                                    statusMenuExpanded = false
                                }
                            ) {

                                trafficStatusOptions.forEach { statusOption ->

                                    DropdownMenuItem(
                                        text = {
                                            Text(statusOption)
                                        },
                                        onClick = {
                                            selectedStatus = statusOption
                                            statusMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            onClick = {

                                viewModel.updateTrafficInspectionStatus(
                                    trafficInspectionId = trafficInspectionId,
                                    newStatus = selectedStatus
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled =
                                selectedStatus.isNotBlank()
                                        && selectedStatus != trafficInspection.status
                        ) {
                            Text("Durumu Güncelle")
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Kaydı Sil")
                        }
                    }

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

                    HorizontalDivider()

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "İşlem Fotoğrafları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    if (trafficPhotos.isEmpty()) {

                        Text(
                            text = "Bu trafik kaydına ait fotoğraf bulunmuyor.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    } else {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            trafficPhotos.forEach { photo ->

                                //31.08.2026
                                // backend tarafından dönen relative fotoğraf URL'sini tam URL'ye çevir
                                val fullPhotoUrl =
                                    "http://10.0.2.2:8000${photo.photoUrl}"

                                AsyncImage(
                                    model = fullPhotoUrl,
                                    contentDescription = "Trafik işlem fotoğrafı",
                                    modifier = Modifier
                                        .size(180.dp),
                                    contentScale = ContentScale.Crop,

                                    onSuccess = {

                                        android.util.Log.d(
                                            "TrafikDetayFoto",
                                            "Fotoğraf başarıyla yüklendi: $fullPhotoUrl"
                                        )
                                    },

                                    onError = { error ->

                                        android.util.Log.e(
                                            "TrafikDetayFoto",
                                            "Fotoğraf yüklenemedi: $fullPhotoUrl",
                                            error.result.throwable
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}