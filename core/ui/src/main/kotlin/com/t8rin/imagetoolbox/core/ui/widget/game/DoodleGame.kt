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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val PlatformWidth = 64f
private const val JumpVelocity = 365f

private const val DoodleBodyPath =
    "M11 2H23V4H28V7H31V11H33V16H41V18H44V23H41V25H33V34H30V38H29V43H25V38H23V44H19V38H17V44H13V38H11V43H7V37H4V32H2V22H3V13H5V8H8V5H11Z"
private const val DoodleFacePath = "M20 9H22V14H20Z M27 11H29V16H27Z M39 19H42V22H39Z"
private const val DoodleStripesPath = "M5 24H30V26H5Z M6 29H29V31H6Z M9 34H27V36H9Z"
private val DoodleBody = ArcadeSprite("DoodleBody", 44, 44, DoodleBodyPath)
private val DoodleFace = ArcadeSprite("DoodleFace", 44, 44, DoodleFacePath)
private val DoodleStripes = ArcadeSprite("DoodleStripes", 44, 44, DoodleStripesPath)

private val FlyingMonster = ArcadeSprite(
    "FlyingMonster", 38, 28,
    "M0 6H5V10H9V5H13V2H25V5H29V10H33V6H38V16H34V20H29V24H25V27H13V24H9V20H4V16H0Z M12 9H17V15H12Z M22 9H27V15H22Z M16 21H23V23H16Z"
)
private val JumpingMonster = ArcadeSprite(
    "JumpingMonster", 32, 32,
    "M4 0H8V5H24V0H28V7H30V12H32V25H28V29H25V32H20V27H12V32H7V29H4V25H0V12H2V7H4Z M6 11H13V18H6Z M19 11H26V18H19Z M10 22H22V24H10Z"
)
private val PlatformMonster = ArcadeSprite(
    "PlatformMonster", 56, 38,
    "M8 3H17V0H22V5H34V0H39V3H48V7H52V13H56V29H52V34H46V38H38V34H18V38H10V34H4V29H0V13H4V7H8Z M8 12H16V21H8Z M24 10H32V19H24Z M40 12H48V21H40Z M12 27H18V30H23V27H33V30H38V27H44V32H12Z"
)
private val FlyingMonsterFace = ArcadeSprite(
    "FlyingMonsterFace", 38, 28,
    "M12 9H17V15H12Z M22 9H27V15H22Z M16 21H23V23H16Z"
)
private val JumpingMonsterFace = ArcadeSprite(
    "JumpingMonsterFace", 32, 32,
    "M6 11H13V18H6Z M19 11H26V18H19Z M10 22H22V24H10Z"
)
private val PlatformMonsterFace = ArcadeSprite(
    "PlatformMonsterFace", 56, 38,
    "M8 12H16V21H8Z M24 10H32V19H24Z M40 12H48V21H40Z M12 27H18V30H23V27H33V30H38V27H44V32H12Z"
)
private val DoodleShootingBody = ArcadeSprite(
    "DoodleShootingBody", 44, 48,
    "M19 0H25V8H29V11H32V16H34V32H31V38H29V43H25V38H23V44H19V38H17V44H13V38H11V43H7V37H4V32H2V22H3V13H5V8H8V5H15V8H19Z"
)

@Composable
fun DoodleGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::DoodleRun)
    val ink = style.playerDetailColor
    ArcadeBoard(
        ArcadeGame.Doodle, run, style, modifier, initialState, onHighScoreChanged, onGameOver
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
                    ArcadeCloud, style.detailColor,
                    if (index % 2 == 0) run.width * 0.16f else run.width * 0.7f,
                    cloudY
                )
                cloudY += 210f
                index++
            }
            for (platform in run.platforms) {
                val falling = platform.breakTime
                val y = run.height - platform.height + run.camera + falling * falling * 280f
                val x = platform.x - platform.width / 2
                val color = when (platform.kind) {
                    PlatformKind.Crumbling -> style.accentColor
                    PlatformKind.Moving -> style.playerColor
                    else -> style.obstacleColor
                }
                val tint = color.copy(alpha = (1 - falling * 1.8f).coerceIn(0f, 1f))
                if (falling > 0) {
                    val half = platform.width / 2 - 2
                    pixelRect(tint, x - falling * 35, y, half, 7f)
                    pixelRect(tint, platform.x + 2 + falling * 35, y + falling * 12, half, 7f)
                } else {
                    pixelRect(tint, x + 2, y, platform.width - 4, 7f)
                    pixelRect(tint, x, y + 2, platform.width, 3f)
                    pixelRect(style.detailColor, x + 6, y + 7, platform.width - 12, 2f)
                    if (platform.kind == PlatformKind.Crumbling) {
                        pixelRect(style.playerDetailColor, platform.x, y, 2f, 3f)
                        pixelRect(style.playerDetailColor, platform.x - 2, y + 3, 2f, 2f)
                        pixelRect(style.playerDetailColor, platform.x, y + 5, 2f, 2f)
                    }
                    if (platform.kind == PlatformKind.Moving) {
                        pixelRect(style.playerDetailColor, platform.x - 9, y + 2, 18f, 2f)
                        pixelRect(style.playerDetailColor, platform.x - 12, y + 3, 3f, 2f)
                        pixelRect(style.playerDetailColor, platform.x + 9, y + 3, 3f, 2f)
                    }
                    if (platform.kind == PlatformKind.Spring) {
                        pixelRect(style.accentColor, platform.x - 6, y - 12, 12f, 2f)
                        pixelRect(style.accentColor, platform.x - 4, y - 8, 8f, 2f)
                        pixelRect(style.accentColor, platform.x - 6, y - 4, 12f, 2f)
                        pixelRect(style.accentColor, platform.x - 4, y - 11, 2f, 11f)
                        pixelRect(style.accentColor, platform.x + 2, y - 11, 2f, 11f)
                    }
                }
            }
            for (bullet in run.bullets) {
                pixelRect(
                    style.playerColor,
                    bullet.x - 2,
                    run.height - bullet.y + run.camera,
                    4f,
                    7f
                )
            }
            for (monster in run.monsters) {
                val sprite = when (monster.kind) {
                    MonsterKind.Flyer -> FlyingMonster
                    MonsterKind.Jumper -> JumpingMonster
                    MonsterKind.Guard -> PlatformMonster
                }
                val face = when (monster.kind) {
                    MonsterKind.Flyer -> FlyingMonsterFace
                    MonsterKind.Jumper -> JumpingMonsterFace
                    MonsterKind.Guard -> PlatformMonsterFace
                }
                val color = if (monster.hitTime > 0f) style.playerColor else style.accentColor
                val x = monster.x - monster.kind.halfWidth
                val y = run.height - monster.height + run.camera - monster.kind.halfHeight
                drawArcadeSprite(sprite, color, x, y)
                drawArcadeSprite(face, style.playerDetailColor, x, y)
            }
            for (burst in run.bursts) {
                val spread = burst.time * 65f
                val color = style.accentColor.copy(alpha = (1 - burst.time / 0.3f).coerceIn(0f, 1f))
                for (direction in listOf(-1f, 1f)) {
                    pixelRect(
                        color,
                        burst.x + spread * direction,
                        run.height - burst.height + run.camera - spread,
                        3f,
                        3f
                    )
                    pixelRect(
                        color,
                        burst.x + spread * direction,
                        run.height - burst.height + run.camera + spread,
                        3f,
                        3f
                    )
                }
            }
            val feet = run.height - run.y + run.camera
            fun player(x: Float) {
                scale(if (run.facingRight || run.shooting) 1f else -1f, 1f, Offset(x, feet)) {
                    drawArcadeSprite(
                        if (run.shooting) DoodleShootingBody else DoodleBody,
                        style.playerColor,
                        x - 22,
                        feet - 44
                    )
                    drawArcadeSprite(DoodleStripes, ink.copy(alpha = 0.4f), x - 22, feet - 44)
                    if (run.shooting) {
                        pixelRect(ink, x - 10, feet - 30, 2f, 5f)
                        pixelRect(ink, x + 4, feet - 30, 2f, 5f)
                    } else drawArcadeSprite(DoodleFace, ink, x - 22, feet - 44)
                }
            }
            player(run.x)
            if (run.x < 22) player(run.x + run.width)
            if (run.x > run.width - 22) player(run.x - run.width)
        }
    }
}

private enum class PlatformKind { Standard, Spring, Crumbling, Moving }

private enum class MonsterKind(val halfWidth: Float, val halfHeight: Float) {
    Flyer(19f, 14f), Jumper(16f, 16f), Guard(28f, 19f)
}

private data class DoodlePlatform(
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

private data class DoodleMonster(
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

private data class DoodleBullet(var x: Float, var y: Float, val vx: Float, val vy: Float)
private data class DoodleBurst(val x: Float, val height: Float, var time: Float = 0f)

private class DoodleRun : ArcadeRun() {
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
    private var touchX: Float? = null
    private var velocity = 0f
    private var row = 0
    private var platformId = 0
    private var lastHeight = 24f
    private var lastX = x
    private var shotTime = 0f

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
        platforms += DoodlePlatform(platformId++, x, y, PlatformWidth, PlatformKind.Standard)
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
        bullets += DoodleBullet(x, y + 44f, vx, sqrt(520f * 520f - vx * vx))
        shotTime = 0.18f
    }

    override fun stopInput() {
        touchX = null
    }

    private fun horizontalDistance(first: Float, second: Float): Float {
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
                    bursts += DoodleBurst(hit.x, hit.height)
                    monsters.remove(hit)
                }
            } else if (bullet.y > camera + height + 120 || bullet.x < -10 || bullet.x > width + 10) shots.remove()
        }
        bursts.forEach { it.time += seconds }
        bursts.removeAll { it.time >= 0.3f }
        val hit = monsters.firstOrNull {
            horizontalDistance(x, it.x) < 13f + it.kind.halfWidth &&
                    y < it.height + it.kind.halfHeight && y + 36f > it.height - it.kind.halfHeight
        }
        if (hit != null) {
            if (hit.kind != MonsterKind.Guard && velocity < 0 && previousY >= hit.previousHeight + hit.kind.halfHeight) {
                monsters.remove(hit)
                bursts += DoodleBurst(hit.x, hit.height)
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
                        horizontalDistance(x, it.x) <= 12f + it.width / 2
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
            val platform = newPlatform(nextX, lastHeight, PlatformWidth - difficulty * 10f, kind)
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
                        extraX,
                        lastHeight + Random.nextFloat() * 16f - 8f,
                        PlatformWidth - 6f,
                        when {
                            row > 6 && row % 4 == 0 -> PlatformKind.Crumbling
                            row > 10 && row % 4 == 1 -> PlatformKind.Moving
                            else -> PlatformKind.Standard
                        }
                    )
                    extra.range = minOf(
                        extra.range,
                        (abs(extra.origin - platform.origin) - (extra.width + platform.width) / 2 - platform.range - 12f).coerceAtLeast(
                            0f
                        )
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
        if (phase == ArcadePhase.Ready) reset() else addPlatforms()
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "runner",
            floatArrayOf(x, y, velocity, camera, time, lastHeight, lastX, shotTime)
        )
        state.putBoolean("facing", facingRight)
        state.putInt("row", row)
        state.putInt("platformId", platformId)
        state.putFloatArray("platforms", platforms.flatMap {
            listOf(
                it.id.toFloat(),
                it.x,
                it.height,
                it.width,
                it.kind.ordinal.toFloat(),
                it.origin,
                it.range,
                it.phase,
                it.speed,
                it.breakTime
            )
        }.toFloatArray())
        state.putFloatArray("monsters", monsters.flatMap {
            listOf(
                it.x,
                it.height,
                it.kind.ordinal.toFloat(),
                it.platformId.toFloat(),
                it.left,
                it.right,
                it.direction.toFloat(),
                it.health.toFloat(),
                it.hitTime,
                it.baseHeight
            )
        }.toFloatArray())
        state.putFloatArray(
            "bullets",
            bullets.flatMap { listOf(it.x, it.y, it.vx, it.vy) }.toFloatArray()
        )
        state.putFloatArray(
            "bursts",
            bursts.flatMap { listOf(it.x, it.height, it.time) }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("runner")?.let {
            x = it[0]
            y = it[1]
            velocity = it[2]
            camera = it[3]
            time = it[4]
            lastHeight = it[5]
            lastX = it[6]
            shotTime = it[7]
        }
        facingRight = state.getBoolean("facing", true)
        row = state.getInt("row")
        platformId = state.getInt("platformId")
        platforms.clear()
        state.getFloatArray("platforms")?.let { points ->
            for (index in points.indices step 10) {
                platforms += DoodlePlatform(
                    points[index].toInt(),
                    points[index + 1],
                    points[index + 2],
                    points[index + 3],
                    PlatformKind.entries[points[index + 4].toInt()],
                    points[index + 5],
                    points[index + 6],
                    points[index + 7],
                    points[index + 8],
                    points[index + 9]
                )
            }
        }
        monsters.clear()
        state.getFloatArray("monsters")?.let { points ->
            for (index in points.indices step 10) {
                monsters += DoodleMonster(
                    points[index],
                    points[index + 1],
                    MonsterKind.entries[points[index + 2].toInt()],
                    points[index + 3].toInt(),
                    points[index + 4],
                    points[index + 5],
                    points[index + 6].toInt(),
                    points[index + 7].toInt(),
                    points[index + 8],
                    points[index + 9]
                )
            }
        }
        bullets.clear()
        state.getFloatArray("bullets")?.let { points ->
            for (index in points.indices step 4) bullets += DoodleBullet(
                points[index],
                points[index + 1],
                points[index + 2],
                points[index + 3]
            )
        }
        bursts.clear()
        state.getFloatArray("bursts")?.let { points ->
            for (index in points.indices step 3) bursts += DoodleBurst(
                points[index],
                points[index + 1],
                points[index + 2]
            )
        }
    }
}
