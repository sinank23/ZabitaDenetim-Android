package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.InspectionCriterionResponse
import com.example.zabitadenetim.data.InspectionResponse
import com.google.android.gms.common.api.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminReportsViewModel : ViewModel() {

    //25.08.2026
    //süper admin ekranında gösterilecek denetimlerin tutulması için

    private val _inspections =
        MutableStateFlow<List<InspectionResponse>>(emptyList())

    val inspections: StateFlow<List<InspectionResponse>> =
        _inspections.asStateFlow()

    //bekleme durumunu tut

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // hata mesajını tut
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    // backendden tüm denetimleri getir
    fun fetchInspections() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response =
                    ApiClient.apiService.getInspections()

                _inspections.value = response

                Log.d(
                    "AdminRapor",
                    "${response.size} denetim rapor ekranı için yüklendi."
                )

            } catch (e: Exception) {
                _inspections.value = emptyList()
                    "Denetimler alınamadı: ${e.localizedMessage}"

                Log.e(
                    "AdminRapor",
                    "Denetim listesi alınırken hata oluştu",
                    e
                )


            } finally {
                _isLoading.value = false

            }
        }
    }
}