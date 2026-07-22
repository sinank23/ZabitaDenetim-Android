package com.example.zabitadenetim.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Android Emülatörü, bilgisayarın localhost'una 10.0.2.2 adresi üzerinden erişir.
    // Eğer fiziksel bir telefon bağlayacaksan, buraya bilgisayarının yerel IP adresini yazacağız (bu sonraki iş).

    private const val BASE_URL = "http://10.0.2.2:8000"

    // retrofit nesnesi oluşturuluyor

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // json kotlin dönüşümü
            .build()  // retrofit nesnesini oluştur
    }

    val apiService: ZabitaApi by lazy {
        retrofit.create(ZabitaApi::class.java)
    }

    // bir üstteki blokta zabıtaapi içine yazdığımız get post fonksiyonlarını
    // gerçek çalışan api fonksiyonlarına dönüştürüyor
}