package com.example.zabitadenetim.data

import android.content.Context
import android.content.SharedPreferences


class TokenManager(context: Context) {

    // gizli bir hafıza alanı
    private val prefs: SharedPreferences = context.getSharedPreferences("ZabitaPrefs", Context.MODE_PRIVATE)

    // aldığımız tokeni bu gizli hafızaya kaydedelim
    fun saveToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    // tokeni hafızadan okuma fonksiyonu
    // eğer token mevcut değilse null döner
    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN",null)

    }

    // çıkış yaoıldığında tokeni sil
    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").apply()
    }

}