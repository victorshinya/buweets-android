package com.victorshinya.buweets

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("get-emotion")
    fun analyze(@Query("text") text: String): Call<NaturalLanguageUnderstandingModel>
}