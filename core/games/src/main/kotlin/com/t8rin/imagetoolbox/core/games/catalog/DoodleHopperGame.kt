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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
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
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Alien
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object DoodleHopperGame : ArcadeGameDefinition {
    override val title = R.string.arcade_doodlehopper
    override val description = R.string.arcade_doodlehopper_sub
    override val icon get() = Icons.Outlined.Alien
    override val order = 1

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::DoodleHopperRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.beginDrag(it.x) },
                onTap = run::shoot,
                onSecondaryTap = run::shoot,
                onDrag = { run.drag(it.x) },
                onRelease = run::stopInput
            ) {
                drawDoodleBackground(style, run.width, run.height)
                for (platform in run.platforms) {
                    val (_, centerX, height, width, kind, _, _, _, _, falling) = platform
                    val y = run.height - height + run.camera +
                            if (kind == PlatformKind.SingleUse) 0f else falling * falling * 280f
                    val x = centerX - width / 2
                    val color = when (kind) {
                        PlatformKind.Crumbling -> style.accentColor
                        PlatformKind.Moving -> style.playerColor
                        PlatformKind.SingleUse -> lerp(
                            style.playerColor,
                            style.playerDetailColor,
                            0.55f
                        )
                        else -> style.obstacleColor
                    }
                    val tint = color.copy(alpha = (1 - falling * 1.8f).coerceIn(0f, 1f))
                    if (falling > 0 && kind == PlatformKind.SingleUse) {
                        pixelRect(tint, x + 2f, y, width - 4f, 7f)
                        continue
                    }
                    if (falling > 0) {
                        val half = width / 2 - 2
                        pixelRect(
                            color = tint,
                            x = x - falling * 35,
                            y = y,
                            width = half,
                            height = 7f
                        )
                        pixelRect(
                            color = tint,
                            x = centerX + 2 + falling * 35,
                            y = y + falling * 12,
                            width = half,
                            height = 7f
                        )
                    } else {
                        pixelRect(
                            color = tint,
                            x = x + 2,
                            y = y,
                            width = width - 4,
                            height = 7f
                        )
                        pixelRect(
                            color = tint,
                            x = x,
                            y = y + 2,
                            width = width,
                            height = 3f
                        )
                        pixelRect(
                            color = style.detailColor,
                            x = x + 6,
                            y = y + 7,
                            width = width - 12,
                            height = 2f
                        )
                        pixelRect(
                            lerp(color, style.playerDetailColor, 0.28f),
                            x + 3f,
                            y + 5f,
                            width - 6f,
                            2f
                        )
                        pixelRect(
                            lerp(color, style.textColor, 0.24f),
                            x + 4f,
                            y + 1f,
                            width - 8f,
                            1f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = 0.3f),
                            x + 8f,
                            y + 3f,
                            2f,
                            1f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = 0.3f),
                            x + width - 10f,
                            y + 3f,
                            2f,
                            1f
                        )
                        if (kind == PlatformKind.SingleUse) {
                            for (mark in -1..1) {
                                pixelRect(
                                    style.playerDetailColor,
                                    centerX + mark * 9f - 2f,
                                    y + 2f,
                                    4f,
                                    2f
                                )
                            }
                        }
                        if (kind == PlatformKind.Crumbling) {
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX,
                                y = y,
                                width = 2f,
                                height = 3f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX - 2,
                                y = y + 3,
                                width = 2f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX,
                                y = y + 5,
                                width = 2f,
                                height = 2f
                            )
                        }
                        if (kind == PlatformKind.Moving) {
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX - 9,
                                y = y + 2,
                                width = 18f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX - 12,
                                y = y + 3,
                                width = 3f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = centerX + 9,
                                y = y + 3,
                                width = 3f,
                                height = 2f
                            )
                        }
                        if (kind == PlatformKind.Spring) {
                            pixelRect(
                                color = style.accentColor,
                                x = centerX - 6,
                                y = y - 12,
                                width = 12f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = centerX - 4,
                                y = y - 8,
                                width = 8f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = centerX - 6,
                                y = y - 4,
                                width = 12f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = centerX - 4,
                                y = y - 11,
                                width = 2f,
                                height = 11f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = centerX + 2,
                                y = y - 11,
                                width = 2f,
                                height = 11f
                            )
                        }
                        if (platform.jetpack) {
                            pixelRect(style.accentColor, centerX - 8f, y - 22f, 6f, 15f)
                            pixelRect(style.accentColor, centerX + 2f, y - 22f, 6f, 15f)
                            pixelRect(style.textColor, centerX - 5f, y - 25f, 10f, 4f)
                            pixelRect(style.playerDetailColor, centerX - 6f, y - 19f, 2f, 7f)
                            pixelRect(style.playerDetailColor, centerX + 4f, y - 19f, 2f, 7f)
                            pixelRect(style.hazardColor, centerX - 7f, y - 6f, 4f, 4f)
                            pixelRect(style.hazardColor, centerX + 3f, y - 6f, 4f, 4f)
                        }
                    }
                }
                for (hole in run.holes) {
                    val point = Offset(hole.x, run.height - hole.height + run.camera)
                    pixelCircle(style.detailColor, hole.radius + 4f, point + Offset(0f, 3f))
                    pixelCircle(
                        lerp(style.hazardColor, style.obstacleColor, 0.35f),
                        hole.radius + 2f,
                        point
                    )
                    pixelCircle(style.overlayColor.copy(alpha = 1f), hole.radius - 1f, point)
                    pixelCircle(
                        style.hazardColor.copy(alpha = 0.45f),
                        hole.radius + 5f,
                        point,
                        style = Stroke(1f)
                    )
                    repeat(5) { index ->
                        val angle = run.time * 1.3f + index * 1.256637f
                        val radius = hole.radius + 7f + sin(run.time * 2f + index) * 2f
                        pixelRect(
                            style.accentColor.copy(alpha = 0.7f),
                            point.x + cos(angle) * radius - 1f,
                            point.y + sin(angle) * radius - 1f,
                            2f,
                            2f
                        )
                    }
                }
                for ((x, y) in run.bullets) {
                    pixelRect(
                        color = style.playerColor,
                        x = x - 2,
                        y = run.height - y + run.camera,
                        width = 4f,
                        height = 7f
                    )
                }
                for ((centerX, height, kind, _, _, _, _, _, hitTime) in run.monsters) {
                    val sprite = when (kind) {
                        MonsterKind.Flyer -> FlyingMonster
                        MonsterKind.Jumper -> JumpingMonster
                        MonsterKind.Guard -> PlatformMonster
                    }
                    val face = when (kind) {
                        MonsterKind.Flyer -> FlyingMonsterFace
                        MonsterKind.Jumper -> JumpingMonsterFace
                        MonsterKind.Guard -> PlatformMonsterFace
                    }
                    val color = if (hitTime > 0f) style.playerColor else style.accentColor
                    val x = centerX - kind.halfWidth
                    val y = run.height - height + run.camera - kind.halfHeight
                    reliefSprite(
                        sprite = sprite,
                        color = color,
                        shade = lerp(color, style.overlayColor.copy(alpha = 1f), 0.28f),
                        light = lerp(color, style.textColor, 0.24f),
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = face,
                        tint = style.playerDetailColor,
                        x = x,
                        y = y
                    )
                }
                for ((x, height, time) in run.bursts) {
                    val spread = time * 65f
                    val color = style.accentColor.copy(alpha = (1 - time / 0.3f).coerceIn(0f, 1f))
                    for (direction in listOf(-1f, 1f)) {
                        pixelRect(
                            color = color,
                            x = x + spread * direction,
                            y = run.height - height + run.camera - spread,
                            width = 3f,
                            height = 3f
                        )
                        pixelRect(
                            color = color,
                            x = x + spread * direction,
                            y = run.height - height + run.camera + spread,
                            width = 3f,
                            height = 3f
                        )
                    }
                }
                val feet = run.height - run.y + run.camera
                val shooting = run.shooting
                fun player(x: Float) {
                    scale(
                        scaleX = if (run.facingRight || shooting) 1f else -1f,
                        scaleY = 1f,
                        pivot = Offset(x = x, y = feet)
                    ) {
                        val spriteX = x - 22f
                        val spriteY = feet - 44f
                        if (run.boost > 0f) {
                            val flame = 11f + (run.time * 30f).toInt() % 3 * 3f
                            pixelRect(style.accentColor, spriteX - 5f, spriteY + 11f, 8f, 20f)
                            pixelRect(style.playerDetailColor, spriteX - 3f, spriteY + 14f, 2f, 10f)
                            pixelRect(style.hazardColor, spriteX - 4f, spriteY + 31f, 6f, flame)
                            pixelRect(
                                style.playerDetailColor,
                                spriteX - 2f,
                                spriteY + 31f,
                                2f,
                                flame - 4f
                            )
                        }
                        drawArcadeSprite(
                            sprite = if (shooting) HopperShootingBody else HopperBody,
                            tint = style.playerDetailColor.copy(alpha = 0.45f),
                            x = spriteX + 2f,
                            y = spriteY + 2f
                        )
                        drawArcadeSprite(
                            sprite = if (shooting) HopperShootingBody else HopperBody,
                            tint = style.playerColor,
                            x = spriteX,
                            y = spriteY
                        )
                        drawArcadeSprite(
                            HopperShade,
                            lerp(style.playerColor, style.playerDetailColor, 0.3f),
                            spriteX,
                            spriteY
                        )
                        drawArcadeSprite(
                            HopperGlint,
                            lerp(style.playerColor, style.obstacleColor, 0.65f),
                            spriteX,
                            spriteY
                        )
                        drawArcadeSprite(
                            sprite = if (shooting) HopperShootingAntenna else HopperAntenna,
                            tint = style.accentColor,
                            x = spriteX,
                            y = spriteY
                        )
                        drawArcadeSprite(
                            sprite = if (shooting) HopperShootingEye else HopperEye,
                            tint = style.playerDetailColor,
                            x = spriteX,
                            y = spriteY
                        )
                        drawArcadeSprite(
                            sprite = if (shooting) HopperShootingFace else HopperFace,
                            tint = style.textColor,
                            x = spriteX,
                            y = spriteY
                        )
                    }
                }

                val center = Offset(run.x, feet - 22f)
                val shrink = (1f - run.sinking / 0.55f).coerceIn(0f, 1f)
                rotate(degrees = run.sinking * 640f, pivot = center) {
                    scale(scale = shrink, pivot = center) {
                        player(run.x)
                        if (run.x < 22) player(run.x + run.width)
                        if (run.x > run.width - 22) player(run.x - run.width)
                    }
                }
            }
        }
    }
}

private const val PlatformWidth = 64f
private const val JumpVelocity = 365f

private val HopperBody = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M4 28H8V24H6V16H8V12H10V10H12V8H16V6H20V4H24V6H28V8H32V10H34V12H36V14H38V18H40V26H38V28" +
            "H36V32H34V34H32V36H34V42H38V44H24V38H20V42H22V44H10V32H4Z"
)
private val HopperShade = ArcadeSprite(
    width = 44, height = 44,
    data = "M28 8H32V12H34V16H36V22H34V28H32V32H28V30H30V24H32V18H30V14H28Z M12 32H16V38H14V42H10V36Z M24 38H28V42H34V44H24Z"
)
private val HopperGlint = ArcadeSprite(
    width = 44, height = 44,
    data = "M10 12H12V10H16V8H20V6H24V8H18V10H14V14H12V22H10Z M14 26H17V28H14Z"
)
private val HopperAntenna = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M18 2H26V4H18Z"
)
private val HopperEye = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M20 18H22V16H24V14H34V16H36V24H34V26H22V24H20Z"
)
private val HopperFace = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M30 16H36V24H30Z M30 28H34V30H30Z"
)

private val HopperShootingBody = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M2 22H6V16H8V12H10V10H12V8H16V6H20V4H24V6H28V8H32V10H34V12H36V16H38V22H42V26H40V28" +
            "H36V30H34V34H32V36H34V42H36V44H24V38H20V44H8V42H10V36H12V34H10V30H8V28H4V26H2Z"
)
private val HopperShootingAntenna = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M14 0H16V2H14Z M18 2H26V4H18Z M28 0H30V2H28Z"
)
private val HopperShootingEye = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M14 12H16V10H28V12H30V20H28V22H16V20H14Z"
)
private val HopperShootingFace = ArcadeSprite(
    width = 44,
    height = 44,
    data = "M18 10H26V16H18Z M20 26H24V30H20Z"
)

private val FlyingMonster = ArcadeSprite(
    width = 38, height = 28,
    data = "M0 6H5V10H9V5H13V2H25V5H29V10H33V6H38V16H34V20H29V24H25V27H13V24H9V20H4V16H0Z M12 9H17V15H12Z M22 9H27V15H22Z M16 21H23V23H16Z"
)
private val JumpingMonster = ArcadeSprite(
    width = 32, height = 32,
    data = "M4 0H8V5H24V0H28V7H30V12H32V25H28V29H25V32H20V27H12V32H7V29H4V25H0V12H2V7H4Z M6 11H13V18H6Z M19 11H26V18H19Z M10 22H22V24H10Z"
)
private val PlatformMonster = ArcadeSprite(
    width = 56, height = 38,
    data = "M8 3H17V0H22V5H34V0H39V3H48V7H52V13H56V29H52V34H46V38H38V34H18V38H10V34H4V29H0V13H4V7H8Z M8 12H16V21H8Z M24 10H32V19H24Z M40 12H48V21H40Z M12 27H18V30H23V27H33V30H38V27H44V32H12Z"
)
private val FlyingMonsterFace = ArcadeSprite(
    width = 38, height = 28,
    data = "M12 9H17V15H12Z M22 9H27V15H22Z M16 21H23V23H16Z"
)
private val JumpingMonsterFace = ArcadeSprite(
    width = 32, height = 32,
    data = "M6 11H13V18H6Z M19 11H26V18H19Z M10 22H22V24H10Z"
)
private val PlatformMonsterFace = ArcadeSprite(
    width = 56, height = 38,
    data = "M8 12H16V21H8Z M24 10H32V19H24Z M40 12H48V21H40Z M12 27H18V30H23V27H33V30H38V27H44V32H12Z"
)

private fun DrawScope.drawDoodleBackground(style: ArcadeStyle, width: Float, height: Float) {
    val color = style.detailColor.copy(alpha = 0.055f)
    var y = 8f
    while (y < height) {
        drawLine(color, Offset(0f, y), Offset(width, y), 0.6f)
        y += 18f
    }
    var x = 17f
    while (x < width) {
        drawLine(color, Offset(x, 0f), Offset(x, height), 0.6f)
        x += 18f
    }
    drawLine(style.detailColor.copy(alpha = 0.16f), Offset(17f, 0f), Offset(17f, height), 1f)
    for (hole in 0..3) {
        pixelCircle(
            style.detailColor.copy(alpha = 0.2f), 2f,
            Offset(7f, height * (hole + 0.5f) / 4f), style = Stroke(0.8f)
        )
    }
}

@Serializable
private enum class PlatformKind { Standard, Spring, Crumbling, Moving, SingleUse }

@Serializable
private enum class MonsterKind(
    val halfWidth: Float,
    val halfHeight: Float
) {
    Flyer(19f, 14f), Jumper(16f, 16f), Guard(28f, 19f)
}

@Serializable
private data class DoodleHopperPlatform(
    val id: Int,
    var x: Float,
    val height: Float,
    val width: Float,
    val kind: PlatformKind,
    var origin: Float = x,
    var range: Float = 0f,
    val phase: Float = 0f,
    val speed: Float = 0f,
    var breakTime: Float = 0f,
    var jetpack: Boolean = false
)

@Serializable
private data class DoodleHopperMonster(
    var x: Float,
    var height: Float,
    val kind: MonsterKind,
    val platformId: Int,
    var left: Float = x,
    var right: Float = x,
    var direction: Int = 1,
    var health: Int = 1,
    var hitTime: Float = 0f,
    val baseHeight: Float = height
) {
    var previousHeight = height
}

@Serializable
private data class DoodleHopperBullet(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float
)

@Serializable
private data class DoodleHopperBurst(
    val x: Float,
    val height: Float,
    var time: Float = 0f
)

@Serializable
private data class DoodleHopperHole(
    var x: Float,
    val height: Float,
    val radius: Float
)

@Serializable
private class DoodleHopperRun : ArcadeRun() {
    var x = width / 2
        private set
    var y = 24f
        private set
    var camera = 0f
        private set
    var time = 0f
        private set
    var facingRight = true
        private set
    val shooting get() = shotTime > 0f
    val platforms = mutableListOf<DoodleHopperPlatform>()
    val monsters = mutableListOf<DoodleHopperMonster>()
    val bullets = mutableListOf<DoodleHopperBullet>()
    val holes = mutableListOf<DoodleHopperHole>()
    var sinking = 0f
        private set
    var boost = 0f
        private set
    private var protection = 0f
    private var sinkX = 0f
    private var sinkHeight = 0f
    val bursts = mutableListOf<DoodleHopperBurst>()

    @Transient
    private var touchX: Float? = null
    private var velocity = 0f
    private var row = 0
    private var platformId = 0
    private var lastHeight = 24f
    private var lastX = x
    private var shotTime = 0f

    override fun isValid(): Boolean = super.isValid() &&
            platforms.isNotEmpty()

    override fun reset() {
        x = width / 2
        touchX = null
        y = 24f
        velocity = JumpVelocity
        camera = 0f
        time = 0f
        shotTime = 0f
        facingRight = true
        row = 0
        platformId = 0
        lastHeight = y
        lastX = x
        platforms.clear()
        monsters.clear()
        bullets.clear()
        holes.clear()
        sinking = 0f
        boost = 0f
        protection = 0f
        bursts.clear()
        platforms += DoodleHopperPlatform(
            id = platformId++,
            x = x,
            height = y,
            width = PlatformWidth,
            kind = PlatformKind.Standard
        )
        addPlatforms()
    }

    fun beginDrag(position: Float) {
        if (sinking > 0f) return
        touchX = position
    }

    fun drag(position: Float) {
        val previous = touchX ?: return
        val movement = position - previous
        x = ((x + movement) % width + width) % width
        if (abs(movement) > 0.5f) facingRight = movement > 0f
        touchX = position
        redraw()
    }

    fun shoot(target: Offset) {
        if (sinking > 0f) return
        if (shotTime > 0.04f) return
        val rise = (height - y + camera - 44f - target.y).coerceAtLeast(24f)
        val dx = target.x - x
        val vx = (dx / hypot(dx, rise) * 520f).coerceIn(-200f, 200f)
        bullets += DoodleHopperBullet(x = x, y = y + 44f, vx = vx, vy = sqrt(520f * 520f - vx * vx))
        shotTime = 0.18f
    }

    override fun stopInput() {
        touchX = null
    }

    private fun horizontalDistance(
        first: Float,
        second: Float
    ): Float {
        val distance = abs(first - second)
        return minOf(distance, width - distance)
    }

    override fun advance(seconds: Float) {
        time += seconds
        if (sinking > 0f) {
            sinking += seconds
            var dx = sinkX - x
            if (dx > width / 2f) dx -= width
            if (dx < -width / 2f) dx += width
            val follow = 1f - exp(-seconds * 12f)
            x = ((x + dx * follow) % width + width) % width
            y += (sinkHeight - 22f - y) * follow
            if (sinking >= 0.55f) finish()
            return
        }
        shotTime = (shotTime - seconds).coerceAtLeast(0f)
        val previousY = y
        boost = (boost - seconds).coerceAtLeast(0f)
        protection = (protection - seconds).coerceAtLeast(0f)
        if (boost > 0f) velocity = 510f else velocity -= 670f * seconds
        y += velocity * seconds
        platforms.forEach {
            if (it.kind == PlatformKind.Moving) it.x =
                it.origin + sin(time * it.speed + it.phase) * it.range
            if (it.breakTime > 0) it.breakTime += seconds
        }
        monsters.forEach { monster ->
            monster.previousHeight = monster.height
            monster.hitTime = (monster.hitTime - seconds).coerceAtLeast(0f)
            if (monster.kind == MonsterKind.Flyer) {
                monster.x += monster.direction * 48f * seconds
                if (monster.x <= monster.left || monster.x >= monster.right) {
                    monster.x = monster.x.coerceIn(monster.left, monster.right)
                    monster.direction = -monster.direction
                }
                monster.height = monster.baseHeight + sin(time * 3f) * 8f
            } else {
                platforms.firstOrNull { it.id == monster.platformId }?.let {
                    monster.x = it.x
                    monster.height = it.height + monster.kind.halfHeight
                    if (monster.kind == MonsterKind.Jumper) monster.height += abs(sin(time * 2.4f + it.phase)) * 24f
                }
            }
        }
        val shots = bullets.iterator()
        while (shots.hasNext()) {
            val bullet = shots.next()
            bullet.x += bullet.vx * seconds
            bullet.y += bullet.vy * seconds
            if (holes.any {
                    horizontalDistance(bullet.x, it.x).let { dx ->
                        val dy = bullet.y - it.height
                        dx * dx + dy * dy < it.radius * it.radius
                    }
                }) {
                shots.remove()
                continue
            }
            val hit = monsters.firstOrNull {
                abs(bullet.x - it.x) < it.kind.halfWidth + 2f && abs(bullet.y - it.height) < it.kind.halfHeight + 4f
            }
            if (hit != null) {
                shots.remove()
                hit.health--
                hit.hitTime = 0.1f
                if (hit.health == 0) {
                    bursts += DoodleHopperBurst(x = hit.x, height = hit.height)
                    monsters.remove(hit)
                }
            } else if (bullet.y > camera + height + 120 || bullet.x < -10 || bullet.x > width + 10) shots.remove()
        }
        bursts.forEach { it.time += seconds }
        bursts.removeAll { it.time >= 0.3f }
        platforms.firstOrNull {
            it.jetpack && horizontalDistance(x, it.x) < 20f &&
                    y < it.height + 26f && y + 36f > it.height + 7f
        }?.let {
            it.jetpack = false
            boost = 1.65f
            protection = 2.25f
            velocity = 510f
            bursts += DoodleHopperBurst(x = x, height = y)
        }
        val hole = holes.firstOrNull {
            val dx = horizontalDistance(x, it.x)
            val dy = y + 22f - it.height
            val radius = it.radius * 0.8f + 10f
            dx * dx + dy * dy < radius * radius
        }
        if (hole != null && protection <= 0f) {
            sinking = 0.001f
            sinkX = hole.x
            sinkHeight = hole.height
            stopInput()
            return
        }
        val hit = monsters.firstOrNull {
            horizontalDistance(
                first = x,
                second = it.x
            ) < 13f + it.kind.halfWidth &&
                    y < it.height + it.kind.halfHeight && y + 36f > it.height - it.kind.halfHeight
        }
        if (hit != null && protection <= 0f) {
            if (hit.kind != MonsterKind.Guard && velocity < 0 &&
                previousY >= hit.previousHeight + hit.kind.halfHeight
            ) {
                monsters.remove(hit)
                bursts += DoodleHopperBurst(x = hit.x, height = hit.height)
                y = hit.height + hit.kind.halfHeight
                velocity = JumpVelocity
            } else {
                phase = ArcadePhase.Over
                return
            }
        }
        if (velocity < 0f) {
            val landing = platforms.filter {
                it.breakTime == 0f && previousY >= it.height && y <= it.height &&
                        horizontalDistance(
                            first = x,
                            second = it.x
                        ) <= 12f + it.width / 2
            }.maxByOrNull { it.height }
            if (landing != null) {
                if (landing.kind == PlatformKind.Crumbling) {
                    landing.breakTime = 0.001f
                } else {
                    y = landing.height
                    velocity =
                        if (landing.kind == PlatformKind.Spring) JumpVelocity * 1.4f else JumpVelocity
                    if (landing.kind == PlatformKind.SingleUse) landing.breakTime = 0.001f
                }
            }
        }
        camera = maxOf(camera, y - (height - topInset) * 0.55f)
        score = maxOf(score, ((y - 24f) / 10f).toInt())
        platforms.removeAll { it.height < camera - 55f || it.breakTime > 0.55f }
        monsters.removeAll { it.height < camera - 55f }
        holes.removeAll { it.height < camera - 70f }
        bullets.removeAll { it.y < camera - 55f }
        addPlatforms()
        if (y < camera - 44f) phase = ArcadePhase.Over
    }

    private fun addPlatforms() {
        val margin = PlatformWidth / 2 + 6f
        while (lastHeight < camera + height + 120f) {
            row++
            val difficulty = (lastHeight / 6000f).coerceIn(0f, 1f)
            val gate = row % 18 == 14
            val nearGate = row % 18 in 12..16
            val holeRow = row >= 10 && row % 22 == 10 && !nearGate
            val gap =
                if (gate || row % 18 == 15) 72f else 38f + Random.nextFloat() * 24f + difficulty * 8f
            fun clearOfHoles(x: Float) = holes.none {
                abs(it.height - (lastHeight + gap + 22f)) < 90f &&
                        horizontalDistance(x, it.x) < it.radius + PlatformWidth / 2f + 18f
            }

            val candidates = List(32) { margin + Random.nextFloat() * (width - margin * 2) }
            val nextX = candidates.firstOrNull {
                val distance = abs(it - lastX)
                distance in width * 0.15f..width * 0.48f && clearOfHoles(it)
            } ?: candidates.firstOrNull(::clearOfHoles)
            ?: (if (lastX < width / 2) lastX + width * 0.2f else lastX - width * 0.2f)
                .coerceIn(margin, width - margin)
            val entryX = lastX
            lastHeight += gap
            lastX = nextX
            val kind = when {
                nearGate || holeRow || row < 5 || holes.any { abs(it.height - lastHeight) < 120f } -> PlatformKind.Standard
                row % 13 == 8 -> PlatformKind.SingleUse
                row % 9 == 6 -> PlatformKind.Spring
                row % 5 == 3 -> PlatformKind.Moving
                else -> PlatformKind.Standard
            }
            val platform = newPlatform(
                x = nextX,
                height = lastHeight,
                width = PlatformWidth - difficulty * 10f,
                kind = kind
            )
            if (row % 36 == 22 && !nearGate && !holeRow) platform.jetpack = true
            if (gate) {
                newPlatform(
                    x = entryX, height = lastHeight - 16f,
                    width = PlatformWidth - 4f, kind = PlatformKind.Standard
                )
                monsters += DoodleHopperMonster(
                    x = platform.x,
                    height = platform.height + MonsterKind.Guard.halfHeight,
                    kind = MonsterKind.Guard,
                    platformId = platform.id,
                    health = if (difficulty < 0.35f) 1 else 2
                )
            }
            if (!nearGate) {
                val choices = List(12) { margin + Random.nextFloat() * (width - margin * 2) }
                val extraX =
                    choices.firstOrNull { abs(it - nextX) > PlatformWidth + 45f && clearOfHoles(it) }
                if (extraX != null) {
                    val extra = newPlatform(
                        x = extraX,
                        height = lastHeight + Random.nextFloat() * 16f - 8f,
                        width = PlatformWidth - 6f,
                        kind = when {
                            row > 14 && row % 6 == 3 -> PlatformKind.SingleUse
                            row > 6 && row % 4 == 0 -> PlatformKind.Crumbling
                            row > 10 && row % 4 == 1 -> PlatformKind.Moving
                            else -> PlatformKind.Standard
                        }
                    )
                    extra.range = minOf(
                        extra.range,
                        (abs(extra.origin - platform.origin) - (extra.width + platform.width) / 2 -
                                platform.range - 12f).coerceAtLeast(0f)
                    )
                    if (row > 10 && row % 7 == 4) {
                        val flying = row % 2 == 0
                        monsters += DoodleHopperMonster(
                            x = extra.x,
                            height = extra.height + if (flying) 40f else 16f,
                            kind = if (flying) MonsterKind.Flyer else MonsterKind.Jumper,
                            platformId = extra.id,
                            left = (extra.x - 45f).coerceAtLeast(24f),
                            right = (extra.x + 45f).coerceAtMost(width - 24f)
                        )
                    }
                }
            }
            if (holeRow) {
                val radius = minOf(24f, width * 0.075f)
                val holeX = listOf(width * 0.18f, width * 0.5f, width * 0.82f)
                    .shuffled().firstOrNull {
                        horizontalDistance(
                            it,
                            nextX
                        ) > radius + PlatformWidth / 2f + 24f
                    }
                if (holeX != null) {
                    val holeHeight = lastHeight + 35f
                    platforms.removeAll {
                        it.id != platform.id && abs(it.height - holeHeight) < radius + 25f &&
                                horizontalDistance(it.x, holeX) < radius + it.width / 2f + 18f
                    }
                    holes += DoodleHopperHole(holeX, holeHeight, radius)
                }
            }
        }
    }

    private fun newPlatform(
        x: Float,
        height: Float,
        width: Float,
        kind: PlatformKind
    ): DoodleHopperPlatform {
        val range = if (kind == PlatformKind.Moving) minOf(
            42f,
            x - width / 2 - 4,
            this.width - x - width / 2 - 4
        ) else 0f
        return DoodleHopperPlatform(
            id = platformId++,
            x = x,
            height = height,
            width = width,
            kind = kind,
            range = range.coerceAtLeast(0f),
            phase = Random.nextFloat() * 6.28f,
            speed = 0.85f + Random.nextFloat() * 0.4f
        ).also {
            it.x = it.origin + sin(time * it.speed + it.phase) * it.range
            platforms += it
        }
    }

    override fun onResize(previous: Size) {
        val ratio = width / previous.width
        x *= ratio
        lastX *= ratio
        stopInput()
        platforms.forEach {
            it.x *= ratio
            it.origin *= ratio
            it.range *= ratio
        }
        monsters.forEach {
            it.x *= ratio
            it.left *= ratio
            it.right *= ratio
        }
        bullets.forEach { it.x *= ratio }
        holes.forEach { it.x *= ratio }
        sinkX *= ratio
        camera = maxOf(camera, y - (height - topInset) * 0.55f)
        addPlatforms()
    }
}