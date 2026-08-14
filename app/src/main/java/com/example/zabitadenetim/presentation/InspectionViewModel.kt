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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.InspectionAnswerResponse

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

    //14.08.2026
    //seçilen işletmeye ait geçiş denetimleri tut
    private val _businessInspections =
        MutableStateFlow<List<InspectionResponse>>(emptyList())

    val businessInspections: StateFlow<List<InspectionResponse>> =
        _businessInspections.asStateFlow()

    //loading durumu
    private val _isLoadingBusinessInspections =
        MutableStateFlow(false)

    val isLoadingBusinessInspections: StateFlow<Boolean> =
        _isLoadingBusinessInspections.asStateFlow()

    // geçmiş denetimleri backendden çek
    fun fetchBusinessInspections(businessId: Int) {
        viewModelScope.launch {
            _isLoadingBusinessInspections.value = true

            try {
                val response =
                    apiService.getBusinessInspections(businessId)
                _businessInspections.value = response

                Log.d(
                    "IsletmeDenetimGecmisi",
                    "${response.size} adet denetim geçmişi yüklendi."
                )

            } catch (e: Exception) {
                _businessInspections.value = emptyList()

                Log.e(
                    "IsletmeDenetimGecmisi",
                    "Geçmiş denetimler alınırken hata oluştu: ${e.localizedMessage}",
                    e
                )

            } finally {
                _isLoadingBusinessInspections.value = false
            }
        }
    }

    //10.08.2026: denetime ait soru ve cevaplarını tut
    private val _inspectionAnswers =
        MutableStateFlow<List<InspectionAnswerResponse>>(emptyList())

    val inspectionAnswers: StateFlow<List<InspectionAnswerResponse>> =
        _inspectionAnswers.asStateFlow()

    // denetime ait cevapları fastapiden çekelim
    fun fetchInspectionAnswers(inspectionId: Int) {
        viewModelScope.launch {
            try {
                val response =
                    apiService.getInspectionAnswers(inspectionId)

                _inspectionAnswers.value = response

                Log.d(
                    "DenetimCevaplari",
                    "${response.size} denetim cevabı yüklendi."
                )

            } catch (e: Exception) {
                _inspectionAnswers.value = emptyList()

                Log.e(
                    "DenetimCevaplari",
                    "Denetim cevapları alınırken hata oluştu: ${e.localizedMessage}",
                    e
                )
            }
        }
    }

    // 05.08.2026
    // yapay zeka raporu yeniden oluşturulurken bekleme durumunu tut
    private val _isRetryingAi = MutableStateFlow(false)
    val isRetryingAi: StateFlow<Boolean> = _isRetryingAi.asStateFlow()

    // kullanıcı mesajı
    private val _retryAiMessage = MutableStateFlow<String?>(null)
    val retryAiMessage: StateFlow<String?> = _retryAiMessage.asStateFlow()

    // yapay zeka raporunu tekrar oluşturt
    fun retryAiReport(inspectionId: Int) {
        viewModelScope.launch {
            _isRetryingAi.value = true
            _retryAiMessage.value = null

            try {
                val response = apiService.completeInspection(inspectionId)

                if(response.isSuccessful) {
                    _retryAiMessage.value = "Yapay zeka raporu başarıyla oluşturuldu."

                    fetchInspections()
                } else {
                    _retryAiMessage.value = "Rapor oluşturulamadı. Sunucu kodu: ${response.code()}"

                }
            } catch (e: Exception) {
                _retryAiMessage.value = "Yapay zeka raporu yeninden oluşturulamadı: ${e.localizedMessage}"

                Log.e(
                    "AiYenidenDeneme",
                    "Yapay zeka raporu yeniden oluşturulurken hata oluştu.",
                    e
                )
            } finally {
                _isRetryingAi.value = false
            }
        }
    }
}
