package com.example.zabitadenetim.ui.screens.login.inspection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.presentation.InspectionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessInspectionHistoryScreen(
    businessId: Int,
    currentInspectionId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToInspectionDetail: (Int) -> Unit,

    // Geçmiş denetim ile mevcut denetimi karşılaştırma ekranına yönlendirir
    onNavigateToComparison: (Int, Int) -> Unit,

    viewModel: InspectionViewModel = viewModel()
) {

    val businessInspections by
    viewModel.businessInspections.collectAsState()

    val isLoadingBusinessInspections by
    viewModel.isLoadingBusinessInspections.collectAsState()


    //14.08.2026
    // ekran açıldığında seçilen işletmeye ait tüm denetimleri getir
    LaunchedEffect(businessId) {
        viewModel.fetchBusinessInspections(businessId)
    }


    // kullanıcının şu an açık olan denetimini geçmiş listesinden çıkar
    val pastInspections = businessInspections.filter {
        it.id != currentInspectionId
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Geçmiş Denetimler")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onNavigateBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,

                        titleContentColor =
                            MaterialTheme.colorScheme.onPrimary,

                        navigationIconContentColor =
                            MaterialTheme.colorScheme.onPrimary
                    )
            )
        }

    ) { paddingValues ->


        when {

            isLoadingBusinessInspections -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Geçmiş denetimler yükleniyor..."
                    )
                }
            }


            pastInspections.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    Text(
                        text = "Bu işletmeye ait başka geçmiş denetim bulunamadı.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }


            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {

                    items(
                        items = pastInspections,
                        key = { inspection ->
                            inspection.id
                        }
                    ) { inspection ->


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    onNavigateToInspectionDetail(
                                        inspection.id
                                    )
                                },

                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 3.dp
                                )
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = inspection.businessName
                                        ?: "İşletme adı belirtilmemiş",

                                    style =
                                        MaterialTheme.typography.titleMedium,

                                    fontWeight = FontWeight.Bold,

                                    color =
                                        MaterialTheme.colorScheme.primary
                                )


                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )


                                Text(
                                    text =
                                        "Denetim No: ${inspection.id}",

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )


                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )


                                val formattedDate =
                                    inspection.inspectionDate
                                        ?.let { date ->

                                            try {

                                                val datePart =
                                                    date.substringBefore("T")

                                                val timePart =
                                                    date.substringAfter("T")
                                                        .take(5)

                                                val dateParts =
                                                    datePart.split("-")

                                                if (
                                                    dateParts.size == 3
                                                ) {

                                                    "${dateParts[2]}." +
                                                            "${dateParts[1]}." +
                                                            "${dateParts[0]} " +
                                                            timePart

                                                } else {

                                                    date
                                                }

                                            } catch (e: Exception) {

                                                date
                                            }
                                        }


                                Text(
                                    text =
                                        "Tarih: ${
                                            formattedDate
                                                ?: "Belirtilmemiş"
                                        }",

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )


                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )


                                Text(
                                    text =
                                        "Durum: ${
                                            inspection.status
                                                ?: "Bekliyor"
                                        }",

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )


                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )


                                Text(
                                    text =
                                        "Faaliyet Konusu: ${
                                            inspection.categoryName
                                                ?: "Belirtilmemiş"
                                        }",

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )

                                //17.08.2026
                                // geçmiş denetimi mevcut denetimle karşılaştırma butonu
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        // karşılaştırma ekranına gönder
                                        onNavigateToComparison(
                                            currentInspectionId,
                                            inspection.id
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Mevcut Denetimle Karşılaştır")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}