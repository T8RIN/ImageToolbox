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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Balance
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object BalanceTowerGame : ArcadeGameDefinition {
    override val title = R.string.arcade_balancetower
    override val description = R.string.arcade_balancetower_sub
    override val icon get() = Icons.Outlined.Balance
    override val order = 34

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BalanceTowerRun
        )

        val indicatorPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onTap = { run.drop() }
            ) {
                val unit = run.unit
                val ground = run.project(180f, 0f)
                val boomY = drawTowerCrane(run, style, ground)
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
                    drawConstructionGround(style, run.width, run.height, ground.y + 13f * unit)
                }
                fun block(
                    load: TowerLoad,
                    ghost: Boolean = false
                ) {
                    val p = run.project(load.x, load.bottom + load.height)
                    val size = Size(width = load.width * unit, height = load.height * unit)
                    val color = when (load.kind) {
                        0 -> style.playerColor
                        1 -> style.obstacleColor
                        else -> style.accentColor
                    }
                    val point = p - Offset(x = size.width / 2f, y = 0f)
                    if (ghost) {
                        drawRoundRect(
                            color = color.copy(alpha = 0.25f),
                            topLeft = point,
                            size = size,
                            cornerRadius = CornerRadius(x = unit * 2f),
                            style = Stroke(width = 1f)
                        )
                    } else {
                        pixelPanel(
                            color = color,
                            ink = style.playerDetailColor,
                            topLeft = point,
                            size = size
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.18f),
                            x = point.x + 3f * unit,
                            y = point.y + 3f * unit,
                            width = size.width - 6f * unit,
                            height = 2f * unit
                        )
                        val frame = style.playerDetailColor.copy(alpha = 0.48f)
                        for (end in listOf(point.x + 3f * unit, point.x + size.width - 5f * unit)) {
                            pixelRect(
                                color = frame,
                                x = end,
                                y = point.y + 3f * unit,
                                width = 2f * unit,
                                height = size.height - 6f * unit
                            )
                        }
                        if (load.kind == 1) {
                            pixelRect(
                                color = frame,
                                x = point.x + 7f * unit,
                                y = point.y + size.height - 5f * unit,
                                width = size.width - 14f * unit,
                                height = 2f * unit
                            )
                        } else {
                            for (section in 1..2) pixelRect(
                                color = frame.copy(alpha = 0.18f),
                                x = point.x + size.width * section / 3f,
                                y = point.y + 5f * unit,
                                width = unit,
                                height = size.height - 10f * unit
                            )
                        }
                        val ballast =
                            run.project(load.x + load.offset, load.bottom + load.height / 2f)
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.7f),
                            x = ballast.x - 7f * unit,
                            y = ballast.y - 4f * unit,
                            width = 14f * unit,
                            height = 8f * unit
                        )
                        pixelCircle(
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
                        rotate(degrees = tilt, pivot = if (run.collapsing) pivot else ground) {
                            block(
                                load
                            )
                        }
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
                    if (run.settle <= 0f) {
                        if (!run.dropping) {
                            val hook = run.project(load.x, load.bottom + load.height)
                            pixelRect(
                                style.detailColor,
                                hook.x - unit,
                                boomY + 4f * unit,
                                unit * 1.4f,
                                (hook.y - boomY - 13f * unit).coerceAtLeast(0f)
                            )
                            pixelPanel(
                                style.obstacleColor,
                                style.playerDetailColor,
                                Offset(hook.x - 6f * unit, boomY - 3f * unit),
                                Size(12f * unit, 7f * unit)
                            )
                            for (side in listOf(-1f, 1f)) {
                                pixelCircle(
                                    style.playerDetailColor, 1.4f * unit,
                                    Offset(hook.x + side * 3.5f * unit, boomY - 3f * unit)
                                )
                                drawLine(
                                    style.detailColor,
                                    Offset(hook.x, hook.y - 9f * unit),
                                    Offset(hook.x + side * 13f * unit, hook.y), 1.4f * unit
                                )
                            }
                            pixelRect(
                                style.accentColor,
                                hook.x - 4f * unit,
                                hook.y - 3f * unit,
                                8f * unit,
                                3f * unit
                            )
                        }
                        block(load)
                    }
                    if (run.loads.isNotEmpty()) {
                        val mark = run.project(run.centerMass, run.stackTop + 7f)
                        val path = indicatorPath.apply {
                            rewind()
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
}

private fun DrawScope.drawConstructionGround(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    y: Float
) {
    pixelRect(style.detailColor.copy(alpha = 0.12f), 0f, y, width, height - y)
    pixelRect(style.detailColor.copy(alpha = 0.4f), 0f, y, width, 1f)
    var x = 0f
    while (x < width) {
        pixelRect(style.detailColor.copy(alpha = 0.21f), x + 2f, y + 3f, 16f, 1f)
        pixelRect(style.detailColor.copy(alpha = 0.18f), x + 21f, y + 1f, 1f, 9f)
        pixelRect(style.detailColor.copy(alpha = 0.2f), x + 9f, y + 7f, 3f, 2f)
        x += 28f
    }
}

private const val TowerBoomHeight = 13f
private const val TowerHoistClearance = 26f

private fun DrawScope.drawTowerCrane(
    run: BalanceTowerRun,
    style: ArcadeStyle,
    ground: Offset
): Float {
    val unit = run.unit
    val boomY = run.topInset + TowerBoomHeight * unit
    val mastX = 23f * unit
    val left = 4f * unit
    val right = run.width - 13f * unit
    val metal = lerp(style.borderColor, style.obstacleColor, 0.3f)
    val shade = lerp(metal, style.overlayColor.copy(alpha = 1f), 0.25f)
    val edge = style.obstacleColor.copy(alpha = 0.65f)
    val bottom = minOf(run.height, ground.y + 12f * unit)
    val skylineColumns = ceil(run.width / 51f).toInt() + 1
    for (index in 0 until skylineColumns) {
        val x = index * 51f - 12f
        val buildingHeight = (35f + index * 23 % 72) * unit
        val top = run.height - buildingHeight
        val ink = style.detailColor.copy(alpha = 0.18f)
        pixelRect(ink.copy(alpha = 0.075f), x, top, 39f, buildingHeight)
        pixelRect(ink, x, top, 39f, 1f)
        pixelRect(ink.copy(alpha = 0.14f), x + 3f, top + 2f, 1f, buildingHeight - 2f)
        pixelRect(ink.copy(alpha = 0.14f), x + 35f, top + 2f, 1f, buildingHeight - 2f)
        var floor = top + 12f
        while (floor < run.height) {
            pixelRect(ink.copy(alpha = 0.12f), x + 2f, floor, 35f, 1f)
            for (window in 0..2) pixelRect(
                ink.copy(alpha = 0.13f),
                x + 7f + window * 10f,
                floor - 6f,
                4f,
                3f
            )
            floor += 13f
        }
        if (index % 3 == 0) {
            drawLine(ink, Offset(x + 7f, top), Offset(x + 7f, top - 12f), 0.8f)
            drawLine(ink, Offset(x + 7f, top - 11f), Offset(x + 33f, top - 7f), 0.8f)
        }
    }
    pixelRect(shade, mastX - 3f * unit, boomY - 22f * unit, 12f * unit, bottom - boomY + 22f * unit)
    pixelRect(metal, mastX - 3f * unit, boomY - 22f * unit, 3f * unit, bottom - boomY + 22f * unit)
    pixelRect(edge, mastX + 6f * unit, boomY - 22f * unit, 2f * unit, bottom - boomY + 22f * unit)
    var y = boomY + 4f * unit
    while (y + 11f * unit < bottom) {
        drawLine(edge, Offset(mastX, y), Offset(mastX + 5f * unit, y + 11f * unit), unit)
        drawLine(edge, Offset(mastX + 5f * unit, y), Offset(mastX, y + 11f * unit), unit)
        y += 14f * unit
    }
    pixelPanel(
        metal,
        style.obstacleColor,
        Offset(left, boomY - 10f * unit),
        Size(right - left, 11f * unit)
    )
    pixelRect(shade, left + 2f * unit, boomY - 7f * unit, right - left - 4f * unit, 5f * unit)
    var x = left + 3f * unit
    while (x < right - 10f * unit) {
        drawLine(edge, Offset(x, boomY - 2f * unit), Offset(x + 5f * unit, boomY - 7f * unit), unit)
        drawLine(
            edge,
            Offset(x + 5f * unit, boomY - 7f * unit),
            Offset(x + 10f * unit, boomY - 2f * unit),
            unit
        )
        x += 10f * unit
    }
    val peak = Offset(mastX + 2f * unit, boomY - 22f * unit)
    drawLine(edge, peak, Offset(right - 20f * unit, boomY - 10f * unit), unit)
    drawLine(edge, peak, Offset(left + 3f * unit, boomY - 10f * unit), unit)
    pixelPanel(
        shade,
        style.obstacleColor,
        Offset(left, boomY - 8f * unit),
        Size(13f * unit, 14f * unit)
    )
    for (panel in 1..2) pixelRect(
        metal,
        left + 2f * unit,
        boomY + (panel * 3f - 7f) * unit,
        9f * unit,
        unit
    )
    val cab = Offset(mastX + 8f * unit, boomY - 15f * unit)
    pixelPanel(style.obstacleColor, style.playerDetailColor, cab, Size(17f * unit, 15f * unit))
    pixelRect(
        style.accentColor.copy(alpha = 0.65f),
        cab.x + 3f * unit,
        cab.y + 3f * unit,
        11f * unit,
        5f * unit
    )
    pixelRect(shade, cab.x + 8f * unit, cab.y + 3f * unit, unit, 5f * unit)
    pixelRect(shade, cab.x + 3f * unit, cab.y + 11f * unit, 11f * unit, 2f * unit)
    return boomY
}

@Serializable
private data class TowerLoad(
    var x: Float,
    var bottom: Float,
    val width: Float,
    val height: Float,
    val offset: Float,
    val kind: Int
) {
    val mass get() = width * height * if (kind == 2) 1.6f else 1f
}

@Serializable
private class BalanceTowerRun : ArcadeRun() {
    val loads = mutableListOf<TowerLoad>()

    @Transient
    val effects = ArcadeEffects()
    var current =
        TowerLoad(x = 180f, bottom = 100f, width = 80f, height = 18f, offset = 0f, kind = 0)
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
    private var chain = 0
    private var velocity = 0f
    private var angularVelocity = 0f
    private var collapseTime = 0f
    private var fallDirection = 1f
    private val kinds: ArcadeDeque<Int> = ArrayDeque()
    val stackTop get() = loads.lastOrNull()?.let { it.bottom + it.height } ?: 0f
    val unit get() = minOf(1.2f, width / 360f, (height - topInset - 16f) / 160f)
    fun project(
        x: Float,
        y: Float
    ) = Offset(x = width / 2f + (x - 180f) * unit, y = height - 24f + (camera - y) * unit)

    override fun reset() {
        loads.clear()
        effects.clear()
        kinds.clear()
        camera = 0f
        wobble = 0f
        tilt = 0f
        time = 0f
        chain = 0
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
        val kind = if (loads.isEmpty()) 0 else kinds.removeFirst().let {
            if (chain > 0 && chain % 4 == 0) 1 else it
        }
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
        val offset = if (loads.size < 2 || chain > 0 && chain % 4 == 0) {
            0f
        } else {
            w * (0.12f + 0.1f * loads.size / (loads.size + 20f)) * if (Random.nextBoolean()) 1f else -1f
        }
        current = TowerLoad(
            x = 180f,
            bottom = stackTop + 100f,
            width = w,
            height = h,
            offset = offset,
            kind = kind
        )
        velocity = 0f
        dropping = false
    }

    fun drop() {
        if (dropping || collapsing || settle > 0f) return
        dropping = true
        velocity = 0f
    }

    fun evaluate() {
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

    private val cameraTarget
        get() = (stackTop - (height - topInset - 72f) / unit * 0.6f).coerceAtLeast(0f)

    private fun liftLoad() {
        val hanging = stackTop + minOf(100f, (height - topInset - 55f) / unit * 0.65f)
        val ceiling = camera + (height - topInset - 24f) / unit -
                TowerBoomHeight - TowerHoistClearance - current.height
        current.bottom = minOf(hanging, maxOf(stackTop + 18f, ceiling))
    }

    override fun onResize(previous: Size) {
        camera = cameraTarget
        if (!dropping && !collapsing && settle <= 0f) liftLoad()
        effects.clear()
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        camera += (cameraTarget - camera) * minOf(1f, seconds * 6f)
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
            liftLoad()
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
                chain = if (stability > 0.85f) chain + 1 else 0
                score += 10 + if (chain > 0) 5 + minOf(chain, 5) * 2 else 0
                angularVelocity = (current.x - centerMass) * if (chain > 0) 0.035f else 0.1f
                settle = 0.3f
                effects.burst(
                    point = project(current.x, current.bottom),
                    count = if (chain >= 3) 14 else 6
                )
            }
        }
    }
}