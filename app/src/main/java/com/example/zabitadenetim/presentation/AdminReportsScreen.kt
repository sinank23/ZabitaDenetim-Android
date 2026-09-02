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
import com.example.zabitadenetim.presentation.AdminReportsViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPdf: (Int) -> Unit,
    viewModel: AdminReportsViewModel = viewModel()
) {

    //25.08.2026
    // Süper Admin rapor ekranındaki denetimleri ve ekran durumlarını takip et
    val inspections by viewModel.inspections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    //02.09.2026
// Süper Admin rapor ekranındaki arama ve tarih filtrelerini tutmak için
    var personnelSearch by remember {
        mutableStateOf("")
    }

    var inspectionNumberSearch by remember {
        mutableStateOf("")
    }

    var monthSearch by remember {
        mutableStateOf("")
    }

    var startDateSearch by remember {
        mutableStateOf("")
    }

    var endDateSearch by remember {
        mutableStateOf("")
    }

    //02.09.2026
// Filtrele butonuna basıldığında uygulanacak filtre değerleri
    var appliedPersonnelSearch by remember { mutableStateOf("") }
    var appliedInspectionNumberSearch by remember { mutableStateOf("") }
    var appliedMonthSearch by remember { mutableStateOf("") }
    var appliedStartDateSearch by remember { mutableStateOf("") }
    var appliedEndDateSearch by remember { mutableStateOf("") }

    //02.09.2026
// personel, denetim numarası ve tarihe göre raporları filtrelemek için
    val filteredInspections = inspections.filter { inspection ->

        val inspectionDate =
            inspection.inspectionDate
                ?.take(10)
                ?: ""

        val personnelMatches =
            appliedPersonnelSearch.isBlank() ||
                    inspection.inspectorName
                        ?.contains(
                            appliedPersonnelSearch,
                            ignoreCase = true
                        ) == true

        val inspectionNumberMatches =
            appliedInspectionNumberSearch.isBlank() ||
                    inspection.id
                        .toString()
                        .contains(appliedInspectionNumberSearch)

        val monthMatches =
            appliedMonthSearch.isBlank() ||
                    inspectionDate.startsWith(appliedMonthSearch)

        val startDateMatches =
            appliedStartDateSearch.isBlank() ||
                    inspectionDate >= appliedStartDateSearch

        val endDateMatches =
            appliedEndDateSearch.isBlank() ||
                    inspectionDate <= appliedEndDateSearch

        personnelMatches &&
                inspectionNumberMatches &&
                monthMatches &&
                startDateMatches &&
                endDateMatches
    }

    // ekran ilk açıldığında tüm denetimleri backend üzerinden getir
    LaunchedEffect(Unit) {
        viewModel.fetchInspections()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Raporlar")
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

            inspections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kayıtlı denetim bulunamadı.")
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

                    item {

                        Text(
                            text = "Rapor Filtreleri",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = personnelSearch,
                            onValueChange = {
                                personnelSearch = it
                            },
                            label = {
                                Text("Personel Ara")
                            },
                            placeholder = {
                                Text("Örn: Ahmet Yılmaz")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = inspectionNumberSearch,
                            onValueChange = {
                                inspectionNumberSearch = it.filter { char ->
                                    char.isDigit()
                                }
                            },
                            label = {
                                Text("Denetim No")
                            },
                            placeholder = {
                                Text("Örn: 2058")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedTextField(
                            value = monthSearch,
                            onValueChange = {
                                monthSearch = it
                            },
                            label = {
                                Text("Ay Filtresi")
                            },
                            placeholder = {
                                Text("Örn: 2026-08")
                            },
                            supportingText = {
                                Text("Belirli bir ay için YYYY-AA biçiminde girin")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedTextField(
                                value = startDateSearch,
                                onValueChange = {
                                    startDateSearch = it
                                },
                                label = {
                                    Text("Başlangıç")
                                },
                                placeholder = {
                                    Text("2026-08-01")
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = endDateSearch,
                                onValueChange = {
                                    endDateSearch = it
                                },
                                label = {
                                    Text("Bitiş")
                                },
                                placeholder = {
                                    Text("2026-08-31")
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            onClick = {
                                appliedPersonnelSearch = personnelSearch
                                appliedInspectionNumberSearch = inspectionNumberSearch
                                appliedMonthSearch = monthSearch
                                appliedStartDateSearch = startDateSearch
                                appliedEndDateSearch = endDateSearch
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Filtrele")
                        }

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        TextButton(
                            onClick = {
                                personnelSearch = ""
                                inspectionNumberSearch = ""
                                monthSearch = ""
                                startDateSearch = ""
                                endDateSearch = ""

                                appliedPersonnelSearch = ""
                                appliedInspectionNumberSearch = ""
                                appliedMonthSearch = ""
                                appliedStartDateSearch = ""
                                appliedEndDateSearch = ""
                            }
                        ) {
                            Text("Filtreleri Temizle")
                        }

                        HorizontalDivider()

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(filteredInspections) { inspection ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = inspection.businessName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "Denetim No: ${inspection.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Durum: ${inspection.status ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Faaliyet Konusu: ${inspection.categoryName ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Denetimi Yapan: ${inspection.inspectorName ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Tarih: ${inspection.inspectionDate ?: "Belirtilmemiş"}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                //25.08.2026
                                //seçilen denetime ait pdf rapor ekranını aç
                                Button(
                                    onClick = {
                                        onNavigateToPdf(inspection.id)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("PDF Raporunu Görüntüle")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}