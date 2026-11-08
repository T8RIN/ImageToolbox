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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BomberGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState = initialState, create = ::BomberRun)
    ArcadeBoard(
        game = ArcadeGame.Bomber,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::begin,
            onDrag = run::drag,
            onSwipe = run::steer,
            swipeOnDrag = true,
            onTap = { run.placeBomb() },
            onRelease = run::release
        ) {
            val cell = run.cell
            val origin = run.origin
            val bounds = Rect(origin, Size(run.columns * cell, run.rows * cell))
            val outline = Path().apply { addRoundRect(RoundRect(bounds, CornerRadius(10f))) }
            clipPath(outline) {
                run.board.forEachIndexed { index, tile ->
                    val p = run.center(index) - Offset(cell / 2f, cell / 2f)
                    when (tile) {
                        1 -> {
                            pixelRect(
                                color = style.obstacleColor.copy(alpha = 0.3f),
                                x = p.x + 1f,
                                y = p.y + 1f,
                                width = cell - 2f,
                                height = cell - 2f
                            )
                            pixelRect(
                                color = style.obstacleColor.copy(alpha = 0.6f),
                                x = p.x + 2f,
                                y = p.y + 2f,
                                width = cell - 4f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.22f),
                                x = p.x + 2f,
                                y = p.y + cell - 4f,
                                width = cell - 4f,
                                height = 2f
                            )
                        }

                        2 -> {
                            pixelRect(
                                color = style.accentColor.copy(alpha = 0.65f),
                                x = p.x + 2f,
                                y = p.y + 2f,
                                width = cell - 4f,
                                height = cell - 4f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.45f),
                                x = p.x + 4f,
                                y = p.y + 4f,
                                width = cell - 8f,
                                height = cell - 8f
                            )
                            repeat(5) { brace ->
                                val step = (cell - 8f) / 5f
                                pixelRect(
                                    color = style.accentColor.copy(alpha = 0.8f),
                                    x = p.x + 4f + brace * step,
                                    y = p.y + 4f + brace * step,
                                    width = step + 0.5f,
                                    height = step + 0.5f
                                )
                                pixelRect(
                                    color = style.accentColor.copy(alpha = 0.55f),
                                    x = p.x + cell - 4f - (brace + 1) * step,
                                    y = p.y + 4f + brace * step,
                                    width = step + 0.5f,
                                    height = step + 0.5f
                                )
                            }
                        }

                        else -> if ((index % run.columns + index / run.columns) % 2 == 0)
                            pixelRect(
                                color = style.detailColor.copy(alpha = 0.08f),
                                x = p.x,
                                y = p.y,
                                width = cell,
                                height = cell
                            )
                    }
                }
                for ((index, kind) in run.items) {
                    val p = run.center(index)
                    drawCircle(
                        color = style.accentColor.copy(alpha = 0.18f),
                        radius = cell * 0.32f,
                        center = p
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = p.x - cell * 0.2f,
                        y = p.y - 1.5f,
                        width = cell * 0.4f,
                        height = 3f
                    )
                    if (kind == 0) {
                        pixelRect(
                            color = style.accentColor,
                            x = p.x - 1.5f,
                            y = p.y - cell * 0.2f,
                            width = 3f,
                            height = cell * 0.4f
                        )
                    } else {
                        drawCircle(
                            color = style.accentColor,
                            radius = cell * 0.18f,
                            center = p,
                            style = Stroke(2f)
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = p.x + cell * 0.1f,
                            y = p.y - cell * 0.28f,
                            width = 3f,
                            height = 4f
                        )
                    }
                }
                for (bomb in run.bombs) {
                    val p = run.center(bomb.position)
                    val pulse = if (bomb.fuse < 0.8f) 1f + sin(bomb.fuse * 32f) * 0.08f else 1f
                    drawCircle(
                        color = style.obstacleColor,
                        radius = cell * 0.31f * pulse,
                        center = p
                    )
                    drawCircle(
                        color = style.playerDetailColor,
                        radius = cell * 0.07f * pulse,
                        center = p - Offset(cell * 0.08f, cell * 0.1f)
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = p.x + cell * 0.12f,
                        y = p.y - cell * 0.4f,
                        width = 3f,
                        height = cell * 0.16f
                    )
                    drawCircle(
                        color = style.hazardColor,
                        radius = cell * 0.07f,
                        center = p + Offset(cell * 0.18f, -cell * 0.42f)
                    )
                    drawArc(
                        color = style.accentColor,
                        startAngle = -90f,
                        sweepAngle = bomb.fuse / 2.2f * 360f,
                        useCenter = false,
                        topLeft = p - Offset(cell * 0.38f, cell * 0.38f),
                        size = Size(cell * 0.76f, cell * 0.76f),
                        style = Stroke(1f)
                    )
                }
                for ((index, life) in run.blasts) {
                    val p = run.center(index)
                    val r = cell * (0.3f + life * 0.5f)
                    pixelRect(
                        color = style.accentColor.copy(alpha = 0.35f),
                        x = p.x - cell / 2f,
                        y = p.y - cell / 2f,
                        width = cell,
                        height = cell
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = p.x - r,
                        y = p.y - r * 0.45f,
                        width = r * 2f,
                        height = r * 0.9f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = p.x - r * 0.45f,
                        y = p.y - r,
                        width = r * 0.9f,
                        height = r * 2f
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = p.x - 2f,
                        y = p.y - 2f,
                        width = 4f,
                        height = 4f
                    )
                }
                for (enemy in run.enemies) {
                    val p = run.enemyPoint(enemy)
                    val leg = if ((run.time * 8f).toInt() % 2 == 0) 1f else -1f
                    pixelRect(
                        color = style.hazardColor,
                        x = p.x - cell * 0.28f,
                        y = p.y - cell * 0.22f,
                        width = cell * 0.56f,
                        height = cell * 0.44f
                    )
                    for (side in listOf(-1f, 1f)) {
                        for (foot in listOf(-1f, 1f)) pixelRect(
                            color = style.hazardColor,
                            x = p.x + side * cell * 0.31f - cell * 0.05f,
                            y = p.y + foot * cell * 0.18f + leg * side * cell * 0.04f,
                            width = cell * 0.1f,
                            height = cell * 0.16f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = p.x + side * cell * 0.12f - 1.5f,
                            y = p.y - 3f,
                            width = 3f,
                            height = 4f
                        )
                    }
                }
                if (run.invulnerable <= 0f || (run.invulnerable * 9f).toInt() % 2 == 0) {
                    val p = run.playerPoint
                    val u = cell / 14f
                    pixelRect(
                        color = style.playerColor,
                        x = p.x - 4f * u,
                        y = p.y - 5f * u,
                        width = 8f * u,
                        height = 7f * u
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = p.x - 3f * u,
                        y = p.y - 3f * u,
                        width = 6f * u,
                        height = 3f * u
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = p.x - 2f * u,
                        y = p.y - 2.5f * u,
                        width = u,
                        height = 2f * u
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = p.x + u,
                        y = p.y - 2.5f * u,
                        width = u,
                        height = 2f * u
                    )
                    pixelRect(
                        color = style.playerColor,
                        x = p.x - 3f * u,
                        y = p.y + 2f * u,
                        width = 6f * u,
                        height = 3f * u
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = p.x - 4f * u,
                        y = p.y + 5f * u,
                        width = 3f * u,
                        height = 2f * u
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = p.x + u,
                        y = p.y + 5f * u,
                        width = 3f * u,
                        height = 2f * u
                    )
                }
            }
            drawRoundRect(
                color = style.detailColor,
                topLeft = bounds.topLeft,
                size = bounds.size,
                cornerRadius = CornerRadius(10f),
                style = Stroke(1f)
            )
            arcadeLives(
                style = style,
                remaining = run.lives,
                y = run.topInset / 2f
            )
            repeat(run.capacity) { index ->
                val p = Offset(83f + index * 13f, run.topInset / 2f)
                val color =
                    style.accentColor.copy(alpha = if (index < run.capacity - run.bombs.size) 1f else 0.2f)
                drawCircle(
                    color = color,
                    radius = 4f,
                    center = p
                )
                pixelRect(
                    color = color,
                    x = p.x + 1f,
                    y = p.y - 7f,
                    width = 2f,
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

internal data class BomberBomb(
    val position: Int,
    var fuse: Float,
    val range: Int
)

internal data class BomberEnemy(
    var position: Int,
    var previous: Int,
    var progress: Float = 1f
)

internal class BomberRun : ArcadeRun() {
    val columns = 11
    var rows = 9
    var board = IntArray(columns * rows)
    val bombs = mutableListOf<BomberBomb>()
    val blasts = mutableMapOf<Int, Float>()
    val items = mutableMapOf<Int, Int>()
    val enemies = mutableListOf<BomberEnemy>()
    val effects = ArcadeEffects()
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 32f) / rows)
    val origin
        get() = Offset(
            (width - columns * cell) / 2f,
            topInset + (height - topInset - rows * cell) / 2f
        )
    var player = columns + 1
    var previous = player
    var progress = 1f
    var lives = 3
    var round = 1
    var range = 2
    var capacity = 1
    var invulnerable = 0f
    var time = 0f
    var transition = 0f
    private var broken = 0
    private var direction = -1
    private var pending = false
    private var moving = false
    private var touch = Offset.Zero
    val playerPoint get() = center(previous) + (center(player) - center(previous)) * progress
    fun enemyPoint(enemy: BomberEnemy) =
        center(enemy.previous) + (center(enemy.position) - center(enemy.previous)) * enemy.progress

    fun center(index: Int) =
        origin + Offset((index % columns + 0.5f) * cell, (index / columns + 0.5f) * cell)

    override fun reset() {
        round = 1
        lives = 3
        range = 2
        capacity = 1
        time = 0f
        broken = 0
        effects.clear()
        newRound()
    }

    private fun newRound() {
        rows = (((height - topInset - 32f) / ((width - 48f) / columns)).toInt()
            .coerceIn(7, 15) / 2) * 2 + 1
        val starts = listOf(columns * 2 - 2, (rows - 2) * columns + 1, (rows - 1) * columns - 2)
        board = IntArray(columns * rows) {
            val x = it % columns
            val y = it / columns
            when {
                x == 0 || y == 0 || x == columns - 1 || y == rows - 1 || x % 2 == 0 && y % 2 == 0 -> 1
                abs(x - 1) + abs(y - 1) <= 2 -> 0
                starts.any { start -> abs(start % columns - x) + abs(start / columns - y) <= 1 } -> 0
                Random.nextFloat() < (0.38f + round * 0.012f).coerceAtMost(0.55f) -> 2
                else -> 0
            }
        }
        bombs.clear()
        blasts.clear()
        items.clear()
        enemies.clear()
        starts.shuffled().take((1 + round / 2).coerceAtMost(3))
            .forEach { enemies += BomberEnemy(it, it) }
        player = columns + 1
        previous = player
        progress = 1f
        invulnerable = 1.5f
        transition = 0f
        stopInput()
    }

    fun next(
        index: Int,
        direction: Int
    ): Int {
        val x = index % columns + if (direction == 0) 1 else if (direction == 2) -1 else 0
        val y = index / columns + if (direction == 1) 1 else if (direction == 3) -1 else 0
        return if (x in 0 until columns && y in 0 until rows) y * columns + x else -1
    }

    private fun open(index: Int) =
        index in board.indices && board[index] == 0 && bombs.none { it.position == index }

    fun begin(point: Offset) {
        touch = point
    }

    fun drag(point: Offset) {
        steer(point - touch); moving = true
    }

    fun release() {
        moving = false
    }

    fun steer(delta: Offset) {
        if (transition > 0f) return
        direction =
            if (abs(delta.x) > abs(delta.y)) if (delta.x > 0f) 0 else 2 else if (delta.y > 0f) 1 else 3
        pending = true
    }

    override fun stopInput() {
        moving = false; pending = false; direction = -1
    }

    fun placeBomb() {
        if (transition > 0f || bombs.size >= capacity) return
        val index = if (progress < 0.5f) previous else player
        if (bombs.none { it.position == index }) bombs += BomberBomb(index, 2.2f, range)
    }

    fun blastCells(
        position: Int,
        range: Int
    ): List<Int> {
        val result = mutableListOf(position)
        repeat(4) { direction ->
            var cell = position
            for (step in 1..range) {
                cell = next(cell, direction)
                if (cell < 0 || board[cell] == 1) break
                result += cell
                if (board[cell] == 2) break
            }
        }
        return result
    }

    internal fun explode(bomb: BomberBomb) {
        val queue = ArrayDeque<BomberBomb>()
        queue += bomb
        while (queue.isNotEmpty()) {
            val nextBomb = queue.removeFirst()
            if (!bombs.remove(nextBomb)) continue
            val cells = blastCells(nextBomb.position, nextBomb.range)
            for (index in cells) {
                blasts[index] = 0.38f
                items.remove(index)
                if (board[index] == 2) {
                    board[index] = 0
                    broken++
                    score += 10
                    if (broken % 4 == 0) items[index] = if (broken % 8 == 0) 1 else 0
                }
                bombs.firstOrNull { it.position == index }?.let(queue::addLast)
            }
            effects.burst(
                point = center(nextBomb.position),
                count = 8
            )
        }
    }

    private fun hurt() {
        lives--
        effects.burst(
            point = playerPoint,
            count = 18
        )
        if (lives == 0) phase = ArcadePhase.Over
        else {
            player = columns + 1
            previous = player
            progress = 1f
            invulnerable = 2f
            stopInput()
        }
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++; newRound()
            }
            return
        }
        invulnerable = (invulnerable - seconds).coerceAtLeast(0f)
        val iterator = blasts.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.setValue(entry.value - seconds)
            if (entry.value <= 0f) iterator.remove()
        }
        bombs.forEach { it.fuse -= seconds }
        bombs.filter { it.fuse <= 0f }.forEach(::explode)
        progress = (progress + seconds / 0.14f).coerceAtMost(1f)
        if (progress >= 1f && direction >= 0 && (pending || moving)) {
            val destination = next(player, direction)
            pending = false
            if (open(destination)) {
                previous = player; player = destination; progress = 0f
            }
        }
        val index = if (progress < 0.5f) previous else player
        if (index !in blasts) items.remove(index)?.let {
            if (it == 0) {
                if (range < 4) range++ else score += 25
            } else if (capacity < 3) capacity++ else score += 25
            effects.burst(
                point = playerPoint,
                count = 10
            )
        }
        val danger =
            bombs.filter { it.fuse < 1f }.flatMap { blastCells(it.position, it.range) }.toSet()
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()
            enemy.progress =
                (enemy.progress + seconds / (0.38f - round.coerceAtMost(12) * 0.012f)).coerceAtMost(
                    1f
                )
            val enemyIndex = if (enemy.progress < 0.5f) enemy.previous else enemy.position
            if (enemyIndex in blasts) {
                score += 50
                effects.burst(
                    point = enemyPoint(enemy),
                    count = 12
                )
                enemyIterator.remove()
                continue
            }
            if (enemy.progress >= 1f && time > 1.5f) {
                val options =
                    (0..3).map { next(enemy.position, it) }.filter { open(it) && it !in blasts }
                val safe = options.filter { it !in danger }.ifEmpty { options }
                val onward = safe.filter { it != enemy.previous }.ifEmpty { safe }
                val destination = if (Random.nextFloat() < 0.55f) onward.minByOrNull {
                    abs(it % columns - player % columns) + abs(it / columns - player / columns)
                } else onward.randomOrNull()
                enemy.previous = enemy.position
                enemy.position = destination ?: enemy.position
                enemy.progress = 0f
            }
        }
        if (invulnerable <= 0f && (index in blasts || enemies.any { (enemyPoint(it) - playerPoint).getDistance() < cell * 0.55f })) hurt()
        if (phase == ArcadePhase.Running && enemies.isEmpty()) {
            score += round * 100
            transition = 1f
            stopInput()
        }
    }

    override fun onResize(previous: Size) {
        if (time == 0f) newRound()
    }

    override fun save(state: Bundle) {
        state.putIntArray("board", board.copyOf())
        state.putIntArray(
            "run",
            intArrayOf(rows, player, previous, lives, round, range, capacity, broken)
        )
        state.putFloatArray("time", floatArrayOf(progress, invulnerable, time, transition))
        state.putFloatArray(
            "bombs",
            bombs.flatMap { listOf(it.position.toFloat(), it.fuse, it.range.toFloat()) }
                .toFloatArray()
        )
        state.putFloatArray(
            "blasts",
            blasts.flatMap { listOf(it.key.toFloat(), it.value) }.toFloatArray()
        )
        state.putIntArray("items", items.flatMap { listOf(it.key, it.value) }.toIntArray())
        state.putFloatArray(
            "enemies",
            enemies.flatMap { listOf(it.position.toFloat(), it.previous.toFloat(), it.progress) }
                .toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getIntArray("board")?.let { board = it.copyOf() }
        state.getIntArray("run")?.let {
            rows = it[0]; player = it[1]; previous = it[2]; lives = it[3]; round = it[4]; range =
            it[5]; capacity = it[6]; broken = it[7]
        }
        state.getFloatArray("time")
            ?.let { progress = it[0]; invulnerable = it[1]; time = it[2]; transition = it[3] }
        bombs.clear()
        state.getFloatArray("bombs")?.let {
            for (i in it.indices step 3) bombs += BomberBomb(
                it[i].toInt(),
                it[i + 1],
                it[i + 2].toInt()
            )
        }
        blasts.clear()
        state.getFloatArray("blasts")
            ?.let { for (i in it.indices step 2) blasts[it[i].toInt()] = it[i + 1] }
        items.clear()
        state.getIntArray("items")?.let { for (i in it.indices step 2) items[it[i]] = it[i + 1] }
        enemies.clear()
        state.getFloatArray("enemies")?.let {
            for (i in it.indices step 3) enemies += BomberEnemy(
                it[i].toInt(),
                it[i + 1].toInt(),
                it[i + 2]
            )
        }
        stopInput()
    }
}