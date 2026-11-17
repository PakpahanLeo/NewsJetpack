package com.assessment.news.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle
import com.assessment.news.presentation.webview.NewsWebViewScreen
import kotlinx.coroutines.flow.distinctUntilChanged

private val Ink = Color(0xFF111827)
private val Muted = Color(0xFF64748B)
private val Background = Color(0xFFF6F8FC)
private val Primary = Color(0xFF4F46E5)

private data class NewsCategory(
    val name: String,
    val emoji: String,
    val color: Color
)

private val newsCategories = listOf(
    NewsCategory("Business", "💼", Color(0xFFEDE9FE)),
    NewsCategory("Entertainment", "🎬", Color(0xFFFFE4E6)),
    NewsCategory("General", "📰", Color(0xFFE2E8F0)),
    NewsCategory("Health", "🩺", Color(0xFFD1FAE5)),
    NewsCategory("Science", "🔬", Color(0xFFDBEAFE)),
    NewsCategory("Sports", "⚽", Color(0xFFFEF3C7)),
    NewsCategory("Technology", "💻", Color(0xFFCFFAFE))
)

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(),
    onSearch: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onSourceSelected: (News) -> Unit = {},
    onLoadMoreArticles: () -> Unit = {},
    onLoadMoreSources: () -> Unit = {},
    onBackToCategories: () -> Unit = {},
    onBackToSources: () -> Unit = {}
) {
    var selectedUrl by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(containerColor = Background) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                selectedUrl != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        BackButton(onClick = { selectedUrl = null })
                        NewsWebViewScreen(url = selectedUrl.orEmpty())
                    }
                }

                uiState.selectedSource != null -> {
                    ArticleListScreen(
                        uiState = uiState,
                        title = uiState.selectedSource.name,
                        subtitle = "Latest stories from this source",
                        onArticleClick = { selectedUrl = it },
                        onBack = onBackToSources,
                        onLoadMore = onLoadMoreArticles
                    )
                }

                uiState.selectedCategory != null -> {
                    NewsSourceListScreen(
                        uiState = uiState,
                        onBack = onBackToCategories,
                        onSourceClick = onSourceSelected,
                        onLoadMore = onLoadMoreSources
                    )
                }

                uiState.isSearchActive -> {
                    ArticleListScreen(
                        uiState = uiState,
                        title = "Search results",
                        subtitle = "Stories about ${uiState.searchQuery}",
                        onArticleClick = { selectedUrl = it },
                        onBack = onBackToCategories,
                        onLoadMore = onLoadMoreArticles
                    )
                }

                else -> {
                    CategoryGridScreen(
                        onSearch = onSearch,
                        onCategorySelected = onCategorySelected
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGridScreen(
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "NEWSJETPACK",
            color = Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )

        Text(
            text = "News from all over the world.",
            style = MaterialTheme.typography.headlineLarge,
            color = Ink,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Discover the stories that matter to you.",
            style = MaterialTheme.typography.bodyLarge,
            color = Muted,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Search topics, people, or places", color = Color(0xFF64748B)) },
            trailingIcon = {
                TextButton(
                    onClick = { onSearch(searchText.trim()) },
                    enabled = searchText.isNotBlank()
                ) {
                    Text("Search", color = Primary, fontWeight = FontWeight.Bold)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,

                focusedBorderColor = Primary,
                unfocusedBorderColor = Color(0xFFCBD5E1),

                focusedLabelColor = Primary,
                unfocusedLabelColor = Color(0xFF64748B),

                cursorColor = Primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(searchText.trim()) }
            ),
            shape = RoundedCornerShape(18.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore topics",
                style = MaterialTheme.typography.titleLarge,
                color = Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${newsCategories.size} categories",
                style = MaterialTheme.typography.labelMedium,
                color = Muted
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(newsCategories) { category ->
                CategoryCard(category = category) {
                    onCategorySelected(category.name)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: NewsCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = category.color
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.emoji, fontSize = 34.sp)
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Read latest",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink.copy(alpha = .55f),
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ArticleListScreen(
    uiState: HomeUiState,
    title: String,
    subtitle: String,
    onArticleClick: (String) -> Unit,
    onBack: () -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LoadMoreWhenNearEnd(
        listState = listState,
        itemCount = uiState.articles.size,
        hasMore = uiState.hasMore,
        isLoading = uiState.isLoading,
        isLoadingMore = uiState.isLoadingMore,
        onLoadMore = onLoadMore
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        BackButton(onClick = onBack)

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Ink,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Muted,
            modifier = Modifier.padding(top = 5.dp, bottom = 18.dp)
        )

        when {
            uiState.isLoading -> FullScreenLoading()
            uiState.error != null && uiState.articles.isEmpty() -> ErrorState(uiState.error)
            uiState.articles.isEmpty() -> EmptyState("No articles found")
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 28.dp)
                ) {
                    itemsIndexed(
                        items = uiState.articles,
                        key = { index, article -> "${article.url}-$index" }
                    ) { _, article ->
                        ArticleItem(article = article) {
                            onArticleClick(article.url)
                        }
                    }
                    if (uiState.isLoadingMore) item { LoadingMoreItem() }
                    if (uiState.error != null && uiState.articles.isNotEmpty()) {
                        item { InlineError(uiState.error) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsSourceListScreen(
    uiState: HomeUiState,
    onBack: () -> Unit,
    onSourceClick: (News) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LoadMoreWhenNearEnd(
        listState = listState,
        itemCount = uiState.newsList.size,
        hasMore = uiState.hasMore,
        isLoading = uiState.isLoading,
        isLoadingMore = uiState.isLoadingMore,
        onLoadMore = onLoadMore
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        BackButton(onClick = onBack)
        Text(
            text = "${uiState.selectedCategory} sources",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Choose a source to read its latest articles",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted,
            modifier = Modifier.padding(top = 5.dp, bottom = 18.dp)
        )

        when {
            uiState.isLoading -> FullScreenLoading()
            uiState.error != null && uiState.newsList.isEmpty() -> ErrorState(uiState.error)
            uiState.newsList.isEmpty() -> EmptyState("No news sources found")
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 28.dp)
                ) {
                    items(uiState.newsList, key = { it.id }) { news ->
                        NewsSourceItem(news = news) { onSourceClick(news) }
                    }
                    if (uiState.isLoadingMore) item { LoadingMoreItem() }
                }
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 4.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        Text("‹  Back", color = Primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ArticleItem(article: NewsArticle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SourceBadge(name = article.sourceName)
                Spacer(modifier = Modifier.width(8.dp))
                if (article.publishedAt.isNotBlank()) {
                    Text(
                        text = article.publishedAt.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = Muted
                    )
                }
            }
            Text(
                text = article.title.ifBlank { "Untitled article" },
                style = MaterialTheme.typography.titleLarge,
                color = Ink,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp)
            )
            if (article.description.isNotBlank()) {
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            HorizontalDivider(
                color = Color(0xFFE2E8F0),
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(
                text = "Read full story  →",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun NewsSourceItem(news: News, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Primary, Color(0xFF7C3AED)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = news.name.firstOrNull()?.uppercase() ?: "N",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = news.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(news.country, news.language)
                        .filter { it.isNotBlank() }
                        .joinToString(" • ")
                        .ifBlank { "News source" },
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text("›", color = Primary, fontSize = 28.sp)
        }
    }
}

@Composable
private fun SourceBadge(name: String) {
    Surface(
        color = Color(0xFFEDE9FE),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = name.ifBlank { "News" },
            color = Primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun LoadMoreWhenNearEnd(
    listState: LazyListState,
    itemCount: Int,
    hasMore: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(listState, itemCount, hasMore, isLoading, isLoadingMore) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            val shouldLoadMore = itemCount > 0 &&
                    lastVisibleIndex >= itemCount - 3 &&
                    hasMore &&
                    !isLoading &&
                    !isLoadingMore

            if (shouldLoadMore) onLoadMore()
        }
    }
}

@Composable
private fun FullScreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
private fun LoadingMoreItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Primary, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun InlineError(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(12.dp)
    )
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Muted)
    }
}