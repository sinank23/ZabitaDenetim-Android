package com.example.zabitadenetim.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.presentation.TrafficInspectionViewModel

import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.zabitadenetim.ui.screens.login.inspection.createImageUri

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTrafficInspectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLocationPicker: (Double?, Double?) -> Unit,
    selectedLatitude: Double? = null,
    selectedLongitude: Double? = null,
    viewModel: TrafficInspectionViewModel = viewModel()
) {

    //26.08.2026
    // Yeni trafik işlem formundaki alanların durumlarını tut
    var violationType by remember {
        mutableStateOf("Hatalı Park")
    }

    var plate by remember {
        mutableStateOf("")
    }

    //26.08.2026
    //trafik kaydı oluşturulurken cihazırn mevcut GPS konumunu tut.

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var currentLongitude by remember {
        mutableStateOf<Double?>(null)
    }

    var vehicleType by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var actionTaken by remember {
        mutableStateOf("")
    }

    var violationMenuExpanded by remember {
        mutableStateOf(false)
    }

    //27.08.2026
    //trafik işlemine eklenecek fotoğrafları tut
    var selectedImageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    var cameraUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    //KAMERA İLE ÇEKİLEN FOTOĞRAFI TRAFİK FOTOĞRAF LİSTESİNE EKLE
    var cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {
            selectedImageUris =
                selectedImageUris + cameraUri
        }

    }

    // galeriden en fazla 5 fotoğraf seç
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 5
        )
    ) { uris ->

        if (uris.isNotEmpty()) {
            selectedImageUris = uris
        }

    }

    //26.08.2026
// Konum izni verildikten sonra cihazın son bilinen konumunu al
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            val isFineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val isCoarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isFineGranted || isCoarseGranted) {

                try {

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->

                            location?.let {
                                currentLatitude = it.latitude
                                currentLongitude = it.longitude

                                Log.d(
                                    "TrafikGPS",
                                    "Konum alındı: ${it.latitude}, ${it.longitude}"
                                )
                            }
                        }

                } catch (e: SecurityException) {

                    Log.e(
                        "TrafikGPS",
                        "Konum alınamadı: ${e.localizedMessage}"
                    )
                }
            }
        }
    )

    //26.08.2026
// Trafik işlem ekranı açıldığında konum iznini kontrol et
    LaunchedEffect(Unit) {

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //26.08.2026
    // Haritadan seçilen koordinatları al ve adres alanını otomatik doldur
    LaunchedEffect(selectedLatitude, selectedLongitude) {

        if (selectedLatitude != null && selectedLongitude != null) {

            currentLatitude = selectedLatitude
            currentLongitude = selectedLongitude

            try {

                val selectedAddress = withContext(Dispatchers.IO) {

                    val geocoder =
                        Geocoder(
                            context,
                            Locale.getDefault()
                        )

                    val addressList =
                        geocoder.getFromLocation(
                            selectedLatitude,
                            selectedLongitude,
                            1
                        )

                    addressList
                        ?.firstOrNull()
                        ?.getAddressLine(0)
                }

                if (!selectedAddress.isNullOrBlank()) {
                    address = selectedAddress
                }

                Log.d(
                    "TrafikHarita",
                    "Seçilen konum: $selectedLatitude, $selectedLongitude - Adres: $selectedAddress"
                )

            } catch (e: Exception) {

                Log.e(
                    "TrafikHarita",
                    "Adres bilgisi alınamadı: ${e.localizedMessage}",
                    e
                )
            }
        }
    }

    val violationTypes = listOf(
        "Hatalı Park",
        "Kaldırım / Yol İşgali",
        "Durak İşgali",
        "Araç Çekme Kaydı",
        "Toplu Taşıma / Servis Denetimi",
        "Trafik Levhası Tespiti",
        "Hafriyat / Malzeme Taşıma İhlali",
        "Diğer Trafik Tespiti"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Yeni Trafik İşlemi")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "İhlal Türü",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = {
                        violationMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(violationType)
                }

                DropdownMenu(
                    expanded = violationMenuExpanded,
                    onDismissRequest = {
                        violationMenuExpanded = false
                    }
                ) {

                    violationTypes.forEach { type ->

                        DropdownMenuItem(
                            text = {
                                Text(type)
                            },
                            onClick = {
                                violationType = type
                                violationMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = plate,
                onValueChange = {
                    plate = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Araç Plakası")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = vehicleType,
                onValueChange = {
                    vehicleType = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Araç Türü")
                },
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Adres")
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {

                    //26.08.2026
                    // Mevcut GPS konumunu başlangıç noktası olarak harita ekranına gönder
                    onNavigateToLocationPicker(
                        currentLatitude,
                        currentLongitude
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Haritadan Konum Seç")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Açıklama")
                },
                minLines = 3
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = actionTaken,
                onValueChange = {
                    actionTaken = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Yapılan İşlem")
                },
                minLines = 2
            )

            //27.08.2026
// Trafik işlemine fotoğraf ekleme alanı
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Trafik İşlem Fotoğrafları",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {

                        multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galeri")
                }

                Button(
                    onClick = {

                        val uri =
                            createImageUri(context)

                        if (uri != null) {
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kamera")
                }
            }

            if (selectedImageUris.isNotEmpty()) {

                Text(
                    text = "Toplam ${selectedImageUris.size} fotoğraf eklendi",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .horizontalScroll(
                            rememberScrollState()
                        ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    selectedImageUris.forEach { imageUri ->

                        Box(
                            modifier = Modifier.size(120.dp)
                        ) {

                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Seçilen trafik fotoğrafı",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = {

                                    selectedImageUris =
                                        selectedImageUris.filterNot {
                                            it == imageUri
                                        }
                                },
                                modifier = Modifier.align(
                                    Alignment.TopEnd
                                )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fotoğrafı kaldır"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {

                    //26.08.2026
                    // Formdaki trafik bilgilerini backend'e gönder
                    viewModel.createTrafficInspection(
                        violationType = violationType,
                        plate = plate.trim(),
                        vehicleType = vehicleType.trim().takeIf { it.isNotEmpty() },
                        address = address.trim().takeIf { it.isNotEmpty() },
                        latitude = currentLatitude,
                        longitude = currentLongitude,
                        description = description.trim().takeIf { it.isNotEmpty() },
                        actionTaken = actionTaken.trim().takeIf { it.isNotEmpty() },
                        onSuccess = { trafficInspectionId ->

                            //27.08.2026
                            // trafik kaydı oluşturulduktan sonra seçilen fotoğrafları yükle
                            selectedImageUris.forEach { imageUri ->

                                val photoPart =
                                    prepareTrafficPhotoPart(
                                        context,
                                        imageUri
                                    )

                                if (photoPart != null) {

                                    viewModel.uploadTrafficInspectionPhoto(
                                        trafficInspectionId = trafficInspectionId,
                                        photoPart = photoPart
                                    )
                                }
                            }

                            // kayıt oluşturulduktan sonra trafik ana ekranına geri dön
                            onNavigateBack()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = plate.isNotBlank()
            ) {
                Text("Trafik Kaydını Oluştur")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

//27.08.2026
// seçilen trafik fotoğrafını Retrofit multipart formatına dönüştür
fun prepareTrafficPhotoPart(
    context: android.content.Context,
    fileUri: Uri
): MultipartBody.Part? {

    return try {

        val inputStream =
            context.contentResolver.openInputStream(fileUri)

        val bytes =
            inputStream?.readBytes()

        inputStream?.close()

        if (bytes != null) {

            val fileName =
                "traffic_photo_${System.currentTimeMillis()}.jpg"

            val requestFile =
                bytes.toRequestBody(
                    "image/jpeg".toMediaTypeOrNull()
                )

            MultipartBody.Part.createFormData(
                "file",
                fileName,
                requestFile
            )

        } else {

            null
        }

    } catch (e: Exception) {

        Log.e(
            "TrafikFoto",
            "Fotoğraf dönüştürme hatası: ${e.localizedMessage}"
        )

        null
    }
}