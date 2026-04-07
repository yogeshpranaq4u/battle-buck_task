package com.example.battlebuck

import com.example.leaderboardcore.LeaderboardSnapshot

data class LeaderboardUiState(
    val rows: List<LeaderboardRowUiModel>,
    val totalPlayers: Int,
    val totalUpdates: Long
) {
    companion object {
        fun fromSnapshot(
            snapshot: LeaderboardSnapshot,
            previousRows: Map<String, LeaderboardRowUiModel> = emptyMap(),
            totalPlayers: Int = snapshot.entries.size
        ): LeaderboardUiState {
            val rows = snapshot.entries.map { entry ->
                val previous = previousRows[entry.playerId]
                LeaderboardRowUiModel(
                    id = entry.playerId,
                    username = entry.displayName,
                    rank = entry.rank,
                    score = entry.score,
                    scoreDelta = entry.score - (previous?.score ?: 0),
                    rankShift = previous?.rank?.minus(entry.rank) ?: 0,
                    updateToken = snapshot.lastUpdateSequence.takeIf {
                        entry.playerId == snapshot.lastUpdatedPlayerId
                    }
                )
            }

            return LeaderboardUiState(
                rows = rows,
                totalPlayers = totalPlayers,
                totalUpdates = snapshot.lastUpdateSequence
            )
        }
    }
}

data class LeaderboardRowUiModel(
    val id: String,
    val username: String,
    val rank: Int,
    val score: Int,
    val scoreDelta: Int,
    val rankShift: Int,
    val updateToken: Long?
)
