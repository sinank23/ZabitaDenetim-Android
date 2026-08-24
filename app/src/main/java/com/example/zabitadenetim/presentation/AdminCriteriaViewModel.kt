package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.InspectionCriterionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.zabitadenetim.data.InspectionCriterionRequest

class AdminCriteriaViewModel : ViewModel() {

    //24.08.2026
    // Süper Admin ekranında gösterilecek tüm denetim kriterlerini tut
    private val _criteria =
        MutableStateFlow<List<InspectionCriterionResponse>>(emptyList())

    val criteria: StateFlow<List<InspectionCriterionResponse>> =
        _criteria.asStateFlow()


    // kriter listesi yüklenirken bekleme durumunu tut
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // kriter işlemlerinde oluşabilecek hata mesajını tut
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()


    // backend üzerinden tüm denetim kriterlerini getir
    fun fetchCriteria() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                val response =
                    ApiClient.apiService.getAllInspectionCriteriaForAdmin()

                _criteria.value = response

                Log.d(
                    "AdminKriter",
                    "${response.size} denetim kriteri yüklendi."
                )

            } catch (e: Exception) {

                _criteria.value = emptyList()

                _errorMessage.value =
                    "Denetim kriterleri alınamadı: ${e.localizedMessage}"

                Log.e(
                    "AdminKriter",
                    "Kriter listesi alınırken hata oluştu.",
                    e
                )

            } finally {

                _isLoading.value = false
            }
        }
    }

    //süper admin yeni denetim oluşturma fonksiyonu
    fun createCriterion(
        categoryId: Int?,
        questionText: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {
            _errorMessage.value = null

            try {
                val request = InspectionCriterionRequest(
                    categoryId = categoryId,
                    questionText = questionText
                )

                ApiClient.apiService.createInspectionCriterion(request)

                // listeyi güncelle
                fetchCriteria()

                Log.d(
                    "AdminKriter",
                    "Yeni denetim kriteri oluşturuldu."
                )

                onSuccess()
            } catch (e: Exception) {

                _errorMessage.value =
                    "Denetim kriteri eklenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKriter",
                    "Kriter oluşturulurken hata oluştu.",
                    e
                )


            }
        }
    }

    //süper admin denetim kriteri güncelle
    fun updateCriterion(
        criterionId: Int,
        categoryId: Int?,
        questionText: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                val request = InspectionCriterionRequest(
                    categoryId = categoryId,
                    questionText = questionText
                )

                ApiClient.apiService.updateInspectionCriterion(
                    criterionId = criterionId,
                    request = request
                )

                //kriter güncelledikten sonra listeyi yenile
                fetchCriteria()

                Log.d(
                    "AdminKriter",
                    "Denetim kriteri güncellendi. ID:$criterionId "
                )

                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value =
                    "Denetim kriteri güncellenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKriter",
                    "Kriter güncellenirken hata oluştu.",
                    e
                )
            }
        }
    }

    //24.08.2026
// Süper Admin tarafından denetim kriterini sil
    fun deleteCriterion(
        criterionId: Int,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                val response =
                    ApiClient.apiService.deleteInspectionCriterion(criterionId)

                if (response.isSuccessful) {

                    // kriter silindikten sonra listeyi yenile
                    fetchCriteria()

                    Log.d(
                        "AdminKriter",
                        "Denetim kriteri silindi. ID: $criterionId"
                    )

                    onSuccess()

                } else {

                    _errorMessage.value =
                        "Denetim kriteri silinemedi. HTTP ${response.code()}"

                    Log.e(
                        "AdminKriter",
                        "Kriter silme başarısız. HTTP ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                _errorMessage.value =
                    "Denetim kriteri silinemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKriter",
                    "Kriter silinirken hata oluştu.",
                    e
                )
            }
        }
    }
}