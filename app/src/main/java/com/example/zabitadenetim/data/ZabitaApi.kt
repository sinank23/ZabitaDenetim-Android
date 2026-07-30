package com.example.zabitadenetim.data

import com.example.zabitadenetim.ui.model.InspectionRequest
import kotlinx.serialization.descriptors.PrimitiveKind
// YENİ EKLENDİ: Multipart ve ResponseBody importları
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.DELETE


interface ZabitaApi {
    // tüm denetimleri almak için yazdığımız endpoint

    @GET("inspections/")
    suspend fun getInspections(): List<InspectionResponse>   // retrofit sunucudan gelen json veriyi otomatik buraya düşürüyor

    // denetimi yapay zekayla tamamlamak için.

    // yeni denetim verisini json formatında sunucuya yollamak için
    @POST("inspections/")
    suspend fun createInspection(@Body request: InspectionRequest): InspectionResponse

    // fotoğraf yükleme endpointi (28.07.2026)
    @Multipart
    @POST("inspections/{inspection_id}/photos/")
    suspend fun uploadInspectionPhotos(
        @Path("inspection_id") inspectionId: Int,
        @Part files: List<MultipartBody.Part>  // birden fazla foto gönderileceği için liste yapısı şeklinde tuttuk

    ): ResponseBody // sunucdan dönen JSON yanıtını şimdilik esnek bir Body olarak alıyoruz



    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("inspections/{inspection_id}/complete/")
    suspend fun completeInspection(
        @Path("inspection_id") inspectionId: Int
    ): Response<CompleteInspectionResponse>


    @DELETE("inspections/{inspection_id}")
    suspend fun deleteInspection(
        @Path("inspection_id") inspectionId: Int
    ): retrofit2.Response<Unit>

}