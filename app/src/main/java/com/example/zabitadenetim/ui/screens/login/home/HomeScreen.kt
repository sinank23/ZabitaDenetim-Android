package com.example.zabitadenetim.ui.screens.login.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy

@OptIn(ExperimentalMaterial3Api::class)
@Composable      // bu fonksiyon bir ekran parçasıdır der
fun HomeScreen(onLogout: () -> Unit, onNavigateToNewInspection: () ->  Unit) {

    Scaffold(
        // üst menü (topbar kısm) oluşturuluyor
        topBar = {
            TopAppBar(
                title = { Text("Zabıta Denetim Sistemi")},
                actions = {
                    // sağ üst köşeeye eklenecek çıkış butonu
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                },

                // üst menü ve arka plan ve yazı renkleri
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        paddingValues ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // yeni bilgi yazısı
            Text(
                text = "Aktif bir denetim bulunmuyor.",
                style = MaterialTheme.typography.bodyLarge
            )

            // yazı ile buton arasında görünmez boşluk (spacer)

            Spacer(modifier = Modifier.height(24.dp))

            // yeni denetim sayfasına yönlendirecek olan butonu oluşturalım
            Button(
                onClick = { onNavigateToNewInspection()  },  // tıklanınca yeni sayfaya geçişi tetikledik.
                modifier = Modifier.fillMaxWidth(0.7f)   // buton genişliği ekranın %70 i olsn

            ) {
                Text("Yeni Denetim Başlat")
            }
        }
    }
}