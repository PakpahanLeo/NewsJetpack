package com.assessment.news.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assessment.news.data.remote.RetrofitClient
import com.assessment.news.data.repository.NewsRepository
import com.assessment.news.data.repository.NewsRepositoryContract
import com.assessment.news.domain.model.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NewsRepositoryContract = NewsRepository(
        api = RetrofitClient.api,
        apiKey = API_KEY
    )
) : ViewModel() {

    private companion object {
        const val API_KEY = "1d2cfc3ad263426bb3bd92a0230501ce"
        const val ARTICLES_PER_PAGE = 20
        const val SOURCES_PER_PAGE = 10
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var articlePage = 1
    private var sourcePage = 1

    private var allSources: List<News> = emptyList()

    fun loadNews(query: String) {
        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            return
        }

        articlePage = 1

        _uiState.value = HomeUiState(
            searchQuery = cleanQuery,
            isSearchActive = true,
            isLoading = true,
            hasMore = true
        )

        viewModelScope.launch {
            try {
                val articles = repository.getEverything(
                    query = cleanQuery,
                    page = articlePage
                )

                _uiState.value = _uiState.value.copy(
                    articles = articles,
                    isLoading = false,
                    hasMore = articles.size >= ARTICLES_PER_PAGE,
                    error = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasMore = false,
                    error = exception.localizedMessage
                        ?: "Gagal memuat berita"
                )
            }
        }
    }

    fun selectCategory(category: String) {
        sourcePage = 1
        allSources = emptyList()

        _uiState.value = HomeUiState(
            selectedCategory = category,
            isLoading = true,
            hasMore = true
        )

        viewModelScope.launch {
            try {
                allSources = repository.getNewsSources(category)

                val firstSources = allSources.take(SOURCES_PER_PAGE)

                _uiState.value = HomeUiState(
                    selectedCategory = category,
                    newsList = firstSources,
                    isLoading = false,
                    hasMore = firstSources.size < allSources.size
                )
            } catch (exception: Exception) {
                _uiState.value = HomeUiState(
                    selectedCategory = category,
                    isLoading = false,
                    hasMore = false,
                    error = exception.localizedMessage
                        ?: "Gagal memuat sumber berita"
                )
            }
        }
    }

    fun selectSource(source: News) {
        if (source.id.isBlank()) {
            _uiState.value = _uiState.value.copy(
                selectedSource = source,
                isLoading = false,
                hasMore = false,
                error = "Source ID tidak tersedia untuk ${source.name}"
            )
            return
        }

        articlePage = 1

        _uiState.value = _uiState.value.copy(
            selectedSource = source,
            articles = emptyList(),
            isLoading = true,
            isLoadingMore = false,
            hasMore = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val articles = repository.getArticlesBySource(
                    sourceId = source.id,
                    page = articlePage
                )

                _uiState.value = _uiState.value.copy(
                    selectedSource = source,
                    articles = articles,
                    isLoading = false,
                    hasMore = articles.size >= ARTICLES_PER_PAGE,
                    error = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasMore = false,
                    error = exception.localizedMessage
                        ?: "Gagal memuat artikel"
                )
            }
        }
    }

    fun loadMoreArticles() {
        val currentState = _uiState.value

        if (
            currentState.isLoading ||
            currentState.isLoadingMore ||
            !currentState.hasMore
        ) {
            return
        }

        val source = currentState.selectedSource
        val query = currentState.searchQuery

        if (source == null && query.isBlank()) {
            return
        }

        _uiState.value = currentState.copy(
            isLoadingMore = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val nextPage = articlePage + 1

                val nextArticles = if (source != null) {
                    repository.getArticlesBySource(
                        sourceId = source.id,
                        page = nextPage
                    )
                } else {
                    repository.getEverything(
                        query = query,
                        page = nextPage
                    )
                }

                articlePage = nextPage

                val combinedArticles =
                    _uiState.value.articles + nextArticles

                _uiState.value = _uiState.value.copy(
                    articles = combinedArticles,
                    isLoadingMore = false,
                    hasMore = nextArticles.size >= ARTICLES_PER_PAGE,
                    error = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = exception.localizedMessage
                        ?: "Gagal memuat artikel berikutnya"
                )
            }
        }
    }

    fun loadMoreSources() {
        val currentState = _uiState.value

        if (
            currentState.isLoading ||
            currentState.isLoadingMore ||
            !currentState.hasMore
        ) {
            return
        }

        val nextPage = sourcePage + 1
        val nextSources = allSources
            .drop((nextPage - 1) * SOURCES_PER_PAGE)
            .take(SOURCES_PER_PAGE)

        if (nextSources.isEmpty()) {
            _uiState.value = currentState.copy(
                hasMore = false
            )
            return
        }

        sourcePage = nextPage

        val combinedSources =
            currentState.newsList + nextSources

        _uiState.value = currentState.copy(
            newsList = combinedSources,
            hasMore = combinedSources.size < allSources.size
        )
    }

    fun clearCategory() {
        _uiState.value = HomeUiState()
        articlePage = 1
        sourcePage = 1
        allSources = emptyList()
    }

    fun clearSource() {
        _uiState.value = _uiState.value.copy(
            selectedSource = null,
            articles = emptyList(),
            isLoading = false,
            isLoadingMore = false,
            hasMore = true,
            error = null
        )

        articlePage = 1
    }
}