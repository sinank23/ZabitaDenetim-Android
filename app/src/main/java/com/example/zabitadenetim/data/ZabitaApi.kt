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
import retrofit2.http.Query


interface ZabitaApi {
    // tüm denetimleri almak için yazdığımız endpoint

    @GET("inspections/")
    suspend fun getInspections(): List<InspectionResponse>   // retrofit sunucudan gelen json veriyi otomatik buraya düşürüyor

    // 04.08.2026 eklendi
    @GET("businesses/")
    suspend fun getBusinesses(): List<BusinessResponse>

    //05.08.2026
    // seçilen işletmeyi veritabanına kaydetme işlemi
    @POST("businesses/")
    suspend fun createBusiness(
        @Body request: BusinessCreateRequest
    ): BusinessResponse

    // 04.08.2026
// Yazılan işletme adını mevcut konuma göre Google Maps üzerinde arar.
    @GET("businesses/search")
    suspend fun searchGooglePlaces(
        // Kullanıcının arama alanına yazdığı işletme adı.
        @Query("query") query: String,

        // Telefonun mevcut GPS konumu.
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): List<GooglePlaceResponse>

    // denetimi yapay zekayla tamamlamak için.

    // yeni denetim verisini json formatında sunucuya yollamak için
    @POST("inspections/")
    suspend fun createInspection(@Body request: InspectionRequest): InspectionResponse

    // fotoğraf yükleme endpointi (28.07.2026)
    @Multipart
    @POST("inspections/{inspection_id}/photos/")
    suspend fun uploadInspectionPhoto(
        @Path("inspection_id") inspectionId: Int,
        @Part file: MultipartBody.Part
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


    //05.08.2026
    // işletmenin google yorumlarını backend üzerinden çek
    @POST("businesses/{businessId}/sync-reviews")
    suspend fun syncBusinessReviews(
        @Path("businessId") businessId: Int
    ): ResponseBody


    // 03.08.2026
    // arayüz google yorumlarıu için
    @GET("/inspections/businesses/{businessId}/reviews")
    suspend fun getBusinessReviews(
        @Path("businessId") businessId: Int
    ): List<ReviewResponse>

    //07.08.2026
    @GET("businesses/categories/all")
    suspend fun getBusinessCategories(): List<BusinessCategoryResponse>

    @GET("inspections/criteria/{category_id}")
    suspend fun getInspectionCriteria(
        @Path("category_id") categoryId: Int
    ): List<InspectionCriterionResponse>

    //10.08.2026
    // denetim sorularını çekme endpointi eklendi.
    @GET("inspections/criteria/common/all")
    suspend fun getCommonInspectionCriteria(): List<InspectionCriterionResponse>


    //10.08.2026
    @GET("inspections/{inspection_id}/answers")
    suspend fun getInspectionAnswers(
        @Path("inspection_id") inspectionId: Int
    ): List<InspectionAnswerResponse>
}