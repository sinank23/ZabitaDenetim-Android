package com.example.zabitadenetim.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.LoginRequest
import com.example.zabitadenetim.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    //17.08.2026
    // giriş işlemi sırasında yüklenme durumunu takip et
    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    //17.08.2026
    // giriş sırasında oluşan hata mesajını ekranda göstermek için tut
    private val _loginError =
        MutableStateFlow<String?>(null)

    val loginError: StateFlow<String?> =
        _loginError.asStateFlow()


    // butona basılınca çalışacak olan yer,2
    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: (String) -> Unit
    ) {

        // arayüz donmasın diye viewmodelscope başlatmak lazım
        viewModelScope.launch {

            // giriş isteği başlarken yüklenme durumunu aç ve eski hatayı temizle
            _isLoading.value = true
            _loginError.value = null

            try {

                // istek kısmı
                val response =
                    ApiClient.apiService.login(
                        email = email,
                        password = password
                    )

                // gelen tokeni cihazın gizli hafızasına kaydediyoruz
                val tokenManager = TokenManager(context)

                tokenManager.saveToken(
                    response.access_token
                )

                // sunucudan dönen yanıt başarılıysa
                Log.d(
                    "LOGIN_TEST",
                    "Token başarıyla alındı."
                )

                // token başarılı şekilde kaydedildiyse ana ekrana geç
                onSuccess(response.role) // giriş başarılıysa kullanıcı rolünü ekrana gönder

            } catch (e: Exception) {

                // yanlış e-posta veya şifre durumunda kullanıcıya hata göster
                _loginError.value =
                    "E-posta veya şifre hatalı."

                Log.e(
                    "LOGIN_TEST",
                    "Hata Oluştu: ${e.message}"
                )

            } finally {

                // giriş işlemi tamamlandığında yüklenme durumunu kapat
                _isLoading.value = false
            }
        }
    }
}