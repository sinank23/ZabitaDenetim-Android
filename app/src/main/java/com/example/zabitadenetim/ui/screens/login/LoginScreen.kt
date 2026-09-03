package com.example.zabitadenetim.ui.screens.login

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.zabitadenetim.R
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background



@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,

    //18.08.2026
    // Süper Admin girişinde ayrı yönetim ekranına geçmek için
    onNavigateToAdmin: () -> Unit,

    //25.08.2026
    // Trafik Zabıta girişinde kendi ekranına geçmek için
    onNavigateToTraffic: () -> Unit,

    // viewmodeli buraya entegre ediyoruz.
    viewModel: LoginViewModel = viewModel()
) {
    // email ve şifreyi tutacağımız durum değişkenleri
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //03.09.2026
// şifrenin görünür veya gizli olma durumunu tutmak için
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // giriş işleminin yüklenme ve hata durumları
    val isLoading by viewModel.isLoading.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // şimdi yazacağımız yapı sayesinde ekranda elemanlar yukardan aşağıya dizilecek
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // dikey ve yatay eksenlerde ortaladık login ekranını

    ) {

        Spacer(modifier = Modifier.height(70.dp))

        // logo ekliyoruz
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Kurumsal Logo",
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 16.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))

        // başlık
        Text(
            text = "Zabıta Denetim Sistemi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Yetkili Personel Girişi",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // e posta alanını oluşturuyoruz.
        //03.09.2026
        // giriş alanlarını daha düzenli ve kurumsal göstermek için kart yapısı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // e posta alanını oluşturuyoruz.
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "E-posta"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // şifre alanına geçiyoruz
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Şifre"
                        )
                    },

                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                imageVector =
                                    if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                contentDescription =
                                    if (passwordVisible) {
                                        "Şifreyi gizle"
                                    } else {
                                        "Şifreyi göster"
                                    }
                            )
                        }
                    },

                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium,

                    visualTransformation =
                        if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // giriş butonu
                Button(
                    onClick = {
                        // Kullanıcının girdiği bilgileri backend'e gönder
                        viewModel.login(
                            context = context,
                            email = email,
                            password = password,

                            // Token başarıyla alınırsa ancak o zaman ana ekrana geç
                            //18.08.2026
                            // Giriş başarılıysa backend'den gelen role göre doğru ekrana yönlendir
                            onSuccess = { role ->

                                when (role) {

                                    "superadmin" -> {
                                        // Süper Admin kendi yönetim ekranına gider
                                        onNavigateToAdmin()
                                    }

                                    "trafik_zabita" -> {
                                        // Trafik Zabıta kendi trafik ekranına gider
                                        onNavigateToTraffic()
                                    }

                                    else -> {
                                        // Zabıta personeli mevcut ana ekrana gider
                                        onNavigateToHome()
                                    }
                                }
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = MaterialTheme.shapes.large
                ) {

                    // Giriş isteği sürerken kullanıcıya bekleme durumunu göster
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Giriş Yap",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Giriş başarısız olursa hata mesajını kullanıcıya göster
        if (loginError != null) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = loginError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

//03.09.2026
// giriş ekranının alt kısmında kurumsal bilgi göstermek için
        Text(
            text = "Gaziantep Büyükşehir Belediyesi",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Bilgi İşlem Daire Başkanlığı",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}