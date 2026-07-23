package com.example.zabitadenetim.data

import android.provider.ContactsContract.CommonDataKinds.Email
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ZabitaApi {
    // tüm denetimleri almak için yazdığımız endpoint

    @GET("inspections/")
    suspend fun getInspections(): List<InspectionResponse>   // retrofit sunucudan gelen json veriyi otomatik buraya düşürüyor

    // denetimi yapay zekayla tamamlamak için.
    @POST("inspections/{id}/complete")
    suspend fun  completeInspection(@Path("id") inspectionId: Int): InspectionResponse

    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginResponse

}