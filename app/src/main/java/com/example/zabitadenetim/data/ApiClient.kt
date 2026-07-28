package com.example.zabitadenetim.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // TokenManager'ı hafızada tutacağımız değişken
    private var tokenManager: TokenManager? = null

    // Uygulama açıldığında bir kez çağırıp TokenManager'ı başlatacağız
    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Eğer init() başarıyla çalıştıysa token'ı alıyoruz
        val token = tokenManager?.getToken()

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ZabitaApi by lazy {
        retrofit.create(ZabitaApi::class.java)
    }
}