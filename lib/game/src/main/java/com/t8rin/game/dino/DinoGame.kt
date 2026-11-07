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

package com.t8rin.game.dino

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import kotlin.math.exp
import kotlin.random.Random

@Immutable
data class DinoGameState(
    val highScore: Int = 0
)

@Immutable
data class DinoStyle(
    val shape: Shape,
    val background: Brush,
    val borderColor: Color,
    val dinoColor: Color,
    val cactusColor: Color,
    val birdColor: Color,
    val cloudColor: Color,
    val groundColor: Color,
    val scoreColor: Color,
    val highScoreColor: Color,
    val scoreTextStyle: TextStyle,
    val gameOverColor: Color,
    val gameOverTextStyle: TextStyle,
    val buttonContainerColor: Color,
    val buttonContentColor: Color
)

object DinoDefaults {
    val style: DinoStyle
        @Composable
        get() {
            val colors = MaterialTheme.colorScheme
            val shape = MaterialTheme.shapes.extraLarge
            val typography = MaterialTheme.typography

            return remember(colors, shape, typography) {
                DinoStyle(
                    shape = shape,
                    background = Brush.verticalGradient(
                        listOf(colors.surfaceContainerLow, colors.surfaceContainer)
                    ),
                    borderColor = colors.outlineVariant,
                    dinoColor = colors.primary,
                    cactusColor = colors.secondary,
                    birdColor = colors.tertiary,
                    cloudColor = colors.outline.copy(alpha = 0.42f),
                    groundColor = colors.outline.copy(alpha = 0.8f),
                    scoreColor = colors.primary,
                    highScoreColor = colors.onSurfaceVariant,
                    scoreTextStyle = typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                    gameOverColor = colors.onSurface,
                    gameOverTextStyle = typography.titleSmall,
                    buttonContainerColor = colors.primary,
                    buttonContentColor = colors.onPrimary
                )
            }
        }
}

@Composable
fun DinoGame(
    modifier: Modifier = Modifier,
    initialState: DinoGameState = DinoGameState(),
    style: DinoStyle = DinoDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = retain { DinoRun() }
    var highScore by rememberSaveable {
        mutableIntStateOf(initialState.highScore.coerceAtLeast(0))
    }
    val currentOnHighScoreChanged by rememberUpdatedState(onHighScoreChanged)
    val currentOnGameOver by rememberUpdatedState(onGameOver)
    val phase = run.phase

    LaunchedEffect(initialState.highScore) {
        highScore = maxOf(highScore, initialState.highScore)
    }

    LaunchedEffect(phase) {
        if (phase != DinoPhase.Running) return@LaunchedEffect

        var previousFrame = withFrameNanos { it }
        while (run.phase == DinoPhase.Running) {
            val frame = withFrameNanos { it }
            val seconds = ((frame - previousFrame) / 1_000_000_000f).coerceIn(0f, 0.032f)
            previousFrame = frame
            run.advance(seconds)
        }

        val score = run.score
        if (score > highScore) {
            highScore = score
            currentOnHighScoreChanged?.invoke(score)
        }
        currentOnGameOver?.invoke(score)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = 280.dp)
            .height(220.dp)
            .clip(style.shape)
            .background(style.background)
            .border(1.dp, style.borderColor, style.shape)
    ) {
        DinoTrack(
            run = run,
            style = style,
            modifier = Modifier
                .matchParentSize()
                .hapticsClickable(
                    interactionSource = null,
                    indication = null,
                    enabled = phase == DinoPhase.Running && !run.isJumping,
                    onClick = run::jump
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HI ${highScore.toString().padStart(5, '0')}",
                color = style.highScoreColor,
                style = style.scoreTextStyle,
                maxLines = 1
            )
            Text(
                text = run.score.toString().padStart(5, '0'),
                color = style.scoreColor,
                style = style.scoreTextStyle,
                maxLines = 1
            )
        }

        if (phase != DinoPhase.Running) {
            val gameOver = phase == DinoPhase.Over
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-12).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (gameOver) {
                    Text(
                        text = stringResource(R.string.game_over),
                        color = style.gameOverColor,
                        style = style.gameOverTextStyle,
                        textAlign = TextAlign.Center
                    )
                }
                EnhancedButton(
                    onClick = run::start,
                    containerColor = style.buttonContainerColor,
                    contentColor = style.buttonContentColor
                ) {
                    Text(stringResource(if (gameOver) R.string.try_again else R.string.play))
                }
            }
        }
    }
}

@Composable
private fun DinoTrack(
    run: DinoRun,
    style: DinoStyle,
    modifier: Modifier = Modifier
) {
    val painters = DinoSprite.entries.map { rememberVectorPainter(it.vector) }

    Canvas(
        modifier = modifier.onSizeChanged {
            val scale = trackScale(it.width.toFloat(), it.height.toFloat())
            if (scale > 0f) run.trackWidth = it.width / scale
        }
    ) {
        val scale = trackScale(size.width, size.height)
        if (scale <= 0f) return@Canvas

        val width = size.width / scale
        val ground = size.height / scale - 25f
        val distance = run.distance

        fun DrawScope.drawSprite(sprite: DinoSprite, x: Float, y: Float, tint: Color) {
            translate(left = x, top = y) {
                with(painters[sprite.ordinal]) {
                    draw(
                        size = Size(sprite.width, sprite.height),
                        colorFilter = ColorFilter.tint(tint)
                    )
                }
            }
        }

        scale(scale = scale, pivot = Offset.Zero) {
            var cloudX = 130f - (distance * 0.16f) % 380f
            while (cloudX < width) {
                drawSprite(DinoSprite.Cloud, cloudX, ground - 112f, style.cloudColor)
                drawSprite(DinoSprite.Cloud, cloudX + 188f, ground - 144f, style.cloudColor)
                cloudX += 380f
            }

            val groundSprite = DinoSprite.Ground
            var groundX = -(distance % groundSprite.width)
            while (groundX < width) {
                drawSprite(groundSprite, groundX, ground, style.groundColor)
                groundX += groundSprite.width
            }

            for (obstacle in run.obstacles) {
                val sprite = obstacle.type.sprite(distance)
                drawSprite(
                    sprite = sprite,
                    x = obstacle.x,
                    y = ground - obstacle.type.altitude - sprite.height,
                    tint = if (obstacle.type == ObstacleType.Bird) style.birdColor else style.cactusColor
                )
            }

            val dino = when {
                run.phase == DinoPhase.Over -> DinoSprite.DinoOver
                run.phase == DinoPhase.Ready || run.jumpHeight > 0f -> DinoSprite.Dino
                (distance / 24f).toInt() % 2 == 0 -> DinoSprite.DinoLeft
                else -> DinoSprite.DinoRight
            }
            drawSprite(dino, DinoX, ground - dino.height - run.jumpHeight, style.dinoColor)
        }
    }
}

private fun trackScale(width: Float, height: Float): Float = minOf(width / 320f, height / 210f)

private const val DinoX = 24f

private enum class DinoPhase {
    Ready, Running, Over
}

private class DinoRun {
    var phase by mutableStateOf(DinoPhase.Ready)
        private set
    var distance by mutableFloatStateOf(0f)
        private set
    var score by mutableIntStateOf(0)
        private set
    var isJumping by mutableStateOf(false)
        private set

    var trackWidth = 320f
    var jumpHeight = 0f
        private set
    val obstacles = mutableListOf<DinoObstacle>()

    private var jumpSpeed = 0f
    private var nextObstacle = 0f

    fun start() {
        if (phase == DinoPhase.Running) return

        jumpHeight = 0f
        jumpSpeed = 0f
        isJumping = false
        obstacles.clear()
        distance = 0f
        score = 0
        nextObstacle = minOf(trackWidth, 420f)
        phase = DinoPhase.Running
    }

    fun jump() {
        if (phase != DinoPhase.Running || isJumping) return

        jumpSpeed = 424f
        isJumping = true
    }

    fun advance(seconds: Float) {
        val speed = 248f + 174f * (1f - exp(-distance / 2800f))
        val step = speed * seconds
        val nextDistance = distance + step

        if (isJumping) {
            jumpHeight =
                (jumpHeight + jumpSpeed * seconds - 550f * seconds * seconds).coerceAtLeast(0f)
            jumpSpeed -= 1100f * seconds
            if (jumpHeight == 0f && jumpSpeed <= 0f) {
                isJumping = false
            }
        }

        for (obstacle in obstacles) obstacle.x -= step
        if (obstacles.firstOrNull()?.let { it.x + it.type.width < 0f } == true) {
            obstacles.removeAt(0)
        }

        if (nextObstacle <= nextDistance + trackWidth) {
            val type = when (Random.nextInt(if (score >= 80) 5 else 3)) {
                0 -> ObstacleType.SmallCactus
                1 -> ObstacleType.LargeCactus
                2 -> ObstacleType.CactusGroup
                else -> ObstacleType.Bird
            }
            obstacles += DinoObstacle(nextObstacle - nextDistance, type)
            nextObstacle += speed * (1.05f + Random.nextFloat() * 0.55f)
        }

        distance = nextDistance
        score = (nextDistance / 25f).toInt()

        if (obstacles.any(::hitsDino)) phase = DinoPhase.Over
    }

    private fun hitsDino(obstacle: DinoObstacle): Boolean {
        val type = obstacle.type
        val bounds = Rect(
            left = obstacle.x + 3f,
            top = -type.altitude - type.height + 3f,
            right = obstacle.x + type.width - 3f,
            bottom = -type.altitude - 1f
        )

        return bounds.overlaps(
            Rect(
                DinoX + 22f,
                -jumpHeight - 45f,
                DinoX + 42f,
                -jumpHeight - 28f
            )
        ) ||
                bounds.overlaps(Rect(DinoX + 9f, -jumpHeight - 28f, DinoX + 30f, -jumpHeight - 4f))
    }
}

private class DinoObstacle(
    var x: Float,
    val type: ObstacleType
)

private enum class ObstacleType(
    private val sprite: DinoSprite,
    val altitude: Float = 0f
) {
    SmallCactus(DinoSprite.SmallCactus),
    LargeCactus(DinoSprite.LargeCactus),
    CactusGroup(DinoSprite.CactusGroup),
    Bird(DinoSprite.BirdUp, altitude = 8f);

    val width: Float get() = sprite.width
    val height: Float get() = sprite.height

    fun sprite(distance: Float): DinoSprite =
        if (this == Bird && (distance / 36f).toInt() % 2 == 1) {
            DinoSprite.BirdDown
        } else {
            sprite
        }
}

private const val DinoBody =
    "M24 2H41V4H44V16H34V19H41V22H31V25H36V29H33V27H31V33H28V39H13V36H9V33H6V30H3V26H1V17H4V22H7V25H12V23H16V20H20V17H22V5H24Z"
private const val DinoEye = "M26 6V9H29V6Z"

private enum class DinoSprite(
    val width: Float,
    val height: Float,
    path: String
) {
    Dino(44f, 47f, "$DinoBody $DinoEye M13 37H19V42H17V44H21V47H13Z M22 37H28V44H31V47H24V43H22Z"),
    DinoLeft(
        44f,
        47f,
        "$DinoBody $DinoEye M13 37H19V43H17V44H21V47H13Z M22 37H28V40H31V43H25V41H22Z"
    ),
    DinoRight(
        44f,
        47f,
        "$DinoBody $DinoEye M13 37H19V40H16V42H12V39H13Z M22 37H28V44H31V47H24V43H22Z"
    ),
    DinoOver(
        44f,
        47f,
        "$DinoBody M25 5V11H31V5Z M27 7H29V9H27Z M13 37H19V42H17V44H21V47H13Z M22 37H28V44H31V47H24V43H22Z"
    ),
    SmallCactus(18f, 29f, "M7 0H11V2H13V14H15V9H18V17H16V19H13V29H6V22H3V20H1V11H4V17H6V2H7Z"),
    LargeCactus(
        26f,
        42f,
        "M10 0H16V2H18V21H21V10H23V8H25V23H23V26H18V42H8V30H4V28H1V15H3V13H6V24H8V3H10Z"
    ),
    CactusGroup(
        46f, 39f,
        "M7 11H11V13H13V24H15V19H17V27H13V39H6V32H3V30H1V22H4V27H6V13H7Z " +
                "M23 0H28V2H30V18H32V12H34V10H36V20H34V23H30V39H21V27H18V25H16V15H19V22H21V2H23Z " +
                "M38 16H42V18H43V27H45V22H46V30H43V39H37V33H33V25H35V29H37V18H38Z"
    ),
    BirdUp(
        42f,
        24f,
        "M0 14H4V11H8V8H12V10H16V12H20V9H18V4H20V1H23V4H26V7H29V12H36V14H42V16H36V18H30V20H15V18H10V15H0Z"
    ),
    BirdDown(
        42f,
        24f,
        "M0 14H4V11H8V8H12V10H16V12H33V14H42V16H36V18H30V20H26V24H22V22H18V20H15V18H10V15H0Z"
    ),
    Cloud(
        54f, 16f,
        "M0 15H7V12H12V9H17V5H22V2H31V4H36V7H41V10H47V12H54V13.5H46V11.5H40V8.5H35V5.5H30V3.5H23V6.5H18V10.5H13V13.5H8V16H0Z"
    ),
    Ground(
        144f, 15f,
        "M0 0H30V1H37V0H87V1H99V0H144V1.5H100V2.5H86V1.5H38V2.5H29V1.5H0Z " +
                "M9 7H14V8H9Z M25 11H28V12H25Z M46 5H50V6H46Z M61 9H69V10H61Z M81 13H85V14H81Z " +
                "M104 7H109V8H104Z M121 11H125V12H121Z M135 5H138V6H135Z"
    );

    val vector: ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = width.dp,
        defaultHeight = height.dp,
        viewportWidth = width,
        viewportHeight = height
    ).addPath(
        pathData = PathParser().parsePathString(path).toNodes(),
        fill = SolidColor(Color.Black)
    ).build()
}

@Composable
@Preview
private fun Preview() = ImageToolboxThemeForPreview(false) {
    DinoGame()
}