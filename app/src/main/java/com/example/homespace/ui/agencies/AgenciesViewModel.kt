package com.example.homespace.ui.agencies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.homespace.GetAgenciesQuery.Agency
import com.example.homespace.data.agency.AgencyRepository
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

class AgenciesViewModel(
    private val agencyRepository: AgencyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val state: StateFlow<UiStateForPulledAgencies>
    val agencies: Flow<PagingData<Agency>>

    val accept: (UiActionForAgency) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiActionForAgency>()
        val pulledAgencies = actionStateFlow
            .filterIsInstance<UiActionForAgency.Pull>()
            .distinctUntilChanged()
            .onStart { emit(UiActionForAgency.Pull("")) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiActionForAgency.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            ).onStart { emit(UiActionForAgency.Scroll("")) }
        agencies = pulledAgencies
            .flatMapLatest { retrieveAgencies() }
            .cachedIn(viewModelScope)
        state = combine(pulledAgencies, queriesScrolled, ::Pair).map { (pulled, scroll) ->
            UiStateForPulledAgencies(
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolled = pulled.query != scroll.currentQuery
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = UiStateForPulledAgencies()
        )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun retrieveAgencies(): Flow<PagingData<Agency>> {
            return agencyRepository.getAgenciesFlow(initialPageNumber = 1)
    }
}

sealed class UiActionForAgency {
    data class Pull(val query: String) : UiActionForAgency()
    data class Scroll(val currentQuery: String) : UiActionForAgency()
}

data class UiStateForPulledAgencies(
    val hasNotScrolled: Boolean = false
)