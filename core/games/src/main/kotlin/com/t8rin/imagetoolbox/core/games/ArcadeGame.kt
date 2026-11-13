/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.games

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.games.components.LocalArcadeButton
import com.t8rin.imagetoolbox.core.games.components.longPress
import com.t8rin.imagetoolbox.core.games.di.GamesEntryPoint
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Pause
import com.t8rin.imagetoolbox.core.resources.icons.PlayCircle
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.serializer

@Composable
fun ArcadeGameContent(
    game: ArcadeGame,
    modifier: Modifier = Modifier,
    height: Dp = Dp.Unspecified,
    isPaused: Boolean = false,
    allowNewRound: Boolean = true,
    onPlayingChanged: ((Boolean) -> Unit)? = null,
    initialState: ArcadeGameState = ArcadeGameState()
) {
    val style = ArcadeDefaults.style.let {
        if (height == Dp.Unspecified) it else it.copy(gameHeight = height)
    }
    val context = ArcadeGameContext(
        name = game.name,
        initialState = initialState,
        style = style,
        isPaused = isPaused,
        allowNewRound = allowNewRound,
        onPlayingChanged = onPlayingChanged
    )
    key(game.name) {
        game.definition.Content(context = context, modifier = modifier)
    }
}

@Immutable
internal data class ArcadeGameContext(
    val name: String,
    val initialState: ArcadeGameState,
    val style: ArcadeStyle,
    val isPaused: Boolean,
    val allowNewRound: Boolean,
    val onPlayingChanged: ((Boolean) -> Unit)?
)

@Immutable
data class ArcadeGameState(
    val highScore: Int = 0,
    val isPlaying: Boolean = false
)

@Immutable
data class ArcadeStyle(
    val shape: Shape,
    val background: Brush,
    val borderColor: Color,
    val playerColor: Color,
    val playerDetailColor: Color,
    val obstacleColor: Color,
    val accentColor: Color,
    val detailColor: Color,
    val textColor: Color,
    val highScoreColor: Color,
    val scoreTextStyle: TextStyle,
    val overlayColor: Color,
    val buttonColor: Color,
    val buttonContentColor: Color,
    val gameHeight: Dp,
    val hazardColor: Color = accentColor
)

object ArcadeDefaults {
    val style: ArcadeStyle
        @Composable
        get() {
            val colors = MaterialTheme.colorScheme
            val shape = MaterialTheme.shapes.extraLarge
            val typography = MaterialTheme.typography
            return remember(colors, shape, typography) {
                ArcadeStyle(
                    shape = shape,
                    background = Brush.verticalGradient(
                        listOf(colors.surfaceContainerLow, colors.surfaceContainer)
                    ),
                    borderColor = colors.outlineVariant,
                    playerColor = colors.primary,
                    playerDetailColor = colors.onPrimary,
                    obstacleColor = colors.secondary,
                    accentColor = colors.tertiary,
                    detailColor = colors.outline.copy(alpha = 0.42f),
                    textColor = colors.onSurface,
                    highScoreColor = colors.onSurfaceVariant,
                    scoreTextStyle = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    overlayColor = colors.scrim.copy(alpha = 0.5f),
                    buttonColor = colors.tertiaryContainer,
                    buttonContentColor = colors.onTertiaryContainer,
                    gameHeight = 280.dp,
                    hazardColor = colors.error
                )
            }
        }
}

@Serializable
internal enum class ArcadePhase { Ready, Running, Paused, Over }

internal const val LevelTransitionDuration = 0.24f

@Serializable
internal abstract class ArcadeRun {
    var phase by mutableStateOf(ArcadePhase.Ready)
    var gameOverReported = false
    var statsRoundStarted = false
    var score by mutableIntStateOf(0)
    var frame by mutableIntStateOf(0)
        private set
    var isAnimating by mutableStateOf(false)
    var levelTransition by mutableFloatStateOf(0f)
    var prepared by mutableStateOf(false)
    var previousBest = -1

    @Transient
    var startWhenReady = false
    var topInset = 44f
        private set
    var width = 360f
        private set
    var height = 240f
        private set

    fun prepare() {
        stopInput()
        score = 0
        isAnimating = false
        levelTransition = 0f
        gameOverReported = false
        statsRoundStarted = false
        phase = ArcadePhase.Ready
        reset()
        prepared = true
        redraw()
    }

    fun start() {
        if (!prepared || phase == ArcadePhase.Over || phase == ArcadePhase.Running) prepare()
        phase = ArcadePhase.Running
        redraw()
    }

    fun pause() {
        if (phase != ArcadePhase.Running) return
        phase = ArcadePhase.Paused
        stopInput()
    }

    fun finish() {
        stopInput()
        phase = ArcadePhase.Over
        redraw()
    }

    fun animateLevel() {
        if (phase == ArcadePhase.Running) {
            stopInput()
            levelTransition = LevelTransitionDuration
        }
    }

    fun redraw() {
        frame++
    }

    fun resize(
        width: Float,
        height: Float,
        topInset: Float = this.topInset
    ) {
        if (prepared && width == this.width && height == this.height &&
            topInset == this.topInset
        ) return
        this.topInset = topInset
        val previous = Size(width = this.width, height = this.height)
        this.width = width
        this.height = height
        if (prepared) onResize(previous)
        else prepare()
        redraw()
    }

    abstract fun reset()

    open fun isValid(): Boolean = width.isFinite() && height.isFinite() &&
            topInset.isFinite() &&
            width >= 120f && height > topInset + 60f

    open fun onRestore() = Unit
    open fun onResize(previous: Size) = Unit
    open fun stopInput() = Unit
    open fun advance(seconds: Float) = Unit
}

@Composable
internal inline fun <reified T : ArcadeRun> rememberArcadeRun(
    startPlaying: Boolean,
    noinline create: () -> T
): T = rememberSaveable(saver = arcadeRunSaver(serializer<T>())) {
    create().apply { startWhenReady = startPlaying }
}

@Composable
internal fun ArcadeBoard(
    context: ArcadeGameContext,
    run: ArcadeRun,
    modifier: Modifier,
    turnBased: Boolean = false,
    field: @Composable () -> Unit
) {
    val game = context.name
    val style = context.style
    val initialState = context.initialState
    val isPaused = context.isPaused
    val allowNewRound = context.allowNewRound
    val playingChanged by rememberUpdatedState(context.onPlayingChanged)
    val phase = run.phase
    var starting by rememberSaveable(run) { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val score = run.score
    var record by rememberSaveable { mutableIntStateOf(initialState.highScore.coerceAtLeast(0)) }
    val isPreview = LocalInspectionMode.current
    val storage = remember(isPreview) {
        if (isPreview) null else appContext.entryPoint<GamesEntryPoint>()
    }
    var recordLoaded by remember(storage, game) { mutableStateOf(storage == null) }

    fun startRound() {
        val resuming = run.phase == ArcadePhase.Paused
        run.start()
        if (!resuming) {
            run.statsRoundStarted = true
            val startedAt = System.currentTimeMillis()
            storage?.appScope?.launch {
                storage.statisticsRepository.recordStart(game, startedAt)
            }
        }
    }

    LaunchedEffect(phase, starting) {
        playingChanged?.invoke(starting || phase == ArcadePhase.Running || phase == ArcadePhase.Paused)
    }
    LaunchedEffect(initialState.highScore) {
        record = maxOf(record, initialState.highScore)
    }
    LaunchedEffect(storage, game) {
        storage?.statisticsRepository?.highScore(game)?.collect {
            record = maxOf(record, it)
            recordLoaded = true
        }
    }
    LaunchedEffect(run.prepared, recordLoaded) {
        if (run.startWhenReady && run.prepared && recordLoaded) {
            run.startWhenReady = false
            run.previousBest = record
            startRound()
            if (isPaused) run.pause()
        }
    }
    LaunchedEffect(phase) {
        if (run.previousBest < 0 && phase != ArcadePhase.Ready) run.previousBest = record
        if (phase == ArcadePhase.Over && !run.gameOverReported) {
            run.stopInput()
            run.gameOverReported = true
            val trackRound = run.statsRoundStarted
            if (trackRound || score > record) {
                storage?.appScope?.launch {
                    storage.statisticsRepository.recordFinish(game, score, trackRound)
                }
            }
            if (score > record) {
                record = score
            }
        }
    }

    val fieldAlpha = remember(run) { Animatable(initialValue = 1f) }
    val overlay = remember(run) { MutableTransitionState(initialState = !run.startWhenReady) }
    overlay.targetState = !run.startWhenReady && phase != ArcadePhase.Running && !starting
    LaunchedEffect(starting, isPaused) {
        if (starting && !isPaused) {
            val restarting = run.phase == ArcadePhase.Over
            if (restarting) {
                fieldAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 180)
                )
            }
            snapshotFlow { overlay.isIdle && !overlay.currentState }.first { it }
            if (run.phase != ArcadePhase.Paused) run.previousBest = record
            startRound()
            if (restarting) run.animateLevel()
            fieldAlpha.snapTo(targetValue = 1f)
            starting = false
        } else {
            fieldAlpha.snapTo(targetValue = 1f)
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(run, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                starting = false
                run.pause()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            run.stopInput()
        }
    }
    LaunchedEffect(run, isPaused, phase) {
        if (isPaused) {
            starting = false
            run.pause()
        }
    }
    val animate =
        !isPaused && phase == ArcadePhase.Running && (!turnBased || run.isAnimating || run.levelTransition > 0f)
    LaunchedEffect(run, animate, lifecycle) {
        if (!animate) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var previous = withFrameNanos { it }
            while (run.phase == ArcadePhase.Running) {
                val now = withFrameNanos { it }
                var remaining = ((now - previous) / 1_000_000_000f).coerceIn(0f, 0.05f)
                while (remaining > 0f && run.phase == ArcadePhase.Running) {
                    val step = minOf(remaining, 1f / 120f)
                    if (run.levelTransition > 0f) {
                        run.levelTransition = (run.levelTransition - step).coerceAtLeast(0f)
                    } else {
                        run.advance(step)
                    }
                    remaining -= step
                }
                run.redraw()
                previous = now
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(style.gameHeight)
            .clip(style.shape)
            .background(style.background)
            .border(1.dp, style.borderColor, style.shape)
    ) {
        val hudScale = (maxWidth / 320.dp).coerceAtMost(1f)
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = fieldAlpha.value }
        ) {
            field()
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 8.dp)
                .height(IntrinsicSize.Min)
                .graphicsLayer {
                    scaleX = hudScale
                    scaleY = hudScale
                    transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0f)
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = phase == ArcadePhase.Running,
                enter = fadeIn(animationSpec = tween(durationMillis = 180)) + scaleIn(
                    animationSpec = tween(
                        durationMillis = 180
                    ), initialScale = 0.85f
                ),
                exit = fadeOut(animationSpec = tween(durationMillis = 120)) + scaleOut(
                    animationSpec = tween(
                        durationMillis = 120
                    ), targetScale = 0.85f
                )
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small)
                        .background(style.background)
                        .clickable(enabled = phase == ArcadePhase.Running && !isPaused) {
                            run.pause()
                            haptics.longPress()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(with(LocalDensity.current) { style.scoreTextStyle.fontSize.toDp() } * 1.25f)
                            .paint(
                                painter = rememberVectorPainter(Icons.Rounded.Pause),
                                colorFilter = ColorFilter.tint(style.accentColor)
                            )
                    )
                }
            }
            Row(
                modifier = Modifier
                    .background(style.background, MaterialTheme.shapes.small)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "HI ${
                        (if (phase == ArcadePhase.Ready) record else run.previousBest.coerceAtLeast(
                            0
                        ))
                            .toString().padStart(5, '0')
                    }",
                    color = style.highScoreColor,
                    style = style.scoreTextStyle,
                    maxLines = 1
                )
                Text(
                    text = score.toString().padStart(5, '0'),
                    color = style.playerColor,
                    style = style.scoreTextStyle,
                    maxLines = 1
                )
            }
        }
        AnimatedVisibility(
            visibleState = overlay,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)) + scaleIn(
                animationSpec = tween(
                    durationMillis = 180
                ), initialScale = 0.96f
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 180)) + scaleOut(
                animationSpec = tween(
                    durationMillis = 180
                ), targetScale = 1.03f
            ),
            modifier = Modifier.matchParentSize()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(style.overlayColor)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown().consume()
                            do {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() }
                            } while (event.changes.any { it.pressed })
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val compact = maxHeight < 230.dp
                AnimatedContent(
                    targetState = phase,
                    contentAlignment = Alignment.Center,
                    transitionSpec = {
                        ((fadeIn(animationSpec = tween(durationMillis = 180)) + slideInVertically { it / 12 }) togetherWith
                                (fadeOut(animationSpec = tween(durationMillis = 100)) + slideOutVertically { -it / 12 }))
                            .using(SizeTransform(clip = false))
                    }
                ) { overlayPhase ->
                    val hasContainer = overlayPhase != ArcadePhase.Ready
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .widthIn(max = 320.dp)
                            .then(
                                if (hasContainer) Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        MaterialTheme.shapes.extraLarge
                                    )
                                    .padding(if (compact) 8.dp else 20.dp)
                                else Modifier
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)
                    ) {
                        if (overlayPhase == ArcadePhase.Over) {
                            Text(
                                text = stringResource(
                                    if (score > run.previousBest) R.string.arcade_new_record else R.string.arcade_game_over
                                ),
                                color = if (score > run.previousBest) style.accentColor else style.textColor,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = score.toString(),
                                color = style.playerColor,
                                style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(
                                    R.string.arcade_previous_best,
                                    run.previousBest.coerceAtLeast(0)
                                ),
                                color = style.highScoreColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (overlayPhase == ArcadePhase.Paused) {
                            Text(
                                text = stringResource(R.string.arcade_paused),
                                color = style.textColor,
                                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge
                            )
                        }
                        if (overlayPhase == ArcadePhase.Paused) {
                            Box(
                                modifier = Modifier
                                    .size(if (compact) 48.dp else 64.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        enabled = phase == overlayPhase && !isPaused && !starting && run.prepared && recordLoaded,
                                        onClick = {
                                            starting = true
                                            haptics.longPress()
                                        }
                                    )
                                    .padding(4.dp)
                                    .paint(
                                        painter = rememberVectorPainter(Icons.Rounded.PlayCircle),
                                        sizeToIntrinsics = false,
                                        contentScale = ContentScale.Fit,
                                        colorFilter = ColorFilter.tint(style.playerColor)
                                    )
                            )
                        } else if (allowNewRound) {
                            LocalArcadeButton.current(
                                {
                                    starting = true
                                    playingChanged?.invoke(true)
                                },
                                phase == overlayPhase && !isPaused && !starting && run.prepared && recordLoaded,
                                style.buttonColor,
                                style.buttonContentColor
                            ) {
                                Text(text = stringResource(if (overlayPhase == ArcadePhase.Ready) R.string.play else R.string.try_again))
                            }
                        }
                        if (overlayPhase == ArcadePhase.Paused) {
                            LocalArcadeButton.current(
                                run::finish,
                                phase == overlayPhase && !isPaused && !starting,
                                style.buttonColor,
                                style.buttonContentColor
                            ) {
                                Text(text = stringResource(R.string.arcade_finish))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ArcadeField(
    run: ArcadeRun,
    worldSize: Size = Size(width = 360f, height = 220f),
    transition: (() -> Float)? = null,
    onPress: ((Offset) -> Unit)? = null,
    onSecondaryTap: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
    onSwipe: ((Offset) -> Unit)? = null,
    swipeOnDrag: Boolean = false,
    lightFeedback: Boolean = false,
    draw: DrawScope.() -> Unit
) {
    val levelEnding by rememberUpdatedState(transition)
    val press by rememberUpdatedState(onPress)
    val secondaryTap by rememberUpdatedState(onSecondaryTap)
    val tap by rememberUpdatedState(onTap)
    val hold by rememberUpdatedState(onLongPress)
    val drag by rememberUpdatedState(onDrag)
    val release by rememberUpdatedState(onRelease)
    val swipe by rememberUpdatedState(onSwipe)
    val haptics = LocalHapticFeedback.current
    val feedback by rememberUpdatedState(
        if (lightFeedback) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress
    )
    val topInset = with(LocalDensity.current) { 44.dp.toPx() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                run.frame
                val remaining = levelEnding?.invoke() ?: 0f
                val outgoing = if (remaining > 0f) {
                    (remaining / LevelTransitionDuration).coerceAtMost(1f)
                } else 1f
                val incoming = 1f - run.levelTransition / LevelTransitionDuration
                alpha = minOf(outgoing, incoming)
            }
            .onSizeChanged {
                val scale = arcadeScale(
                    width = it.width.toFloat(),
                    height = it.height.toFloat(),
                    worldSize = worldSize
                )
                if (scale > 0f) run.resize(
                    width = it.width / scale,
                    height = it.height / scale,
                    topInset = topInset / scale
                )
            }
            .pointerInput(run, haptics, swipeOnDrag, run.phase, worldSize) {
                fun acceptsInput() = run.phase == ArcadePhase.Running &&
                        run.levelTransition == 0f && (levelEnding?.invoke() ?: 0f) <= 0f

                if (run.phase != ArcadePhase.Running) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (!acceptsInput()) return@awaitEachGesture
                    val scale = arcadeScale(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        worldSize = worldSize
                    )
                    if (scale <= 0f) return@awaitEachGesture
                    down.consume()
                    press?.let {
                        it(down.position / scale)
                        haptics.performHapticFeedback(feedback)
                    }
                    var swiped = false
                    var dragged = false
                    var held = false
                    var released = false
                    val gestureSize = size
                    val secondaryTouches = mutableMapOf<PointerId, Pair<Offset, Long>>()
                    try {
                        while (run.phase == ArcadePhase.Running) {
                            val event = if (hold != null && !held && !dragged) {
                                val remaining = viewConfiguration.longPressTimeoutMillis -
                                        (SystemClock.uptimeMillis() - down.uptimeMillis)
                                if (remaining <= 0L) null else withTimeoutOrNull(remaining) {
                                    awaitPointerEvent()
                                }
                            } else awaitPointerEvent()
                            if (!acceptsInput() || size != gestureSize) break
                            if (event == null) {
                                held = true
                                hold?.invoke(down.position / scale)
                                haptics.longPress()
                                continue
                            }
                            secondaryTap?.let { fire ->
                                event.changes.filter { it.id != down.id }.forEach { change ->
                                    if (change.isConsumed) {
                                        secondaryTouches.remove(change.id)
                                    } else if (change.pressed && !change.previousPressed) {
                                        secondaryTouches[change.id] =
                                            change.position to change.uptimeMillis
                                    } else {
                                        secondaryTouches[change.id]?.let { (position, time) ->
                                            if ((change.position - position).getDistance() > viewConfiguration.touchSlop) {
                                                secondaryTouches.remove(change.id)
                                            } else if (!change.pressed) {
                                                if (change.uptimeMillis - time < viewConfiguration.longPressTimeoutMillis) {
                                                    fire(change.position / scale)
                                                    haptics.performHapticFeedback(feedback)
                                                }
                                                secondaryTouches.remove(change.id)
                                            }
                                        }
                                    }
                                    change.consume()
                                }
                            }
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.isConsumed) break
                            val delta = change.position - down.position
                            if (delta.getDistance() > viewConfiguration.touchSlop) dragged = true
                            if (!swiped && (swipeOnDrag || !change.pressed)) {
                                if (delta.getDistance() > viewConfiguration.touchSlop) {
                                    swipe?.let {
                                        it(delta / scale)
                                        haptics.performHapticFeedback(feedback)
                                        swiped = true
                                    }
                                }
                            }
                            if (!change.pressed) {
                                released = true
                                if (!held && !swiped && !dragged && change.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis) {
                                    tap?.let {
                                        it(change.position / scale)
                                        if (press == null) haptics.performHapticFeedback(feedback)
                                    }
                                }
                                change.consume()
                                break
                            }
                            if (dragged) drag?.invoke(change.position / scale)
                            change.consume()
                        }
                    } finally {
                        if (released && acceptsInput()) release?.invoke()
                        else run.stopInput()
                    }
                }
            }
    ) {
        run.frame
        val scale = arcadeScale(
            width = size.width,
            height = size.height,
            worldSize = worldSize
        )
        if (scale > 0f) scale(scale, Offset.Zero) { draw() }
    }
}

private fun arcadeScale(
    width: Float,
    height: Float,
    worldSize: Size
): Float = minOf(width / worldSize.width, height / worldSize.height)

internal fun DrawScope.pixelRect(
    color: Color,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    if (width > 0f && height > 0f) drawRect(
        color = color,
        topLeft = Offset(x = x, y = y),
        size = Size(width = width, height = height)
    )
}

internal class ArcadeSprite(
    val width: Int,
    val height: Int,
    data: String
) {
    val path: Path = PathParser().parsePathString(data).toPath().apply {
        fillType = PathFillType.EvenOdd
    }
}

internal fun DrawScope.drawArcadeSprite(
    sprite: ArcadeSprite,
    tint: Color,
    x: Float,
    y: Float
) {
    translate(left = x, top = y) {
        drawPath(path = sprite.path, color = tint)
    }
}