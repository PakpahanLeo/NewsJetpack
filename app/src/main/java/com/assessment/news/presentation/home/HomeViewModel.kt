package com.assessment.news.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assessment.news.data.remote.RetrofitClient
import com.assessment.news.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){
    private val apiKey = "1d2cfc3ad263426bb3bd92a0230501ce"

    private val repository = NewsRepository(
        api = RetrofitClient.api,
        apiKey = apiKey
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun selectCategory(category: String) {
        _uiState.value = HomeUiState(
            selectedCategory = category,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val sources = repository.getNewsSources(category)
                _uiState.value = HomeUiState(
                    selectedCategory = category,
                    newsList = sources
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    selectedCategory = category,
                    error = e.localizedMessage ?: "Gagal memuat sumber berita"
                )
            }
        }
    }

    fun clearCategory(){
        _uiState.value = HomeUiState()
    }

}