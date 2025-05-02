package com.example.summarynews.geminiAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun getApi(): GeminiApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GeminiApi::class.java)
    }
}
