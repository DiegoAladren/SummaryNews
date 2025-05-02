package com.example.summarynews.geminiAPI
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    fun generarContenido(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Call<GeminiResponse>
}
