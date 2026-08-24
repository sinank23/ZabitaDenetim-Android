package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

//07.08.2026 tarihinde verileri dinamik hale getirmek için oluşturuldu.

data class InspectionCriterionResponse(
    val id: Int,
    @SerializedName("category_id")
    val categoryId: Int?,   // null değerleri de dinle
    @SerializedName("question_text")
    val questionText: String

)

//24.08.2026
// yeni denetim eklemek ve güncellemek için gönderileceke veri
data class InspectionCriterionRequest(
    @SerializedName("category_id")
    val categoryId: Int?,

    @SerializedName("question_text")
    val questionText: String
)