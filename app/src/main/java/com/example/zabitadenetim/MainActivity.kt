package com.example.zabitadenetim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.zabitadenetim.ui.screens.login.LoginScreen
import com.example.zabitadenetim.ui.theme.ZabitaDenetimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZabitaDenetimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Kendi çizdiğimiz Login ekranını buraya çağırıyoruz
                    LoginScreen()
                }
            }
        }
    }
}