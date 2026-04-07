package com.example.battlebuck

import com.example.leaderboardcore.LeaderboardSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

internal class LeaderboardRepository(
    moduleFactory: LeaderboardModuleFactory
) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val snapshots: StateFlow<LeaderboardSnapshot> = moduleFactory
        .leaderboard
        .stream(moduleFactory.scoreGenerator.updates())
        .stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
            initialValue = moduleFactory.leaderboard.initialSnapshot()
        )
}
