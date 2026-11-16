package com.assessment.news.data.remote.dto

import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle

data class NewsResponseDto (
    val status: String,
    val totalResults: Int = 0,
    val articles: List<ArticleDto> = emptyList(),
    val sources: List<SourceDto> = emptyList()
)

data class ArticleDto(
    val source: ArticleSourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

data class ArticleSourceDto(
    val id: String?,
    val name: String?
)

data class SourceDto(
    val id: String?,
    val name: String?,
    val description: String?,
    val url: String?,
    val category: String?,
    val language: String?,
    val country: String?
)

fun ArticleDto.toNewsArticle(): NewsArticle {
    return NewsArticle(
        sourceName = source?.name.orEmpty(),
        author = author.orEmpty(),
        title = title.orEmpty(),
        description = description.orEmpty(),
        url = url.orEmpty(),
        imageUrl = urlToImage,
        publishedAt = publishedAt.orEmpty()
    )
}

fun SourceDto.toNews(): News = News(
    id = id.orEmpty(),
    name = name.orEmpty(),
    description = description.orEmpty(),
    url = url.orEmpty(),
    category = category.orEmpty(),
    language = language.orEmpty(),
    country = country.orEmpty()
)