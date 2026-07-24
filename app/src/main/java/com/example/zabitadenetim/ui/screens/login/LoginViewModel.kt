package com.example.zabitadenetim.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.LoginRequest
import kotlinx.coroutines.launch
import com.example.zabitadenetim.data.TokenManager

class LoginViewModel : ViewModel() {

    // butona basılınca çalışacak olan yer,2
    fun login( context: Context, email: String, password: String) {

        // arayüz donmasın diye viewmodelscope başlatmak lazım
        viewModelScope.launch {
            try {
                // istek kısmı
                val response = ApiClient.apiService.login(email = email, password = password)

                // gelen tokeni cihazın gizli hafızasına kaydediyoruz

                val tokenManager = TokenManager(context)
                tokenManager.saveToken(response.access_token)

                // sunucudan dönen yanıt başarılıysa
                Log.d("LOGIN_TEST", "Token başarıyla alındı: ${response.access_token}")

            } catch (e: Exception){
                Log.e("LOGIN_TEST", "Hata Oluştu: ${e.message}")
            }
        }
    }

}