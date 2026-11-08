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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BalanceTowerGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::BalanceTowerRun
    )

    ArcadeBoard(
        game = ArcadeGame.BalanceTower,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onTap = { run.drop() }
        ) {
            repeat(7) { index ->
                val x = (index * 151f + 32f) % run.width
                val y = ((index * 79f + 39f + run.camera * 0.18f) % run.height)
                drawArcadeSprite(
                    sprite = ArcadeCloud,
                    tint = style.detailColor.copy(alpha = 0.45f),
                    x = x,
                    y = y
                )
            }
            val unit = run.unit
            val ground = run.project(180f, 0f)
            if (ground.y < run.height + 10f) {
                pixelRect(
                    color = style.obstacleColor,
                    x = ground.x - 52f * unit,
                    y = ground.y,
                    width = 104f * unit,
                    height = 8f * unit
                )
                pixelRect(
                    color = style.detailColor,
                    x = ground.x - 46f * unit,
                    y = ground.y + 8f * unit,
                    width = 92f * unit,
                    height = 4f * unit
                )
                arcadeGround(style, run.width, ground.y + 13f * unit)
            }
            fun block(
                load: TowerLoad,
                ghost: Boolean = false
            ) {
                val p = run.project(load.x, load.bottom + load.height)
                val size = Size(load.width * unit, load.height * unit)
                val color = when (load.kind) {
                    0 -> style.playerColor
                    1 -> style.obstacleColor
                    else -> style.accentColor
                }
                val point = p - Offset(size.width / 2f, 0f)
                if (ghost) {
                    drawRoundRect(
                        color = color.copy(alpha = 0.25f),
                        topLeft = point,
                        size = size,
                        cornerRadius = CornerRadius(unit * 2f),
                        style = Stroke(1f)
                    )
                } else {
                    drawRoundRect(
                        color = color.copy(alpha = 0.85f),
                        topLeft = point,
                        size = size,
                        cornerRadius = CornerRadius(unit * 2f)
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.18f),
                        x = point.x + 3f * unit,
                        y = point.y + 3f * unit,
                        width = size.width - 6f * unit,
                        height = 2f * unit
                    )
                    val ballast = run.project(load.x + load.offset, load.bottom + load.height / 2f)
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.38f),
                        x = ballast.x - 7f * unit,
                        y = ballast.y - 4f * unit,
                        width = 14f * unit,
                        height = 8f * unit
                    )
                    drawCircle(
                        color = color,
                        radius = 2f * unit,
                        center = ballast
                    )
                    for (side in listOf(-1f, 1f)) pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.3f),
                        x = p.x + side * (size.width / 2f - 7f * unit),
                        y = p.y + size.height - 5f * unit,
                        width = 2f * unit,
                        height = 2f * unit
                    )
                }
            }

            val pivot = run.project(run.pivotX, run.pivotY)
            run.loads.forEachIndexed { index, load ->
                val point = run.project(load.x, load.bottom)
                if (point.y >= -load.height * unit && point.y <= run.height + load.height * unit) {
                    val tilt =
                        if (run.collapsing && index >= run.pivot) run.tilt else if (!run.collapsing) run.wobble else 0f
                    rotate(tilt, if (run.collapsing) pivot else ground) { block(load) }
                }
            }
            if (!run.collapsing) {
                val load = run.current
                if (!run.dropping && run.settle <= 0f) {
                    block(
                        load.copy(bottom = run.stackTop),
                        ghost = true
                    )
                    val from = run.project(load.x + load.offset, load.bottom)
                    val to = run.project(load.x + load.offset, run.stackTop)
                    var y = from.y + 5f
                    while (y < to.y - 4f) {
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.5f),
                            x = from.x,
                            y = y,
                            width = 1f,
                            height = 2f
                        )
                        y += 7f
                    }
                }
                if (run.settle <= 0f) block(load)
                if (run.loads.isNotEmpty()) {
                    val mark = run.project(run.centerMass, run.stackTop + 7f)
                    val path = Path().apply {
                        moveTo(mark.x, mark.y + 3f)
                        lineTo(mark.x - 3f, mark.y - 2f)
                        lineTo(mark.x + 3f, mark.y - 2f)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = style.accentColor
                    )
                }
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

internal data class TowerLoad(
    var x: Float,
    var bottom: Float,
    val width: Float,
    val height: Float,
    val offset: Float,
    val kind: Int
) {
    val mass get() = width * height * if (kind == 2) 1.6f else 1f
}

internal class BalanceTowerRun : ArcadeRun() {
    val loads = mutableListOf<TowerLoad>()
    val effects = ArcadeEffects()
    var current = TowerLoad(180f, 100f, 80f, 18f, 0f, 0)
    var camera = 0f
    var wobble = 0f
    var tilt = 0f
    var dropping = false
    var collapsing = false
    var settle = 0f
    var pivot = 0
    var pivotX = 180f
    var pivotY = 0f
    var centerMass = 180f
    var stability = 1f
        private set
    private var time = 0f
    private var velocity = 0f
    private var angularVelocity = 0f
    private var collapseTime = 0f
    private var fallDirection = 1f
    private val kinds = ArrayDeque<Int>()
    val stackTop get() = loads.lastOrNull()?.let { it.bottom + it.height } ?: 0f
    val unit get() = minOf(1.2f, width / 360f, (height - topInset - 16f) / 160f)
    fun project(
        x: Float,
        y: Float
    ) = Offset(width / 2f + (x - 180f) * unit, height - 24f + (camera - y) * unit)

    override fun reset() {
        loads.clear()
        effects.clear()
        kinds.clear()
        camera = 0f
        wobble = 0f
        tilt = 0f
        time = 0f
        velocity = 0f
        angularVelocity = 0f
        collapseTime = 0f
        settle = 0f
        dropping = false
        collapsing = false
        centerMass = 180f
        stability = 1f
        pivot = 0
        pivotX = 180f
        pivotY = 0f
        nextLoad()
    }

    private fun nextLoad() {
        if (kinds.isEmpty()) kinds.addAll(listOf(0, 1, 2).shuffled())
        val kind = if (loads.isEmpty()) 0 else kinds.removeFirst()
        val w = when (kind) {
            0 -> 76f + Random.nextFloat() * 14f
            1 -> 104f + Random.nextFloat() * 20f
            else -> 49f + Random.nextFloat() * 14f
        }
        val h = when (kind) {
            0 -> 18f
            1 -> 13f
            else -> 26f
        }
        val offset =
            if (loads.size < 2) 0f else w * (0.12f + 0.1f * loads.size / (loads.size + 20f)) * if (Random.nextBoolean()) 1f else -1f
        current = TowerLoad(180f, stackTop + 100f, w, h, offset, kind)
        velocity = 0f
        dropping = false
    }

    fun drop() {
        if (dropping || collapsing || settle > 0f) return
        dropping = true
        velocity = 0f
    }

    internal fun evaluate() {
        var mass = 0f
        var moment = 0f
        stability = 1f
        for (index in loads.indices.reversed()) {
            val load = loads[index]
            mass += load.mass
            moment += (load.x + load.offset) * load.mass
            val below = loads.getOrNull(index - 1)
            val left = maxOf(load.x - load.width / 2f, below?.let { it.x - it.width / 2f } ?: 128f)
            val right = minOf(load.x + load.width / 2f, below?.let { it.x + it.width / 2f } ?: 232f)
            val center = moment / mass
            val margin =
                minOf(center - left, right - center) / ((right - left) / 2f).coerceAtLeast(1f)
            if (margin < stability) {
                stability = margin
                pivot = index
                pivotX = if (center < (left + right) / 2f) left else right
                pivotY = load.bottom
                fallDirection = if (center < (left + right) / 2f) -1f else 1f
            }
        }
        centerMass = if (mass > 0f) moment / mass else 180f
        if (stability < 0f) {
            collapsing = true
            collapseTime = 0f
            tilt = 0f
            effects.burst(
                point = project(pivotX, pivotY),
                count = 12
            )
        }
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        val targetCamera = (stackTop - (height - topInset - 72f) / unit * 0.6f).coerceAtLeast(0f)
        camera += (targetCamera - camera) * minOf(1f, seconds * 6f)
        angularVelocity += (-wobble * 45f - angularVelocity * 9f) * seconds
        wobble += angularVelocity * seconds
        if (collapsing) {
            collapseTime += seconds
            tilt += fallDirection * (12f + collapseTime * 160f) * seconds
            if (collapseTime >= 0.85f) phase = ArcadePhase.Over
            return
        }
        if (settle > 0f) {
            settle -= seconds
            if (settle <= 0f) nextLoad()
            return
        }
        if (!dropping) {
            current.bottom = stackTop + minOf(100f, (height - topInset - 55f) / unit * 0.65f)
            current.x =
                180f + sin(time * (0.7f + loads.size / (loads.size + 35f))) * 83f
            return
        }
        velocity += 360f * seconds
        current.bottom -= velocity * seconds
        if (current.bottom <= stackTop) {
            val below = loads.lastOrNull()
            val left = below?.let { it.x - it.width / 2f } ?: 128f
            val right = below?.let { it.x + it.width / 2f } ?: 232f
            if (current.x + current.width / 2f <= left || current.x - current.width / 2f >= right) {
                if (project(current.x, current.bottom + current.height).y > height + 24f) phase =
                    ArcadePhase.Over
                return
            }
            current.bottom = stackTop
            loads += current.copy()
            evaluate()
            dropping = false
            if (!collapsing) {
                score += 10 + if (stability > 0.85f) 5 else 0
                angularVelocity = (current.x - centerMass) * 0.1f
                settle = 0.3f
                effects.burst(
                    point = project(current.x, current.bottom),
                    count = 6
                )
            }
        }
    }

    override fun save(state: Bundle) {
        fun TowerLoad.values() = listOf(x, bottom, width, height, offset, kind.toFloat())
        state.putFloatArray("loads", loads.flatMap { it.values() }.toFloatArray())
        state.putFloatArray("current", current.values().toFloatArray())
        state.putFloatArray(
            "run",
            floatArrayOf(
                camera,
                wobble,
                tilt,
                settle,
                pivotX,
                pivotY,
                centerMass,
                stability,
                time,
                velocity,
                angularVelocity,
                collapseTime,
                fallDirection
            )
        )
        state.putInt("pivot", pivot)
        state.putBoolean("dropping", dropping)
        state.putBoolean("collapsing", collapsing)
        state.putIntArray("kinds", kinds.toIntArray())
    }

    override fun restore(state: Bundle) {
        fun FloatArray.load(index: Int) = TowerLoad(
            this[index],
            this[index + 1],
            this[index + 2],
            this[index + 3],
            this[index + 4],
            this[index + 5].toInt()
        )
        loads.clear()
        state.getFloatArray("loads")!!
            .let { for (index in it.indices step 6) loads += it.load(index) }
        current = state.getFloatArray("current")!!.load(0)
        state.getFloatArray("run")!!.let {
            camera = it[0]
            wobble = it[1]
            tilt = it[2]
            settle = it[3]
            pivotX = it[4]
            pivotY = it[5]
            centerMass = it[6]
            stability = it[7]
            time = it[8]
            velocity = it[9]
            angularVelocity = it[10]
            collapseTime = it[11]
            fallDirection = it[12]
        }
        pivot = state.getInt("pivot")
        dropping = state.getBoolean("dropping")
        collapsing = state.getBoolean("collapsing")
        kinds.clear()
        state.getIntArray("kinds")?.forEach(kinds::addLast)
        stopInput()
    }
}