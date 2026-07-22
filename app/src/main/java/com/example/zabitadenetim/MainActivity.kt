package com.example.zabitadenetim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.zabitadenetim.presentation.InspectionScreen
import com.example.zabitadenetim.ui.theme.ZabitaDenetimTheme // Kendi proje adına göre import edilebilir

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZabitaDenetimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Kendi yazdığımız ekranı çağırıyoruz
                    InspectionScreen()
                }
            }
        }
    }
}