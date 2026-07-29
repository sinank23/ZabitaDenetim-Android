package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName

// fastapiden gelecek json verilerinin kotlindeki hali

data class InspectionResponse(
    val id: Int,
    @SerializedName("businessName") val businessName: String,
    @SerializedName("inspector_id") val inspectorId: Int,
    val notes: String,
    val status: String,
    @SerializedName("ai_summary") val aiSummary: String?,
    @SerializedName("inspection_date") val inspectionDate: String
)

// yeni ekleyeceğimiz data class (29.07.2026)
data class CompleteInspectionResponse(
    val message: String,
    @SerializedName("inspection_id") val inspectionId: Int,
    val score: Double,
    // serializedname kullanmamızın sebebi retrofit
    // verileri çekerken isim değişikliğini algılarması için
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("yes_answers") val yesAnswers: Int,
    @SerializedName("ai_report") val aiReport: String
)