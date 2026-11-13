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

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Dinosaur
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object DinoGame : ArcadeGameDefinition {
    override val title = R.string.arcade_dino
    override val description = R.string.arcade_dino_sub
    override val icon get() = Icons.Outlined.Dinosaur
    override val order = 0

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::DinoRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                worldSize = Size(width = 320f, height = 210f),
                onPress = { run.jump() },
                onRelease = run::releaseJump
            ) {
                val width = run.width
                val ground = run.height - 25f
                val distance = run.distance

                drawDinoSky(style, width, ground, distance)
                var cloudX = 130f - (distance * 0.16f) % 380f
                while (cloudX < width) {
                    drawArcadeSprite(
                        sprite = DinoCloud,
                        x = cloudX,
                        y = ground - 112f,
                        tint = style.detailColor
                    )
                    drawArcadeSprite(
                        sprite = DinoCloud,
                        x = cloudX + 188f,
                        y = ground - 144f,
                        tint = style.detailColor
                    )
                    cloudX += 380f
                }

                val groundSprite = DinoGround
                var groundX = -(distance % groundSprite.width)
                while (groundX < width) {
                    drawArcadeSprite(
                        sprite = groundSprite,
                        x = groundX,
                        y = ground,
                        tint = style.detailColor.copy(alpha = 0.8f)
                    )
                    groundX += groundSprite.width
                }

                for (obstacle in run.obstacles) {
                    val sprite = obstacle.type.sprite(distance)
                    drawArcadeSprite(
                        sprite = sprite,
                        x = obstacle.x,
                        y = ground - obstacle.type.altitude - sprite.height,
                        tint = if (obstacle.type == ObstacleType.Bird) style.accentColor else style.obstacleColor
                    )
                }

                val dino = when {
                    run.phase == ArcadePhase.Over -> DinoCrashed
                    run.phase == ArcadePhase.Ready || run.jumpHeight > 0f -> DinoStanding
                    (distance / 24f).toInt() % 2 == 0 -> DinoRunningLeft
                    else -> DinoRunningRight
                }
                drawArcadeSprite(
                    sprite = dino,
                    x = DinoX,
                    y = ground - dino.height - run.jumpHeight,
                    tint = style.playerColor
                )
            }
        }
    }
}

private const val DinoX = 24f

private fun DrawScope.drawDinoSky(
    style: ArcadeStyle,
    width: Float,
    ground: Float,
    distance: Float
) {
    repeat(7) { index ->
        val x =
            ((index * 71f + 37f - distance * 0.025f) % (width + 20f) + width + 20f) % (width + 20f) - 10f
        val y = ground * (0.1f + (index * 29 % 67) / 200f)
        val color = style.detailColor.copy(alpha = if (index % 3 == 0) 0.4f else 0.26f)
        pixelRect(color, x, y, 1f, if (index % 3 == 0) 4f else 1f)
        if (index % 3 == 0) pixelRect(color, x - 1f, y + 1f, 3f, 1f)
    }
}

@Serializable
private class DinoRun : ArcadeRun() {
    var distance = 0f
        private set
    var jumpHeight = 0f
        private set
    val obstacles = mutableListOf<DinoObstacle>()

    private var isJumping = false
    private var jumpSpeed = 0f
    private var nextObstacle = 0f
    private var released = false

    override fun reset() {
        jumpHeight = 0f
        jumpSpeed = 0f
        isJumping = false
        released = false
        obstacles.clear()
        distance = 0f
        score = 0
        nextObstacle = minOf(width, 420f)
        addObstacle(0f, 248f)
    }

    fun jump(): Boolean {
        if (phase != ArcadePhase.Running || isJumping) return false

        jumpSpeed = 424f
        released = false
        isJumping = true
        return true
    }

    fun releaseJump() {
        released = true
    }

    override fun advance(seconds: Float) {
        val initialSpeed = 248f
        val originalSpeed = initialSpeed + 174f * (1f - exp(-distance / 2800f))
        val initialSpeedSquared = initialSpeed * initialSpeed
        // Scaling the change in speed squared scales acceleration at the same distance.
        val speedSquared =
            initialSpeedSquared + (originalSpeed * originalSpeed - initialSpeedSquared) * 1.15f
        val lateProgress = ((distance - 4000f) / 46000f).coerceIn(0f, 1f)
        val lateBlend = lateProgress * lateProgress * (3f - 2f * lateProgress)
        val maxSpeed = 800f
        val speed = sqrt(speedSquared + (maxSpeed * maxSpeed - speedSquared) * lateBlend)
        val step = speed * seconds
        val nextDistance = distance + step

        if (isJumping) {
            if (released && jumpHeight >= 34f && jumpSpeed > 150f) jumpSpeed = 150f
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

        if (nextObstacle <= nextDistance + width) addObstacle(nextDistance, speed)

        distance = nextDistance
        score = (nextDistance / 25f).toInt()

        if (obstacles.any(::hitsDino)) phase = ArcadePhase.Over
    }

    private fun addObstacle(nextDistance: Float, speed: Float) {
        val type = when (Random.nextInt(if (score >= 200) 6 else if (score >= 80) 5 else 3)) {
            0 -> ObstacleType.SmallCactus
            1 -> ObstacleType.LargeCactus
            2 -> ObstacleType.CactusGroup
            5 -> ObstacleType.HighBird
            else -> ObstacleType.Bird
        }
        obstacles += DinoObstacle(x = nextObstacle - nextDistance, type = type)
        nextObstacle += speed * (1.05f + Random.nextFloat() * 0.55f)
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

@Serializable
private class DinoObstacle(
    var x: Float,
    val type: ObstacleType
)

@Serializable
private enum class ObstacleType(
    private val sprite: ArcadeSprite,
    val altitude: Float = 0f
) {
    SmallCactus(DinoSmallCactus),
    LargeCactus(DinoLargeCactus),
    CactusGroup(DinoCactusGroup),
    Bird(DinoBirdUp, altitude = 8f),
    HighBird(DinoBirdUp, altitude = 62f);

    val width: Float get() = sprite.width.toFloat()
    val height: Float get() = sprite.height.toFloat()

    fun sprite(distance: Float): ArcadeSprite =
        if ((this == Bird || this == HighBird) && (distance / 36f).toInt() % 2 == 1) {
            DinoBirdDown
        } else {
            sprite
        }
}

private const val DinoBody =
    "M24 2H41V4H44V16H34V19H41V22H31V25H36V29H33V27H31V33H28V39H13V36H9V33H6V30H3V26H1V17H4V22H7V25H12V23H16V20H20V17H22V5H24Z"
private const val DinoEye = "M26 6V9H29V6Z"

private val DinoStanding = ArcadeSprite(
    width = 44,
    height = 47,
    data = "$DinoBody $DinoEye M13 39H19V42H17V44H21V47H13Z M22 39H28V44H31V47H24V43H22Z"
)

private val DinoRunningLeft = ArcadeSprite(
    width = 44, height = 47,
    data = "$DinoBody $DinoEye M13 39H19V43H17V44H21V47H13Z M22 39H28V40H31V43H25V41H22Z"
)

private val DinoRunningRight = ArcadeSprite(
    width = 44, height = 47,
    data = "$DinoBody $DinoEye M13 39H19V40H16V42H12V39H13Z M22 39H28V44H31V47H24V43H22Z"
)

private val DinoCrashed = ArcadeSprite(
    width = 44, height = 47,
    data = "$DinoBody M25 5V11H31V5Z M27 7H29V9H27Z M13 39H19V42H17V44H21V47H13Z M22 39H28V44H31V47H24V43H22Z"
)

private val DinoSmallCactus = ArcadeSprite(
    width = 18,
    height = 29,
    data = "M7 0H11V2H13V14H15V9H18V17H16V19H13V29H6V22H3V20H1V11H4V17H6V2H7Z"
)

private val DinoLargeCactus = ArcadeSprite(
    width = 26, height = 42,
    data = "M10 0H16V2H18V21H21V10H23V8H25V23H23V26H18V42H8V30H4V28H1V15H3V13H6V24H8V3H10Z"
)

private val DinoCactusGroup = ArcadeSprite(
    width = 46, height = 39,
    data = "M1 22H4V27H6V13H7V11H11V13H13V24H15V19H16V15H19V22H21V2H23V0H28V2H30V18H32V12H34V10H36V20H34V23H30V39H21V27H18V25H17V27H13V39H6V32H3V30H1Z M33 25H35V29H37V18H38V16H42V18H43V27H45V22H46V30H43V39H37V33H33Z"
)

private val DinoBirdUp = ArcadeSprite(
    width = 42, height = 24,
    data = "M0 14H4V11H8V8H12V10H16V12H20V9H18V4H20V1H23V4H26V7H29V12H36V14H42V16H36V18H30V20H15V18H10V15H0Z"
)

private val DinoBirdDown = ArcadeSprite(
    width = 42, height = 24,
    data = "M0 14H4V11H8V8H12V10H16V12H33V14H42V16H36V18H30V20H26V24H22V22H18V20H15V18H10V15H0Z"
)

private val DinoCloud = ArcadeSprite(
    width = 54, height = 16,
    data = "M0 15H7V12H12V9H17V5H22V2H31V4H36V7H41V10H47V12H54V13.5H46V11.5H40V8.5H35V5.5H30V3.5H23V6.5H18V10.5H13V13.5H8V16H0Z"
)

private val DinoGround = ArcadeSprite(
    width = 144, height = 15,
    data = "M0 0H30V1H37V0H87V1H99V0H144V1.5H100V2.5H86V1.5H38V2.5H29V1.5H0Z " +
            "M9 7H14V8H9Z M25 11H28V12H25Z M46 5H50V6H46Z M61 9H69V10H61Z M81 13H85V14H81Z " +
            "M104 7H109V8H104Z M121 11H125V12H121Z M135 5H138V6H135Z"
)