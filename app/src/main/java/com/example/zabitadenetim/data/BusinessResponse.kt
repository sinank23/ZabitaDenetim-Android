package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

// 04.08.2026 oluşturuldu

data class BusinessResponse(
    val id: Int,
    val name: String,

    @SerializedName("category_id")
    val categoryId: Int,

    val address: String?,
    val latitude: Double?,
    val longitude: Double?,

    @SerializedName("owner_name")
    val ownerName: String?,

    @SerializedName("contact_info")
    val contactInfo: String?,

    @SerializedName("created_at")
    val createdAt: String?
)