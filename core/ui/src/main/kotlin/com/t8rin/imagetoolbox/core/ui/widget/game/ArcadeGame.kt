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

package com.t8rin.imagetoolbox.core.ui.widget.game

import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Alien
import com.t8rin.imagetoolbox.core.resources.icons.Brick
import com.t8rin.imagetoolbox.core.resources.icons.BubbleChart
import com.t8rin.imagetoolbox.core.resources.icons.Dinosaur
import com.t8rin.imagetoolbox.core.resources.icons.Filter2
import com.t8rin.imagetoolbox.core.resources.icons.Hexagon
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.Nutrition
import com.t8rin.imagetoolbox.core.resources.icons.PestControlRodent
import com.t8rin.imagetoolbox.core.resources.icons.Phishing
import com.t8rin.imagetoolbox.core.resources.icons.Raven
import com.t8rin.imagetoolbox.core.resources.icons.RocketLaunch
import com.t8rin.imagetoolbox.core.resources.icons.Shield
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.resources.icons.Snake
import com.t8rin.imagetoolbox.core.resources.icons.SportsMotorsports
import com.t8rin.imagetoolbox.core.resources.icons.SportsTennis
import com.t8rin.imagetoolbox.core.resources.icons.Stacks
import com.t8rin.imagetoolbox.core.resources.icons.TrackChanges
import com.t8rin.imagetoolbox.core.resources.icons.ViewQuilt
import com.t8rin.imagetoolbox.core.resources.icons.Water
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException

val ArcadeGame.title: Int
    get() = when (this) {
        ArcadeGame.Random -> R.string.grid_random
        ArcadeGame.Dino -> R.string.arcade_dino
        ArcadeGame.Doodle -> R.string.arcade_doodle
        ArcadeGame.Bird -> R.string.arcade_bird
        ArcadeGame.Stack -> R.string.arcade_stack
        ArcadeGame.Pong -> R.string.arcade_pong
        ArcadeGame.`2048` -> R.string.arcade_2048
        ArcadeGame.Snake -> R.string.arcade_snake
        ArcadeGame.Tetris -> R.string.arcade_tetris
        ArcadeGame.Hexagon -> R.string.arcade_hexagon
        ArcadeGame.Fishing -> R.string.arcade_fishing
        ArcadeGame.BubblePop -> R.string.arcade_bubblepop
        ArcadeGame.SimonSays -> R.string.arcade_simonsays
        ArcadeGame.WhackAMole -> R.string.arcade_whackamole
        ArcadeGame.RiverHop -> R.string.arcade_riverhop
        ArcadeGame.MissileCommand -> R.string.arcade_missilecommand
        ArcadeGame.KnifeHit -> R.string.arcade_knifehit
        ArcadeGame.FruitSlice -> R.string.arcade_fruitslice
        ArcadeGame.Asteroids -> R.string.arcade_asteroids
        ArcadeGame.Ricochet -> R.string.arcade_ricochet
        ArcadeGame.Racer -> R.string.arcade_racer
    }


val ArcadeGame.description: Int
    get() = when (this) {
        ArcadeGame.Random -> R.string.arcade_games_sub
        ArcadeGame.Dino -> R.string.arcade_dino_sub
        ArcadeGame.Doodle -> R.string.arcade_doodle_sub
        ArcadeGame.Bird -> R.string.arcade_bird_sub
        ArcadeGame.Stack -> R.string.arcade_stack_sub
        ArcadeGame.Pong -> R.string.arcade_pong_sub
        ArcadeGame.`2048` -> R.string.arcade_2048_sub
        ArcadeGame.Snake -> R.string.arcade_snake_sub
        ArcadeGame.Tetris -> R.string.arcade_tetris_sub
        ArcadeGame.Hexagon -> R.string.arcade_hexagon_sub
        ArcadeGame.Fishing -> R.string.arcade_fishing_sub
        ArcadeGame.BubblePop -> R.string.arcade_bubblepop_sub
        ArcadeGame.SimonSays -> R.string.arcade_simonsays_sub
        ArcadeGame.WhackAMole -> R.string.arcade_whackamole_sub
        ArcadeGame.RiverHop -> R.string.arcade_riverhop_sub
        ArcadeGame.MissileCommand -> R.string.arcade_missilecommand_sub
        ArcadeGame.KnifeHit -> R.string.arcade_knifehit_sub
        ArcadeGame.FruitSlice -> R.string.arcade_fruitslice_sub
        ArcadeGame.Asteroids -> R.string.arcade_asteroids_sub
        ArcadeGame.Ricochet -> R.string.arcade_ricochet_sub
        ArcadeGame.Racer -> R.string.arcade_racer_sub
    }

@Composable
fun ArcadeGameContent(
    game: ArcadeGame,
    modifier: Modifier = Modifier,
    height: Dp = Dp.Unspecified,
    isPaused: Boolean = false,
    onPlayingChanged: ((Boolean) -> Unit)? = null
) {
    val style = ArcadeDefaults.style.let {
        if (height == Dp.Unspecified) it else it.copy(gameHeight = height)
    }
    val selected = rememberSaveable(game) {
        if (game == ArcadeGame.Random) ArcadeGame.playable.random() else game
    }
    CompositionLocalProvider(
        LocalArcadePaused provides isPaused,
        LocalArcadePlayingChanged provides onPlayingChanged
    ) {
        when (selected) {
            ArcadeGame.Dino -> DinoGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Doodle -> DoodleGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Bird -> BirdGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Stack -> StackGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Pong -> PongGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.`2048` -> Game2048(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Snake -> SnakeGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Tetris -> TetrisGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Racer -> RacerGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Ricochet -> RicochetGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Asteroids -> AsteroidsGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.FruitSlice -> FruitSliceGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.KnifeHit -> KnifeHitGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.MissileCommand -> MissileCommandGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.RiverHop -> RiverHopGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.WhackAMole -> WhackAMoleGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.SimonSays -> SimonSaysGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.BubblePop -> BubblePopGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Fishing -> FishingGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Hexagon -> HexagonGame(
                modifier = modifier,
                style = style
            )

            ArcadeGame.Random -> Unit
        }
    }
}

internal val LocalArcadePaused = compositionLocalOf { false }
private val LocalArcadePlayingChanged = compositionLocalOf<((Boolean) -> Unit)?> { null }

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

internal enum class ArcadePhase { Ready, Running, Over }

internal abstract class ArcadeRun {
    var phase by mutableStateOf(ArcadePhase.Ready)
    var gameOverReported = false
    var score by mutableIntStateOf(0)
    var frame by mutableIntStateOf(0)
        private set
    var isAnimating by mutableStateOf(false)
        protected set
    var topInset = 44f
        private set
    var width = 360f
        private set
    var height = 240f
        private set

    fun start() {
        gameOverReported = false
        score = 0
        reset()
        redraw()
        phase = ArcadePhase.Running
    }

    fun redraw() {
        frame++
    }

    fun resize(
        width: Float,
        height: Float,
        topInset: Float = this.topInset
    ) {
        if (width == this.width && height == this.height && topInset == this.topInset) return
        this.topInset = topInset
        val previous = Size(this.width, this.height)
        this.width = width
        this.height = height
        onResize(previous)
        redraw()
    }

    abstract fun reset()
    abstract fun save(state: Bundle)
    abstract fun restore(state: Bundle)

    open fun onResize(previous: Size) = Unit
    open fun stopInput() = Unit
    open fun advance(seconds: Float) = Unit
}

@Composable
internal fun <T : ArcadeRun> rememberArcadeRun(
    initialState: ArcadeGameState,
    create: () -> T
): T {
    val saver = Saver<T, Bundle>(
        save = { run ->
            Bundle().apply {
                putString("phase", run.phase.name)
                putInt("score", run.score)
                putBoolean("gameOverReported", run.gameOverReported)
                putFloat("width", run.width)
                putFloat("height", run.height)
                putFloat("topInset", run.topInset)
                run.save(this)
            }
        },
        restore = { state ->
            create().apply {
                reset()
                resize(
                    width = state.getFloat("width", 360f),
                    height = state.getFloat("height", 240f),
                    topInset = state.getFloat("topInset", 44f)
                )
                phase = state.getString("phase")?.let(ArcadePhase::valueOf) ?: ArcadePhase.Ready
                score = state.getInt("score")
                gameOverReported = state.getBoolean("gameOverReported")
                restore(state)
            }
        }
    )
    return rememberSaveable(saver = saver) {
        create().apply {
            reset()
            if (initialState.isPlaying) start()
        }
    }
}

@Composable
internal fun ArcadeBoard(
    game: ArcadeGame,
    run: ArcadeRun,
    style: ArcadeStyle,
    modifier: Modifier,
    initialState: ArcadeGameState,
    onHighScoreChanged: ((Int) -> Unit)?,
    onGameOver: ((Int) -> Unit)?,
    turnBased: Boolean = false,
    field: @Composable () -> Unit
) {
    val phase = run.phase
    val playingChanged by rememberUpdatedState(LocalArcadePlayingChanged.current)
    LaunchedEffect(phase) {
        playingChanged?.invoke(phase == ArcadePhase.Running)
    }
    val isPaused = LocalArcadePaused.current
    val score = run.score
    var record by rememberSaveable { mutableIntStateOf(initialState.highScore.coerceAtLeast(0)) }
    val highScoreChanged by rememberUpdatedState(onHighScoreChanged)
    val gameOver by rememberUpdatedState(onGameOver)
    val isPreview = LocalInspectionMode.current
    val storage = remember(isPreview) {
        if (isPreview) null else appContext.entryPoint<GamesEntryPoint>()
    }
    val recordKey = remember(game) {
        intPreferencesKey(
            if (game == ArcadeGame.Dino) "DINO_HIGH_SCORE"
            else "ARCADE_${game.name.uppercase()}_HIGH_SCORE"
        )
    }

    fun saveRecord() {
        val value = run.score
        if (value <= record) return
        record = value
        storage?.appScope?.launch {
            storage.dataStore.edit { it[recordKey] = maxOf(it[recordKey] ?: 0, value) }
        }
        highScoreChanged?.invoke(value)
    }

    LaunchedEffect(initialState.highScore) {
        record = maxOf(record, initialState.highScore)
    }
    LaunchedEffect(storage, recordKey) {
        storage?.dataStore?.data
            ?.catch { if (it !is IOException) throw it }
            ?.collect { record = maxOf(record, it[recordKey] ?: 0) }
    }
    LaunchedEffect(phase, if (turnBased) score else 0) {
        if (phase == ArcadePhase.Over || turnBased) saveRecord()
        if (phase == ArcadePhase.Over && !run.gameOverReported) {
            run.gameOverReported = true
            gameOver?.invoke(score)
        }
    }
    DisposableEffect(run, storage) {
        onDispose { saveRecord() }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(run, isPaused) {
        if (isPaused) run.stopInput()
    }
    val animate = !isPaused && phase == ArcadePhase.Running && (!turnBased || run.isAnimating)
    LaunchedEffect(run, animate, lifecycle) {
        if (!animate) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                var previous = withFrameNanos { it }
                while (run.phase == ArcadePhase.Running) {
                    val now = withFrameNanos { it }
                    var remaining = ((now - previous) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    while (remaining > 0f && run.phase == ArcadePhase.Running) {
                        val step = minOf(remaining, 1f / 120f)
                        run.advance(step)
                        remaining -= step
                    }
                    run.redraw()
                    previous = now
                }
            } finally {
                run.stopInput()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(style.gameHeight)
            .clip(style.shape)
            .background(style.background)
            .border(1.dp, style.borderColor, style.shape)
    ) {
        Box(Modifier.matchParentSize()) {
            field()
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 8.dp)
                .background(style.background, MaterialTheme.shapes.small)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "HI ${maxOf(record, score).toString().padStart(5, '0')}",
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
        if (phase != ArcadePhase.Running) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(style.overlayColor),
                contentAlignment = Alignment.Center
            ) {
                EnhancedButton(
                    onClick = run::start,
                    enabled = !isPaused,
                    containerColor = style.buttonColor,
                    contentColor = style.buttonContentColor
                ) {
                    Text(stringResource(if (phase == ArcadePhase.Ready) R.string.play else R.string.try_again))
                }
            }
        }
    }
}

@Composable
internal fun ArcadeField(
    run: ArcadeRun,
    onPress: ((Offset) -> Unit)? = null,
    onSecondaryTap: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
    onSwipe: ((Offset) -> Unit)? = null,
    swipeOnDrag: Boolean = false,
    draw: DrawScope.() -> Unit
) {
    val press by rememberUpdatedState(onPress)
    val secondaryTap by rememberUpdatedState(onSecondaryTap)
    val tap by rememberUpdatedState(onTap)
    val drag by rememberUpdatedState(onDrag)
    val release by rememberUpdatedState(onRelease)
    val swipe by rememberUpdatedState(onSwipe)
    val haptics = LocalHapticFeedback.current
    val isPaused = LocalArcadePaused.current
    val topInset = with(LocalDensity.current) { 44.dp.toPx() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer()
            .onSizeChanged {
                val scale = arcadeScale(
                    width = it.width.toFloat(),
                    height = it.height.toFloat()
                )
                if (scale > 0f) run.resize(
                    width = it.width / scale,
                    height = it.height / scale,
                    topInset = topInset / scale
                )
            }
            .pointerInput(run, haptics, swipeOnDrag, isPaused) {
                if (isPaused) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (run.phase != ArcadePhase.Running) return@awaitEachGesture
                    val scale = arcadeScale(
                        width = size.width.toFloat(),
                        height = size.height.toFloat()
                    )
                    if (scale <= 0f) return@awaitEachGesture
                    down.consume()
                    press?.let {
                        it(down.position / scale)
                        haptics.longPress()
                    }
                    var swiped = false
                    var dragged = false
                    val secondaryTouches = mutableMapOf<PointerId, Pair<Offset, Long>>()
                    try {
                        while (run.phase == ArcadePhase.Running) {
                            val event = awaitPointerEvent()
                            if (run.phase != ArcadePhase.Running) break
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
                                                    haptics.longPress()
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
                                        haptics.longPress()
                                        swiped = true
                                    }
                                }
                            }
                            if (!change.pressed) {
                                if (!swiped && !dragged && change.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis) {
                                    tap?.let {
                                        it(change.position / scale)
                                        if (press == null) haptics.longPress()
                                    }
                                }
                                change.consume()
                                break
                            }
                            if (dragged) drag?.invoke(change.position / scale)
                            change.consume()
                        }
                    } finally {
                        release?.invoke()
                    }
                }
            }
    ) {
        run.frame
        val scale = arcadeScale(
            width = size.width,
            height = size.height
        )
        if (scale > 0f) scale(scale, Offset.Zero) { draw() }
    }
}

private fun arcadeScale(
    width: Float,
    height: Float
): Float = minOf(width / 360f, height / 220f)

internal fun DrawScope.pixelRect(
    color: Color,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    if (width > 0f && height > 0f) drawRect(color, Offset(x, y), Size(width, height))
}

internal class ArcadeSprite(
    name: String,
    width: Int,
    height: Int,
    data: String
) {
    private val parser = PathParser().parsePathString(data)
    val path: Path = parser.toPath().apply { fillType = PathFillType.EvenOdd }
    val vector: ImageVector = ImageVector.Builder(
        name, width.dp, height.dp, width.toFloat(), height.toFloat()
    ).addPath(
        pathData = parser.toNodes(),
        pathFillType = PathFillType.EvenOdd,
        fill = SolidColor(Color.Black)
    ).build()
}

internal fun DrawScope.drawArcadeSprite(
    sprite: ArcadeSprite,
    tint: Color,
    x: Float,
    y: Float
) {
    translate(x, y) {
        drawPath(sprite.path, tint)
    }
}

internal val ArcadeCloud = ArcadeSprite(
    name = "Cloud",
    width = 54,
    height = 16,
    data = "M0 15H7V12H12V9H17V5H22V2H31V4H36V7H41V10H47V12H54V13H46V11H40V8H35V5H30V3H23V6H18V10H13V13H8V16H0Z"
)

internal fun DrawScope.arcadeGround(
    style: ArcadeStyle,
    width: Float,
    y: Float,
    distance: Float = 0f
) {
    pixelRect(
        color = style.detailColor,
        x = 0f,
        y = y,
        width = width,
        height = 1f
    )
    var x = -(distance % 144f)
    while (x < width) {
        pixelRect(
            color = style.detailColor,
            x = x + 9,
            y = y + 7,
            width = 5f,
            height = 1f
        )
        pixelRect(
            color = style.detailColor,
            x = x + 25,
            y = y + 11,
            width = 3f,
            height = 1f
        )
        pixelRect(
            color = style.detailColor,
            x = x + 46,
            y = y + 5,
            width = 4f,
            height = 1f
        )
        pixelRect(
            color = style.detailColor,
            x = x + 61,
            y = y + 9,
            width = 8f,
            height = 1f
        )
        pixelRect(
            color = style.detailColor,
            x = x + 104,
            y = y + 7,
            width = 5f,
            height = 1f
        )
        pixelRect(
            color = style.detailColor,
            x = x + 121,
            y = y + 11,
            width = 4f,
            height = 1f
        )
        x += 144f
    }
}

val ArcadeGame.icon: ImageVector
    get() = when (this) {
        ArcadeGame.Racer -> Icons.Outlined.SportsMotorsports
        ArcadeGame.Ricochet -> Icons.Outlined.Brick
        ArcadeGame.Asteroids -> Icons.Outlined.RocketLaunch
        ArcadeGame.FruitSlice -> Icons.Outlined.Nutrition
        ArcadeGame.KnifeHit -> Icons.Outlined.TrackChanges
        ArcadeGame.MissileCommand -> Icons.Outlined.Shield
        ArcadeGame.RiverHop -> Icons.Rounded.Water
        ArcadeGame.WhackAMole -> Icons.Outlined.PestControlRodent
        ArcadeGame.SimonSays -> Icons.Outlined.Neurology
        ArcadeGame.BubblePop -> Icons.Outlined.BubbleChart
        ArcadeGame.Fishing -> Icons.Outlined.Phishing
        ArcadeGame.Hexagon -> Icons.Outlined.Hexagon
        ArcadeGame.Random -> Icons.Rounded.Shuffle
        ArcadeGame.Dino -> Icons.Outlined.Dinosaur
        ArcadeGame.Doodle -> Icons.Outlined.Alien
        ArcadeGame.Bird -> Icons.Outlined.Raven
        ArcadeGame.Stack -> Icons.Outlined.Stacks
        ArcadeGame.Pong -> Icons.Outlined.SportsTennis
        ArcadeGame.`2048` -> Icons.Outlined.Filter2
        ArcadeGame.Snake -> Icons.Outlined.Snake
        ArcadeGame.Tetris -> Icons.Outlined.ViewQuilt
    }