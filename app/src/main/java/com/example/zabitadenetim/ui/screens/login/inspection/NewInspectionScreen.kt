package com.example.zabitadenetim.ui.screens.login.inspection

// YENİ EKLENDİ (UX): Animasyon boyutlandırması için size importu
// YENİ EKLENDİ (UX): Yükleme animasyonu bileşeni

// 0959 - GPS işlemleri için gerekli importlar
import android.Manifest
import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.BusinessCreateRequest
import com.example.zabitadenetim.data.GooglePlaceResponse
import com.example.zabitadenetim.ui.model.InspectionRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import coil.compose.AsyncImage
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.layout.ContentScale
import com.example.zabitadenetim.data.BusinessCategoryResponse
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.zabitadenetim.data.InspectionCriterionResponse
import com.example.zabitadenetim.ui.model.InspectionAnswerRequest

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewInspectionScreen(onNavigateBack: () -> Unit) {

    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // 07.08.2026 eklendi işletme bilgileri ve işletme sahibi bilgileri
    var ownerName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }

    // google aramasından dönen işletmeleri tut
    var googlePlaces by remember {
        mutableStateOf<List<GooglePlaceResponse>>(emptyList())
    }

    // google sonuçlarını seç
    var selectedGooglePlace by remember {
        mutableStateOf<GooglePlaceResponse?>(null)
    }

    var isGooglePlacesMenuExpanded by remember {
        mutableStateOf(false)
    }

    var isGooglePlacesLoading by remember {
        mutableStateOf(false)
    }

    var businessCategories by remember {
        mutableStateOf<List<BusinessCategoryResponse>>(emptyList())

    }

    var inspectionCriteria by remember {
        mutableStateOf<List<InspectionCriterionResponse>>(emptyList())
    }

    //01.09.2026
// faaliyet konusu Google'dan otomatik gelir, gelmezse zabıta elle girer
    var activityType by remember {
        mutableStateOf("")
    }

    // zabıta notu modülü
    var inspectorNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // dosyaları okuyabilmek için uygulama bağlamını alalım (28.07.2026)
    val context = LocalContext.current

    //lokasyon gps sensörü için client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isFineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val isCoarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isFineGranted || isCoarseGranted) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            currentLatitude = it.latitude
                            currentLongitude = it.longitude
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("GPS", "Konum alınamadı: ${e.localizedMessage}")
                }
            }
        }
    )

    // 0959 - Sayfa açıldığında otomatik olarak konum izni isteğini tetikler
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //07.08.2026 kategoriler için
    val questionStates = remember {
        mutableStateListOf<Boolean>()
    }

    LaunchedEffect(Unit) {
        try {
            businessCategories =
                ApiClient.apiService.getBusinessCategories()

            Log.d(
                "IsletmeKategori",
                "${businessCategories.size} kategori yüklendi"
            )
        } catch (e: Exception) {
            Log.e(
                "IsletmeKategori",
                "Kategoriler alınırken hata oluştu: ${e.localizedMessage}",
                e
            )
        }

        try {
            inspectionCriteria =
                ApiClient.apiService.getCommonInspectionCriteria()

            questionStates.clear()
            questionStates.addAll(
                List(inspectionCriteria.size) { false }
            )

            Log.d(
                "OrtakDenetimKriterleri",
                "${inspectionCriteria.size} ortak kriter yüklendi"
            )
        } catch (e: Exception) {
            Log.e(
                "OrtakDenetimKriterleri",
                "Ortak kriterler alınırken hata oluştu: ${e.localizedMessage}",
                e
            )
        }
    }

    // seçilen fotoğrafların urlelerini tutalım.
    var selectedImageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    // kameraya erişim için launcher ve state
    var cameraUri by remember { mutableStateOf(Uri.EMPTY) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // eğer çekim başarılıysa o anki cameraurisini lsteye ekle
            selectedImageUris = selectedImageUris + cameraUri
        }
    }

    // galeriyi açmak ve çoklu seçim yapack olan launcher
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris = uris
        }
    }

    // YENİ EKLENDİ (AI): Yapay Zeka Raporu için durum yöneticileri
    var showAiReportDialog by remember { mutableStateOf(false) }
    var aiReportText by remember { mutableStateOf("") }
    var isAiLoading by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Yeni Denetim") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri Dön"
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = businessName,
                onValueChange = {
                    businessName = it
                    selectedGooglePlace = null
                },
                label = { Text("İşletme Adı") },
                modifier = Modifier.fillMaxWidth()
            )

            // 04.08.2026
// Yazılan işletme adını telefonun mevcut konumuna göre Google Maps'te arar.
            Button(
                onClick = {
                    Log.d("GoogleIsletmeArama", "Arama butonuna basıldı")

                    // Telefonun o anda tuttuğu GPS değerlerini alıyoruz.
                    val latitude = currentLatitude
                    val longitude = currentLongitude

                    Log.d(
                        "GoogleIsletmeArama",
                        "Konum değerleri -> latitude: $latitude, longitude: $longitude"
                    )

                    // Arama yapabilmek için işletme adı ve GPS bilgileri hazır olmalı.
                    // Google Maps araması için yalnızca işletme adının yazılması yeterlidir.
                    if (businessName.isNotBlank()) {
                        coroutineScope.launch {
                            isGooglePlacesLoading = true

                            try {
                                // İşletme adı ve telefonun konumu backend'e gönderiliyor.
                                if (latitude == null || longitude == null) {
                                    snackbarHostState.showSnackbar(
                                        message = "Konum bilgisi henüz alınamadı. Lütfen birkaç saniye sonra tekrar deneyin."
                                    )
                                    return@launch
                                }

                                googlePlaces =
                                    ApiClient.apiService.searchGooglePlaces(
                                        query = businessName,
                                        latitude = latitude,
                                        longitude = longitude
                                    )

                                Log.d(
                                    "GoogleIsletmeArama",
                                    "${googlePlaces.size} işletme bulundu."
                                )

                                // 06.08.2026
                                if (googlePlaces.isNotEmpty()) {
                                    isGooglePlacesMenuExpanded = true
                                } else {
                                    isGooglePlacesMenuExpanded = false

                                    snackbarHostState.showSnackbar(
                                        message = "Bu isimle yakınlarda bir işletme bulunamadı."
                                    )
                                }


                            } catch (e: Exception) {
                                googlePlaces = emptyList()
                                isGooglePlacesMenuExpanded = false

                                Log.e(
                                    "GoogleIsletmeArama",
                                    "İşletme aranırken hata oluştu: ${e.localizedMessage}",
                                    e
                                )

                                snackbarHostState.showSnackbar(
                                    message = "İşletme aranırken bağlantı hatası oluştu. Lütfen tekrar deneyin."
                                )
                            }finally {
                                isGooglePlacesLoading = false
                            }
                        }
                    } else {
                        Log.e(
                            "GoogleIsletmeArama",
                            "İşletme adı boş bırakıldı."
                        )

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Lütfen aramak istediğiniz işletmenin adını yazın."
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !isGooglePlacesLoading
            ) {
                if (isGooglePlacesLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google'da aranıyor...")
                } else {
                    Text("Google Maps'te Ara")
                }
            }

            DropdownMenu(
                expanded = isGooglePlacesMenuExpanded,
                onDismissRequest = {
                    isGooglePlacesMenuExpanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                googlePlaces.forEach { place ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = place.name,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = place.address
                                        ?: "Adres bilgisi bulunamadı",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        onClick = {
                            selectedGooglePlace = place
                            businessName = place.name
                            address = place.address ?: ""

                            //01.09.2026
// Google faaliyet konusu döndürdüyse otomatik doldur
                            activityType = place.activityType ?: ""

                            isGooglePlacesMenuExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Açık Adres") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = activityType,
                onValueChange = { activityType = it },
                label = { Text("Faaliyet Konusu") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text ("İşletme Sahibi Adı Soyadı")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))



            //02.09.2026
// telefon numarası yalnızca rakam kabul eder ve 11 haneyle sınırlandırılır


            OutlinedTextField(
                value = contactInfo,


                onValueChange = { newValue ->

                    val digits = newValue
                        .filter { it.isDigit() }
                        .take(11)

                    contactInfo = when {
                        digits.length <= 4 ->
                            digits

                        digits.length <= 7 ->
                            "${digits.substring(0, 4)} ${digits.substring(4)}"

                        digits.length <= 9 ->
                            "${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7)}"

                        else ->
                            "${digits.substring(0, 4)} ${digits.substring(4, 7)} ${digits.substring(7, 9)} ${digits.substring(9)}"
                    }


                },
                label = { Text("Telefon Numarası") },
                placeholder = { Text("0532 123 45 67") },
                modifier = Modifier.fillMaxWidth(),
                //02.09.2026
// telefon numarasının ilk iki rakamını gösterip kalan rakamları yıldızla gizlemek için
                visualTransformation = object : VisualTransformation {

                    override fun filter(text: AnnotatedString): TransformedText {

                        var digitCount = 0

                        val maskedText = buildString {
                            text.text.forEach { char ->

                                if (char.isDigit()) {
                                    digitCount++

                                    if (digitCount <= 2) {
                                        append(char)
                                    } else {
                                        append('*')
                                    }
                                } else {
                                    // telefon formatındaki boşlukları aynen koru
                                    append(char)
                                }
                            }
                        }

                        return TransformedText(
                            AnnotatedString(maskedText),
                            OffsetMapping.Identity
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Denetim Kriterleri",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))


            inspectionCriteria.forEachIndexed { index, criterion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = questionStates[index],
                        onCheckedChange = { isChecked ->
                            questionStates[index] = isChecked
                        }
                    )

                    Text(
                        text = if (questionStates[index]) "Evet" else "Hayır",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(40.dp)
                    )

                    Text(
                        text = criterion.questionText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // 30.07.2026 eklendi zabıta notu modülü
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inspectorNotes,
                onValueChange = { inspectorNotes = it },
                label = { Text("Zabıta Gözelm ve Notları") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // foto seçim arayüzü
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Denetim Fotoğrafları",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // (28.07.2026) galeri ve kamera butonların
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
                        // kamerayı açmadan boş bir dosya yolu oluştur.
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
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                //06.08.2026
                // fotoğrafları ekranda görüp kaydrıma modülü ekleyelim

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedImageUris.forEach { imageUri ->
                        Box(
                            modifier = Modifier.size(120.dp)
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Seçilen denetim fotoğrafı",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = {
                                    selectedImageUris =
                                        selectedImageUris.filterNot { it == imageUri }
                                },
                                modifier = Modifier.align(Alignment.TopEnd)
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

            // fotoğraf seçme arayüzünün sonu

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (
                        businessName.isNotBlank() &&
                        address.isNotBlank() &&
                        selectedGooglePlace != null &&
                        activityType.isNotBlank() &&
                        selectedImageUris.isNotEmpty()
                    ) {
                        coroutineScope.launch {
                            try {
                                isAiLoading = true

                                // Google Maps'ten bir işletme seçildiyse önce backend'e kaydediyoruz.
// İşletme daha önce kayıtlıysa backend mevcut kaydı geri döndürür.
                                val googlePlace = selectedGooglePlace!!

                                //02.09.2026
// backend'e gönderilecek telefon numarasını kontrol etmek için
                                Log.d(
                                    "TelefonKontrol",
                                    "Gönderilecek telefon: ${contactInfo.filter { it.isDigit() }}"
                                )

                                val createdBusiness =
                                    ApiClient.apiService.createBusiness(
                                        BusinessCreateRequest(
                                            name = googlePlace.name,
                                            activityType = activityType.ifBlank { null },
                                            address = googlePlace.address,
                                            latitude = googlePlace.latitude,
                                            longitude = googlePlace.longitude,
                                            ownerName = ownerName.ifBlank { null },
                                            //02.09.2026
// ekranda maskeli görünen telefon numarasını veritabanına yalnızca rakam olarak gönder
                                            contactInfo = contactInfo
                                                .filter { it.isDigit() }
                                                .ifBlank { null },
                                            googlePlaceId = googlePlace.placeId
                                        )
                                    )

                                Log.d(
                                    "DenetimKayit",
                                    "Google işletmesi kaydedildi. İşletme ID: ${createdBusiness.id}"
                                )

                                val businessId = createdBusiness.id

                                // işletmenin google yorumlarını denetimden önce güncelle
                                try {
                                    ApiClient.apiService
                                        .syncBusinessReviews(businessId)

                                    Log.d(
                                        "DenetimKayit",
                                        "Google yorumları eşitlendi. İşletme ID: $businessId"
                                    )

                                } catch (e: Exception) {
                                    Log.e(
                                        "DenetimKayit",
                                        "Google yorumları eşitlenemedi: ${e.localizedMessage}",
                                        e
                                    )
                                }

                                val answerRecords = inspectionCriteria.mapIndexed { index, criterion ->
                                    InspectionAnswerRequest(
                                        criterion_id = criterion.id,
                                        is_yes = questionStates[index]
                                    )
                                }

// İşletme kaydı tamamlandıktan sonra denetim isteğini oluşturuyoruz.
                                val requestData = InspectionRequest(
                                    businessName = businessName,
                                    address = address,
                                    answers = questionStates.toList(),
                                    answer_records = answerRecords,
                                    inspector_notes = inspectorNotes,
                                    business_id = businessId,
                                    // 0959 - Eksik olan notlar eklendi
                                    latitude = googlePlace.latitude, // 0959 - State'ten gelen güncel enlem eklendi,
                                    // 0959 - State'ten gelen güncel enlem eklendi
                                    longitude = googlePlace.longitude
                                )

                                // 1. Aşama: Metinleri Kaydet
                                val response =
                                    ApiClient.apiService
                                        .createInspection(requestData)

                                Log.d(
                                    "DenetimKayit",
                                    "Kayıt Başarılı!"
                                )

                                // 2. Aşama: Fotoğrafları Gönder ve Analiz Et
                                if (selectedImageUris.isNotEmpty()) {
                                    val multipartParts =
                                        selectedImageUris.mapNotNull { uri ->
                                            prepareFilePart(context, uri)
                                        }

                                    if (multipartParts.isNotEmpty()) {
                                        multipartParts.forEach { photoPart ->
                                            ApiClient.apiService
                                                .uploadInspectionPhoto(
                                                    response.id,
                                                    photoPart
                                                )
                                        }

                                        Log.d(
                                            "DenetimKayit",
                                            "Fotoğraflar başarıyla gönderildi"
                                        )
                                    }
                                }

                                // 3. Aşama: Nihai Raporu İste (YENİ GÜNCELLEME)
                                val aiResponse =
                                    ApiClient.apiService
                                        .completeInspection(response.id)

                                if (
                                    aiResponse.isSuccessful &&
                                    aiResponse.body() != null
                                ) {
                                    aiReportText =
                                        aiResponse.body()!!.aiReport

                                    showAiReportDialog =
                                        true // Başarılıysa raporu göster
                                } else {
                                    // Sunucudan 404, 500 gibi bir hata dönerse ana ekrana atma, hatayı ekrana bas!
                                    aiReportText =
                                        "Sunucu Hatası (Kod: ${aiResponse.code()})\nDetay: ${aiResponse.errorBody()?.string()}"

                                    showAiReportDialog = true
                                }

                            } catch (e: retrofit2.HttpException) {
                                // HTTP bağlantı kopması olursa ekranda göster
                                aiReportText =
                                    "Bağlantı (HTTP) Hatası:\n${e.response()?.errorBody()?.string()}"

                                showAiReportDialog = true

                            } catch (e: Exception) {
                                // Timeout (Zaman Aşımı) veya çökme olursa ekranda göster
                                aiReportText =
                                    "İşlem Hatası (Muhtemelen Zaman Aşımı):\n${e.localizedMessage}"

                                showAiReportDialog = true

                            } finally {
                                isAiLoading = false
                            }
                        }
                    } else {
                        val errorMessage =
                            when {
                                selectedGooglePlace == null ||
                                        businessName.isBlank() ||
                                        address.isBlank() -> {
                                    "Lütfen önce Google Maps sonuçlarından bir işletme seçin."
                                }

                                activityType.isBlank() -> {
                                    "Lütfen işletmenin faaliyet konusunu girin."
                                }

                                selectedImageUris.isEmpty() -> {
                                    "Lütfen denetim için en az bir fotoğraf seçin."
                                }

                                else -> {
                                    "Denetim kaydı oluşturulamadı."
                                }
                            }

                        Log.e(
                            "DenetimKayit",
                            "HATA: $errorMessage"
                        )

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAiLoading
            ) {
                // yükleme sırasında dönen bi ikon ekliyoruz 29.07.2026

                if (isAiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Yapay zeka analiz ediyor...")
                } else {
                    Text("Denetimi Kaydet")
                }

                // YENİ EKLENDİ (AI): Yükleme esnasında butonun yazısını değişti
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // YENİ EKLENDİ (AI): Yapay Zeka Raporunu Gösteren Açılır Pencere
        if (showAiReportDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    /* Boşluğa tıklayınca kapanmasın, butona basması zorunlu olsun */
                },
                title = {
                    Text(
                        text = "Yapay Zeka Değerlendirme Raporu",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(
                            rememberScrollState()
                        )
                    ) {
                        Text(text = aiReportText)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAiReportDialog = false
                            onNavigateBack() // Rapor okunduktan sonra ana ekrana yönlendir
                        }
                    ) {
                        Text("Tamam ve Kapat")
                    }
                }
            )
        }
    }
}

// ---------------------------------------------------------
// YENİ EKLENDİ: YARDIMCI FONKSİYON (Uri -> MultipartBody.Part Dönüştürücü)
// Bu fonksiyonu dosyanın en altına, NewInspectionScreen'in dışına ekliyoruz.
// ---------------------------------------------------------

// 1. Kameranın çekeceği fotoğraf için geçici dosya (URI) oluşturan fonksiyon
fun createImageUri(context: Context): Uri? {
    val timestamp: String =
        SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

    val imageFileName = "JPEG_${timestamp}_"
    val storageDir: File = context.cacheDir

    val imageFile =
        File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}

fun prepareFilePart(
    context: Context,
    fileUri: Uri
): MultipartBody.Part? {
    return try {
        // Dosyayı okumak için bir kanal açıyoruz
        val inputStream =
            context.contentResolver.openInputStream(fileUri)

        val bytes =
            inputStream?.readBytes() // Dosyayı baytlara çevir

        inputStream?.close()

        if (bytes != null) {
            // Sunucuya gidecek rastgele bir dosya adı oluştur
            val fileName =
                "photo_${System.currentTimeMillis()}.jpg"

            // Baytları RequestBody formatına çevir
            val requestFile =
                bytes.toRequestBody(
                    "image/jpeg".toMediaTypeOrNull()
                )

            // Retrofit'in backend'de eşleştireceği "files" anahtarı (FastAPI'deki parametre ismi ile AYNI olmalı)
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
            "DenetimKayit",
            "Fotoğraf dönüştürme hatası: ${e.localizedMessage}"
        )

        null
    }
}