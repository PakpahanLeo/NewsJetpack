package com.assessment.news.data.repository

import com.assessment.news.data.remote.NewsApiService
import com.assessment.news.data.remote.dto.toNews
import com.assessment.news.domain.model.News

class NewsRepository(
    private val api: NewsApiService,
    private val apiKey: String
){
    suspend fun getNewsSources(category: String): List<News>{
        return api.getNewsSources(
            category = category.lowercase(),
            apiKey = apiKey
        ).sources.map { it.toNews() }
    }
}