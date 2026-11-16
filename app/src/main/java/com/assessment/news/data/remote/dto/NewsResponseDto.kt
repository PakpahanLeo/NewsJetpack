package com.assessment.news.data.remote.dto

import com.assessment.news.domain.model.News

data class NewsResponseDto (
    val status: String,
    val sources: List<SourceDto>
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

fun SourceDto.toNews(): News = News(
    id = id.orEmpty(),
    name = name.orEmpty(),
    description = description.orEmpty(),
    url = url.orEmpty(),
    category = category.orEmpty(),
    language = language.orEmpty(),
    country = country.orEmpty()
)