package com.example.zabitadenetim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
// YENİ EKLENDİ: Argümanlı geçişler için gerekli kütüphaneler
import androidx.navigation.NavType
import androidx.navigation.navArgument
// YENİ EKLENDİ BİTTİ

import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.TokenManager
import com.example.zabitadenetim.ui.screens.login.LoginScreen
import com.example.zabitadenetim.ui.screens.login.home.HomeScreen
import com.example.zabitadenetim.ui.screens.login.inspection.InspectionDetailScreen
import com.example.zabitadenetim.ui.screens.login.inspection.NewInspectionScreen
import com.example.zabitadenetim.ui.theme.ZabitaDenetimTheme

import com.example.zabitadenetim.ui.screens.login.inspection.PdfViewerScreen
import com.example.zabitadenetim.ui.screens.login.inspection.BusinessInspectionHistoryScreen

import com.example.zabitadenetim.ui.screens.login.inspection.InspectionComparisonScreen

import com.example.zabitadenetim.ui.screens.login.AdminHomeScreen
import com.example.zabitadenetim.ui.screens.login.AdminCategoryScreen

import com.example.zabitadenetim.ui.screens.login.AdminCriteriaScreen
import com.example.zabitadenetim.ui.screens.login.AdminUserScreen
import com.example.zabitadenetim.ui.screens.login.AdminReportsScreen
import com.example.zabitadenetim.ui.screens.login.TrafficHomeScreen
import com.example.zabitadenetim.ui.screens.login.TrafficRecordsScreen

import com.example.zabitadenetim.ui.screens.login.NewTrafficInspectionScreen

import com.example.zabitadenetim.ui.screens.login.TrafficLocationPickerScreen

import com.example.zabitadenetim.ui.screens.login.TrafficInspectionDetailScreen

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

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {

                        // 1. DURAK: GİRİŞ EKRANI
                        composable("login") {

                            LoginScreen(
                                onNavigateToHome = {

                                    // Zabıta personeli mevcut ana ekrana gider
                                    navController.navigate("home") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                },

                                //18.08.2026
                                // Süper Admin kendi yönetim ekranına yönlendirilir
                                onNavigateToAdmin = {

                                    navController.navigate("admin_home") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                },

                                //25.08.2026
                                // Trafik Zabıta kendi ana ekranına yönlendirilir
                                onNavigateToTraffic = {

                                    navController.navigate("traffic_home") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }


                        composable("admin_home") {

                            AdminHomeScreen(
                                onNavigateToCategories = {
                                    navController.navigate("admin_categories")
                                },

                                onNavigateToCriteria = {
                                    navController.navigate("admin_criteria")
                                },

                                onNavigateToUsers = {
                                    navController.navigate("admin_users")
                                },

                                onNavigateToReports = {
                                    navController.navigate("admin_reports")
                                }
                            )
                        }


                        //25.08.2026
                        // Trafik Zabıta ana ekranı
                        composable("traffic_home") {

                            TrafficHomeScreen(
                                onNavigateToNewTrafficInspection = {
                                    navController.navigate("traffic_new")
                                },

                                onNavigateToTrafficRecords = {
                                    navController.navigate("traffic_records")
                                }
                            )
                        }


                        //26.08.2026
// Trafik Zabıta yeni işlem oluşturma ekranı
                        composable("traffic_new") { backStackEntry ->

                            val selectedLatitude =
                                backStackEntry
                                    .savedStateHandle
                                    .get<Double>("traffic_selected_latitude")

                            val selectedLongitude =
                                backStackEntry
                                    .savedStateHandle
                                    .get<Double>("traffic_selected_longitude")

                            NewTrafficInspectionScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },

                                onNavigateToLocationPicker = { latitude, longitude ->

                                    navController.navigate(
                                        "traffic_location_picker/${latitude ?: "null"}/${longitude ?: "null"}"
                                    )
                                },

                                selectedLatitude = selectedLatitude,
                                selectedLongitude = selectedLongitude
                            )
                        }


                        //26.08.2026
// Trafik Zabıta kayıtlarını listeleme ekranı
                        composable("traffic_records") {

                            TrafficRecordsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },

                                onNavigateToDetail = { trafficInspectionId ->

                                    //27.08.2026
                                    // seçilen trafik kaydının detay ekranına git
                                    navController.navigate(
                                        "traffic_detail/$trafficInspectionId"
                                    )
                                }
                            )
                        }

                        //27.08.2026
// Trafik Zabıta kayıt detay ekranı
                        composable(
                            route = "traffic_detail/{trafficInspectionId}",
                            arguments = listOf(
                                navArgument("trafficInspectionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { navBackStackEntry ->

                            val trafficInspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("trafficInspectionId")
                                    ?: 0

                            TrafficInspectionDetailScreen(
                                trafficInspectionId = trafficInspectionId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //26.08.2026
                        // Trafik Zabıta haritadan konum seçim ekranı
                        composable(
                            route = "traffic_location_picker/{latitude}/{longitude}",
                            arguments = listOf(

                                navArgument("latitude") {
                                    type = NavType.StringType
                                },

                                navArgument("longitude") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->

                            val latitude =
                                backStackEntry.arguments
                                    ?.getString("latitude")
                                    ?.toDoubleOrNull()

                            val longitude =
                                backStackEntry.arguments
                                    ?.getString("longitude")
                                    ?.toDoubleOrNull()

                            TrafficLocationPickerScreen(
                                initialLatitude = latitude,
                                initialLongitude = longitude,

                                onLocationSelected = { selectedLatitude, selectedLongitude ->

                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            "traffic_selected_latitude",
                                            selectedLatitude
                                        )

                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(
                                            "traffic_selected_longitude",
                                            selectedLongitude
                                        )

                                    navController.popBackStack()
                                },

                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //19.08.2026

                        //admin kategori yönetim ekranı
                        composable("admin_categories") {

                            AdminCategoryScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //24.08.2026
                        //süper admin denetim kriteri yönetim ekranı
                        composable("admin_criteria") {

                            AdminCriteriaScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //24.08.2026
                        // Süper Admin kullanıcı yönetim ekranı
                        composable("admin_users") {

                            AdminUserScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //25.08.2026
                        //süper admin rapor ekranı
                        composable("admin_reports") {

                            AdminReportsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },

                                // rapor kartından seçilen denetimin mevcut PDF ekranını aç
                                onNavigateToPdf = { inspectionId ->

                                    navController.navigate(
                                        "pdf_viewer/$inspectionId"
                                    )
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

                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                },

                                onNavigateToNewInspection = {

                                    // yeni denetim sayfasına geçiş
                                    navController.navigate("new_inspection")
                                },

                                // karta tıklanıldığında detay sayfasına git (30.07.2026)
                                onNavigateToDetail = { inspectionId ->

                                    navController.navigate(
                                        "inspection_detail/$inspectionId"
                                    )
                                }
                            )
                        }


                        //3. ekranımız yeni denetim ekranı
                        composable("new_inspection") {

                            NewInspectionScreen(
                                onNavigateBack = {

                                    // geri tuşuna basıldığında bir önceki sayfaya dön
                                    navController.popBackStack()
                                }
                            )
                        }


                        // 4.ekran detay ekranı 30.07.2026
                        composable(
                            route = "inspection_detail/{inspectionId}",
                            arguments = listOf(

                                navArgument("inspectionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { navBackStackEntry ->

                            // tıklanan kartın idsi?
                            val inspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("inspectionId")
                                    ?: 0

                            // Burada yeni oluşturacağımız Detay Sayfasını çağıracağız
                            // Hata vermemesi için şimdilik yorum satırında, birazdan bu dosyayı açacağız:

                            InspectionDetailScreen(
                                inspectionId = inspectionId,

                                onNavigateBack = {
                                    navController.popBackStack()
                                },

                                onNavigateToPdf = { selectedInspectionId ->

                                    navController.navigate(
                                        "pdf_viewer/$selectedInspectionId"
                                    )
                                },

                                onNavigateToBusinessHistory = { businessId, currentInspectionId ->

                                    navController.navigate(
                                        "business_history/$businessId/$currentInspectionId"
                                    )
                                }
                            )
                        }


                        //17.08.2026
                        // iki denetimi karşılaştırma ekranı
                        composable(
                            route = "inspection_comparison/{currentInspectionId}/{previousInspectionId}",
                            arguments = listOf(

                                navArgument("currentInspectionId") {
                                    type = NavType.IntType
                                },

                                navArgument("previousInspectionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { navBackStackEntry ->

                            val currentInspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("currentInspectionId")
                                    ?: 0

                            val previousInspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("previousInspectionId")
                                    ?: 0

                            InspectionComparisonScreen(
                                currentInspectionId = currentInspectionId,
                                previousInspectionId = previousInspectionId,

                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //14.08.2026 pdf raporunu uygulama içinde göstermek için ekran
                        composable(
                            route = "pdf_viewer/{inspectionId}",
                            arguments = listOf(

                                navArgument("inspectionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { navBackStackEntry ->

                            val inspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("inspectionId")
                                    ?: 0

                            PdfViewerScreen(
                                inspectionId = inspectionId,

                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        //14.08.2026
                        // seçilen işletmenin geçmiş denetimlerini göstermek için ekran
                        composable(
                            route = "business_history/{businessId}/{currentInspectionId}",
                            arguments = listOf(

                                navArgument("businessId") {
                                    type = NavType.IntType
                                },

                                navArgument("currentInspectionId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { navBackStackEntry ->

                            val businessId =
                                navBackStackEntry.arguments
                                    ?.getInt("businessId")
                                    ?: 0

                            val currentInspectionId =
                                navBackStackEntry.arguments
                                    ?.getInt("currentInspectionId")
                                    ?: 0

                            BusinessInspectionHistoryScreen(
                                businessId = businessId,
                                currentInspectionId = currentInspectionId,

                                // Geçmiş denetimler ekranından geri dön
                                onNavigateBack = {
                                    navController.popBackStack()
                                },

                                // Geçmiş denetim kartına tıklanınca eski denetimin detayını aç
                                onNavigateToInspectionDetail = { inspectionId ->

                                    navController.navigate(
                                        "inspection_detail/$inspectionId"
                                    )
                                },

                                // Mevcut ve geçmiş denetim ID'lerini karşılaştırma ekranına gönder
                                onNavigateToComparison = { currentId, previousId ->

                                    navController.navigate(
                                        "inspection_comparison/$currentId/$previousId"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}