package com.example.leaderboardcore

import com.example.scoreengine.Player
import com.example.scoreengine.ScoreUpdate
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RealTimeLeaderboardTest {

    private val players = listOf(
        Player(id = "p1", displayName = "Astra"),
        Player(id = "p2", displayName = "Blaze"),
        Player(id = "p3", displayName = "Cipher")
    )

    @Test
    fun `stream applies score updates and ignores stale scores`() = runTest {
        val leaderboard = RealTimeLeaderboard(
            players = players,
            computationDispatcher = StandardTestDispatcher(testScheduler)
        )

        val snapshots = leaderboard.stream(
            flowOf(
                ScoreUpdate(sequence = 1, playerId = "p1", delta = 20, newScore = 20),
                ScoreUpdate(sequence = 2, playerId = "p2", delta = 30, newScore = 30),
                ScoreUpdate(sequence = 3, playerId = "p2", delta = 0, newScore = 25),
                ScoreUpdate(sequence = 4, playerId = "p1", delta = 15, newScore = 35)
            )
        ).toList()

        val finalSnapshot = snapshots.last()

        assertEquals(listOf("Astra", "Blaze", "Cipher"), finalSnapshot.entries.map { it.displayName })
        assertEquals(listOf(35, 30, 0), finalSnapshot.entries.map { it.score })
        assertEquals("p1", finalSnapshot.lastUpdatedPlayerId)
        assertEquals(4L, finalSnapshot.lastUpdateSequence)
    }
}
