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
import com.example.zabitadenetim.presentation.AdminCriteriaViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCriteriaScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminCriteriaViewModel = viewModel()
) {

    //24.08.2026
    // ViewModel üzerindeki kriter listesini ve ekran durumlarını takip et
    val criteria by viewModel.criteria.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // yeni kriter eklenirken metin alanının durumunu tut.
    var newCriterionText by remember {
        mutableStateOf("")
    }

    // düzenlencek kriterin bilgilerini tut
    var editingCriterionId by remember {
        mutableStateOf<Int?>(null)
    }

    var editingCriterionText by remember {
        mutableStateOf("")
    }

    var editingCriterionCategoryId by remember {
        mutableStateOf<Int?>(null)
    }

    // silinmek üzere seçilen kriteri tut
    var deletingCriterionId by remember {
        mutableStateOf<Int?>(null)
    }

    var deletingCriterionText by remember {
        mutableStateOf("")
    }

    // ekran ilk açıldığında tüm kriterleri backend üzerinden getir
    LaunchedEffect(Unit) {
        viewModel.fetchCriteria()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Denetim Kriterleri Yönetimi")
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

            criteria.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kayıtlı denetim kriteri bulunamadı.")
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

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = "Yeni Ortak Kriter",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                OutlinedTextField(
                                    value = newCriterionText,
                                    onValueChange = {
                                        newCriterionText = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("Kriter Sorusu")
                                    }
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Button(
                                    onClick = {

                                        val trimmedText = newCriterionText.trim()

                                        if (trimmedText.isNotEmpty()) {

                                            viewModel.createCriterion(
                                                categoryId = null,
                                                questionText = trimmedText,
                                                onSuccess = {
                                                    newCriterionText = ""
                                                }
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = newCriterionText.isNotBlank()
                                ) {
                                    Text("Ortak Kriter Ekle")
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(criteria) { criterion ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = criterion.questionText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "Kriter ID: ${criterion.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        if (criterion.categoryId == null) {
                                            "Kapsam: Ortak Kriter"
                                        } else {
                                            "Kategori ID: ${criterion.categoryId}"
                                        },
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    Button(
                                        onClick = {
                                            editingCriterionId = criterion.id
                                            editingCriterionText = criterion.questionText
                                            editingCriterionCategoryId = criterion.categoryId
                                        }
                                    ) {
                                        Text("Düzenle")
                                    }

                                    Button(
                                        onClick = {
                                            deletingCriterionId = criterion.id
                                            deletingCriterionText = criterion.questionText
                                        }
                                    ) {
                                        Text("Sil")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //24.08.2026
    // Kriter düzenleme penceresi
    if (editingCriterionId != null) {

        AlertDialog(
            onDismissRequest = {
                editingCriterionId = null
                editingCriterionText = ""
                editingCriterionCategoryId = null
            },

            title = {
                Text("Denetim Kriterini Düzenle")
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = editingCriterionText,
                        onValueChange = {
                            editingCriterionText = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Kriter Sorusu")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            if (editingCriterionCategoryId == null) {
                                "Kapsam: Ortak Kriter"
                            } else {
                                "Kategori ID: $editingCriterionCategoryId"
                            },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val criterionId = editingCriterionId
                        val trimmedText = editingCriterionText.trim()

                        if (
                            criterionId != null &&
                            trimmedText.isNotEmpty()
                        ) {

                            viewModel.updateCriterion(
                                criterionId = criterionId,
                                categoryId = editingCriterionCategoryId,
                                questionText = trimmedText,
                                onSuccess = {
                                    editingCriterionId = null
                                    editingCriterionText = ""
                                    editingCriterionCategoryId = null
                                }
                            )
                        }
                    },
                    enabled = editingCriterionText.isNotBlank()
                ) {
                    Text("Kaydet")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        editingCriterionId = null
                        editingCriterionText = ""
                        editingCriterionCategoryId = null
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    //24.08.2026
    // Kriter silme işlemi için kullanıcıdan onay al
    if (deletingCriterionId != null) {

        AlertDialog(
            onDismissRequest = {
                deletingCriterionId = null
                deletingCriterionText = ""
            },

            title = {
                Text("Denetim Kriterini Sil")
            },

            text = {
                Text(
                    text = "\"$deletingCriterionText\" kriterini silmek istediğinize emin misiniz?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val criterionId = deletingCriterionId

                        if (criterionId != null) {

                            viewModel.deleteCriterion(
                                criterionId = criterionId,
                                onSuccess = {
                                    deletingCriterionId = null
                                    deletingCriterionText = ""
                                }
                            )
                        }
                    }
                ) {
                    Text("Sil")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        deletingCriterionId = null
                        deletingCriterionText = ""
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}