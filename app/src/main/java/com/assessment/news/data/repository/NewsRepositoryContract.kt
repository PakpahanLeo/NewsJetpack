package com.assessment.news.data.repository

import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle

interface NewsRepositoryContract {

    suspend fun getEverything(
        query: String,
        page: Int
    ): List<NewsArticle>

    suspend fun getNewsSources(
        category: String
    ): List<News>

    suspend fun getArticlesBySource(
        sourceId: String,
        page: Int
    ): List<NewsArticle>
}