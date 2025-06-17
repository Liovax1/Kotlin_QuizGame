package com.supdevinci.quizgame.service

import com.supdevinci.quizgame.model.QuizzResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Quizz {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 5,
        @Query("category") category: Int,
        @Query("encode") encode: String = "url3986"
    ): QuizzResponse
}
