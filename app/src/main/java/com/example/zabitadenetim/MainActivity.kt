package com.example.zabitadenetim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.TokenManager
import com.example.zabitadenetim.ui.screens.login.LoginScreen
import com.example.zabitadenetim.ui.screens.login.home.HomeScreen
import com.example.zabitadenetim.ui.screens.login.inspection.NewInspectionScreen
import com.example.zabitadenetim.ui.theme.ZabitaDenetimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiClient.init(applicationContext)

        setContent {
            ZabitaDenetimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {

                        // 1. DURAK: GİRİŞ EKRANI
                        composable("login") {
                            LoginScreen(
                                onNavigateToHome = {
                                    // Giriş başarılı olunca Ana Ekrana (home) geç
                                    // popUpTo ile geri tuşuna basıldığında tekrar login'e düşmeyi engelliyoruz
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. DURAK: ANA EKRAN
                        composable("home") {
                            val context = LocalContext.current

                            HomeScreen(
                                onLogout = {
                                    // Çıkış yapıldığında token'ı hafızadan sil
                                    val tokenManager = TokenManager(context)
                                    tokenManager.clearToken()

                                    // Login ekranına geri dön ve home ekranını geçmişten sil
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToNewInspection = {
                                    // yeni denetim sayfasına geçiş
                                    navController.navigate("new_inspection")
                                }
                            )
                        }

                        //3. ekranımız yeni denetim ekranı
                        composable("new_inspection") {
                            NewInspectionScreen (
                                onNavigateBack = {
                                    // geri tuşuna basıldığında bir önceki sayfaya dön
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}