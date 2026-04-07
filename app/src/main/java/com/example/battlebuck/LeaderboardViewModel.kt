package com.example.battlebuck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battlebuck.session.SessionProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

internal class LeaderboardViewModel(
    private val repository: LeaderboardRepository,
    sessionProvider: SessionProvider,
    private val totalPlayers: Int
) : ViewModel() {

    val currentUserId: String = sessionProvider.currentUserId()

    private val initialUiState = LeaderboardUiState.fromSnapshot(
        snapshot = repository.snapshots.value,
        totalPlayers = totalPlayers
    )

    val uiState: StateFlow<LeaderboardUiState> = repository.snapshots
        .runningFold(initialUiState) { previous, snapshot ->
            LeaderboardUiState.fromSnapshot(
                snapshot = snapshot,
                previousRows = previous.rows.associateBy { it.id },
                totalPlayers = totalPlayers
            )
        }
        .map { state ->
            state.copy(totalPlayers = totalPlayers)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = initialUiState
        )
}
