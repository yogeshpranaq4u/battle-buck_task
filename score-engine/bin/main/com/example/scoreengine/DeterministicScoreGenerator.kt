package com.example.scoreengine

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class DeterministicScoreGenerator(
    override val players: List<Player>,
    private val config: ScoreEngineConfig
) : ScoreGenerator {

    init {
        require(players.isNotEmpty()) { "players must not be empty." }
    }

    override fun updates(): Flow<ScoreUpdate> = flow {
        val random = Random(config.seed)
        val scoreByPlayer = players.associate { it.id to 0 }.toMutableMap()
        var sequence = 0L

        while (currentCoroutineContext().isActive) {
            delay(nextDelayMillis(random))

            val player = players[random.nextInt(players.size)]
            val delta = random.nextInt(
                from = config.minScoreIncrement,
                until = config.maxScoreIncrement + 1
            )
            val updatedScore = (scoreByPlayer[player.id] ?: 0) + delta
            scoreByPlayer[player.id] = updatedScore
            sequence += 1

            emit(
                ScoreUpdate(
                    sequence = sequence,
                    playerId = player.id,
                    delta = delta,
                    newScore = updatedScore
                )
            )
        }
    }

    private fun nextDelayMillis(random: Random): Long {
        if (config.minDelayMillis == config.maxDelayMillis) {
            return config.minDelayMillis
        }

        return random.nextLong(config.minDelayMillis, config.maxDelayMillis + 1)
    }
}
