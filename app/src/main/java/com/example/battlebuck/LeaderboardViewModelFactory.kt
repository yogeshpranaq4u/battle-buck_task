package com.example.battlebuck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battlebuck.di.AppContainer

internal class LeaderboardViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(
                repository = container.leaderboardRepository,
                sessionProvider = container.sessionProvider,
                totalPlayers = container.moduleFactory.players.size
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
