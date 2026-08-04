package com.example.zabitadenetim.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class InspectionResponse(
    val id: Int,

    @SerializedName("businessName")
    val businessName: String,

    val address: String?,
    val answers: List<Boolean>,

    @SerializedName("inspector_notes")
    val notes: String?,

    @SerializedName("inspector_id")
    val inspectorId: Int?,

    @SerializedName("business_id")
    val businessId: Int?,

    val status: String?,

    @SerializedName("ai_summary")
    val aiSummary: String?,

    @SerializedName("inspection_date")
    val inspectionDate: String?,

    val latitude: Double?,
    val longitude: Double?
)

data class CompleteInspectionResponse(
    val message: String,
    @SerializedName("inspection_id") val inspectionId: Int,
    val score: Double,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("yes_answers") val yesAnswers: Int,
    @SerializedName("ai_report") val aiReport: String
)