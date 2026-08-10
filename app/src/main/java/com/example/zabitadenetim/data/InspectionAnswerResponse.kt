package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

data class InspectionAnswerResponse(
    @SerializedName("criterion_id")
    val criterionId: Int,

    @SerializedName("question_text")
    val questionText: String,

    @SerializedName("is_yes")
    val isYes: Boolean
)