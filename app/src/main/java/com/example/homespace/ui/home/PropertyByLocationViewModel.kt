package com.example.homespace.ui.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.data.property.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PropertyByLocationViewModel(
    private val propertyRepository: PropertyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val state: StateFlow<UiState2>
    val properties: Flow<PagingData<GetPropertiesStartWithCountryQuery.Property>>

    val accept: (UiAction2) -> Unit

    init {
        val initialQuery: String = savedStateHandle[LAST_PULL_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle[LAST_PULL_QUERY_SCROLLED] ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction2>()
        val pulled = actionStateFlow
            .filterIsInstance<UiAction2.Pull>()
            .distinctUntilChanged()
        if (DEFAULT_QUERY != "")
            pulled.onStart { emit(UiAction2.Pull(query = initialQuery)) }

        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction2.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
        if (DEFAULT_QUERY != "")
            queriesScrolled.onStart { emit(UiAction2.Scroll(currentQuery = lastQueryScrolled)) }

        properties = pulled
            .flatMapLatest { getProperties(searchString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(pulled, queriesScrolled, ::Pair).map { (pulled, scroll) ->
            UiState2(
                query = pulled.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = pulled.query != scroll.currentQuery
            )
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = UiState2()
        )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun getProperties(searchString: String): Flow<PagingData<GetPropertiesStartWithCountryQuery.Property>> {
        val l = propertyRepository.getPropertyByCountryFlow(
            query = searchString, initialPageNumber = 1)
        Log.d("NA HERE WE DEY", l.toString())
        return l;
    }

    override fun onCleared() {
        savedStateHandle[LAST_PULL_QUERY] = state.value.query
        savedStateHandle[LAST_PULL_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }
}

sealed class UiAction2 {
    data class Pull(val query: String) : UiAction2()
    data class Scroll(val currentQuery: String) : UiAction2()
}

data class UiState2(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val LAST_PULL_QUERY_SCROLLED: String = "last_pull_query_scrolled"
private const val LAST_PULL_QUERY: String = "last_pull_query"
private const val DEFAULT_QUERY = ""