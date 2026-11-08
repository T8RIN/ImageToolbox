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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.hypot
import kotlin.random.Random

private val SpaceShip = ArcadeSprite(
    "SpaceShip", 26, 30,
    "M11 0H15V5H17V10H20V17H24V22H26V28H18V25H16V22H10V25H8V28H0V22H2V17H6V10H9V5H11Z"
)
private val SpaceInvader = ArcadeSprite(
    "SpaceInvader", 40, 24,
    "M14 0H26V3H30V8H35V11H40V17H35V20H29V24H24V20H16V24H11V20H5V17H0V11H5V8H10V3H14Z"
)
private val SpaceBoulder = ArcadeSprite(
    "SpaceBoulder", 48, 48,
    "M12 1H29V5H39V11H44V24H48V31H42V40H33V46H17V42H7V35H2V25H0V15H6V7H12Z"
)

@Composable
fun AsteroidsGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::AsteroidsRun
    )
    ArcadeBoard(
        game = ArcadeGame.Asteroids,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::aim,
            onDrag = run::aim,
            onRelease = run::stopInput
        ) {
            arcadeStars(
                style = style,
                width = run.width,
                height = run.height,
                drift = run.distance
            )
            for (rock in run.rocks) {
                rotate(rock.spin * 180f / PI.toFloat(), Offset(rock.x, rock.y)) {
                    scale(rock.radius / 24f, Offset(rock.x, rock.y)) {
                        drawArcadeSprite(
                            sprite = SpaceBoulder,
                            tint = style.obstacleColor,
                            x = rock.x - 24f,
                            y = rock.y - 24f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = rock.x - 12f,
                            y = rock.y - 11f,
                            width = 8f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = rock.x - 15f,
                            y = rock.y - 8f,
                            width = 3f,
                            height = 7f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = rock.x + 6f,
                            y = rock.y + 8f,
                            width = 7f,
                            height = 3f
                        )
                    }
                }
            }
            for (invader in run.invaders) {
                drawArcadeSprite(
                    sprite = SpaceInvader,
                    tint = style.accentColor,
                    x = invader.x - 20f,
                    y = invader.y - 12f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = invader.x - 7f,
                    y = invader.y - 7f,
                    width = 14f,
                    height = 5f
                )
                repeat(3) { index ->
                    pixelRect(
                        color = style.playerDetailColor,
                        x = invader.x - 12f + index * 10f,
                        y = invader.y + 2f,
                        width = 4f,
                        height = 3f
                    )
                }
                if (invader.cooldown < 0.35f) {
                    drawCircle(
                        color = style.hazardColor.copy(alpha = 0.25f),
                        radius = 9f,
                        center = Offset(invader.x, invader.y + 10f)
                    )
                    pixelRect(
                        color = style.hazardColor,
                        x = invader.x - 2f,
                        y = invader.y + 7f,
                        width = 4f,
                        height = 7f
                    )
                }
            }
            for (shot in run.enemyShots) {
                val point = Offset(shot.x, shot.y)
                val tail = Offset(shot.vx, shot.vy) * 0.06f
                drawLine(
                    color = style.hazardColor.copy(alpha = 0.3f),
                    start = point - tail,
                    end = point,
                    strokeWidth = 5f
                )
                pixelRect(
                    color = style.hazardColor,
                    x = shot.x - 2f,
                    y = shot.y - 3f,
                    width = 4f,
                    height = 6f
                )
            }
            for (shot in run.shots) {
                pixelRect(
                    color = style.accentColor.copy(alpha = 0.2f),
                    x = shot.x - 2f,
                    y = shot.y,
                    width = 4f,
                    height = 15f
                )
                pixelRect(
                    color = style.accentColor,
                    x = shot.x - 1.5f,
                    y = shot.y - 5f,
                    width = 3f,
                    height = 10f
                )
            }
            if (run.invincible <= 0f || (run.invincible * 12).toInt() % 2 == 0) {
                drawArcadeSprite(
                    sprite = SpaceShip,
                    tint = style.playerColor,
                    x = run.x - 13f,
                    y = run.y - 16f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = run.x - 2f,
                    y = run.y - 7f,
                    width = 4f,
                    height = 10f
                )
                if (run.phase == ArcadePhase.Running) {
                    pixelRect(
                        color = style.accentColor,
                        x = run.x - 3f,
                        y = run.y + 12f,
                        width = 6f,
                        height = 5f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = run.x - 1f,
                        y = run.y + 17f,
                        width = 2f,
                        height = 4f
                    )
                }
            }
            if (run.invincible > 0f) {
                drawCircle(
                    color = style.playerColor.copy(alpha = 0.4f),
                    radius = 22f,
                    center = Offset(run.x, run.y),
                    style = Stroke(1.5f)
                )
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
            arcadeLives(
                style = style,
                remaining = run.lives,
                y = run.topInset / 2f
            )
        }
    }
}

internal data class SpaceRock(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    var spin: Float,
    var health: Int = 1
)

internal data class SpaceShot(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    var life: Float
)


internal data class SpaceInvaderShip(
    var x: Float,
    var y: Float,
    val vx: Float,
    var cooldown: Float,
    var health: Int = 4
)

internal class AsteroidsRun : ArcadeRun() {
    val rocks = mutableListOf<SpaceRock>()
    val invaders = mutableListOf<SpaceInvaderShip>()
    val enemyShots = mutableListOf<SpaceShot>()
    val shots = mutableListOf<SpaceShot>()
    val effects = ArcadeEffects()
    var x = width / 2f
    val y get() = height - 30f
    var distance = 0f
    var lives = 3
    var invincible = 0f
    private var target = x
    private var cooldown = 0f
    private var elapsed = 0f
    private var spawn = 0.7f
    private var invaderTimer = 3.5f
    private var wave = 0

    override fun reset() {
        rocks.clear()
        shots.clear()
        invaders.clear()
        enemyShots.clear()
        effects.clear()
        x = width / 2f
        target = x
        cooldown = 0f
        elapsed = 0f
        spawn = 0.7f
        invaderTimer = 3.5f
        wave = 0
        distance = 0f
        lives = 3
        invincible = 0f
    }

    fun aim(point: Offset) {
        target = point.x.coerceIn(18f, width - 18f)
    }

    override fun stopInput() {
        target = x
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        distance = (distance + seconds * 35f) % 10000f
        effects.advance(seconds)
        invincible = (invincible - seconds).coerceAtLeast(0f)
        x += (target - x).coerceIn(-480f * seconds, 480f * seconds)
        cooldown -= seconds
        if (cooldown <= 0f) {
            shots += SpaceShot(x, y - 17f, 0f, -440f, (height + 30f) / 440f)
            cooldown += 0.19f
        }
        spawn -= seconds
        if (spawn <= 0f) {
            val speed = (height + 40f) / maxOf(1.8f, 3.5f - elapsed * 0.014f)
            val count = if (elapsed > 12f) 2 else 1
            repeat(count) { index ->
                val large = wave % 3 == 2 && index == 0
                val radius = if (large) 34f else 22f
                val px = if (large) x.coerceIn(40f, width - 40f)
                else 35f + Random.nextFloat() * (width - 70f)
                rocks += SpaceRock(
                    px, -radius - index * 35f,
                    (Random.nextFloat() - 0.5f) * 50f, speed,
                    radius, Random.nextFloat() * 6f, if (large) 7 else 2
                )
            }
            wave++
            spawn += maxOf(0.48f, 1.1f - elapsed * 0.006f)
        }
        invaderTimer -= seconds
        if (invaderTimer <= 0f && invaders.size < 2) {
            val fromLeft = Random.nextBoolean()
            invaders += SpaceInvaderShip(
                if (fromLeft) -24f else width + 24f,
                topInset + 26f + Random.nextFloat() * (height - topInset) * 0.15f,
                (if (fromLeft) 1f else -1f) * width / 5.5f, 0.8f
            )
            invaderTimer = maxOf(2.8f, 6f - elapsed * 0.03f)
        }
        for (invader in invaders) {
            invader.x += invader.vx * seconds
            invader.y += sin(elapsed * 2.5f) * 9f * seconds
            invader.cooldown -= seconds
            if (invader.cooldown <= 0f && invader.x in 12f..(width - 12f)) {
                val dx = x - invader.x
                val dy = y - invader.y
                val distance = hypot(dx, dy).coerceAtLeast(1f)
                val speed = maxOf(130f, (height - topInset) / 1.5f)
                enemyShots += SpaceShot(
                    invader.x, invader.y + 10f, dx / distance * speed,
                    dy / distance * speed, distance / speed + 1f
                )
                invader.cooldown = maxOf(0.85f, 1.55f - elapsed * 0.006f)
            }
        }
        invaders.removeAll { it.x < -40f || it.x > width + 40f }
        val enemy = enemyShots.iterator()
        while (enemy.hasNext()) {
            val shot = enemy.next()
            shot.x += shot.vx * seconds
            shot.y += shot.vy * seconds
            shot.life -= seconds
            if (hypot(shot.x - x, shot.y - y) < 12f) {
                damage()
                enemy.remove()
            } else if (shot.life <= 0f || shot.y > height + 12f) enemy.remove()
        }
        if (phase == ArcadePhase.Over) return
        val moving = rocks.iterator()
        while (moving.hasNext()) {
            val rock = moving.next()
            rock.x += rock.vx * seconds
            rock.y += rock.vy * seconds
            if (rock.x < rock.radius || rock.x > width - rock.radius) {
                rock.x = rock.x.coerceIn(rock.radius, width - rock.radius)
                rock.vx = -rock.vx
            }
            rock.spin += seconds * if (rock.vx > 0f) 0.45f else -0.45f
            if (invincible == 0f && hypot(rock.x - x, rock.y - y) < rock.radius + 7f) {
                damage()
                moving.remove()
                if (phase == ArcadePhase.Over) return
            } else if (rock.y > height + rock.radius) moving.remove()
        }
        val iterator = shots.iterator()
        while (iterator.hasNext()) {
            val shot = iterator.next()
            shot.y += shot.vy * seconds
            shot.life -= seconds
            val hit = rocks.indexOfFirst { hypot(it.x - shot.x, it.y - shot.y) < it.radius + 3f }
            val ship = invaders.firstOrNull { hypot(it.x - shot.x, it.y - shot.y) < 20f }
            if (hit >= 0) {
                val rock = rocks[hit]
                rock.health--
                effects.burst(
                    point = Offset(shot.x, shot.y),
                    count = 3
                )
                if (rock.health <= 0) {
                    rocks.removeAt(hit)
                    score += if (rock.radius > 30f) 30 else 10
                    effects.burst(point = Offset(rock.x, rock.y))
                    if (rock.radius > 15f) {
                        for (sign in -1..1 step 2) {
                            rocks += SpaceRock(
                                rock.x + sign * 12f, rock.y, sign * 75f,
                                rock.vy * 1.25f, 11f, rock.spin
                            )
                        }
                    }
                }
                iterator.remove()
            } else if (ship != null) {
                ship.health--
                effects.burst(
                    point = Offset(shot.x, shot.y),
                    count = 4
                )
                if (ship.health <= 0) {
                    invaders.remove(ship)
                    score += 50
                    effects.burst(
                        point = Offset(ship.x, ship.y),
                        count = 16
                    )
                }
                iterator.remove()
            } else if (shot.life <= 0f || shot.y < -8f) iterator.remove()
        }
    }

    private fun damage() {
        if (invincible > 0f) return
        lives--
        invincible = 1.25f
        effects.burst(
            point = Offset(x, y),
            count = 12
        )
        if (lives <= 0) phase = ArcadePhase.Over
    }

    override fun onResize(previous: Size) {
        x *= width / previous.width
        target = x
        for (rock in rocks) {
            rock.x *= width / previous.width
            rock.y *= height / previous.height
            rock.vy *= height / previous.height
        }
        for (invader in invaders) {
            invader.x *= width / previous.width
            invader.y *= height / previous.height
        }
        shots.clear()
        enemyShots.clear()
        effects.clear()
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "ship",
            floatArrayOf(
                x,
                cooldown,
                elapsed,
                spawn,
                distance,
                lives.toFloat(),
                invincible,
                invaderTimer,
                wave.toFloat()
            )
        )
        state.putFloatArray(
            "rocks",
            rocks.flatMap {
                listOf(
                    it.x,
                    it.y,
                    it.vx,
                    it.vy,
                    it.radius,
                    it.spin,
                    it.health.toFloat()
                )
            }.toFloatArray()
        )
        state.putFloatArray(
            "invaders",
            invaders.flatMap { listOf(it.x, it.y, it.vx, it.cooldown, it.health.toFloat()) }
                .toFloatArray()
        )
        state.putFloatArray(
            "enemyShots",
            enemyShots.flatMap { listOf(it.x, it.y, it.vx, it.vy, it.life) }.toFloatArray()
        )
        state.putFloatArray(
            "shots",
            shots.flatMap { listOf(it.x, it.y, it.vx, it.vy, it.life) }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("ship")?.let {
            x = it[0]
            cooldown = it[1]
            elapsed = it[2]
            spawn = it[3]
            distance = it[4]
            lives = it[5].toInt()
            invincible = it[6]
            invaderTimer = it[7]
            wave = it[8].toInt()
        }
        stopInput()
        rocks.clear()
        shots.clear()
        state.getFloatArray("rocks")?.let {
            for (i in it.indices step 7) rocks += SpaceRock(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4],
                it[i + 5],
                it[i + 6].toInt()
            )
        }
        invaders.clear()
        enemyShots.clear()
        state.getFloatArray("invaders")?.let {
            for (i in it.indices step 5) invaders += SpaceInvaderShip(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4].toInt()
            )
        }
        state.getFloatArray("enemyShots")?.let {
            for (i in it.indices step 5) enemyShots += SpaceShot(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4]
            )
        }
        state.getFloatArray("shots")?.let {
            for (i in it.indices step 5) shots += SpaceShot(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4]
            )
        }
    }
}