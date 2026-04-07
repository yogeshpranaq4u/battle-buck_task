package com.example.leaderboardcore

import com.example.scoreengine.Player

class LeaderboardRankCalculator {

    fun rank(
        players: List<Player>,
        scoreByPlayerId: Map<String, Int>
    ): List<LeaderboardEntry> {
        val sortedPlayers = players.sortedWith(
            compareByDescending<Player> { scoreByPlayerId[it.id] ?: 0 }
                .thenBy { it.displayName }
                .thenBy { it.id }
        )

        var lastScore: Int? = null
        var lastRank = 0

        return sortedPlayers.mapIndexed { index, player ->
            val score = scoreByPlayerId[player.id] ?: 0
            val rank = if (score == lastScore) lastRank else index + 1
            lastScore = score
            lastRank = rank

            LeaderboardEntry(
                playerId = player.id,
                displayName = player.displayName,
                score = score,
                rank = rank
            )
        }
    }
}
