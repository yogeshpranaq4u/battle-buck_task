package com.example.leaderboardcore

import com.example.scoreengine.Player
import com.example.scoreengine.ScoreUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

class RealTimeLeaderboard(
    private val players: List<Player>,
    private val rankCalculator: LeaderboardRankCalculator = LeaderboardRankCalculator(),
    private val computationDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    fun initialSnapshot(): LeaderboardSnapshot {
        val initialScores = players.associate { it.id to 0 }
        return LeaderboardSnapshot(entries = rankCalculator.rank(players, initialScores))
    }

    fun stream(scoreUpdates: Flow<ScoreUpdate>): Flow<LeaderboardSnapshot> {
        val initialAccumulator = LeaderboardAccumulator(
            scoreByPlayerId = players.associate { it.id to 0 }
        )

        return scoreUpdates
            .runningFold(initialAccumulator) { accumulator, update ->
                accumulator.applyUpdate(update)
            }
            .map { accumulator ->
                LeaderboardSnapshot(
                    entries = rankCalculator.rank(players, accumulator.scoreByPlayerId),
                    lastUpdatedPlayerId = accumulator.lastUpdatedPlayerId,
                    lastUpdateSequence = accumulator.lastUpdateSequence
                )
            }
            .distinctUntilChanged()
            .flowOn(computationDispatcher)
    }
}

private data class LeaderboardAccumulator(
    val scoreByPlayerId: Map<String, Int>,
    val lastUpdatedPlayerId: String? = null,
    val lastUpdateSequence: Long = 0L
) {
    fun applyUpdate(update: ScoreUpdate): LeaderboardAccumulator {
        val currentScore = scoreByPlayerId[update.playerId] ?: return this
        if (update.newScore <= currentScore) {
            return this
        }

        return copy(
            scoreByPlayerId = scoreByPlayerId + (update.playerId to update.newScore),
            lastUpdatedPlayerId = update.playerId,
            lastUpdateSequence = update.sequence
        )
    }
}
