package com.example.battlebuck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel : ViewModel() {

    private val initialUiState = LeaderboardUiState.fromSnapshot(
        snapshot = LeaderboardRepository.snapshots.value,
        totalPlayers = LeaderboardModuleFactory.players.size
    )

    val uiState: StateFlow<LeaderboardUiState> = LeaderboardRepository.snapshots
        .runningFold(initialUiState) { previous, snapshot ->
            LeaderboardUiState.fromSnapshot(
                snapshot = snapshot,
                previousRows = previous.rows.associateBy { it.id },
                totalPlayers = LeaderboardModuleFactory.players.size
            )
        }
        .map { state ->
            state.copy(totalPlayers = LeaderboardModuleFactory.players.size)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = initialUiState
        )
}
