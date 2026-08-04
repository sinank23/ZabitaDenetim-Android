package com.example.zabitadenetim.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.ApiClient.apiService
import com.example.zabitadenetim.data.InspectionResponse
import com.example.zabitadenetim.data.ReviewResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

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
                // Hatanın gerçek sebebini konsola (Logcat) kırmızı renkli olarak yazdırıyoruz:
                Log.e("DenetimHata", "Veri çekerken patladık: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 03.08.2026
    // google yorumlarını tutacağımz listeler (steteflow)
    private val _reviews = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<ReviewResponse>> = _reviews.asStateFlow()

    // yükleniyor durumunu tutalım
    private val _isLoadingReviews = MutableStateFlow(false)
    val isLoadingReviews: StateFlow<Boolean> = _isLoadingReviews.asStateFlow()

    // apiden dükkan yorumlarını çekelim
    fun fetchBusinessReviews(businessId: Int) {
        viewModelScope.launch {
            _isLoadingReviews.value = true
            try {
                val response = apiService.getBusinessReviews(businessId)
                _reviews.value = response

            } catch (e:Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingReviews.value = false

            }
        }
    }
}
