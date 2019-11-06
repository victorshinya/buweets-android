package com.victorshinya.buweets

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://buweets.mybluemix.net/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService get() = retrofit.create(APIService::class.java)
}