package com.example.scoreengine

data class ScoreEngineConfig(
    val seed: Long,
    val minDelayMillis: Long = 500L,
    val maxDelayMillis: Long = 2_000L,
    val minScoreIncrement: Int = 5,
    val maxScoreIncrement: Int = 35
) {
    init {
        require(minDelayMillis >= 0L) { "minDelayMillis must be >= 0." }
        require(maxDelayMillis >= minDelayMillis) { "maxDelayMillis must be >= minDelayMillis." }
        require(minScoreIncrement > 0) { "minScoreIncrement must be > 0." }
        require(maxScoreIncrement >= minScoreIncrement) {
            "maxScoreIncrement must be >= minScoreIncrement."
        }
    }
}
