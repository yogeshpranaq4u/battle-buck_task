package com.example.scoreengine

data class ScoreUpdate(
    val sequence: Long,
    val playerId: String,
    val delta: Int,
    val newScore: Int
)
