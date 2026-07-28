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