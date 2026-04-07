package com.example.battlebuck

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.battlebuck.ui.theme.ColorWhite
import com.example.battlebuck.ui.theme.EmblemFlameHigh
import com.example.battlebuck.ui.theme.EmblemFlameLow
import com.example.battlebuck.ui.theme.EmblemGlow
import com.example.battlebuck.ui.theme.EmblemHood
import com.example.battlebuck.ui.theme.EmblemInnerHigh
import com.example.battlebuck.ui.theme.EmblemInnerLow
import com.example.battlebuck.ui.theme.EmblemOuterHigh
import com.example.battlebuck.ui.theme.EmblemOuterLow
import com.example.battlebuck.ui.theme.RayGlow
import com.example.battlebuck.ui.theme.ScreenBg
import com.example.battlebuck.ui.theme.SpotlightBottom
import com.example.battlebuck.ui.theme.SpotlightGlow
import com.example.battlebuck.ui.theme.SpotlightTop
import kotlin.math.roundToInt

@Composable
internal fun AnimatedLegendSummary(
    currentUser: LeaderboardRowUiModel?,
    progress: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val emblemSize = lerp(96.dp, 56.dp, progress)
        val compactY = 10.dp
        val expandedY = 0.dp
        val emblemY = lerp(expandedY, compactY, progress)
        val startX = (maxWidth - emblemSize) / 2
        val endX = 8.dp
        val emblemX = lerp(startX, endX, progress)

        val chipsBaseWidth = 196.dp
        val chipShiftProgress = ((progress - 0.72f) / 0.28f).coerceIn(0f, 1f)
        val centeredChipsX = (maxWidth - chipsBaseWidth) / 2
        val chipsEndX = 76.dp
        val chipsShiftX = lerp(0.dp, chipsEndX - centeredChipsX, chipShiftProgress)
        val chipsY = lerp(emblemSize + 10.dp, 16.dp, progress)
        val summaryHeight = lerp(
            LeaderboardUiTokens.legendSummaryExpandedHeight,
            LeaderboardUiTokens.legendSummaryCollapsedHeight,
            progress
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(summaryHeight)
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = emblemX.roundToPx(),
                            y = emblemY.roundToPx()
                        )
                    }
                    .size(emblemSize)
            ) {
                ExactHeroEmblem(modifier = Modifier.fillMaxSize())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(
                            x = chipsShiftX.roundToPx(),
                            y = chipsY.roundToPx()
                        )
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RankChip(rank = currentUser?.rank ?: 7, progress = progress)
                Spacer(modifier = Modifier.width(10.dp))
                TrophyChip(score = currentUser?.score ?: 1100, progress = progress)
            }
        }
    }
}

@Composable
internal fun ExactAppBackground() {
    val transition = rememberInfiniteTransition(label = "bg")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "bgRot"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(ScreenBg)

        val cx = size.width / 2f
        val cy = size.height * 0.25f

        withTransform({
            translate(left = cx, top = cy)
            rotate(rotation)
            translate(left = -cx, top = -cy)
        }) {
            val rayPath = Path().apply {
                moveTo(cx, cy)
                lineTo(cx - size.width * 1.5f, cy + size.height * 1.5f)
                lineTo(cx + size.width * 0.5f, cy + size.height * 1.5f)
                close()

                moveTo(cx, cy)
                lineTo(cx + size.width * 1.5f, cy - size.height * 1.5f)
                lineTo(cx - size.width * 0.5f, cy - size.height * 1.5f)
                close()
            }
            drawPath(
                path = rayPath,
                brush = Brush.radialGradient(
                    colors = listOf(RayGlow, Color.Transparent),
                    center = Offset(cx, cy),
                    radius = size.width * 1.2f
                )
            )
        }

        val spotlightCenter = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width * 0.65f, size.height * 0.85f)
            lineTo(size.width * 0.35f, size.height * 0.85f)
            close()
        }

        drawPath(
            path = spotlightCenter,
            brush = Brush.verticalGradient(
                colors = listOf(
                    SpotlightTop,
                    SpotlightBottom
                )
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SpotlightGlow, Color.Transparent),
                center = Offset(size.width / 2f, size.height * 0.25f),
                radius = size.width * 0.6f
            ),
            center = Offset(size.width / 2f, size.height * 0.25f)
        )
    }
}

@Composable
internal fun ExactHeroEmblem(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "emblem")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.60f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val floatOffset by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    val flameScaleY by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flame"
    )

    Box(
        modifier = modifier.offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EmblemGlow.copy(alpha = glowAlpha), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension * 0.6f
                )
            )
        }

        Canvas(modifier = Modifier.fillMaxSize(0.72f)) {
            val w = size.width
            val h = size.height

            val outerShield = Path().apply {
                moveTo(w * 0.5f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.35f)
                lineTo(w * 0.70f, h * 0.85f)
                lineTo(w * 0.5f, h)
                lineTo(w * 0.30f, h * 0.85f)
                lineTo(w * 0.15f, h * 0.35f)
                close()
            }

            val innerShield = Path().apply {
                moveTo(w * 0.5f, h * 0.28f)
                lineTo(w * 0.73f, h * 0.42f)
                lineTo(w * 0.62f, h * 0.78f)
                lineTo(w * 0.5f, h * 0.90f)
                lineTo(w * 0.38f, h * 0.78f)
                lineTo(w * 0.27f, h * 0.42f)
                close()
            }

            val flame = Path().apply {
                moveTo(w * 0.50f, 0f)
                quadraticTo(w * 0.60f, h * 0.15f, w * 0.65f, h * 0.25f)
                quadraticTo(w * 0.55f, h * 0.22f, w * 0.50f, h * 0.30f)
                quadraticTo(w * 0.40f, h * 0.18f, w * 0.35f, h * 0.25f)
                quadraticTo(w * 0.40f, h * 0.10f, w * 0.50f, 0f)
                close()
            }

            val hood = Path().apply {
                moveTo(w * 0.40f, h * 0.45f)
                lineTo(w * 0.60f, h * 0.45f)
                lineTo(w * 0.55f, h * 0.80f)
                lineTo(w * 0.45f, h * 0.80f)
                close()
            }

            withTransform({
                scale(scaleX = 1f, scaleY = flameScaleY, pivot = Offset(w * 0.5f, h * 0.25f))
            }) {
                drawPath(flame, Brush.verticalGradient(listOf(EmblemFlameHigh, EmblemFlameLow)))
            }

            drawPath(outerShield, Brush.verticalGradient(listOf(EmblemOuterHigh, EmblemOuterLow)))
            drawPath(innerShield, Brush.verticalGradient(listOf(EmblemInnerHigh, EmblemInnerLow)))
            drawPath(hood, EmblemHood)

            drawRect(ColorWhite, Offset(w * 0.44f, h * 0.55f), Size(w * 0.04f, h * 0.05f))
            drawRect(ColorWhite, Offset(w * 0.52f, h * 0.55f), Size(w * 0.04f, h * 0.05f))
        }
    }
}
