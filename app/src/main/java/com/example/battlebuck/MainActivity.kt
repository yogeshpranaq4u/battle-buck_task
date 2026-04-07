package com.example.battlebuck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.battlebuck.ui.theme.*
import com.example.battlebuck.ui.theme.BattlebuckTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            BattlebuckTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlack
                ) {
                    LeaderboardScreen()
                }
            }
        }
    }
}

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ExactAppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Header: Genesis Season + LEGENDS watermark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            ) {
                // Background Watermark LEGENDS (Stretched to match concise Impact font)
                Text(
                    text = stringResource(id = R.string.label_legends),
                    color = HeaderWatermark,
                    fontSize = 58.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .scale(scaleX = 0.75f, scaleY = 1.4f) // Crucial to match tall, bold, condensed font
                        .offset(x = 24.dp), // Push it slightly right so the "L" aligns behind the dropdown
                    letterSpacing = (-0.5).sp
                )

                // Left flush GENESIS SEASON card (Exactly like reference)
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)) // Flat on left, rounded on right
                        .background(HeaderAccent) // Exact intense orange from reference
                        .padding(start = 24.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left back chevron
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
                                width = 2.dp.toPx(), // Slightly thinner
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
                    
                    // Right dropdown solid triangle
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

            // Subtitle
            Text(
                text = stringResource(id = R.string.label_season_ends),
                color = HeaderSubtext,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedLegendSummary(
                currentUser = currentUser,
                progress = headerCollapseProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
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
                        Spacer(modifier = Modifier.height(10.dp))
                        ExactLeaderboardRow(
                            row = currentUser,
                            index = leaderboardRows.size,
                            isCurrentUser = true
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun AnimatedLegendSummary(
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
        val summaryHeight = lerp(138.dp, 84.dp, progress)

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

// ── Background Geometry ─────────────────────────────────────────────────────────

@Composable
private fun ExactAppBackground() {
    val transition = rememberInfiniteTransition(label = "bg")
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "bgRot"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Base very dark off-black
        drawRect(ScreenBg)
        
        val cx = size.width / 2f
        val cy = size.height * 0.25f
        
        withTransform({
            translate(left = cx, top = cy)
            rotate(rotation)
            translate(left = -cx, top = -cy)
        }) {
            // Rotating sunburst rays mimicking the spotlight wedges
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
        
        // Static central spotlight V going down to ground it
        val spotlightCenter = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width * 0.65f, size.height * 0.85f)
            lineTo(size.width * 0.35f, size.height * 0.85f)
            close()
        }

        // Draw the large V spotlight gradient
        drawPath(
            path = spotlightCenter,
            brush = Brush.verticalGradient(
                colors = listOf(
                    SpotlightTop, // Top bright orange-red
                    SpotlightBottom // Bottom fade
                )
            )
        )
        
        // Overlay a slight soft red glow in the exact center behind the emblem
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

// ── Hero Emblem ─────────────────────────────────────────────────────────────────

@Composable
private fun ExactHeroEmblem(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "emblem")
    val glowAlpha by transition.animateFloat(
        initialValue = 0.30f, targetValue = 0.60f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    
    val floatOffset by transition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    
    val flameScaleY by transition.animateFloat(
        initialValue = 0.9f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flame"
    )

    Box(
        modifier = modifier
            .offset(y = floatOffset.dp), // Levitating animation!
        contentAlignment = Alignment.Center
    ) {
        // Red glow behind emblem
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EmblemGlow.copy(alpha = glowAlpha), Color.Transparent),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension * 0.6f
                )
            )
        }
        
        // Exact geometric shield from reference
        Canvas(modifier = Modifier.fillMaxSize(0.72f)) {
            val w = size.width
            val h = size.height
            
            // Outer Shield Geometry (Deep Red-Orange)
            val outerShield = Path().apply {
                moveTo(w * 0.5f, h * 0.15f) // top point
                lineTo(w * 0.85f, h * 0.35f) // top right
                lineTo(w * 0.70f, h * 0.85f) // bottom right
                lineTo(w * 0.5f, h) // bottom point
                lineTo(w * 0.30f, h * 0.85f) // bottom left
                lineTo(w * 0.15f, h * 0.35f) // top left
                close()
            }
            
            // Inner Shield Geometry (Bright Orange-Yellow)
            val innerShield = Path().apply {
                moveTo(w * 0.5f, h * 0.28f)
                lineTo(w * 0.73f, h * 0.42f)
                lineTo(w * 0.62f, h * 0.78f)
                lineTo(w * 0.5f, h * 0.90f)
                lineTo(w * 0.38f, h * 0.78f)
                lineTo(w * 0.27f, h * 0.42f)
                close()
            }
            
            // Tall Flame on Top
            val flame = Path().apply {
                moveTo(w * 0.50f, 0f) // Tippy top
                quadraticTo(w * 0.60f, h * 0.15f, w * 0.65f, h * 0.25f)
                quadraticTo(w * 0.55f, h * 0.22f, w * 0.50f, h * 0.30f)
                quadraticTo(w * 0.40f, h * 0.18f, w * 0.35f, h * 0.25f)
                quadraticTo(w * 0.40f, h * 0.10f, w * 0.50f, 0f)
                close()
            }
            
            // Center Hood / Mask
            val hood = Path().apply {
                moveTo(w * 0.40f, h * 0.45f)
                lineTo(w * 0.60f, h * 0.45f)
                lineTo(w * 0.55f, h * 0.80f)
                lineTo(w * 0.45f, h * 0.80f)
                close()
            }

            // Draw with breathing flame animation
            withTransform({
                scale(scaleX = 1f, scaleY = flameScaleY, pivot = Offset(w * 0.5f, h * 0.25f))
            }) {
                drawPath(flame, Brush.verticalGradient(listOf(EmblemFlameHigh, EmblemFlameLow)))
            }
            
            drawPath(outerShield, Brush.verticalGradient(listOf(EmblemOuterHigh, EmblemOuterLow)))
            drawPath(innerShield, Brush.verticalGradient(listOf(EmblemInnerHigh, EmblemInnerLow)))
            drawPath(hood, EmblemHood) // Dark red-black face
            
            // Eyes
            drawRect(ColorWhite, Offset(w * 0.44f, h * 0.55f), Size(w * 0.04f, h * 0.05f))
            drawRect(ColorWhite, Offset(w * 0.52f, h * 0.55f), Size(w * 0.04f, h * 0.05f))
        }
    }
}

// ── Leaderboard Row ─────────────────────────────────────────────────────────────

@Composable
private fun ExactLeaderboardRow(
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
            .height(64.dp)
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
        // Exact simple avatar boxes
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
private fun RankChip(rank: Int, progress: Float) {
    val background = lerp(RankChipBgFrom, RankChipBgTo, progress)
    val border = lerp(RankChipBorderFrom, RankChipBorderTo, progress)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
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
private fun TrophyChip(score: Int, progress: Float) {
    val border = lerp(TrophyChipBorderFrom, TrophyChipBorderTo, progress)
    val textColor = lerp(TrophyChipTextFrom, TrophyChipTextTo, progress)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(TrophyChipBg)
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
private fun TrophyIcon(
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
private fun ExactRankShiftIndicator(
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
            val hoodColor = AvatarHood
            
            // Simple tapered hood silhoutte
            val hood = Path().apply {
                moveTo(size.width * 0.25f, size.height)
                lineTo(size.width * 0.35f, size.height * 0.35f)
                lineTo(size.width * 0.65f, size.height * 0.35f)
                lineTo(size.width * 0.75f, size.height)
                close()
            }
            drawPath(hood, hoodColor)
            
            // Eyes
            drawRect(ColorWhite, Offset(size.width * 0.40f, size.height * 0.5f), Size(size.width * 0.06f, size.height * 0.05f))
            drawRect(ColorWhite, Offset(size.width * 0.54f, size.height * 0.5f), Size(size.width * 0.06f, size.height * 0.05f))
            
            // Colored bandana line for variations
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

// ── Helpers ─────────────────────────────────────────────────────────────────────

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
