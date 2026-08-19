package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.BusinessCategoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.zabitadenetim.data.BusinessCategoryRequest

class AdminCategoryViewModel : ViewModel() {
    //19.08.2026
    // süper admin listesinde gösterilecek kategori listesi
    private val _categories =
        MutableStateFlow<List<BusinessCategoryResponse>>(emptyList())

    val categories =
        _categories.asStateFlow()

    //kategori listesi yüklenirken bekleme durumu
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // hata oluşursa hata mesajını tut
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    // backendden ttüm kategorileri getir
    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response =
                    ApiClient.apiService.getBusinessCategories()

                _categories.value = response

                Log.d(
                    "AdminKategori",
                    "${response.size} kategori yüklendi"

                )
            } catch (e: Exception) {
                _categories.value = emptyList()
                _errorMessage.value =
                    "Kategoriler alınamadı: ${e.localizedMessage}"

                Log.e("AdminKategori", "Kategori listesi alınırken hata oluştu.", e)

            } finally {
                _isLoading.value = false
            }
        }
    }

    //19.08.2026
    // adminin kategori eklemesi için
    fun createCategory(
        categoryName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                //backende gönderilecek kategori isteğini hazırla
                val request = BusinessCategoryRequest(
                    name = categoryName
                )

                // yeni kategoryi önce backende kaydet
                ApiClient.apiService.createBusinessCategory(request)

                // ekleme başarılı olursa listeyi tekrar getir yani güncelle
                fetchCategories()

                onSuccess()

                Log.d(
                    "AdminKategori",
                    "Kategori başarıyla eklendi: $categoryName"
                )

            } catch (e: Exception) {
                _errorMessage.value =
                    "Kategori eklenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKategori",
                    "Kategori eklenirken hata oluştu.",
                    e
                )
            } finally {
                _isLoading.value = false

            }
        }
    }

    //admin kategori adını güncellesin diye
    fun updateCategory(
        categoryId: Int,
        newCategoryName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = BusinessCategoryRequest(
                    name = newCategoryName
                )

                // backendde güncelle
                ApiClient.apiService.updateBusinessCategory(
                    categoryId = categoryId,
                    request = request
                )

                // güncelledikten sonra listeyi yeniden getir.
                fetchCategories()

                onSuccess()

                Log.d(
                    "AdminKategori",
                    "Kategori güncellendi ID: $categoryId "
                )
            } catch (e: Exception) {
                _errorMessage.value =
                    "Kategori güncellenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKategori",
                    "Kategori güncellenirken bir hata oluştu.",
                    e
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // adminin silme işlemleri
    fun deleteCategory(
        categoryId: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                ApiClient.apiService.deleteBusinessCategory(
                    categoryId = categoryId
                )

                fetchCategories()

                onSuccess()

                Log.d(
                    "AdminKategori",
                    "Kategori silindi. ID: $categoryId"
                )

            } catch (e: Exception) {
                _errorMessage.value =
                    "Kategori silinemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKategori",
                    "Kategori silinirken bir hata oluştu",
                    e
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

}