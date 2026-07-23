package com.example.zabitadenetim.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zabitadenetim.data.ApiClient
import com.example.zabitadenetim.data.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // butona basılınca çalışacak olan yer,2
    fun login(email: String, password: String) {

        // arayüz donmasın diye viewmodelscope başlatmak lazım
        viewModelScope.launch {
            try {
                // istek kısmı
                val response = ApiClient.apiService.login(email = email, password = password)

                // sunucudan dönen yanıt başarılıysa
                Log.d("LOGIN_TEST", "Token başarıyla alındı: ${response.access_token}")

            } catch (e: Exception){
                Log.e("LOGIN_TEST", "Hata Oluştu: ${e.message}")
            }
        }
    }

}