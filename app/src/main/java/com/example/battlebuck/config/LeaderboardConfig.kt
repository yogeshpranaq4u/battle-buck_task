package com.example.battlebuck.config

import com.example.scoreengine.Player
import com.example.scoreengine.ScoreEngineConfig

data class LeaderboardConfig(
    val players: List<Player>,
    val scoreEngineConfig: ScoreEngineConfig
)
