package com.example.zabitadenetim.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.UserCreateRequest
import com.example.zabitadenetim.data.UserResponse
import com.example.zabitadenetim.data.UserUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminUserViewModel : ViewModel() {

    //24.08.2026
    // Süper Admin ekranında gösterilecek tüm kullanıcıları tut
    private val _users =
        MutableStateFlow<List<UserResponse>>(emptyList())

    val users: StateFlow<List<UserResponse>> =
        _users.asStateFlow()


    // kullanıcı listesi yüklenirken bekleme durumunu tut
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // kullanıcı işlemlerinde oluşabilecek hata mesajını tut
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()


    // backend üzerinden tüm kullanıcıları getir
    fun fetchUsers() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                val response =
                    ApiClient.apiService.getAllUsersForAdmin()

                _users.value = response

                Log.d(
                    "AdminKullanici",
                    "${response.size} kullanıcı yüklendi."
                )

            } catch (e: Exception) {

                _users.value = emptyList()

                _errorMessage.value =
                    "Kullanıcılar alınamadı: ${e.localizedMessage}"

                Log.e(
                    "AdminKullanici",
                    "Kullanıcı listesi alınırken hata oluştu.",
                    e
                )

            } finally {

                _isLoading.value = false
            }
        }
    }


    // Süper Admin tarafından yeni kullanıcı oluştur
    fun createUser(
        fullName: String,
        email: String,
        role: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                val request = UserCreateRequest(
                    fullName = fullName,
                    email = email,
                    role = role,
                    password = password
                )

                ApiClient.apiService.createUserForAdmin(request)

                // yeni kullanıcı eklendikten sonra listeyi yenile
                fetchUsers()

                Log.d(
                    "AdminKullanici",
                    "Yeni kullanıcı oluşturuldu."
                )

                onSuccess()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Kullanıcı eklenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKullanici",
                    "Kullanıcı oluşturulurken hata oluştu.",
                    e
                )
            }
        }
    }


    // Süper Admin tarafından mevcut kullanıcıyı güncelle
    fun updateUser(
        userId: Int,
        fullName: String,
        email: String,
        role: String,
        password: String?,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                val request = UserUpdateRequest(
                    fullName = fullName,
                    email = email,
                    role = role,
                    password = password
                )

                ApiClient.apiService.updateUserForAdmin(
                    userId = userId,
                    request = request
                )

                // kullanıcı güncellendikten sonra listeyi yenile
                fetchUsers()

                Log.d(
                    "AdminKullanici",
                    "Kullanıcı güncellendi. ID: $userId"
                )

                onSuccess()

            } catch (e: Exception) {

                _errorMessage.value =
                    "Kullanıcı güncellenemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKullanici",
                    "Kullanıcı güncellenirken hata oluştu.",
                    e
                )
            }
        }
    }


    // Süper Admin tarafından kullanıcı sil
    fun deleteUser(
        userId: Int,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                val response =
                    ApiClient.apiService.deleteUserForAdmin(userId)

                if (response.isSuccessful) {

                    // kullanıcı silindikten sonra listeyi yenile
                    fetchUsers()

                    Log.d(
                        "AdminKullanici",
                        "Kullanıcı silindi. ID: $userId"
                    )

                    onSuccess()

                } else {

                    _errorMessage.value =
                        "Kullanıcı silinemedi. HTTP ${response.code()}"

                    Log.e(
                        "AdminKullanici",
                        "Kullanıcı silme başarısız. HTTP ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                _errorMessage.value =
                    "Kullanıcı silinemedi: ${e.localizedMessage}"

                Log.e(
                    "AdminKullanici",
                    "Kullanıcı silinirken hata oluştu.",
                    e
                )
            }
        }
    }
}