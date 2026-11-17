package com.assessment.news.data.remote

import com.assessment.news.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto

    @GET("top-headlines/sources")
    suspend fun getNewsSources(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto

    @GET("top-headlines")
    suspend fun getTopHeadlinesBySource(
        @Query("sources") sourceId: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto
}