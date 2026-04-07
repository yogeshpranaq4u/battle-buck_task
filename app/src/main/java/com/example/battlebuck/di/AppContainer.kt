package com.example.battlebuck.di

import android.content.Context
import com.example.battlebuck.BuildConfig
import com.example.battlebuck.LeaderboardModuleFactory
import com.example.battlebuck.LeaderboardRepository
import com.example.battlebuck.config.LeaderboardConfigProvider
import com.example.battlebuck.config.ResourceLeaderboardConfigProvider
import com.example.battlebuck.session.BuildConfigSessionProvider
import com.example.battlebuck.session.SessionProvider

internal interface AppContainer {
    val configProvider: LeaderboardConfigProvider
    val sessionProvider: SessionProvider
    val moduleFactory: LeaderboardModuleFactory
    val leaderboardRepository: LeaderboardRepository
}

internal class DefaultAppContainer(context: Context) : AppContainer {
    override val configProvider: LeaderboardConfigProvider =
        ResourceLeaderboardConfigProvider(context)

    override val sessionProvider: SessionProvider =
        BuildConfigSessionProvider(currentUserId = BuildConfig.CURRENT_USER_ID)

    override val moduleFactory: LeaderboardModuleFactory =
        LeaderboardModuleFactory(configProvider = configProvider)

    override val leaderboardRepository: LeaderboardRepository =
        LeaderboardRepository(moduleFactory = moduleFactory)
}
