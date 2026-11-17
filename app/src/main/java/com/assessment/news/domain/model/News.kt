package com.assessment.news.domain.model

data class News (
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String
)

data class NewsArticle(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String
)
