package com.assessment.news.presentation.home

import com.assessment.news.domain.model.News

data class HomeUiState(
    val selectedCategory: String? = null,
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)