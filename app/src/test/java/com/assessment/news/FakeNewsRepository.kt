package com.assessment.news

import com.assessment.news.data.repository.NewsRepositoryContract
import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle

class FakeNewsRepository : NewsRepositoryContract {

    val requestedSearchPages = mutableListOf<Int>()
    val requestedSourcePages = mutableListOf<Int>()
    val requestedCategories = mutableListOf<String>()
    val requestedSourceIds = mutableListOf<String>()

    var searchPages: MutableMap<Int, List<NewsArticle>> = mutableMapOf()
    var sourcePages: MutableMap<Int, List<NewsArticle>> = mutableMapOf()

    var sources: List<News> = emptyList()

    var searchException: Exception? = null
    var sourcesException: Exception? = null
    var sourceArticlesException: Exception? = null

    override suspend fun getEverything(
        query: String,
        page: Int
    ): List<NewsArticle> {
        requestedSearchPages += page

        searchException?.let {
            throw it
        }

        return searchPages[page].orEmpty()
    }

    override suspend fun getNewsSources(
        category: String
    ): List<News> {
        requestedCategories += category

        sourcesException?.let {
            throw it
        }

        return sources
    }

    override suspend fun getArticlesBySource(
        sourceId: String,
        page: Int
    ): List<NewsArticle> {
        requestedSourceIds += sourceId
        requestedSourcePages += page

        sourceArticlesException?.let {
            throw it
        }

        return sourcePages[page].orEmpty()
    }
}