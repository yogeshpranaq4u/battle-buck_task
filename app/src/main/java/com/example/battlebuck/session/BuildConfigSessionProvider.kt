package com.example.battlebuck.session

internal class BuildConfigSessionProvider(
    private val currentUserId: String
) : SessionProvider {

    override fun currentUserId(): String = currentUserId
}
