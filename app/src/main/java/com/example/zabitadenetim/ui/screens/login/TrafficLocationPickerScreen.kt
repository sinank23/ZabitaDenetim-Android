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

import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //26.08.2026
    // Kullanıcının haritada seçtiği konumu tut
    var selectedLatitude by remember {
        mutableStateOf(initialPosition.latitude)
    }

    var selectedLongitude by remember {
        mutableStateOf(initialPosition.longitude)
    }
    //26.08.2026
    // Compose içinde klasik Google Maps MapView kullan

    var selectedMarker: Marker? = remember {
        null
    }

    //27.08.2026
    //harita üzerinde mahalle cadde şehir aramak için
    var searchText by remember {
        mutableStateOf("")
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var searchMessage by remember {
        mutableStateOf<String?>(null)
    }

    var googleMapInstance: GoogleMap? by remember {
        mutableStateOf(null)
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

            //27.08.2026
// Kullanıcı mahalle, cadde, şehir veya açık adres arayabilir
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    searchMessage = null
                },
                label = {
                    Text("Mahalle, cadde, şehir veya adres ara")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp
                    ),
                singleLine = true
            )

            Button(
                onClick = {

                    if (searchText.isBlank()) {
                        searchMessage = "Lütfen aranacak bir adres yazın."
                        return@Button
                    }

                    coroutineScope.launch {

                        isSearching = true
                        searchMessage = null

                        try {

                            val searchedAddress =
                                withContext(Dispatchers.IO) {

                                    val geocoder = Geocoder(
                                        context,
                                        Locale.getDefault()
                                    )

                                    geocoder
                                        .getFromLocationName(
                                            searchText,
                                            1
                                        )
                                        ?.firstOrNull()
                                }

                            if (searchedAddress != null) {

                                val searchedPosition = LatLng(
                                    searchedAddress.latitude,
                                    searchedAddress.longitude
                                )

                                selectedLatitude = searchedAddress.latitude
                                selectedLongitude = searchedAddress.longitude

                                selectedMarker?.remove()

                                selectedMarker =
                                    googleMapInstance?.addMarker(
                                        MarkerOptions()
                                            .position(searchedPosition)
                                            .title("Seçilen Konum")
                                    )

                                googleMapInstance?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        searchedPosition,
                                        16f
                                    )
                                )

                                searchMessage = null

                            } else {

                                searchMessage =
                                    "Aradığınız konum bulunamadı."
                            }

                        } catch (e: Exception) {

                            searchMessage =
                                "Adres aranırken hata oluştu."

                        } finally {

                            isSearching = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp
                    ),
                enabled = !isSearching
            ) {

                if (isSearching) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Aranıyor...")

                } else {

                    Text("Haritada Ara")
                }
            }

            searchMessage?.let { message ->

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 6.dp
                    )
                )
            }

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
                            googleMapInstance = googleMap

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