package com.example.zabitadenetim.ui.screens.login

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficLocationPickerScreen(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationSelected: (Double, Double) -> Unit,
    onNavigateBack: () -> Unit
) {

    //26.08.2026
    // Harita açıldığında kullanılacak başlangıç konumu
    val initialPosition = LatLng(
        initialLatitude ?: 37.0662,
        initialLongitude ?: 37.3833
    )

    //26.08.2026
    // Kullanıcının haritada seçtiği konumu tut
    var selectedLatitude = remember {
        initialPosition.latitude
    }

    var selectedLongitude = remember {
        initialPosition.longitude
    }

    //26.08.2026
    // Compose içinde klasik Google Maps MapView kullan

    var selectedMarker: Marker? = remember {
        null
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Haritadan Konum Seç")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onNavigateBack
                    ) {
                        Text("Geri")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                factory = { context ->

                    MapView(context).apply {

                        onCreate(Bundle())
                        onStart()
                        onResume()

                        getMapAsync { googleMap ->

                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    initialPosition,
                                    16f
                                )
                            )

                            selectedMarker =
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(initialPosition)
                                        .title("Seçilen Konum")
                                )

                            //26.08.2026
                            // Kullanıcı haritaya bastığında marker'ı seçilen noktaya taşı
                            googleMap.setOnMapClickListener { latLng ->

                                selectedLatitude =
                                    latLng.latitude

                                selectedLongitude =
                                    latLng.longitude

                                selectedMarker?.remove()

                                selectedMarker =
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title("Seçilen Konum")
                                    )
                            }
                        }
                    }
                }
            )

            Button(
                onClick = {

                    //26.08.2026
                    // Haritada seçilen koordinatları trafik formuna geri gönder
                    onLocationSelected(
                        selectedLatitude,
                        selectedLongitude
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Bu Konumu Kullan")
            }
        }
    }
}