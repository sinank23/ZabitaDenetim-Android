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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.zabitadenetim.data.ApiClient
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewInspectionScreen(onNavigateBack: () -> Unit) {

    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // zabıta notu modülü
    var inspectorNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // dosyaları okuyabilmek için uygulama bağlamını alalım (28.07.2026)
    val context = LocalContext.current

    //lokasyon gps sensörü için client
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isFineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val isCoarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

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

    // seçilen fotoğrafların urlelerini tutalım.
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }


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

    val inspectionQuestions = listOf(
        "İşyeri açma ve çalıştırma ruhsatır var mı?",
        "Yangın tüpü var mı?",
        "Hijyen belgesi var mı?",
        "Ecza dolabı var mı?",
        "İşyerinin detaylı genel temizliği/tertip düzeni uygun mu?",
        "Periyodik ilaçlama yapılıyor mu",
        "Fiyat tarifesi var mı",
        "Personelin maske, eldiven, bone temiz kıyafet ve kişisel bakımı uygun mu?",
        "Sigara içilmez levhası var mı?",
        "Kapaklı çöp kovası var mı?",
        "Ürünlerin son kullanma tarihi satışa uygun mu",
        "Fiyat tarifesi ile kasa fiyatları uyumlu mu?",
        "Masalarda fiyat tarifesi var mı?",
        "Bay/Bayan lavabo var mı?",
        "Hazırlama, pişirme ve bulaşık yıkama bölümleri ayrı şekilde mi",
        "Baca filtre sistemi var mı? (Fırını olanlar için)",
        "Genel havalandırma yapılıyor mu?",
        "Tuvaletlerde kağıt havlu ve sabun  var mı?",
        "Gıdalar uygun koşullarda muhafaza ediliyor mu?",
        "Soğuk zinciri gerektiren ürünler uygun şekilde muhafaza ediliyor mu?",
        "Diğer"
    )

    val questionStates = remember {
        mutableStateListOf<Boolean>().apply {
            addAll(List(inspectionQuestions.size) { false })
        }
    }

    Scaffold(
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
                onValueChange = { businessName = it },
                label = { Text("İşletme Adı") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Açık Adres") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Denetim Kriterleri",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            inspectionQuestions.forEachIndexed { index, question ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = questionStates[index],
                        onCheckedChange = { isChecked ->
                            questionStates[index] = isChecked
                        }
                    )
                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // 30.07.2026 eklendi zabıta notu modülü
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inspectorNotes,
                onValueChange = {inspectorNotes = it},
                label = { Text("Zabıta Gözelm ve Notları")},
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
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galeri")

                }

                Button(
                    onClick = {
                        // kamerayı açmadan boş bir dosya yolu oluştur.
                        val uri = createImageUri(context)
                        if(uri != null) {
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
            }

            // fotoğraf seçme arayüzünün sonu

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (businessName.isNotBlank() && address.isNotBlank()) {
                        val requestData = InspectionRequest(
                            businessName = businessName,
                            address = address,
                            answers = questionStates.toList(),
                            inspectorNotes = inspectorNotes,
                            // 0959 - Eksik olan notlar eklendi
                            latitude = currentLatitude, // 0959 - State'ten gelen güncel enlem eklendi,
                            // 0959 - State'ten gelen güncel enlem eklendi
                            longitude = currentLongitude
                        )

                        coroutineScope.launch {
                            try {
                                isAiLoading = true

                                // 1. Aşama: Metinleri Kaydet
                                val response = ApiClient.apiService.createInspection(requestData)
                                Log.d("DenetimKayit", "Kayıt Başarılı!")

                                // 2. Aşama: Fotoğrafları Gönder ve Analiz Et
                                if (selectedImageUris.isNotEmpty()) {
                                    val multipartParts = selectedImageUris.mapNotNull { uri ->
                                        prepareFilePart(context, uri)
                                    }

                                    if (multipartParts.isNotEmpty()) {
                                        multipartParts.forEach { photoPart ->
                                            ApiClient.apiService.uploadInspectionPhoto(
                                                response.id,
                                                photoPart
                                            )
                                        }

                                        Log.d("DenetimKayit", "Fotoğraflar başarıyla gönderildi")
                                    }
                                }

                                // 3. Aşama: Nihai Raporu İste (YENİ GÜNCELLEME)
                                val aiResponse = ApiClient.apiService.completeInspection(response.id)

                                if (aiResponse.isSuccessful && aiResponse.body() != null) {
                                    aiReportText = aiResponse.body()!!.aiReport
                                    showAiReportDialog = true // Başarılıysa raporu göster
                                } else {
                                    // Sunucudan 404, 500 gibi bir hata dönerse ana ekrana atma, hatayı ekrana bas!
                                    aiReportText = "Sunucu Hatası (Kod: ${aiResponse.code()})\nDetay: ${aiResponse.errorBody()?.string()}"
                                    showAiReportDialog = true
                                }

                            } catch (e: retrofit2.HttpException) {
                                // HTTP bağlantı kopması olursa ekranda göster
                                aiReportText = "Bağlantı (HTTP) Hatası:\n${e.response()?.errorBody()?.string()}"
                                showAiReportDialog = true
                            } catch (e: Exception) {
                                // Timeout (Zaman Aşımı) veya çökme olursa ekranda göster
                                aiReportText = "İşlem Hatası (Muhtemelen Zaman Aşımı):\n${e.localizedMessage}"
                                showAiReportDialog = true
                            } finally {
                                isAiLoading = false
                            }
                        }
                    } else {
                        Log.e("DenetimKayit", "HATA: İşletme adı veya adres boş bırakılamaz!")
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

                } else{
                    Text("Denetimi Kaydet")
                }



                // YENİ EKLENDİ (AI): Yükleme esnasında butonun yazısını değişti
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // YENİ EKLENDİ (AI): Yapay Zeka Raporunu Gösteren Açılır Pencere
        if (showAiReportDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { /* Boşluğa tıklayınca kapanmasın, butona basması zorunlu olsun */ },
                title = {
                    Text(
                        text = "Yapay Zeka Değerlendirme Raporu",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
    val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timestamp}_"
    val storageDir: File = context.cacheDir
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

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
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val bytes = inputStream?.readBytes() // Dosyayı baytlara çevir
        inputStream?.close()

        if (bytes != null) {
            // Sunucuya gidecek rastgele bir dosya adı oluştur
            val fileName = "photo_${System.currentTimeMillis()}.jpg"

            // Baytları RequestBody formatına çevir
            val requestFile =
                bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

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