package com.assessment.news

import com.assessment.news.domain.model.News
import com.assessment.news.domain.model.NewsArticle
import com.assessment.news.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var repository: FakeNewsRepository
    private lateinit var viewModel: HomeViewModel

    private val dispatcher: TestDispatcher =
        StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = FakeNewsRepository()
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNews with valid query loads first page successfully`() =
        runTest {
            val firstPage = createArticles(20)

            repository.searchPages[1] = firstPage

            viewModel.loadNews("Android")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals("Android", state.searchQuery)
            assertTrue(state.isSearchActive)
            assertEquals(20, state.articles.size)
            assertFalse(state.isLoading)
            assertTrue(state.hasMore)
            assertEquals(listOf(1), repository.requestedSearchPages)
        }

    @Test
    fun `loadNews with blank query does not call repository`() =
        runTest {
            viewModel.loadNews("   ")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(repository.requestedSearchPages.isEmpty())
            assertTrue(state.articles.isEmpty())
            assertFalse(state.isLoading)
        }

    @Test
    fun `loadNews with less than 20 articles sets hasMore false`() =
        runTest {
            repository.searchPages[1] = createArticles(5)

            viewModel.loadNews("Android")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(5, state.articles.size)
            assertFalse(state.hasMore)
        }

    @Test
    fun `loadMoreArticles requests page two and appends articles`() =
        runTest {
            repository.searchPages[1] = createArticles(
                count = 20,
                startIndex = 0
            )
            repository.searchPages[2] = createArticles(
                count = 20,
                startIndex = 20
            )

            viewModel.loadNews("Android")
            advanceUntilIdle()

            viewModel.loadMoreArticles()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(40, state.articles.size)
            assertEquals(listOf(1, 2), repository.requestedSearchPages)
            assertTrue(state.hasMore)
            assertFalse(state.isLoadingMore)
        }

    @Test
    fun `loadMoreArticles with empty next page stops pagination`() =
        runTest {
            repository.searchPages[1] = createArticles(20)
            repository.searchPages[2] = emptyList()

            viewModel.loadNews("Android")
            advanceUntilIdle()

            viewModel.loadMoreArticles()
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(20, state.articles.size)
            assertEquals(listOf(1, 2), repository.requestedSearchPages)
            assertFalse(state.hasMore)
        }

    @Test
    fun `loadMoreArticles does not request when hasMore is false`() =
        runTest {
            repository.searchPages[1] = createArticles(5)

            viewModel.loadNews("Android")
            advanceUntilIdle()

            viewModel.loadMoreArticles()
            advanceUntilIdle()

            assertEquals(
                listOf(1),
                repository.requestedSearchPages
            )
        }

    @Test
    fun `loadNews error updates error state`() =
        runTest {
            repository.searchException =
                IllegalStateException("Network error")

            viewModel.loadNews("Android")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertFalse(state.hasMore)
            assertEquals("Network error", state.error)
        }

    @Test
    fun `selectCategory loads first ten sources`() =
        runTest {
            repository.sources = createSources(25)

            viewModel.selectCategory("Technology")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals("Technology", state.selectedCategory)
            assertEquals(10, state.newsList.size)
            assertTrue(state.hasMore)
            assertEquals(
                listOf("technology"),
                repository.requestedCategories
            )
        }

    @Test
    fun `loadMoreSources appends next ten sources`() =
        runTest {
            repository.sources = createSources(25)

            viewModel.selectCategory("Technology")
            advanceUntilIdle()

            viewModel.loadMoreSources()

            val state = viewModel.uiState.value

            assertEquals(20, state.newsList.size)
            assertTrue(state.hasMore)
        }

    @Test
    fun `loadMoreSources appends remaining sources and stops`() =
        runTest {
            repository.sources = createSources(25)

            viewModel.selectCategory("Technology")
            advanceUntilIdle()

            viewModel.loadMoreSources()
            viewModel.loadMoreSources()

            val state = viewModel.uiState.value

            assertEquals(25, state.newsList.size)
            assertFalse(state.hasMore)
        }

    @Test
    fun `selectCategory error updates error state`() =
        runTest {
            repository.sourcesException =
                IllegalStateException("Sources unavailable")

            viewModel.selectCategory("Technology")
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertFalse(state.hasMore)
            assertEquals("Sources unavailable", state.error)
        }

    @Test
    fun `selectSource loads first article page`() =
        runTest {
            val source = createSource("bbc-news")

            repository.sourcePages[1] = createArticles(20)

            viewModel.selectSource(source)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(source, state.selectedSource)
            assertEquals(20, state.articles.size)
            assertEquals(
                listOf("bbc-news"),
                repository.requestedSourceIds
            )
            assertEquals(
                listOf(1),
                repository.requestedSourcePages
            )
        }

    @Test
    fun `selectSource without id shows error and does not call API`() =
        runTest {
            val source = createSource("")

            viewModel.selectSource(source)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertEquals(source, state.selectedSource)
            assertTrue(state.articles.isEmpty())
            assertFalse(state.hasMore)
            assertTrue(
                state.error?.contains("Source ID") == true
            )
            assertTrue(repository.requestedSourcePages.isEmpty())
        }

    @Test
    fun `selectSource error updates error state`() =
        runTest {
            repository.sourceArticlesException =
                IllegalStateException("Source articles unavailable")

            viewModel.selectSource(createSource("bbc-news"))
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertFalse(state.hasMore)
            assertEquals(
                "Source articles unavailable",
                state.error
            )
        }

    @Test
    fun `clearCategory resets all state`() =
        runTest {
            repository.sources = createSources(20)

            viewModel.selectCategory("Sports")
            advanceUntilIdle()

            viewModel.clearCategory()

            val state = viewModel.uiState.value

            assertEquals(null, state.selectedCategory)
            assertTrue(state.newsList.isEmpty())
            assertTrue(state.articles.isEmpty())
            assertFalse(state.isLoading)
        }

    @Test
    fun `clearSource removes selected source and articles`() =
        runTest {
            repository.sourcePages[1] = createArticles(20)

            viewModel.selectSource(createSource("cnn"))
            advanceUntilIdle()

            viewModel.clearSource()

            val state = viewModel.uiState.value

            assertEquals(null, state.selectedSource)
            assertTrue(state.articles.isEmpty())
            assertFalse(state.isLoading)
        }

    private fun createArticles(
        count: Int,
        startIndex: Int = 0
    ): List<NewsArticle> {
        return (startIndex until startIndex + count).map { index ->
            NewsArticle(
                sourceName = "Test Source",
                author = "Author $index",
                title = "Article $index",
                description = "Description $index",
                url = "https://example.com/article-$index",
                imageUrl = null,
                publishedAt = "2026-01-01T00:00:00Z"
            )
        }
    }

    private fun createSources(
        count: Int
    ): List<News> {
        return (0 until count).map { index ->
            createSource("source-$index")
        }
    }

    private fun createSource(
        id: String
    ): News {
        return News(
            id = id,
            name = if (id.isBlank()) {
                "Invalid Source"
            } else {
                "Source $id"
            },
            description = "Test source",
            url = "https://example.com/$id",
            category = "technology",
            language = "en",
            country = "us"
        )
    }
}