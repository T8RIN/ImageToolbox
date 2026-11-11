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
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelAsteroid
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelGem
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Planet
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object SpaceRocksGame : ArcadeGameDefinition {
    override val title = R.string.arcade_spacerocks
    override val description = R.string.arcade_spacerocks_sub
    override val icon get() = Icons.Outlined.Planet
    override val order = 10

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::SpaceRocksRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onPress = run::aim,
                onDrag = run::aim,
                onRelease = run::stopInput
            ) {
                drawSpaceBackground(style, run.width, run.height, run.distance)
                for ((x, y, _, _, radius, spin) in run.rocks) {
                    pixelAsteroid(
                        color = style.obstacleColor,
                        ink = style.overlayColor.copy(alpha = 1f),
                        light = style.textColor,
                        center = Offset(x, y), radius = radius,
                        angle = spin * 180f / PI.toFloat()
                    )
                }
                for (pickup in run.pickups) {
                    val point = Offset(pickup.x, pickup.y)
                    pixelCircle(style.accentColor.copy(alpha = 0.14f), 12f, point)
                    pixelGem(style.accentColor, style.textColor, point, 7f)
                }
                if (run.boost > 0f) {
                    pixelRect(
                        style.accentColor.copy(alpha = 0.18f),
                        82f,
                        run.topInset / 2f - 3f,
                        44f,
                        6f
                    )
                    pixelRect(
                        style.accentColor,
                        82f,
                        run.topInset / 2f - 3f,
                        44f * run.boost / 6f,
                        6f
                    )
                }
                for ((x, y, _, cooldown) in run.invaders) {
                    reliefSprite(
                        sprite = SpaceInvader,
                        color = style.accentColor,
                        shade = lerp(style.accentColor, style.overlayColor.copy(alpha = 1f), 0.25f),
                        light = lerp(style.accentColor, style.textColor, 0.2f),
                        x = x - 20f,
                        y = y - 12f
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = x - 7f,
                        y = y - 7f,
                        width = 14f,
                        height = 5f
                    )
                    repeat(3) { index ->
                        pixelRect(
                            color = style.playerDetailColor,
                            x = x - 12f + index * 10f,
                            y = y + 2f,
                            width = 4f,
                            height = 3f
                        )
                    }
                    if (cooldown < 0.35f) {
                        pixelCircle(
                            color = style.hazardColor.copy(alpha = 0.25f),
                            radius = 9f,
                            center = Offset(x = x, y = y + 10f)
                        )
                        pixelRect(
                            color = style.hazardColor,
                            x = x - 2f,
                            y = y + 7f,
                            width = 4f,
                            height = 7f
                        )
                    }
                }
                for ((x, y, vx, vy) in run.enemyShots) {
                    val point = Offset(x = x, y = y)
                    val tail = Offset(x = vx, y = vy) * 0.06f
                    drawLine(
                        color = style.hazardColor.copy(alpha = 0.3f),
                        start = point - tail,
                        end = point,
                        strokeWidth = 5f
                    )
                    pixelRect(
                        color = style.hazardColor,
                        x = x - 2f,
                        y = y - 3f,
                        width = 4f,
                        height = 6f
                    )
                }
                for ((x, y) in run.shots) {
                    pixelRect(
                        color = style.accentColor.copy(alpha = 0.2f),
                        x = x - 2f,
                        y = y,
                        width = 4f,
                        height = 15f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = x - 1.5f,
                        y = y - 5f,
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
                    for (side in listOf(-1f, 1f)) {
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.5f),
                            x = run.x + side * 8f - 1f,
                            y = run.y,
                            width = 2f,
                            height = 7f
                        )
                    }
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
                    pixelCircle(
                        color = style.playerColor.copy(alpha = 0.4f),
                        radius = 22f,
                        center = Offset(x = run.x, y = run.y),
                        style = Stroke(width = 1.5f)
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
}

private val SpaceShip = ArcadeSprite(
    width = 26, height = 30,
    data = "M12 0H14V3H16V12H18V8H21V15H23V18H26V27H22V25H18V28H15V24H11V28H8V25H4V27H0V18H3V15H5V8H8V12H10V3H12Z"
)
private val SpaceInvader = ArcadeSprite(
    width = 40, height = 24,
    data = "M14 0H26V2H30V6H32V10H37V12H40V16H37V18H29V21H25V24H22V20H18V24H15V21H11V18H3V16H0V12H3V10H8V6H10V2H14Z"
)

private fun DrawScope.drawSpaceBackground(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    distance: Float
) {
    repeat(24) { index ->
        val x = ((index * 79 + index * index * 17 + 31) % 359) / 359f * width
        val y = (index * 113f + distance * (0.025f + index % 3 * 0.035f)) % (height + 8f) - 4f
        val tint = style.detailColor.copy(alpha = if (index % 5 == 0) 0.4f else 0.18f)
        pixelRect(tint, x, y, if (index % 5 == 0) 2f else 1f, if (index % 5 == 0) 2f else 1f)
    }
    val planet =
        Offset(width * 0.82f, height * 0.24f)
    val planetRadius = minOf(width, height) * 0.16f
    pixelCircle(style.detailColor.copy(alpha = 0.09f), planetRadius, planet)
    pixelCircle(
        style.detailColor.copy(alpha = 0.12f), planetRadius * 0.32f,
        planet + Offset(-planetRadius * 0.3f, -planetRadius * 0.25f)
    )
    drawOval(
        style.detailColor.copy(alpha = 0.14f),
        planet - Offset(planetRadius * 1.5f, planetRadius * 0.28f),
        Size(planetRadius * 3f, planetRadius * 0.55f), style = Stroke(1f)
    )
}

@Serializable
private data class SpaceRock(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    var spin: Float,
    var health: Int = 1
)

@Serializable
private data class SpaceShot(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float
)

@Serializable
private data class SpaceInvaderShip(
    var x: Float,
    var y: Float,
    val vx: Float,
    var cooldown: Float,
    var health: Int = 4
)

@Serializable
private data class SpacePickup(var x: Float, var y: Float)

@Serializable
private class SpaceRocksRun : ArcadeRun() {
    val rocks = mutableListOf<SpaceRock>()
    val invaders = mutableListOf<SpaceInvaderShip>()
    val enemyShots = mutableListOf<SpaceShot>()
    val shots = mutableListOf<SpaceShot>()
    val pickups = mutableListOf<SpacePickup>()
    var boost = 0f
        private set
    private var chain = 0
    private var chainTime = 0f

    @Transient
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
        pickups.clear()
        boost = 0f
        chain = 0
        chainTime = 0f
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
        boost = (boost - seconds).coerceAtLeast(0f)
        chainTime = (chainTime - seconds).coerceAtLeast(0f)
        if (chainTime == 0f) chain = 0
        val salvage = pickups.iterator()
        while (salvage.hasNext()) {
            val pickup = salvage.next()
            pickup.y += (height - topInset) * 0.24f * seconds
            if (hypot(pickup.x - x, pickup.y - y) < 23f) {
                boost = 6f
                effects.burst(Offset(x, y), 14)
                salvage.remove()
            } else if (pickup.y > height + 14f) salvage.remove()
        }
        distance = (distance + seconds * 35f) % 10000f
        effects.advance(seconds)
        invincible = (invincible - seconds).coerceAtLeast(0f)
        x += (target - x).coerceIn(-480f * seconds, 480f * seconds)
        cooldown -= seconds
        if (cooldown <= 0f) {
            shots += SpaceShot(
                x = x,
                y = y - 17f,
                vx = 0f,
                vy = -440f,
                life = (height + 30f) / 440f
            )
            if (boost > 0f) for (side in listOf(-1f, 1f)) {
                shots += SpaceShot(
                    x = x,
                    y = y - 17f,
                    vx = side * 115f,
                    vy = -420f,
                    life = (height + 30f) / 420f
                )
            }
            cooldown += if (boost > 0f) 0.14f else 0.19f
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
                    x = px, y = -radius - index * 35f,
                    vx = (Random.nextFloat() - 0.5f) * 50f, vy = speed,
                    radius = radius, spin = Random.nextFloat() * 6f, health = if (large) 7 else 2
                )
            }
            wave++
            spawn += maxOf(0.48f, 1.1f - elapsed * 0.006f) + if (wave % 10 == 0) 0.7f else 0f
        }
        invaderTimer -= seconds
        if (invaderTimer <= 0f && invaders.size < 2) {
            val fromLeft = Random.nextBoolean()
            invaders += SpaceInvaderShip(
                x = if (fromLeft) -24f else width + 24f,
                y = topInset + 26f + Random.nextFloat() * (height - topInset) * 0.15f,
                vx = (if (fromLeft) 1f else -1f) * width / 5.5f, cooldown = 0.8f
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
                    x = invader.x, y = invader.y + 10f, vx = dx / distance * speed,
                    vy = dy / distance * speed, life = distance / speed + 1f
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
            shot.x += shot.vx * seconds
            shot.y += shot.vy * seconds
            shot.life -= seconds
            val hit = rocks.indexOfFirst { hypot(it.x - shot.x, it.y - shot.y) < it.radius + 3f }
            val ship = invaders.firstOrNull { hypot(it.x - shot.x, it.y - shot.y) < 20f }
            if (hit >= 0) {
                val rock = rocks[hit]
                rock.health--
                effects.burst(
                    point = Offset(x = shot.x, y = shot.y),
                    count = 3
                )
                if (rock.health <= 0) {
                    rocks.removeAt(hit)
                    reward(if (rock.radius > 30f) 30 else 10)
                    if (rock.radius > 30f && pickups.size < 3) pickups += SpacePickup(
                        rock.x,
                        rock.y
                    )
                    effects.burst(point = Offset(x = rock.x, y = rock.y))
                    if (rock.radius > 15f) {
                        for (sign in -1..1 step 2) {
                            rocks += SpaceRock(
                                x = rock.x + sign * 12f, y = rock.y, vx = sign * 75f,
                                vy = rock.vy * 1.25f, radius = 11f, spin = rock.spin
                            )
                        }
                    }
                }
                iterator.remove()
            } else if (ship != null) {
                ship.health--
                effects.burst(
                    point = Offset(x = shot.x, y = shot.y),
                    count = 4
                )
                if (ship.health <= 0) {
                    invaders.remove(ship)
                    reward(50)
                    if (pickups.size < 3) pickups += SpacePickup(
                        ship.x.coerceIn(14f, width - 14f),
                        ship.y
                    )
                    effects.burst(
                        point = Offset(x = ship.x, y = ship.y),
                        count = 16
                    )
                }
                iterator.remove()
            } else if (shot.life <= 0f || shot.y < -8f) iterator.remove()
        }
    }

    private fun reward(points: Int) {
        chain++
        chainTime = 2.8f
        score += points * (1 + (chain / 4).coerceAtMost(3))
    }

    private fun damage() {
        if (invincible > 0f) return
        lives--
        chain = 0
        chainTime = 0f
        boost = 0f
        invincible = 1.25f
        effects.burst(
            point = Offset(x = x, y = y),
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
        for (pickup in pickups) {
            pickup.x *= width / previous.width
            pickup.y *= height / previous.height
        }
        for (invader in invaders) {
            invader.x *= width / previous.width
            invader.y *= height / previous.height
        }
        for (shot in shots + enemyShots) {
            shot.x *= width / previous.width
            shot.y *= height / previous.height
            shot.vx *= width / previous.width
            shot.vy *= height / previous.height
        }
        effects.clear()
    }
}