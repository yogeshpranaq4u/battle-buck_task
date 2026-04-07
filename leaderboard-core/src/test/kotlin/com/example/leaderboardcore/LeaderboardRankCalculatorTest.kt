package com.example.leaderboardcore

import com.example.scoreengine.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class LeaderboardRankCalculatorTest {

    private val players = listOf(
        Player(id = "p1", displayName = "Astra"),
        Player(id = "p2", displayName = "Blaze"),
        Player(id = "p3", displayName = "Cipher"),
        Player(id = "p4", displayName = "Dune")
    )

    @Test
    fun `same score shares rank and next rank is skipped`() {
        val result = LeaderboardRankCalculator().rank(
            players = players,
            scoreByPlayerId = mapOf(
                "p1" to 150,
                "p2" to 220,
                "p3" to 220,
                "p4" to 120
            )
        )

        assertEquals(listOf(1, 1, 3, 4), result.map { it.rank })
        assertEquals(listOf("Blaze", "Cipher", "Astra", "Dune"), result.map { it.displayName })
    }

    @Test
    fun `tie ordering is stable by display name then id`() {
        val result = LeaderboardRankCalculator().rank(
            players = listOf(
                Player(id = "p2", displayName = "Rogue"),
                Player(id = "p1", displayName = "Rogue")
            ),
            scoreByPlayerId = mapOf("p1" to 99, "p2" to 99)
        )

        assertEquals(listOf("p1", "p2"), result.map { it.playerId })
        assertEquals(listOf(1, 1), result.map { it.rank })
    }
}
