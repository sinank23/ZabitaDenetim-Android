package com.example.zabitadenetim.ui.screens.login

import android.widget.Space
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

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    // viewmodeli buraya entegre ediyoruz.
    viewModel: LoginViewModel = viewModel()
) {
    // email ve şifreyi tutacağımız durum değişkenleri
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    // giriş işleminin yüklenme ve hata durumları
    val isLoading by viewModel.isLoading.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // şimdi yazacağımız yapı sayesinde ekranda elemanlar yukardan aşağıya dizilecek
    Column(
        modifier = Modifier
            .fillMaxSize() // ekranın tamamını kaplaması için
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // dikey ve yatay eksenlerde ortaladık login ekranını

    ) {

        // logo ekliyoruz
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Kurumsal Logo",
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 16.dp)
        )

        // başlık
        Text(
            text = "Zabıta Denetim Sistemi",
            style = MaterialTheme.typography.headlineMedium, // hazır büüyk başlık stili
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // e posta alanını oluşturuyoruz.
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // şifre alanına geçiyoruz
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation() // şifreyi noktalı (gizli) hale getirir.
        )

        Spacer(modifier = Modifier.height(32.dp))

        // giriş butonu
        Button(
            onClick = {
                // Kullanıcının girdiği bilgileri backend'e gönder
                viewModel.login(
                    context = context,
                    email = email,
                    password = password,

                    // Token başarıyla alınırsa ancak o zaman ana ekrana geç
                    onSuccess = {
                        onNavigateToHome()
                    }
                )
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
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
                    style = MaterialTheme.typography.titleMedium
                )
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
    }
}