package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

data class BusinessCategoryResponse(
    val id: Int,
    val name: String,
    @SerializedName("created_at")
    val createdAt: String?
)