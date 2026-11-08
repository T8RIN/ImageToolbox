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
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MissileCommandGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
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
                pixelRect(
                    color = style.obstacleColor,
                    x = x - 15f,
                    y = run.ground - 19f,
                    width = 10f,
                    height = 19f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = x - 4f,
                    y = run.ground - 28f,
                    width = 13f,
                    height = 28f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = x + 10f,
                    y = run.ground - 14f,
                    width = 7f,
                    height = 14f
                )
                repeat(3) { row ->
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.75f),
                        x = x - 1f,
                        y = run.ground - 23f + row * 7f,
                        width = 3f,
                        height = 3f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.75f),
                        x = x + 4f,
                        y = run.ground - 23f + row * 7f,
                        width = 3f,
                        height = 3f
                    )
                }
                pixelRect(
                    color = style.playerDetailColor,
                    x = x - 12f,
                    y = run.ground - 14f,
                    width = 3f,
                    height = 4f
                )
            }
            for (missile in run.missiles) {
                val point = Offset(missile.x, missile.y)
                drawLine(
                    color = style.hazardColor.copy(alpha = 0.4f),
                    start = Offset(missile.start, 0f),
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
            for (shot in run.shots) {
                val point = Offset(shot.x, shot.y)
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
                val target = Offset(shot.targetX, shot.targetY)
                drawLine(
                    color = style.detailColor,
                    start = target - Offset(3f, 3f),
                    end = target + Offset(3f, 3f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = style.detailColor,
                    start = target + Offset(3f, -3f),
                    end = target + Offset(-3f, 3f),
                    strokeWidth = 1f
                )
            }
            for (blast in run.blasts) {
                val radius = blast.radius * run.blastSize
                val point = Offset(blast.x, blast.y)
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
                    style = Stroke(1.5f)
                )
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = radius * 0.72f,
                    center = point,
                    style = Stroke(1f)
                )
            }
            drawArc(
                color = style.playerColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = run.launch - Offset(14f, 10f),
                size = Size(28f, 20f)
            )
            pixelRect(
                color = style.playerColor,
                x = run.launch.x - 3f,
                y = run.launch.y - 18f,
                width = 6f,
                height = 12f
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

internal data class IncomingMissile(
    var x: Float,
    var y: Float,
    var start: Float,
    val city: Int,
    var speed: Float
)

internal data class Interceptor(
    var x: Float,
    var y: Float,
    var targetX: Float,
    var targetY: Float
)

internal data class MissileBlast(
    var x: Float,
    var y: Float,
    var age: Float = 0f,
    val chain: Boolean = false
) {
    val radius get() = sin((age / 1.4f).coerceIn(0f, 1f) * PI.toFloat())
}

internal class MissileCommandRun : ArcadeRun() {
    val cities = IntArray(4) { 1 }
    val missiles = mutableListOf<IncomingMissile>()
    val shots = mutableListOf<Interceptor>()
    val blasts = mutableListOf<MissileBlast>()
    val effects = ArcadeEffects()
    val ground get() = height - 22f
    val launch get() = Offset(width / 2f, ground)
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
            launch.x, launch.y, point.x.coerceIn(8f, width - 8f),
            point.y.coerceIn(topInset + 6f, ground - 32f)
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
            missiles += IncomingMissile(x, -8f, x, city, speed)
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
                blasts += MissileBlast(shot.targetX, shot.targetY)
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
                blasts += MissileBlast(missile.x, missile.y, 0.2f, true)
                effects.burst(
                    point = Offset(missile.x, missile.y),
                    count = 4
                )
                iterator.remove()
            } else if (distance <= missile.speed * seconds) {
                cities[missile.city] = 0
                effects.burst(
                    point = Offset(cityX(missile.city), ground - 8f),
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
            spawn = 1.5f
            if (wave % 4 == 0) {
                cities.indices.firstOrNull { cities[it] == 0 }?.let {
                    cities[it] = 1
                    effects.burst(point = Offset(cityX(it), ground - 16f))
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

    override fun save(state: Bundle) {
        state.putIntArray("cities", cities.copyOf())
        state.putFloatArray("wave", floatArrayOf(wave.toFloat(), pending.toFloat(), spawn, charge))
        state.putFloatArray(
            "missiles",
            missiles.flatMap { listOf(it.x, it.y, it.start, it.city.toFloat(), it.speed) }
                .toFloatArray()
        )
        state.putFloatArray(
            "shots",
            shots.flatMap { listOf(it.x, it.y, it.targetX, it.targetY) }.toFloatArray()
        )
        state.putFloatArray(
            "blasts",
            blasts.flatMap { listOf(it.x, it.y, it.age, if (it.chain) 1f else 0f) }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getIntArray("cities")?.copyInto(cities)
        state.getFloatArray("wave")?.let {
            wave = it[0].toInt()
            pending = it[1].toInt()
            spawn = it[2]
            charge = it[3]
        }
        missiles.clear()
        shots.clear()
        blasts.clear()
        state.getFloatArray("missiles")?.let {
            for (i in it.indices step 5) missiles += IncomingMissile(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3].toInt(),
                it[i + 4]
            )
        }
        state.getFloatArray("shots")?.let {
            for (i in it.indices step 4) shots += Interceptor(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3]
            )
        }
        state.getFloatArray("blasts")?.let {
            for (i in it.indices step 4) blasts += MissileBlast(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3] == 1f
            )
        }
    }
}