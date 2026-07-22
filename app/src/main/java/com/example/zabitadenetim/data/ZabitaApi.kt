package com.example.zabitadenetim.data

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

}