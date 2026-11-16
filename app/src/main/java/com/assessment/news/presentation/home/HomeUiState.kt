package com.assessment.news.presentation.home

import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle

data class HomeUiState(
    val selectedCategory: String? = null,
    val selectedSource: News? = null,
    val newsList: List<News> = emptyList(),
    val articles: List<NewsArticle> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)