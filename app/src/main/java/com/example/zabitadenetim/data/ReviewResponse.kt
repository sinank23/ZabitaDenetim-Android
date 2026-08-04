package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

data class ReviewResponse (
    @SerializedName("id")
    val id: Int,

    @SerializedName("author_name")
    val authorName: String, // kotlin standartlarına uygun isimlendirme

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("text")
    val text: String?, // kullanıcı sadece puan vermiş olabilir,
                       // bu yüzdfen nullable yaptık?

    @SerializedName("time")
    val time: Long
)