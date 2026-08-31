package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.TrafficInspectionCreateRequest
import com.example.zabitadenetim.data.TrafficInspectionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

import com.example.zabitadenetim.data.TrafficInspectionPhotoResponse

class TrafficInspectionViewModel : ViewModel() {

    //26.08.2026
    // Trafik Zabıta tarafından oluşturulan kayıtları tut
    private val _trafficInspections =
        MutableStateFlow<List<TrafficInspectionResponse>>(emptyList())

    val trafficInspections: StateFlow<List<TrafficInspectionResponse>> =
        _trafficInspections.asStateFlow()


    // trafik kayıtları yüklenirken bekleme durumunu tut
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // trafik işlemlerinde oluşabilecek hata mesajını tut
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    //31.08.2026
// seçilen trafik işlemine ait fotoğrafları tutmak için
    private val _trafficPhotos =
        MutableStateFlow<List<TrafficInspectionPhotoResponse>>(
            emptyList()
        )

    val trafficPhotos =
        _trafficPhotos.asStateFlow()


    // backend üzerinden tüm trafik kayıtlarını getir
    fun fetchTrafficInspections() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                val response =
                    ApiClient.apiService.getTrafficInspections()

                _trafficInspections.value = response

                Log.d(
                    "TrafikZabita",
                    "${response.size} trafik kaydı yüklendi."
                )

            } catch (e: Exception) {

                _trafficInspections.value = emptyList()

                _errorMessage.value =
                    "Trafik kayıtları alınamadı: ${e.localizedMessage}"

                Log.e(
                    "TrafikZabita",
                    "Trafik kayıtları alınırken hata oluştu.",
                    e
                )

            } finally {

                _isLoading.value = false
            }
        }
    }

    //31.08.2026
// süper admin trafik işlem kaydının durumunu güncellemek için
    fun updateTrafficInspectionStatus(
        trafficInspectionId: Int,
        newStatus: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            try {

                ApiClient.apiService.updateTrafficInspectionStatus(
                    trafficInspectionId = trafficInspectionId,
                    newStatus = newStatus
                )

                Log.d(
                    "TrafikDurum",
                    "Trafik işlem durumu başarıyla güncellendi."
                )

                // durum değiştikten sonra listeyi tekrar getir
                fetchTrafficInspections()

                onSuccess()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Trafik işlem durumu güncellenemedi: ${e.localizedMessage}"

                Log.e(
                    "TrafikDurum",
                    "Trafik işlem durumu güncellenirken hata oluştu.",
                    e
                )
            }
        }
    }

    //31.08.2026
// süper admin trafik işlem kaydını silmek için
    fun deleteTrafficInspection(
        trafficInspectionId: Int,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            try {

                ApiClient.apiService.deleteTrafficInspection(
                    trafficInspectionId
                )

                Log.d(
                    "TrafikSil",
                    "Trafik işlem kaydı başarıyla silindi."
                )

                fetchTrafficInspections()

                onSuccess()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Trafik işlem kaydı silinemedi: ${e.localizedMessage}"

                Log.e(
                    "TrafikSil",
                    "Trafik işlem kaydı silinirken hata oluştu.",
                    e
                )
            }
        }
    }

    //31.08.2026
    //seçilen trafik işlemine ait fotoları backendden çekmek için
    fun fetchTrafficInspectionPhotos(
        trafficInspectionId: Int
    ) {
        viewModelScope.launch {

            try {

                val photos =
                    ApiClient.apiService.getTrafficInspectionPhotos(
                        trafficInspectionId
                    )

                _trafficPhotos.value = photos

                Log.d(
                    "TrafikFoto",
                    "${photos.size} trafik fotoğrafı getirildi."
                )
            } catch (e: Exception) {
                _trafficPhotos.value =
                    emptyList()

                Log.e(
                    "TrafikFoto",
                    "Trafik fotoğrafları alınırken hata oluştu.",
                    e
                )
            }
        }
    }


    // yeni trafik işlemi oluştur
    fun createTrafficInspection(
        violationType: String,
        plate: String,
        vehicleType: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        description: String?,
        actionTaken: String?,
        onSuccess: (Int) -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                val request = TrafficInspectionCreateRequest(
                    violationType = violationType,
                    plate = plate,
                    vehicleType = vehicleType,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    description = description,
                    actionTaken = actionTaken
                )

                val createdTrafficInspection =
                    ApiClient.apiService.createTrafficInspection(request)

                // yeni kayıt oluşturulduktan sonra listeyi yenile
                fetchTrafficInspections()

                Log.d(
                    "TrafikZabita",
                    "Yeni trafik işlemi oluşturuldu."
                )

                onSuccess(createdTrafficInspection.id)

            } catch (e: Exception) {

                _errorMessage.value =
                    "Trafik işlemi oluşturulamadı: ${e.localizedMessage}"

                Log.e(
                    "TrafikZabita",
                    "Trafik işlemi oluşturulurken hata oluştu.",
                    e
                )
            }
        }
    }

    //27.08.2026
    //trafik işlemm kaydına fotoğraf yüklemek için
    fun uploadTrafficInspectionPhoto(
        trafficInspectionId: Int,
        photoPart: MultipartBody.Part,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {
            try {
                ApiClient.apiService.uploadTrafficInspectionPhoto(
                    trafficInspectionId = trafficInspectionId,
                    file = photoPart
                )

                Log.d(
                    "TrafikFoto",
                    "Trafik fotoğrafı başarıyla yüklendi."
                )
                onSuccess()

            } catch (e: Exception) {
                _errorMessage.value =
                    "Trafik fotoğrafı yüklenemedi: ${e.localizedMessage}"

                Log.e(
                    "TrafikFoto",
                    "Trafik fotoğrafı yüklenirken hata oluştu.",
                    e
                )
            }
        }
    }

    //27.08.2026
// trafik işlem kaydının PDF raporunu backend üzerinden almak için
    fun getTrafficInspectionPdf(
        trafficInspectionId: Int,
        onSuccess: (ResponseBody) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val pdfResponse =
                    ApiClient.apiService.getTrafficInspectionPdf(
                        trafficInspectionId
                    )

                Log.d(
                    "TrafikPDF",
                    "Trafik PDF raporu başarıyla alındı."
                )

                onSuccess(pdfResponse)

            } catch (e: Exception) {

                _errorMessage.value =
                    "Trafik PDF raporu alınamadı: ${e.localizedMessage}"

                Log.e(
                    "TrafikPDF",
                    "Trafik PDF raporu alınırken hata oluştu.",
                    e
                )
            }
        }
    }
}