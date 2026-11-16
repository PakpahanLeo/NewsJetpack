package com.assessment.news.data.repository

import com.assessment.news.data.remote.NewsApiService
import com.assessment.news.data.remote.dto.toNews
import com.assessment.news.data.remote.dto.toNewsArticle
import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle

class NewsRepository(
    private val api: NewsApiService,
    private val apiKey: String
) : NewsRepositoryContract {

    override suspend fun getEverything(
        query: String,
        page: Int
    ): List<NewsArticle> {
        return api.getEverything(
            query = query,
            page = page,
            apiKey = apiKey
        ).articles.map { it.toNewsArticle() }
    }

    override suspend fun getNewsSources(
        category: String
    ): List<News> {
        return api.getNewsSources(
            category = category.lowercase(),
            apiKey = apiKey
        ).sources.map { it.toNews() }
    }

    override suspend fun getArticlesBySource(
        sourceId: String,
        page: Int
    ): List<NewsArticle> {
        return api.getTopHeadlinesBySource(
            sourceId = sourceId,
            page = page,
            apiKey = apiKey
        ).articles.map { it.toNewsArticle() }
    }
}