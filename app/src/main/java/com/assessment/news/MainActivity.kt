package com.assessment.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.assessment.news.presentation.home.HomeScreen
import com.assessment.news.presentation.home.HomeViewModel
import com.assessment.news.ui.theme.NewsJetpackTheme

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewsJetpackTheme {
                val uiState by viewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onCategorySelected = { category ->
                        viewModel.selectCategory(category)
                    },
                    onBackToCategories = {
                        viewModel.clearCategory()
                    }
                )
            }
        }
    }
}