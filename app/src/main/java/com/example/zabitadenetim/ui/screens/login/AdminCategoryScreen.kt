package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.presentation.AdminCategoryViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryScreen (
    onNavigateBack: () -> Unit,
    viewModel: AdminCategoryViewModel = viewModel()
) {

    //19.08.2026
    // kategori durumlarını ekrandan takip et
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    //süper adminin yazdığı yeni kategori adını tut.
    var newCategoryName by remember {
        mutableStateOf("")
    }

    // düzenleynecğimiz kategoriyi ve güncel adını aklında tut
    var editingCategoryId by remember {
        mutableStateOf<Int?>(null)
    }
    var editingCategoryName by remember {
        mutableStateOf("")
    }

    // silinecek kategori bilgisini tut
    var deletingCategoryId by remember {
        mutableStateOf<Int?>(null)
    }

    var deletingCategoryName by remember {
        mutableStateOf("")
    }

    // ekran ilk açıldığında backendden kategorileri getir
    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text("Kategori Yönetimi")
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
                    contentAlignment = androidx.compose.ui.Alignment.Center
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
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Bilinmeyen bir hata oluştu.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {

                    //19.08.2026
                    // Yeni kategori adı giriş alanı
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = {
                            newCategoryName = it
                        },
                        label = {
                            Text("Yeni Kategori Adı")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {

                                viewModel.createCategory(
                                    categoryName = newCategoryName.trim(),

                                    // Kategori eklenirse giriş alanını temizle
                                    onSuccess = {
                                        newCategoryName = ""
                                    }
                                )
                            }
                        },
                        enabled = newCategoryName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Kategori Ekle")
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Mevcut Kategoriler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(categories) { category ->

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(
                                        modifier = Modifier.height(4.dp)
                                    )

                                    Text(
                                        text = "Kategori ID: ${category.id}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = {
                                            editingCategoryId = category.id
                                            editingCategoryName = category.name
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Düzenle")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = {
                                            deletingCategoryId = category.id
                                            deletingCategoryName = category.name
                                        },
                                        modifier = Modifier.fillMaxWidth()
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

    //19.08.2026
    // kategori düzenleme penceresi
    if (editingCategoryId != null) {

        AlertDialog(
            onDismissRequest = {
                editingCategoryId = null
                editingCategoryName = ""
            },

            title = {
                Text("Kategori Düzenle")
            },

            text = {
                OutlinedTextField(
                    value = editingCategoryName,
                    onValueChange = {
                        editingCategoryName = it
                    },
                    label = {
                        Text("Kategori Adı")
                    },
                    singleLine = true
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val categoryId = editingCategoryId

                        if (
                            categoryId != null &&
                            editingCategoryName.isNotBlank()
                        ) {

                            viewModel.updateCategory(
                                categoryId = categoryId,
                                newCategoryName = editingCategoryName.trim(),

                                // Güncelleme başarılı olursa pencereyi kapat
                                onSuccess = {
                                    editingCategoryId = null
                                    editingCategoryName = ""
                                }
                            )
                        }
                    }
                ) {
                    Text("Güncelle")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        editingCategoryId = null
                        editingCategoryName = ""
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    //19.08.2026
// Kategori silme işlemi için kullanıcıdan onay al
    if (deletingCategoryId != null) {

        AlertDialog(
            onDismissRequest = {
                deletingCategoryId = null
                deletingCategoryName = ""
            },

            title = {
                Text("Kategori Sil")
            },

            text = {
                Text(
                    text = "\"$deletingCategoryName\" kategorisini silmek istediğinize emin misiniz?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val categoryId = deletingCategoryId

                        if (categoryId != null) {

                            viewModel.deleteCategory(
                                categoryId = categoryId,

                                // Silme başarılıysa pencereyi kapat
                                onSuccess = {
                                    deletingCategoryId = null
                                    deletingCategoryName = ""
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
                        deletingCategoryId = null
                        deletingCategoryName = ""
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}