package com.example.battlebuck

import com.example.battlebuck.config.LeaderboardConfigProvider
import com.example.leaderboardcore.RealTimeLeaderboard
import com.example.scoreengine.DeterministicScoreGenerator
import com.example.scoreengine.Player
import com.example.scoreengine.ScoreEngineConfig

internal class LeaderboardModuleFactory(
    configProvider: LeaderboardConfigProvider
) {
    private val config = configProvider.getConfig()

    val players: List<Player> = config.players

    val scoreGenerator = DeterministicScoreGenerator(
        players = players,
        config = ScoreEngineConfig(
            seed = config.scoreEngineConfig.seed,
            minDelayMillis = config.scoreEngineConfig.minDelayMillis,
            maxDelayMillis = config.scoreEngineConfig.maxDelayMillis,
            minScoreIncrement = config.scoreEngineConfig.minScoreIncrement,
            maxScoreIncrement = config.scoreEngineConfig.maxScoreIncrement
        )
    )

    val leaderboard = RealTimeLeaderboard(players = players)
}
