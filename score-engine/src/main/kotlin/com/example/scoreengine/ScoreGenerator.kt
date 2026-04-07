package com.example.scoreengine

import kotlinx.coroutines.flow.Flow

interface ScoreGenerator {
    val players: List<Player>

    fun updates(): Flow<ScoreUpdate>
}
