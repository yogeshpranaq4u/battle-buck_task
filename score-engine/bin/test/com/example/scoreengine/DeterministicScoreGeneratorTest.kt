package com.example.scoreengine

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeterministicScoreGeneratorTest {

    private val players = listOf(
        Player(id = "p1", displayName = "Astra"),
        Player(id = "p2", displayName = "Blaze"),
        Player(id = "p3", displayName = "Cipher")
    )

    @Test
    fun `same seed produces the same score stream`() = runTest {
        val config = ScoreEngineConfig(
            seed = 99L,
            minDelayMillis = 0L,
            maxDelayMillis = 0L,
            minScoreIncrement = 1,
            maxScoreIncrement = 10
        )

        val firstRun = DeterministicScoreGenerator(players, config).updates().take(8).toList()
        val secondRun = DeterministicScoreGenerator(players, config).updates().take(8).toList()

        assertEquals(firstRun, secondRun)
    }

    @Test
    fun `scores only increase for each player`() = runTest {
        val generator = DeterministicScoreGenerator(
            players = players,
            config = ScoreEngineConfig(
                seed = 7L,
                minDelayMillis = 0L,
                maxDelayMillis = 0L,
                minScoreIncrement = 1,
                maxScoreIncrement = 3
            )
        )

        val updates = generator.updates().take(12).toList()
        val lastScoreByPlayer = mutableMapOf<String, Int>()

        updates.forEach { update ->
            val previous = lastScoreByPlayer[update.playerId] ?: 0
            assertTrue(update.newScore > previous)
            lastScoreByPlayer[update.playerId] = update.newScore
        }
    }
}
