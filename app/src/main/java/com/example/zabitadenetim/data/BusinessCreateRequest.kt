package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

data class BusinessCreateRequest(

    // işletmenin adı
    val name: String,

    // kotlinin anlayacağı modele dönüştürmek için serializedname
    @SerializedName("category_id")
    val categoryId: Int,

    //işletme adresi
    val address: String?,

    val latitude: Double?,
    val longitude: Double?,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    val contactInfo: String? = null,

    @SerializedName("google_place_id")
    val googlePlaceId: String?
)
