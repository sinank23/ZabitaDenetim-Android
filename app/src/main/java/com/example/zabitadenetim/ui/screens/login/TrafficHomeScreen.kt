package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable

fun TrafficHomeScreen(
    onNavigateToNewTrafficInspection: () -> Unit,
    onNavigateToTrafficRecords: () -> Unit
) {
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
    }
}
