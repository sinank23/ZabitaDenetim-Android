package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.TrafficInspectionCreateRequest
import com.example.zabitadenetim.data.TrafficInspectionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        onSuccess: () -> Unit = {}
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

                ApiClient.apiService.createTrafficInspection(request)

                // yeni kayıt oluşturulduktan sonra listeyi yenile
                fetchTrafficInspections()

                Log.d(
                    "TrafikZabita",
                    "Yeni trafik işlemi oluşturuldu."
                )

                onSuccess()

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
}