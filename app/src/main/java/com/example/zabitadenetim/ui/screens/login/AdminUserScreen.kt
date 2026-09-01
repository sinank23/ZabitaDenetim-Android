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
import com.example.zabitadenetim.presentation.AdminUserViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminUserViewModel = viewModel()
) {

    //24.08.2026
    // Süper Admin ekranındaki kullanıcı listesini ve ekran durumlarını takip et
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    //24.08.2026
    // Yeni kullanıcı ekleme alanlarının durumlarını tut
    var newUserFullName by remember {
        mutableStateOf("")
    }

    var newUserEmail by remember {
        mutableStateOf("")
    }

    var newUserPassword by remember {
        mutableStateOf("")
    }

    var newUserRole by remember {
        mutableStateOf("zabita")
    }

    //24.08.2026
    // Düzenlenecek kullanıcının bilgilerini tut
    var editingUserId by remember {
        mutableStateOf<Int?>(null)
    }

    var editingUserFullName by remember {
        mutableStateOf("")
    }

    var editingUserEmail by remember {
        mutableStateOf("")
    }

    var editingUserRole by remember {
        mutableStateOf("zabita")
    }

    var editingUserPassword by remember {
        mutableStateOf("")
    }

    //24.08.2026
    // Silinmek üzere seçilen kullanıcının bilgilerini tut
    var deletingUserId by remember {
        mutableStateOf<Int?>(null)
    }

    var deletingUserName by remember {
        mutableStateOf("")
    }

    // ekran ilk açıldığında kullanıcı listesini backend üzerinden getir
    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kullanıcı Yönetimi")
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

            users.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kayıtlı kullanıcı bulunamadı.")
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
                                    text = "Yeni Kullanıcı Ekle",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                OutlinedTextField(
                                    value = newUserFullName,
                                    onValueChange = {
                                        newUserFullName = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("Ad Soyad")
                                    }
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                OutlinedTextField(
                                    value = newUserEmail,
                                    onValueChange = {
                                        newUserEmail = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("E-posta")
                                    }
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                OutlinedTextField(
                                    value = newUserPassword,
                                    onValueChange = {
                                        newUserPassword = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text("Şifre")
                                    }
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Rol: $newUserRole",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    TextButton(
                                        onClick = {
                                            newUserRole = "zabita"
                                        }
                                    ) {
                                        Text("Zabıta")
                                    }

//01.09.2026
// trafik zabıta kullanıcı rolünü seçebilmek için
                                    TextButton(
                                        onClick = {
                                            newUserRole = "trafik_zabita"
                                        }
                                    ) {
                                        Text("Trafik Zabıta")
                                    }

                                    TextButton(
                                        onClick = {
                                            newUserRole = "superadmin"
                                        }
                                    ) {
                                        Text("Süper Admin")
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Button(
                                    onClick = {

                                        viewModel.createUser(
                                            fullName = newUserFullName.trim(),
                                            email = newUserEmail.trim(),
                                            role = newUserRole,
                                            password = newUserPassword,
                                            onSuccess = {
                                                newUserFullName = ""
                                                newUserEmail = ""
                                                newUserPassword = ""
                                                newUserRole = "zabita"
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled =
                                        newUserFullName.isNotBlank() &&
                                                newUserEmail.isNotBlank() &&
                                                newUserPassword.isNotBlank()
                                ) {
                                    Text("Kullanıcı Ekle")
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                    }

                    items(users) { user ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "Kullanıcı ID: ${user.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "E-posta: ${user.email}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Rol: ${user.role}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    Button(
                                        onClick = {
                                            editingUserId = user.id
                                            editingUserFullName = user.fullName
                                            editingUserEmail = user.email
                                            editingUserRole = user.role
                                            editingUserPassword = ""
                                        }
                                    ) {
                                        Text("Düzenle")
                                    }

                                    Button(
                                        onClick = {
                                            deletingUserId = user.id
                                            deletingUserName = user.fullName
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
    // Kullanıcı düzenleme penceresi
    if (editingUserId != null) {

        AlertDialog(
            onDismissRequest = {
                editingUserId = null
                editingUserFullName = ""
                editingUserEmail = ""
                editingUserRole = "zabita"
                editingUserPassword = ""
            },

            title = {
                Text("Kullanıcıyı Düzenle")
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = editingUserFullName,
                        onValueChange = {
                            editingUserFullName = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Ad Soyad")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = editingUserEmail,
                        onValueChange = {
                            editingUserEmail = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("E-posta")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = editingUserPassword,
                        onValueChange = {
                            editingUserPassword = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Yeni Şifre (isteğe bağlı)")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Rol: $editingUserRole"
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        TextButton(
                            onClick = {
                                editingUserRole = "zabita"
                            }
                        ) {
                            Text("Zabıta")
                        }

//01.09.2026
// mevcut kullanıcının rolünü trafik zabıta olarak değiştirebilmek için
                        TextButton(
                            onClick = {
                                editingUserRole = "trafik_zabita"
                            }
                        ) {
                            Text("Trafik Zabıta")
                        }

                        TextButton(
                            onClick = {
                                editingUserRole = "superadmin"
                            }
                        ) {
                            Text("Süper Admin")
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val userId = editingUserId

                        if (
                            userId != null &&
                            editingUserFullName.isNotBlank() &&
                            editingUserEmail.isNotBlank()
                        ) {

                            viewModel.updateUser(
                                userId = userId,
                                fullName = editingUserFullName.trim(),
                                email = editingUserEmail.trim(),
                                role = editingUserRole,
                                password =
                                    editingUserPassword
                                        .takeIf { it.isNotBlank() },
                                onSuccess = {
                                    editingUserId = null
                                    editingUserFullName = ""
                                    editingUserEmail = ""
                                    editingUserRole = "zabita"
                                    editingUserPassword = ""
                                }
                            )
                        }
                    },
                    enabled =
                        editingUserFullName.isNotBlank() &&
                                editingUserEmail.isNotBlank()
                ) {
                    Text("Kaydet")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        editingUserId = null
                        editingUserFullName = ""
                        editingUserEmail = ""
                        editingUserRole = "zabita"
                        editingUserPassword = ""
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    //24.08.2026
    // Kullanıcı silme işlemi için onay penceresi
    if (deletingUserId != null) {

        AlertDialog(
            onDismissRequest = {
                deletingUserId = null
                deletingUserName = ""
            },

            title = {
                Text("Kullanıcıyı Sil")
            },

            text = {
                Text(
                    text = "\"$deletingUserName\" kullanıcısını silmek istediğinize emin misiniz?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        val userId = deletingUserId

                        if (userId != null) {

                            viewModel.deleteUser(
                                userId = userId,
                                onSuccess = {
                                    deletingUserId = null
                                    deletingUserName = ""
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
                        deletingUserId = null
                        deletingUserName = ""
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}