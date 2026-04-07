package com.example.battlebuck

import com.example.leaderboardcore.RealTimeLeaderboard
import com.example.scoreengine.DeterministicScoreGenerator
import com.example.scoreengine.Player
import com.example.scoreengine.ScoreEngineConfig

internal object LeaderboardModuleFactory {

    val players: List<Player> = listOf(
        Player(id = "p1", displayName = "Deepender"),
        Player(id = "p2", displayName = "Predekin_Singh"),
        Player(id = "p3", displayName = "Himanshu"),
        Player(id = "p4", displayName = "Manya Aggarwal"),
        Player(id = "p5", displayName = "Vishal"),
        Player(id = "p6", displayName = "Shreyas"),
        Player(id = "p7", displayName = "Mohit Sharma"),
        Player(id = "p8", displayName = "Anwesha"),
        Player(id = "p9", displayName = "Premjit"),
        Player(id = "p10", displayName = "Harshit"),
        Player(id = "p11", displayName = "Ayush Rawat"),
        Player(id = "p12", displayName = "Arshi"),
        Player(id = "p13", displayName = "Iqbal"),
        Player(id = "p14", displayName = "Shubham"),
        Player(id = "p15", displayName = "Aditya Patil")
    )

    val scoreGenerator = DeterministicScoreGenerator(
        players = players,
        config = ScoreEngineConfig(
            seed = 20260407L,
            minDelayMillis = 500L,
            maxDelayMillis = 1_400L,
            minScoreIncrement = 8,
            maxScoreIncrement = 28
        )
    )

    val leaderboard = RealTimeLeaderboard(players = players)
}
