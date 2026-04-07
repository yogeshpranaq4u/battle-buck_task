package com.example.battlebuck

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battlebuck.ui.theme.AvatarBandEven
import com.example.battlebuck.ui.theme.AvatarBandOdd
import com.example.battlebuck.ui.theme.AvatarBg1
import com.example.battlebuck.ui.theme.AvatarBg2
import com.example.battlebuck.ui.theme.AvatarBg3
import com.example.battlebuck.ui.theme.AvatarBg4
import com.example.battlebuck.ui.theme.AvatarHood
import com.example.battlebuck.ui.theme.ColorWhite
import com.example.battlebuck.ui.theme.RankChipBgFrom
import com.example.battlebuck.ui.theme.RankChipBgTo
import com.example.battlebuck.ui.theme.RankChipBorderFrom
import com.example.battlebuck.ui.theme.RankChipBorderTo
import com.example.battlebuck.ui.theme.RankShiftActive
import com.example.battlebuck.ui.theme.RankShiftIdle
import com.example.battlebuck.ui.theme.RowCurrent
import com.example.battlebuck.ui.theme.RowCurrentSlope
import com.example.battlebuck.ui.theme.RowCurrentText
import com.example.battlebuck.ui.theme.RowRankText
import com.example.battlebuck.ui.theme.RowStripeA
import com.example.battlebuck.ui.theme.RowStripeB
import com.example.battlebuck.ui.theme.ScorePillBg
import com.example.battlebuck.ui.theme.TrophyChipBg
import com.example.battlebuck.ui.theme.TrophyChipBorderFrom
import com.example.battlebuck.ui.theme.TrophyChipBorderTo
import com.example.battlebuck.ui.theme.TrophyChipTextFrom
import com.example.battlebuck.ui.theme.TrophyChipTextTo
import com.example.battlebuck.ui.theme.TrophyGold

@Composable
internal fun ExactLeaderboardRow(
    row: LeaderboardRowUiModel,
    index: Int,
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    val baseColor = if (isCurrentUser) {
        RowCurrent
    } else if (index % 2 == 0) {
        RowStripeA
    } else {
        RowStripeB
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LeaderboardUiTokens.rowHeight)
            .background(baseColor)
    ) {
        if (isCurrentUser) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val slope = Path().apply {
                    moveTo(size.width * 0.66f, size.height)
                    lineTo(size.width * 0.82f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(slope, RowCurrentSlope)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExactAvatarBox(index = row.rank)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${row.rank}.",
                color = RowRankText,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                modifier = Modifier.width(28.dp)
            )

            Text(
                text = row.username,
                color = if (isCurrentUser) RowCurrentText else ColorWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (row.rankShift > 0) {
                ExactRankShiftIndicator(
                    modifier = Modifier.size(width = 12.dp, height = 18.dp),
                    active = row.updateToken != null || isCurrentUser
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(ScorePillBg.copy(alpha = if (isCurrentUser) 0.72f else 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                TrophyIcon(
                    modifier = Modifier.size(16.dp),
                    tint = TrophyGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                ExactAnimatedScoreText(score = row.score)
            }
        }
    }
}

@Composable
internal fun RankChip(rank: Int, progress: Float) {
    val background = lerp(RankChipBgFrom, RankChipBgTo, progress)
    val border = lerp(RankChipBorderFrom, RankChipBorderTo, progress)

    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(50))
            .border(1.dp, border, RoundedCornerShape(50))
            .padding(horizontal = 18.dp, vertical = 7.dp)
    ) {
        Text(
            text = formatOrdinal(rank),
            color = ColorWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
}

@Composable
internal fun TrophyChip(score: Int, progress: Float) {
    val border = lerp(TrophyChipBorderFrom, TrophyChipBorderTo, progress)
    val textColor = lerp(TrophyChipTextFrom, TrophyChipTextTo, progress)

    Row(
        modifier = Modifier
            .background(TrophyChipBg, RoundedCornerShape(50))
            .border(1.5.dp, border, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrophyIcon(
            modifier = Modifier.size(16.dp),
            tint = TrophyGold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = score.toString(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
}

@Composable
internal fun TrophyIcon(
    modifier: Modifier = Modifier,
    tint: Color
) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.1f
        val cupLeft = size.width * 0.28f
        val cupTop = size.height * 0.22f
        val cupWidth = size.width * 0.44f
        val cupHeight = size.height * 0.3f

        drawRoundRect(
            color = tint,
            topLeft = Offset(cupLeft, cupTop),
            size = Size(cupWidth, cupHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke, stroke)
        )

        drawRect(
            color = tint,
            topLeft = Offset(size.width * 0.46f, size.height * 0.52f),
            size = Size(size.width * 0.08f, size.height * 0.16f)
        )

        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width * 0.32f, size.height * 0.68f),
            size = Size(size.width * 0.36f, size.height * 0.1f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke, stroke)
        )

        val leftHandle = Path().apply {
            moveTo(cupLeft, cupTop + stroke)
            cubicTo(
                size.width * 0.05f, size.height * 0.18f,
                size.width * 0.05f, size.height * 0.48f,
                cupLeft, size.height * 0.48f
            )
        }
        val rightHandle = Path().apply {
            moveTo(cupLeft + cupWidth, cupTop + stroke)
            cubicTo(
                size.width * 0.95f, size.height * 0.18f,
                size.width * 0.95f, size.height * 0.48f,
                cupLeft + cupWidth, size.height * 0.48f
            )
        }

        drawPath(
            path = leftHandle,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        drawPath(
            path = rightHandle,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
    }
}

@Composable
internal fun ExactRankShiftIndicator(
    modifier: Modifier = Modifier,
    active: Boolean
) {
    val arrowColor = if (active) RankShiftActive else RankShiftIdle

    Canvas(modifier = modifier) {
        fun drawChevron(top: Float) {
            val path = Path().apply {
                moveTo(size.width * 0.2f, top + size.height * 0.2f)
                lineTo(size.width * 0.5f, top)
                lineTo(size.width * 0.8f, top + size.height * 0.2f)
            }
            drawPath(
                path = path,
                color = arrowColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = size.width * 0.22f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }

        drawChevron(size.height * 0.18f)
        drawChevron(size.height * 0.48f)
    }
}

@Composable
private fun ExactAvatarBox(index: Int) {
    val bgColors = listOf(AvatarBg1, AvatarBg2, AvatarBg3, AvatarBg4)
    val bgColor = bgColors[(index - 1).mod(bgColors.size)]

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hood = Path().apply {
                moveTo(size.width * 0.25f, size.height)
                lineTo(size.width * 0.35f, size.height * 0.35f)
                lineTo(size.width * 0.65f, size.height * 0.35f)
                lineTo(size.width * 0.75f, size.height)
                close()
            }
            drawPath(hood, AvatarHood)

            drawRect(ColorWhite, Offset(size.width * 0.40f, size.height * 0.5f), Size(size.width * 0.06f, size.height * 0.05f))
            drawRect(ColorWhite, Offset(size.width * 0.54f, size.height * 0.5f), Size(size.width * 0.06f, size.height * 0.05f))

            if (index % 2 == 0) {
                drawRect(AvatarBandEven, Offset(size.width * 0.27f, size.height * 0.2f), Size(size.width * 0.46f, size.height * 0.15f))
            } else {
                drawRect(AvatarBandOdd, Offset(size.width * 0.31f, size.height * 0.2f), Size(size.width * 0.38f, size.height * 0.15f))
            }
        }
    }
}

@Composable
private fun ExactAnimatedScoreText(score: Int) {
    var displayScore by remember { mutableIntStateOf(score) }
    val animatable = remember { Animatable(score.toFloat()) }

    LaunchedEffect(score) {
        animatable.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }
    displayScore = animatable.value.toInt()

    Text(
        text = displayScore.toString(),
        color = ColorWhite,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        modifier = Modifier.width(42.dp),
        textAlign = TextAlign.End
    )
}

private fun formatOrdinal(n: Int): String {
    val suffix = when {
        n % 100 in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}
