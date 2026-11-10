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

@file:UseSerializers(ArcadeOffsetSerializer::class)

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeCloud
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val PlatformWidth = 64f
private const val JumpVelocity = 365f

private const val DoodleBodyPath =
    "M11 2H24V4H28V7H31V13H33V16H41V17H44V21H41V22H33V30H31V34H29V37H28V43H25V38" +
            "H23V44H20V39H17V44H14V38H12V43H9V37H6V33H4V28H2V20H3V13H5V8H8V5H11Z"
private const val DoodleFacePath = "M20 9H22V14H20Z M27 11H29V16H27Z M40 18H44V20H40Z"
private const val DoodleStripesPath = "M5 27H30V29H5Z M8 33H27V35H8Z"
private val DoodleBody = ArcadeSprite(
    width = 44,
    height = 44,
    data = DoodleBodyPath
)
private val DoodleFace = ArcadeSprite(
    width = 44,
    height = 44,
    data = DoodleFacePath
)
private val DoodleStripes = ArcadeSprite(
    width = 44,
    height = 44,
    data = DoodleStripesPath
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
private val DoodleShootingBody = ArcadeSprite(
    width = 44, height = 44,
    data = "M19 0H25V9H29V12H32V18H33V30H31V34H29V37H28V43H25V38H23V44H20V39H17V44H14V38" +
            "H12V43H9V37H6V33H4V28H2V20H3V13H5V8H8V5H14V8H19Z"
)

@Composable
fun DoodleGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::DoodleRun
    )
    val ink = style.playerDetailColor

    ArcadeBoard(
        game = ArcadeGame.Doodle,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        isPaused = isPaused,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = { run.beginDrag(it.x) },
            onTap = run::shoot,
            onSecondaryTap = run::shoot,
            onDrag = { run.drag(it.x) },
            onRelease = run::stopInput
        ) {
            var cloudY = run.camera * 0.2f % 420f - 16f
            var index = 0
            while (cloudY < run.height) {
                drawArcadeSprite(
                    sprite = ArcadeCloud,
                    tint = style.detailColor,
                    x = if (index % 2 == 0) run.width * 0.16f else run.width * 0.7f,
                    y = cloudY
                )
                cloudY += 210f
                index++
            }
            for ((_, centerX, height, width, kind, _, _, _, _, falling) in run.platforms) {
                val y = run.height - height + run.camera + falling * falling * 280f
                val x = centerX - width / 2
                val color = when (kind) {
                    PlatformKind.Crumbling -> style.accentColor
                    PlatformKind.Moving -> style.playerColor
                    else -> style.obstacleColor
                }
                val tint = color.copy(alpha = (1 - falling * 1.8f).coerceIn(0f, 1f))
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
                drawArcadeSprite(
                    sprite = sprite,
                    tint = color,
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
            fun player(x: Float) {
                scale(
                    scaleX = if (run.facingRight || run.shooting) 1f else -1f,
                    scaleY = 1f,
                    pivot = Offset(x = x, y = feet)
                ) {
                    drawArcadeSprite(
                        sprite = if (run.shooting) DoodleShootingBody else DoodleBody,
                        tint = style.playerColor,
                        x = x - 22,
                        y = feet - 44
                    )
                    drawArcadeSprite(
                        sprite = DoodleStripes,
                        tint = ink.copy(alpha = 0.4f),
                        x = x - 22,
                        y = feet - 44
                    )
                    if (run.shooting) {
                        pixelRect(
                            color = ink,
                            x = x - 2f,
                            y = feet - 44f,
                            width = 4f,
                            height = 2f
                        )
                        pixelRect(
                            color = ink,
                            x = x - 10,
                            y = feet - 30,
                            width = 2f,
                            height = 5f
                        )
                        pixelRect(
                            color = ink,
                            x = x + 4,
                            y = feet - 30,
                            width = 2f,
                            height = 5f
                        )
                    } else drawArcadeSprite(
                        sprite = DoodleFace,
                        tint = ink,
                        x = x - 22,
                        y = feet - 44
                    )
                }
            }
            player(run.x)
            if (run.x < 22) player(run.x + run.width)
            if (run.x > run.width - 22) player(run.x - run.width)
        }
    }
}

@Serializable
internal enum class PlatformKind { Standard, Spring, Crumbling, Moving }

@Serializable
internal enum class MonsterKind(
    val halfWidth: Float,
    val halfHeight: Float
) {
    Flyer(19f, 14f), Jumper(16f, 16f), Guard(28f, 19f)
}

@Serializable
internal data class DoodlePlatform(
    val id: Int,
    var x: Float,
    val height: Float,
    val width: Float,
    val kind: PlatformKind,
    var origin: Float = x,
    var range: Float = 0f,
    val phase: Float = 0f,
    val speed: Float = 0f,
    var breakTime: Float = 0f
)

@Serializable
internal data class DoodleMonster(
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
internal data class DoodleBullet(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float
)

@Serializable
internal data class DoodleBurst(
    val x: Float,
    val height: Float,
    var time: Float = 0f
)

@Serializable
internal class DoodleRun : ArcadeRun() {
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
    val platforms = mutableListOf<DoodlePlatform>()
    val monsters = mutableListOf<DoodleMonster>()
    val bullets = mutableListOf<DoodleBullet>()
    val bursts = mutableListOf<DoodleBurst>()

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
        bursts.clear()
        platforms += DoodlePlatform(
            id = platformId++,
            x = x,
            height = y,
            width = PlatformWidth,
            kind = PlatformKind.Standard
        )
        addPlatforms()
    }

    fun beginDrag(position: Float) {
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
        if (shotTime > 0.04f) return
        val rise = (height - y + camera - 44f - target.y).coerceAtLeast(120f)
        val vx = ((target.x - x) / rise * 520f).coerceIn(-200f, 200f)
        bullets += DoodleBullet(x = x, y = y + 44f, vx = vx, vy = sqrt(520f * 520f - vx * vx))
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
        shotTime = (shotTime - seconds).coerceAtLeast(0f)
        val previousY = y
        velocity -= 670f * seconds
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
            val hit = monsters.firstOrNull {
                abs(bullet.x - it.x) < it.kind.halfWidth + 2f && abs(bullet.y - it.height) < it.kind.halfHeight + 4f
            }
            if (hit != null) {
                shots.remove()
                hit.health--
                hit.hitTime = 0.1f
                if (hit.health == 0) {
                    bursts += DoodleBurst(x = hit.x, height = hit.height)
                    monsters.remove(hit)
                }
            } else if (bullet.y > camera + height + 120 || bullet.x < -10 || bullet.x > width + 10) shots.remove()
        }
        bursts.forEach { it.time += seconds }
        bursts.removeAll { it.time >= 0.3f }
        val hit = monsters.firstOrNull {
            horizontalDistance(
                first = x,
                second = it.x
            ) < 13f + it.kind.halfWidth &&
                    y < it.height + it.kind.halfHeight && y + 36f > it.height - it.kind.halfHeight
        }
        if (hit != null) {
            if (hit.kind != MonsterKind.Guard && velocity < 0 &&
                previousY >= hit.previousHeight + hit.kind.halfHeight
            ) {
                monsters.remove(hit)
                bursts += DoodleBurst(x = hit.x, height = hit.height)
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
                }
            }
        }
        camera = maxOf(camera, y - (height - topInset) * 0.55f)
        score = maxOf(score, ((y - 24f) / 10f).toInt())
        platforms.removeAll { it.height < camera - 55f || it.breakTime > 0.55f }
        monsters.removeAll { it.height < camera - 55f }
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
            val gap =
                if (gate || row % 18 == 15) 72f else 38f + Random.nextFloat() * 24f + difficulty * 8f
            val candidates = List(20) { margin + Random.nextFloat() * (width - margin * 2) }
            val nextX = candidates.firstOrNull {
                val distance = abs(it - lastX)
                distance in width * 0.15f..width * 0.48f
            } ?: (if (lastX < width / 2) lastX + width * 0.2f else lastX - width * 0.2f)
                .coerceIn(margin, width - margin)
            lastHeight += gap
            lastX = nextX
            val kind = when {
                nearGate || row < 5 -> PlatformKind.Standard
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
            if (gate) {
                monsters += DoodleMonster(
                    x = platform.x,
                    height = platform.height + MonsterKind.Guard.halfHeight,
                    kind = MonsterKind.Guard,
                    platformId = platform.id,
                    health = if (difficulty < 0.35f) 1 else 2
                )
            }
            if (!nearGate) {
                val choices = List(12) { margin + Random.nextFloat() * (width - margin * 2) }
                val extraX = choices.firstOrNull { abs(it - nextX) > PlatformWidth + 45f }
                if (extraX != null) {
                    val extra = newPlatform(
                        x = extraX,
                        height = lastHeight + Random.nextFloat() * 16f - 8f,
                        width = PlatformWidth - 6f,
                        kind = when {
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
                        monsters += DoodleMonster(
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
        }
    }

    private fun newPlatform(
        x: Float,
        height: Float,
        width: Float,
        kind: PlatformKind
    ): DoodlePlatform {
        val range = if (kind == PlatformKind.Moving) minOf(
            42f,
            x - width / 2 - 4,
            this.width - x - width / 2 - 4
        ) else 0f
        return DoodlePlatform(
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
        camera = maxOf(camera, y - (height - topInset) * 0.55f)
        addPlatforms()
    }
}