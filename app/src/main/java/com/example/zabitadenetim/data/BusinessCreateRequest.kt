package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

data class BusinessCreateRequest(

    // işletmenin adı
    val name: String,

    // kotlinin anlayacağı modele dönüştürmek için serializedname
    //01.09.2026
// Google'dan otomatik gelen veya zabıtanın elle girdiği faaliyet konusu
    @SerializedName("activity_type")
    val activityType: String?,

    //işletme adresi
    val address: String?,

    val latitude: Double?,
    val longitude: Double?,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    @SerializedName("contact_info")
    val contactInfo: String? = null,

    @SerializedName("google_place_id")
    val googlePlaceId: String?
)
