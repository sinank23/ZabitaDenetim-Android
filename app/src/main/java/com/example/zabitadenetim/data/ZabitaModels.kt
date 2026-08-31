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

    @SerializedName("inspector_name")
    val inspectorName: String?,

    @SerializedName("business_id")
    val businessId: Int?,

    @SerializedName("category_name")
    val categoryName: String?,

    @SerializedName("owner_name")
    val ownerName: String?,

    @SerializedName("contact_info")
    val contactInfo: String?,

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

// süper admin kategroi işlemleri
data class BusinessCategoryRequest(
    val name: String
)

//24.08.2026
// Süper Admin kullanıcı yönetiminde backend'den gelen kullanıcı bilgileri
data class UserResponse(
    val id: Int,

    @SerializedName("full_name")
    val fullName: String,

    val email: String,
    val role: String,

    @SerializedName("created_at")
    val createdAt: String
)

//24.08.2026
// Süper Admin tarafından yeni kullanıcı oluşturmak için gönderilecek veri
data class UserCreateRequest(
    @SerializedName("full_name")
    val fullName: String,

    val email: String,
    val role: String,
    val password: String
)

//24.08.2026
// Süper Admin tarafından mevcut kullanıcıyı güncellemek için gönderilecek veri
data class UserUpdateRequest(
    @SerializedName("full_name")
    val fullName: String,

    val email: String,
    val role: String,
    val password: String? = null
)

//26.08.2026
// yeni trafik işlemi oluşturulurken backende gönderilecek veri
data class TrafficInspectionCreateRequest(
    @SerializedName("violation_type")
    val violationType: String,

    val plate: String,

    @SerializedName("vehicle_type")
    val vehicleType: String?,

    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?,

    @SerializedName("action_taken")
    val actionTaken: String?
)

//26.08.2026
//backenddern androide dönen trafik işlem kaydı

data class TrafficInspectionResponse(
    val id: Int,

    @SerializedName("violation_type")
    val violationType: String,

    val plate: String,

    @SerializedName("vehicle_type")
    val vehicleType: String?,

    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?,

    @SerializedName("action_taken")
    val actionTaken: String?,

    val status: String?,

    @SerializedName("inspector_id")
    val inspectorId: Int,

    @SerializedName("created_at")
    val createdAt: String
)

//31.08.2026
// fotoğraf bilgisini tutmak için

data class TrafficInspectionPhotoResponse(
    @SerializedName("photo_id")
    val photoId: Int,

    @SerializedName("traffic_inspection_id")
    val trafficInspectionId: Int,

    @SerializedName("photo_path")
    val photoPath: String,

    @SerializedName("photo_url")
    val photoUrl: String,

    @SerializedName("created_at")
    val createdAt: String
)

//31.08.2026
// trafik işlem durum güncelleme sonucunu tutmak için
data class TrafficStatusUpdateResponse(
    val message: String,

    @SerializedName("traffic_inspection_id")
    val trafficInspectionId: Int,

    val status: String
)