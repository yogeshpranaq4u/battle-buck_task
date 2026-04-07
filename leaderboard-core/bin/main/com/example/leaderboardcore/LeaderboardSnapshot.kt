package com.example.leaderboardcore

data class LeaderboardSnapshot(
    val entries: List<LeaderboardEntry>,
    val lastUpdatedPlayerId: String? = null,
    val lastUpdateSequence: Long = 0L
)
