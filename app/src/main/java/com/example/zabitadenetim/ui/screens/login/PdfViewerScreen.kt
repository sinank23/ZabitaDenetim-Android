package com.example.zabitadenetim.ui.screens.login.inspection

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.zabitadenetim.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.foundation.horizontalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    inspectionId: Int,
    onNavigateBack: () -> Unit,
    isTrafficReport: Boolean = false
) {
    val context = LocalContext.current

    //14.08.2026
    // PDF indirme ve görüntüleme durumlarını takip etmek için state'ler
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var pdfRenderer by remember {
        mutableStateOf<PdfRenderer?>(null)
    }

    var parcelFileDescriptor by remember {
        mutableStateOf<ParcelFileDescriptor?>(null)
    }

    var currentPageIndex by remember {
        mutableIntStateOf(0)
    }

    var pageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var pageCount by remember {
        mutableIntStateOf(0)
    }

    //28.08.2026
    // PDF sayfasının yakınlaştırma seviyesini tut
    var zoomScale by remember {
        mutableStateOf(1f)
    }


    //14.08.2026
    // ekran açıldığında backend üzerinden ilgili denetimin PDF raporunu indir
    LaunchedEffect(inspectionId) {

        isLoading = true
        errorMessage = null

        try {

            val responseBody = withContext(Dispatchers.IO) {

                if (isTrafficReport) {

                    //28.08.2026
                    // trafik zabıta kaydının PDF raporunu backend üzerinden al
                    ApiClient.apiService.getTrafficInspectionPdf(
                        inspectionId
                    )

                } else {

                    // normal zabıta denetiminin mevcut PDF raporunu al
                    val response =
                        ApiClient.apiService.getInspectionPdf(
                            inspectionId
                        )

                    if (!response.isSuccessful) {
                        throw Exception(
                            "PDF raporu alınamadı. HTTP kodu: ${response.code()}"
                        )
                    }

                    response.body()
                        ?: throw Exception(
                            "PDF raporu boş geldi."
                        )
                }
            }

            // PDF dosyasını uygulamanın geçici cache klasörüne kaydet
            val pdfFile = File(
                context.cacheDir,

                if (isTrafficReport) {
                    "trafik_raporu_$inspectionId.pdf"
                } else {
                    "denetim_raporu_$inspectionId.pdf"
                }
            )

            withContext(Dispatchers.IO) {

                responseBody.byteStream().use { inputStream ->

                    pdfFile.outputStream().use { outputStream ->

                        inputStream.copyTo(outputStream)
                    }
                }
            }


            // indirilen PDF dosyasını Android PdfRenderer ile aç
            val fileDescriptor = withContext(Dispatchers.IO) {

                ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            }

            val renderer = PdfRenderer(fileDescriptor)

            parcelFileDescriptor = fileDescriptor
            pdfRenderer = renderer

            pageCount = renderer.pageCount
            currentPageIndex = 0

            isLoading = false

        } catch (e: Exception) {

            errorMessage =
                "PDF görüntülenirken hata oluştu: ${e.message}"

            isLoading = false
        }
    }


    //14.08.2026
    // seçilen PDF sayfasını Bitmap haline getirip ekranda göster
    LaunchedEffect(
        pdfRenderer,
        currentPageIndex
    ) {

        val renderer = pdfRenderer
            ?: return@LaunchedEffect

        if (
            currentPageIndex < 0 ||
            currentPageIndex >= renderer.pageCount
        ) {
            return@LaunchedEffect
        }

        try {

            val bitmap = withContext(Dispatchers.IO) {

                val page = renderer.openPage(
                    currentPageIndex
                )

                try {

                    val renderedBitmap =
                        Bitmap.createBitmap(
                            page.width,
                            page.height,
                            Bitmap.Config.ARGB_8888
                        )

                    renderedBitmap.eraseColor(
                        android.graphics.Color.WHITE
                    )

                    page.render(
                        renderedBitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    renderedBitmap

                } finally {

                    page.close()
                }
            }

            pageBitmap = bitmap

        } catch (e: Exception) {

            errorMessage =
                "PDF sayfası görüntülenemedi: ${e.message}"
        }
    }


    //14.08.2026
    // ekrandan çıkıldığında açık PDF kaynaklarını kapat
    DisposableEffect(Unit) {

        onDispose {

            pageBitmap?.recycle()

            pdfRenderer?.close()

            parcelFileDescriptor?.close()
        }
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        if (isTrafficReport) {
                            "Trafik PDF Raporu"
                        } else {
                            "Denetim PDF Raporu"
                        }
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onNavigateBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },

                actions = {

                    //28.08.2026
                    // PDF raporunu küçültmek için
                    IconButton(
                        onClick = {
                            if (zoomScale > 0.75f) {
                                zoomScale -= 0.25f
                            }
                        },
                        enabled = zoomScale > 0.75f
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "PDF'i küçült"
                        )
                    }

                    Text(
                        text = "${(zoomScale * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )

                    //28.08.2026
                    // PDF raporunu büyütmek için
                    IconButton(
                        onClick = {
                            if (zoomScale < 2.5f) {
                                zoomScale += 0.25f
                            }
                        },
                        enabled = zoomScale < 2.5f
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "PDF'i büyüt"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,

                        titleContentColor =
                            MaterialTheme.colorScheme.onPrimary,

                        navigationIconContentColor =
                            MaterialTheme.colorScheme.onPrimary
                    )
            )
        }

    ) { paddingValues ->


        when {

            isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text("PDF raporu hazırlanıyor...")
                    }
                }
            }


            errorMessage != null -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = errorMessage
                            ?: "Bilinmeyen bir hata oluştu.",

                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }


            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {


                    // PDF sayfasının gösterildiği alan
                    // PDF sayfasının gösterildiği alan
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {

                        val pdfWidth =
                            maxWidth * zoomScale

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(
                                    rememberScrollState()
                                )
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .padding(8.dp),

                            contentAlignment =
                                Alignment.TopCenter
                        ) {

                            pageBitmap?.let { bitmap ->

                                Image(
                                    bitmap =
                                        bitmap.asImageBitmap(),

                                    contentDescription =
                                        "PDF Sayfası ${currentPageIndex + 1}",

                                    modifier =
                                        Modifier.width(pdfWidth),

                                    contentScale =
                                        ContentScale.FillWidth
                                )
                            }
                        }
                    }


                    HorizontalDivider()


                    // PDF sayfaları arasında geçiş yapmak için alt kontrol alanı
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {


                        OutlinedButton(

                            onClick = {

                                if (currentPageIndex > 0) {

                                    pageBitmap?.recycle()
                                    pageBitmap = null

                                    currentPageIndex--
                                }
                            },

                            enabled =
                                currentPageIndex > 0

                        ) {

                            Text("Önceki")
                        }


                        Text(
                            text =
                                "${currentPageIndex + 1} / $pageCount"
                        )


                        OutlinedButton(

                            onClick = {

                                if (
                                    currentPageIndex <
                                    pageCount - 1
                                ) {

                                    pageBitmap?.recycle()
                                    pageBitmap = null

                                    currentPageIndex++
                                }
                            },

                            enabled =
                                currentPageIndex <
                                        pageCount - 1

                        ) {

                            Text("Sonraki")
                        }
                    }
                }
            }
        }
    }
}