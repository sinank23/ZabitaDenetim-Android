package com.example.zabitadenetim.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.InspectionResponse
import kotlinx.coroutines.launch

class InspectionViewModel : ViewModel() {

    // Ekranda gösterilecek denetim listesi
    private val _inspections = mutableStateOf<List<InspectionResponse>>(emptyList())
    val inspections: State<List<InspectionResponse>> = _inspections

    // Yükleniyor animasyonu için durum (State)
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Hata mesajlarını tutacak durum
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // apidemden verileri çeken asenkron fonksiyon
    fun fetchInspections() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // backende istek
                val response = ApiClient.apiService.getInspections()
                _inspections.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Sunucuya bağlanılamadı: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}