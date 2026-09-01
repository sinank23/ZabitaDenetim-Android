package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable

fun TrafficHomeScreen(
    onNavigateToNewTrafficInspection: () -> Unit,
    onNavigateToTrafficRecords: () -> Unit,
    onLogout: () -> Unit
) {

    //01.09.2026
    // trafik zabıta çıkış onay penceresinin durumunu tutmak için
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    //25.08.2026
    // trafik zabıta giirş yaptıktan sonra kendi ekranını görsün

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Trafik Zabıta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Trafik Zabıta İşlem Sistemi",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = {
                onNavigateToNewTrafficInspection()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Yeni Trafik İşlemi")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onNavigateToTrafficRecords()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trafik Kayıtları")
        }

        Spacer(modifier = Modifier.height(24.dp))

        //01.09.2026
        // trafik zabıta sistemden güvenli şekilde çıkış yapabilsin
        OutlinedButton(
            onClick = {
                showLogoutDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Çıkış Yap")
        }
    }

    //01.09.2026
    // trafik zabıta çıkış işleminden önce kullanıcıdan onay almak için
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