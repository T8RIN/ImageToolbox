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
    val run = rememberArcadeRun(initialState, ::MissileCommandRun)
    ArcadeBoard(
        ArcadeGame.MissileCommand,
        run,
        style,
        modifier,
        initialState,
        onHighScoreChanged,
        onGameOver
    ) {
        ArcadeField(run, onPress = run::fire) {
            arcadeStars(style, run.width, run.height, 0f)
            arcadeGround(style, run.width, run.ground)
            for (index in run.cities.indices) {
                val x = run.cityX(index)
                if (run.cities[index] == 0) {
                    pixelRect(style.detailColor, x - 13f, run.ground - 4f, 9f, 4f)
                    pixelRect(style.detailColor, x + 1f, run.ground - 7f, 5f, 7f)
                    pixelRect(style.detailColor, x + 8f, run.ground - 3f, 6f, 3f)
                    continue
                }
                pixelRect(style.obstacleColor, x - 15f, run.ground - 19f, 10f, 19f)
                pixelRect(style.obstacleColor, x - 4f, run.ground - 28f, 13f, 28f)
                pixelRect(style.obstacleColor, x + 10f, run.ground - 14f, 7f, 14f)
                repeat(3) { row ->
                    pixelRect(
                        style.playerDetailColor.copy(alpha = 0.75f),
                        x - 1f,
                        run.ground - 23f + row * 7f,
                        3f,
                        3f
                    )
                    pixelRect(
                        style.playerDetailColor.copy(alpha = 0.75f),
                        x + 4f,
                        run.ground - 23f + row * 7f,
                        3f,
                        3f
                    )
                }
                pixelRect(style.playerDetailColor, x - 12f, run.ground - 14f, 3f, 4f)
            }
            for (missile in run.missiles) {
                val point = Offset(missile.x, missile.y)
                drawLine(style.hazardColor.copy(alpha = 0.4f), Offset(missile.start, 0f), point, 1f)
                drawCircle(style.hazardColor.copy(alpha = 0.15f), 6f, point)
                drawCircle(style.hazardColor, 2.5f, point)
            }
            for (shot in run.shots) {
                val point = Offset(shot.x, shot.y)
                drawLine(style.playerColor.copy(alpha = 0.5f), run.launch, point, 1f)
                drawCircle(style.playerColor, 2.5f, point)
                val target = Offset(shot.targetX, shot.targetY)
                drawLine(style.detailColor, target - Offset(3f, 3f), target + Offset(3f, 3f), 1f)
                drawLine(style.detailColor, target + Offset(3f, -3f), target + Offset(-3f, 3f), 1f)
            }
            for (blast in run.blasts) {
                val radius = blast.radius * run.blastSize
                val point = Offset(blast.x, blast.y)
                val color = if (blast.chain) style.accentColor else style.playerColor
                drawCircle(color.copy(alpha = 0.12f), radius, point)
                drawCircle(color, radius, point, style = Stroke(1.5f))
                drawCircle(color.copy(alpha = 0.3f), radius * 0.72f, point, style = Stroke(1f))
            }
            drawArc(
                style.playerColor,
                180f,
                180f,
                true,
                run.launch - Offset(14f, 10f),
                Size(28f, 20f)
            )
            pixelRect(style.playerColor, run.launch.x - 3f, run.launch.y - 18f, 6f, 12f)
            repeat(4) { index ->
                pixelRect(
                    if (run.charge >= index + 1) style.accentColor else style.detailColor,
                    run.launch.x - 14f + index * 8f, run.ground + 6f, 5f, 3f
                )
            }
            run.effects.draw(this, style.accentColor)
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

internal data class Interceptor(var x: Float, var y: Float, var targetX: Float, var targetY: Float)
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
                effects.burst(Offset(missile.x, missile.y), 4)
                iterator.remove()
            } else if (distance <= missile.speed * seconds) {
                cities[missile.city] = 0
                effects.burst(Offset(cityX(missile.city), ground - 8f), 12)
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
                    effects.burst(Offset(cityX(it), ground - 16f))
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