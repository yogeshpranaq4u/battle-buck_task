package com.example.leaderboardcore

data class LeaderboardEntry(
    val playerId: String,
    val displayName: String,
    val score: Int,
    val rank: Int
)
