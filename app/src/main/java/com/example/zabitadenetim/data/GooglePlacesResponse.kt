package com.example.zabitadenetim.data


import com.google.gson.annotations.SerializedName

// Google Places aramasından dönen tek bir işletme sonucunu temsil eder.
data class GooglePlaceResponse(

    // Google Maps üzerindeki benzersiz işletme kimliği.
    // Daha sonra yorumları çekmek ve işletmeyi kaydetmek için kullanacağız.
    @SerializedName("place_id")
    val placeId: String,

    // İşletmenin Google Maps üzerindeki adı.
    val name: String,

    // İşletmenin açık adresi.
    val address: String?,

    // İşletmenin Google Maps koordinatları.
    val latitude: Double?,
    val longitude: Double?
)