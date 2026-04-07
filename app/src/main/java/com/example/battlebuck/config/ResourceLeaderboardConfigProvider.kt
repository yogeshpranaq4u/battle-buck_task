package com.example.battlebuck.config

import android.content.Context
import com.example.battlebuck.BuildConfig
import com.example.battlebuck.R
import com.example.scoreengine.Player
import com.example.scoreengine.ScoreEngineConfig
import org.json.JSONArray

internal class ResourceLeaderboardConfigProvider(
    private val context: Context
) : LeaderboardConfigProvider {

    override fun getConfig(): LeaderboardConfig {
        val players = loadPlayers()
        val scoreConfig = ScoreEngineConfig(
            seed = BuildConfig.LEADERBOARD_SEED,
            minDelayMillis = BuildConfig.LEADERBOARD_MIN_DELAY_MS,
            maxDelayMillis = BuildConfig.LEADERBOARD_MAX_DELAY_MS,
            minScoreIncrement = BuildConfig.LEADERBOARD_MIN_INCREMENT,
            maxScoreIncrement = BuildConfig.LEADERBOARD_MAX_INCREMENT
        )
        return LeaderboardConfig(players = players, scoreEngineConfig = scoreConfig)
    }

    private fun loadPlayers(): List<Player> {
        val rawJson = context.resources
            .openRawResource(R.raw.players)
            .bufferedReader()
            .use { it.readText() }

        val playersArray = JSONArray(rawJson)
        return buildList(playersArray.length()) {
            for (index in 0 until playersArray.length()) {
                val playerJson = playersArray.getJSONObject(index)
                add(
                    Player(
                        id = playerJson.getString("id"),
                        displayName = playerJson.getString("displayName")
                    )
                )
            }
        }
    }
}
