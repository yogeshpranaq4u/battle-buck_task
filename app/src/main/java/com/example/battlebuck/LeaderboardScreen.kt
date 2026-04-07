package com.example.battlebuck

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battlebuck.ui.theme.ColorWhite
import com.example.battlebuck.ui.theme.HeaderAccent
import com.example.battlebuck.ui.theme.HeaderSubtext
import com.example.battlebuck.ui.theme.HeaderWatermark
import com.example.battlebuck.ui.theme.InfoBadgeBg

@Composable
fun LeaderboardScreen() {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as BattlebuckApplication).container
    val viewModel: LeaderboardViewModel = viewModel(
        factory = remember(appContainer) { LeaderboardViewModelFactory(appContainer) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId
    val currentUser = uiState.rows.firstOrNull { it.id == currentUserId }
    val leaderboardRows = uiState.rows
        .filterNot { it.id == currentUserId }
        .sortedBy { it.id }

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val collapseDistancePx = with(density) { 140.dp.toPx() }
    val headerCollapseProgress by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (listState.firstVisibleItemScrollOffset / collapseDistancePx).coerceIn(0f, 1f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ExactAppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            LeaderboardTopHeader()

            Text(
                text = stringResource(id = R.string.label_season_ends),
                color = HeaderSubtext,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(LeaderboardUiTokens.subtitleToLegendSpacing))

            AnimatedLegendSummary(
                currentUser = currentUser,
                progress = headerCollapseProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LeaderboardUiTokens.horizontalPadding)
            )

            Spacer(modifier = Modifier.height(LeaderboardUiTokens.legendToTitleSpacing))

            LeaderboardTitleBar()

            Spacer(modifier = Modifier.height(LeaderboardUiTokens.titleToListSpacing))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = LeaderboardUiTokens.horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(leaderboardRows, key = { _, it -> it.id }) { index, row ->
                    ExactLeaderboardRow(
                        row = row,
                        index = index
                    )
                }

                if (currentUser != null) {
                    item(key = "current-user-row") {
                        Spacer(modifier = Modifier.height(LeaderboardUiTokens.currentUserTopSpacing))
                        ExactLeaderboardRow(
                            row = currentUser,
                            index = leaderboardRows.size,
                            isCurrentUser = true
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(LeaderboardUiTokens.listBottomSpacing)) }
            }
        }
    }
}

@Composable
private fun LeaderboardTopHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LeaderboardUiTokens.topHeaderHeight)
    ) {
        Text(
            text = stringResource(id = R.string.label_legends),
            color = HeaderWatermark,
            fontSize = 58.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .scale(scaleX = 0.75f, scaleY = 1.4f)
                .offset(x = 24.dp),
            letterSpacing = (-0.5).sp
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(HeaderAccent)
                .padding(start = 24.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.9f, size.height * 0.05f)
                    lineTo(size.width * 0.2f, size.height * 0.5f)
                    lineTo(size.width * 0.9f, size.height * 0.95f)
                }
                drawPath(
                    path = path,
                    color = ColorWhite,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = stringResource(id = R.string.label_genesis_season),
                color = ColorWhite,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.width(18.dp))

            Canvas(modifier = Modifier.width(10.dp).height(6.dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width * 0.5f, size.height)
                    close()
                }
                drawPath(path, ColorWhite)
            }
        }
    }
}

@Composable
private fun LeaderboardTitleBar() {
    Row(
        modifier = Modifier.padding(horizontal = LeaderboardUiTokens.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.label_leaderboard),
            color = ColorWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(InfoBadgeBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.label_info),
                color = ColorWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
