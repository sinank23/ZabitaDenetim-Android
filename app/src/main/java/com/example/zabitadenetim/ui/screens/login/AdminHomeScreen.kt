package com.example.zabitadenetim.ui.screens.login

import android.service.autofill.UserData
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AdminHomeScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToCriteria: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToBusinesses: () -> Unit,
    onNavigateToReports: () -> Unit
) {

    //18.08.2026
    // süper admin için oluşturulan yönetim ekranı
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Süper Admin Paneli",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Yönetim işlemlerini aşağıdaki bölümlerden gerçekleştirebilirsiniz.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        //18.08.2026
        // işletme kategorilerini yönetmek için
        Button(
            onClick = onNavigateToCategories,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kategori Yönetimi")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // denetim maddelerini yönetmek için
        Button(
            onClick = onNavigateToCriteria,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Denetim Kriterleri")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // sistem kullanıcılarını ve rollerini yönetmek için
        Button(
            onClick = onNavigateToUsers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kullanıcı Yönetimi")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // kayıtlı işletmeleri yönetmek için
        Button(
            onClick = onNavigateToBusinesses,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("İşletme Yönetimi")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // rapor ve istatistik ekranlarına geçmek için
        Button(
            onClick = onNavigateToReports,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Raporlar")
        }
    }
}