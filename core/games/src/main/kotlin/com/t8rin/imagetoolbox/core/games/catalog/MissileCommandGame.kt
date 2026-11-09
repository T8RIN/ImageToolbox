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
import androidx.compose.ui.graphics.drawscope.Stroke
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.arcadeGround
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeStars
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

private val MissileCity = ArcadeSprite(
    width = 34,
    height = 30,
    data = "M0 14H3V10H11V30H0Z M12 8H15V3H18V0H20V3H24V8H26V30H12Z M27 18H30V15H34V30H27Z"
)

private val MissileFactory = ArcadeSprite(
    width = 34,
    height = 30,
    data = "M0 18H4V15H8V12H12V18H16V15H20V12H24V18H27V5H31V18H34V30H0Z"
)

private val MissileBunker = ArcadeSprite(
    width = 28,
    height = 22,
    data = "M11 0H17V12H21V14H25V17H28V22H0V17H3V14H7V12H11Z M5 18H9V20H5Z M19 18H23V20H19Z"
)

@Composable
fun MissileCommandGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::MissileCommandRun
    )
    ArcadeBoard(
        game = ArcadeGame.MissileCommand,
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
            onPress = run::fire
        ) {
            arcadeStars(
                style = style,
                width = run.width,
                height = run.height,
                drift = 0f
            )
            arcadeGround(
                style = style,
                width = run.width,
                y = run.ground
            )
            for (index in run.cities.indices) {
                val x = run.cityX(index)
                if (run.cities[index] == 0) {
                    pixelRect(
                        color = style.detailColor,
                        x = x - 13f,
                        y = run.ground - 4f,
                        width = 9f,
                        height = 4f
                    )
                    pixelRect(
                        color = style.detailColor,
                        x = x + 1f,
                        y = run.ground - 7f,
                        width = 5f,
                        height = 7f
                    )
                    pixelRect(
                        color = style.detailColor,
                        x = x + 8f,
                        y = run.ground - 3f,
                        width = 6f,
                        height = 3f
                    )
                    continue
                }
                drawArcadeSprite(
                    sprite = if (index % 2 == 0) MissileCity else MissileFactory,
                    tint = style.obstacleColor,
                    x = x - 17f,
                    y = run.ground - 30f
                )
                if (index % 2 == 0) {
                    repeat(3) { row ->
                        for (column in 0..1) pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.75f),
                            x = x - 2f + column * 5f,
                            y = run.ground - 19f + row * 6f,
                            width = 2f,
                            height = 3f
                        )
                    }
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.5f),
                        x = x - 13f,
                        y = run.ground - 15f,
                        width = 4f,
                        height = 2f
                    )
                } else {
                    repeat(4) { window ->
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.7f),
                            x = x - 13f + window * 7f,
                            y = run.ground - 9f,
                            width = 4f,
                            height = 4f
                        )
                    }
                }
            }
            for ((x, y, start) in run.missiles) {
                val point = Offset(x = x, y = y)
                drawLine(
                    color = style.hazardColor.copy(alpha = 0.4f),
                    start = Offset(x = start, y = 0f),
                    end = point,
                    strokeWidth = 1f
                )
                drawCircle(
                    color = style.hazardColor.copy(alpha = 0.15f),
                    radius = 6f,
                    center = point
                )
                drawCircle(
                    color = style.hazardColor,
                    radius = 2.5f,
                    center = point
                )
            }
            for ((x, y, targetX, targetY) in run.shots) {
                val point = Offset(x = x, y = y)
                drawLine(
                    color = style.playerColor.copy(alpha = 0.5f),
                    start = run.launch,
                    end = point,
                    strokeWidth = 1f
                )
                drawCircle(
                    color = style.playerColor,
                    radius = 2.5f,
                    center = point
                )
                val target = Offset(x = targetX, y = targetY)
                drawLine(
                    color = style.detailColor,
                    start = target - Offset(x = 3f, y = 3f),
                    end = target + Offset(x = 3f, y = 3f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = style.detailColor,
                    start = target + Offset(x = 3f, y = -3f),
                    end = target + Offset(x = -3f, y = 3f),
                    strokeWidth = 1f
                )
            }
            for (blast in run.blasts) {
                val radius = blast.radius * run.blastSize
                val point = Offset(x = blast.x, y = blast.y)
                val color = if (blast.chain) style.accentColor else style.playerColor
                drawCircle(
                    color = color.copy(alpha = 0.12f),
                    radius = radius,
                    center = point
                )
                drawCircle(
                    color = color,
                    radius = radius,
                    center = point,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = radius * 0.72f,
                    center = point,
                    style = Stroke(width = 1f)
                )
            }
            drawArcadeSprite(
                sprite = MissileBunker,
                tint = style.playerColor,
                x = run.launch.x - 14f,
                y = run.launch.y - 22f
            )
            pixelRect(
                color = style.playerDetailColor.copy(alpha = 0.5f),
                x = run.launch.x - 1f,
                y = run.launch.y - 20f,
                width = 2f,
                height = 9f
            )
            repeat(4) { index ->
                pixelRect(
                    color = if (run.charge >= index + 1) style.accentColor else style.detailColor,
                    x = run.launch.x - 14f + index * 8f,
                    y = run.ground + 6f,
                    width = 5f,
                    height = 3f
                )
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

@Serializable
internal data class IncomingMissile(
    var x: Float,
    var y: Float,
    var start: Float,
    val city: Int,
    var speed: Float
)

@Serializable
internal data class Interceptor(
    var x: Float,
    var y: Float,
    var targetX: Float,
    var targetY: Float
)

@Serializable
internal data class MissileBlast(
    var x: Float,
    var y: Float,
    var age: Float = 0f,
    val chain: Boolean = false
) {
    val radius get() = sin((age / 1.4f).coerceIn(0f, 1f) * PI.toFloat())
}

@Serializable
internal class MissileCommandRun : ArcadeRun() {
    val cities = IntArray(4) { 1 }
    val missiles = mutableListOf<IncomingMissile>()
    val shots = mutableListOf<Interceptor>()
    val blasts = mutableListOf<MissileBlast>()

    @Transient
    val effects = ArcadeEffects()
    val ground get() = height - 22f
    val launch get() = Offset(x = width / 2f, y = ground)
    val blastSize get() = minOf(width * 0.12f, (height - topInset) * 0.23f)
    var charge = 4f
    private var wave = 1
    private var pending = 5
    private var spawn = 1f

    fun cityX(index: Int) = width * when (index) {
        0 -> 0.12f
        1 -> 0.3f
        2 -> 0.7f
        else -> 0.88f
    }

    override fun reset() {
        cities.fill(1)
        missiles.clear()
        shots.clear()
        blasts.clear()
        effects.clear()
        wave = 1
        pending = 5
        spawn = 1f
        charge = 4f
    }

    fun fire(point: Offset) {
        if (charge < 1f || point.y < topInset) return
        shots += Interceptor(
            x = launch.x, y = launch.y, targetX = point.x.coerceIn(8f, width - 8f),
            targetY = point.y.coerceIn(topInset + 6f, ground - 32f)
        )
        charge -= 1f
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        charge = (charge + seconds * 1.8f).coerceAtMost(4f)
        spawn -= seconds
        if (pending > 0 && spawn <= 0f) {
            val city = cities.indices.filter { cities[it] > 0 }.randomOrNull() ?: return
            val x = Random.nextFloat() * (width - 30f) + 15f
            val speed = (ground + 8f) / maxOf(2.7f, 5.5f - wave * 0.15f)
            missiles += IncomingMissile(x = x, y = -8f, start = x, city = city, speed = speed)
            pending--
            spawn = maxOf(0.4f, 0.95f - wave * 0.025f)
        }
        val interceptors = shots.iterator()
        while (interceptors.hasNext()) {
            val shot = interceptors.next()
            val dx = shot.targetX - shot.x
            val dy = shot.targetY - shot.y
            val distance = hypot(dx, dy)
            val step = maxOf(320f, height * 0.9f) * seconds
            if (distance <= step) {
                blasts += MissileBlast(x = shot.targetX, y = shot.targetY)
                interceptors.remove()
            } else {
                shot.x += dx / distance * step
                shot.y += dy / distance * step
            }
        }
        for (blast in blasts) blast.age += seconds
        blasts.removeAll { it.age >= 1.4f }
        val iterator = missiles.iterator()
        while (iterator.hasNext()) {
            val missile = iterator.next()
            val dx = cityX(missile.city) - missile.x
            val dy = ground - 8f - missile.y
            val distance = hypot(dx, dy)
            if (blasts.any {
                    hypot(
                        it.x - missile.x,
                        it.y - missile.y
                    ) < it.radius * blastSize + 2f
                }) {
                score += 10
                blasts += MissileBlast(x = missile.x, y = missile.y, age = 0.2f, chain = true)
                effects.burst(
                    point = Offset(x = missile.x, y = missile.y),
                    count = 4
                )
                iterator.remove()
            } else if (distance <= missile.speed * seconds) {
                cities[missile.city] = 0
                effects.burst(
                    point = Offset(x = cityX(missile.city), y = ground - 8f),
                    count = 12
                )
                iterator.remove()
            } else {
                missile.x += dx / distance * missile.speed * seconds
                missile.y += dy / distance * missile.speed * seconds
            }
        }
        if (cities.all { it == 0 }) {
            phase = ArcadePhase.Over
            return
        }
        if (pending == 0 && missiles.isEmpty()) {
            score += cities.sum() * 5
            wave++
            pending = minOf(22, 4 + wave)
            spawn = 1.75f
            if (wave % 4 == 0) {
                cities.indices.firstOrNull { cities[it] == 0 }?.let {
                    cities[it] = 1
                    effects.burst(point = Offset(x = cityX(it), y = ground - 16f))
                }
            }
        }
    }

    override fun onResize(previous: Size) {
        val sx = width / previous.width
        val sy = height / previous.height
        for (missile in missiles) {
            missile.x *= sx
            missile.y *= sy
            missile.start *= sx
            missile.speed *= sy
        }
        for (shot in shots) {
            shot.x *= sx
            shot.y *= sy
            shot.targetX *= sx
            shot.targetY *= sy
        }
        for (blast in blasts) {
            blast.x *= sx
            blast.y *= sy
        }
        effects.clear()
    }
}