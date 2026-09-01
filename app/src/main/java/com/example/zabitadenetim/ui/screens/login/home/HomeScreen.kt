package com.example.zabitadenetim.ui.screens.login.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// YENİ EKLENDİ (SİLME İŞLEMİ): Silme ikonu için import
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// YENİ EKLENDİ (SİLME İŞLEMİ): Coroutine için import
import kotlinx.coroutines.launch
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.InspectionResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToNewInspection: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {

    // veri tutucu (state) ekliyoruz
    var inspections by remember { mutableStateOf<List<InspectionResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    //01.09.2026
// çıkış onay penceresinin durumunu tutmak için
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    // 11.08.2026
    // işletme adına göre denetim arama
    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember { mutableStateOf("Tümü") }

    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val availableCategories = inspections
        .mapNotNull { it.categoryName }
        .distinct()      // faaliyet konularını çıkar
        .sorted()

    val filteredInspections = inspections.filter { inspection ->

        val matchesSearch =
            inspection.businessName
                ?.contains(searchQuery, ignoreCase = true) == true

        val matchesCategory =
            selectedCategory == "Tümü" ||
                    inspection.categoryName == selectedCategory

        matchesSearch && matchesCategory
    }

    // YENİ EKLENDİ (SİLME İŞLEMİ): Arka planda silme isteği atmak için coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // ekran açıldığında çalışacak modül
    LaunchedEffect(Unit) {
        try {
            inspections = ApiClient.apiService.getInspections()
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Veriler alınamadı: ${e.localizedMessage}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zabıta Denetim Sistemi") },
                actions = {
                    IconButton(
                        onClick = {
                            showLogoutDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = { onNavigateToNewInspection() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Yeni Denetim Başlat")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("İşletme adına göre ara") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        categoryMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Filtre: $selectedCategory")
                }

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = {
                        categoryMenuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tümü") },
                        onClick = {
                            selectedCategory = "Tümü"
                            categoryMenuExpanded = false
                        }
                    )

                    availableCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (inspections.isEmpty()) {
                Text(
                    text = "Kayıtlı bir denetim yok",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (filteredInspections.isEmpty()) {
                Text(
                    text = "Arama veya filtreye uygun denetim bulunamadı.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredInspections) { inspection ->

                        // YENİ EKLENDİ (SİLME İŞLEMİ): Emin misiniz diyalogu için state
                        var showDialog by remember { mutableStateOf(false) }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Denetimi Sil") },
                                text = { Text("${inspection.businessName} isimli işletmenin denetim kaydını silmek istediğinize emin misiniz?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDialog = false
                                        // Silme işlemini başlat
                                        coroutineScope.launch {
                                            try {
                                                val response = ApiClient.apiService.deleteInspection(inspection.id)
                                                if (response.isSuccessful) {
                                                    // Silme başarılıysa listeyi tekrar sunucudan çekip güncelle
                                                    inspections = ApiClient.apiService.getInspections()
                                                }
                                            } catch (e: Exception) {
                                                // Hata durumunda loglanabilir
                                            }
                                        }
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
                        // YENİ EKLENDİ BİTTİ

                        Card(
                            onClick = {
                                onNavigateToDetail(inspection.id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            // YENİ EKLENDİ (SİLME İŞLEMİ): Yazılar ve butonu yan yana koymak için Row eklendi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mevcut tasarımın (Sol Taraf)
                                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        // İşletme adı kalan alanı kullanır ve en fazla 2 satır görünür.
                                        Text(
                                            text = inspection.businessName ?: "İşletme adı yok",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 2,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Tarih ve denetim numarası sağ tarafta alt alta gösterilir.
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = if (!inspection.inspectionDate.isNullOrEmpty()) {
                                                    inspection.inspectionDate.take(10)
                                                } else {
                                                    "Tarih yok"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text = "Denetim #${inspection.id}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Faaliyet Konusu: ${inspection.categoryName ?: "Belirtilmemiş"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Durum: ${inspection.status ?: "Bekliyor"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // YENİ EKLENDİ (SİLME İŞLEMİ): Sağ tarafa hizalanmış çöp tenekesi ikonu
                                IconButton(
                                    onClick = { showDialog = true },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Sil",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //01.09.2026
// normal zabıta çıkış işleminden önce kullanıcıdan onay almak için
    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },

            title = {
                Text("Çıkış Yap")
            },

            text = {
                Text("Çıkış yapmak istediğinize emin misiniz?")
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Evet")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Hayır")
                }
            }
        )
    }
}