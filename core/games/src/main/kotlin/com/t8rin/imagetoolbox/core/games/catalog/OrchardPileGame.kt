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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

private const val OrchardWidth = 224f
private const val OrchardFloor = 249f
private const val OrchardDrop = 20f
private const val OrchardLimit = 52f
private const val OrchardCooldown = 0.43f
private val OrchardDashes = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))

private val OrchardWatermelonStripe = Path().apply {
    moveTo(-1.15f, -0.13f)
    cubicTo(-0.71f, -0.42f, -0.47f, 0.05f, 0.03f, -0.08f)
    cubicTo(0.43f, -0.2f, 0.71f, -0.03f, 1.15f, -0.18f)
    lineTo(1.15f, 0.09f)
    cubicTo(0.70f, 0.32f, 0.41f, 0.05f, 0.05f, 0.18f)
    cubicTo(-0.38f, 0.33f, -0.71f, -0.13f, -1.15f, 0.16f)
    close()
}

private val OrchardPout = Path().apply {
    moveTo(-0.15f, 0.23f)
    quadraticTo(-0.09f, 0.34f, 0f, 0.24f)
    quadraticTo(0.09f, 0.34f, 0.15f, 0.23f)
}

private enum class OrchardKind(
    val radius: Float,
    val color: Color,
    silhouette: String = "M0,-1 A1,1 0 1,1 0,1 A1,1 0 1,1 0,-1 Z"
) {
    Cherry(9f, Color(0xFFCF3268)),
    Strawberry(
        12f, Color(0xFFFF5D65),
        "M0,-0.71 C-0.58,-1 -1,-0.69 -0.94,-0.16 C-0.89,0.23 -0.32,1 0,1 C0.32,1 0.89,0.23 " +
                "0.94,-0.16 C1,-0.69 0.58,-1 0,-0.71 Z"
    ),
    Grape(
        15f, Color(0xFF8C61CB),
        "M0,-1 C0.32,-1 0.48,-0.82 0.5,-0.63 C0.92,-0.72 1.09,-0.36 0.94,-0.08 C1.08,0.21 " +
                "0.8,0.58 0.55,0.55 C0.5,0.81 0.29,1 0,1 C-0.29,1 -0.5,0.81 -0.55,0.55 C-0.8,0.58 " +
                "-1.08,0.21 -0.94,-0.08 C-1.09,-0.36 -0.92,-0.72 -0.5,-0.63 C-0.48,-0.82 -0.32,-1 0,-1 Z"
    ),
    Dekopon(
        19f, Color(0xFFFFC756),
        "M-0.32,-0.7 Q-0.26,-1 0,-1 Q0.3,-1 0.33,-0.69 C1.22,-0.36 1.22,0.97 0,1 C-1.15,0.98 " +
                "-1.24,-0.34 -0.32,-0.7 Z"
    ),
    Orange(23f, Color(0xFFFB8A38)),
    Apple(
        27f, Color(0xFFE04D3B),
        "M0,-0.72 C0.53,-1.03 1.04,-0.74 0.99,-0.07 C0.94,0.67 0.48,1.05 0.1,0.86 Q0,0.82 " +
                "-0.1,0.86 C-0.48,1.05 -0.94,0.67 -0.99,-0.07 C-1.04,-0.74 -0.53,-1.03 0,-0.72 Z"
    ),
    Pear(
        33f, Color(0xFFF2CB67),
        "M0,-1 C0.45,-1 0.43,-0.47 0.68,-0.15 C1.21,0.46 0.85,0.97 0,1 C-0.85,0.97 -1.21,0.46 " +
                "-0.68,-0.15 C-0.43,-0.47 -0.45,-1 0,-1 Z"
    ),
    Peach(
        38f, Color(0xFFFFAFA0),
        "M0,-0.78 C0.62,-1.02 1.07,-0.54 0.99,0.06 C0.94,0.53 0.4,0.94 0,1 C-0.4,0.94 -0.94,0.53 " +
                "-0.99,0.06 C-1.07,-0.54 -0.62,-1.02 0,-0.78 Z"
    ),
    Pineapple(
        44f, Color(0xFFF2B536),
        "M0,-0.86 C0.54,-0.86 0.82,-0.45 0.82,0.08 C0.82,0.65 0.54,1 0,1 C-0.54,1 -0.82,0.65 " +
                "-0.82,0.08 C-0.82,-0.45 -0.54,-0.86 0,-0.86 Z"
    ),
    Melon(51f, Color(0xFFB9D78B)),
    Watermelon(59f, Color(0xFF76BF55));

    val shape = PathParser().parsePathString(silhouette).toPath()
}

@Composable
fun OrchardPileGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(initialState, ::OrchardPileRun)
    ArcadeBoard(
        game = ArcadeGame.OrchardPile,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver,
        isPaused = isPaused
    ) {
        ArcadeField(
            run = run,
            onPress = run::aim,
            onDrag = run::aim,
            onRelease = run::drop
        ) {
            translate(run.left, run.top) {
                scale(run.unit, Offset.Zero) {
                    val floor = run.floor
                    val walls = style.obstacleColor.copy(alpha = 0.78f)
                    drawRoundRect(
                        color = style.obstacleColor.copy(alpha = 0.12f),
                        topLeft = Offset(0f, OrchardLimit),
                        size = Size(OrchardWidth, floor - OrchardLimit),
                        cornerRadius = CornerRadius(4f)
                    )
                    for (wall in listOf(0f, OrchardWidth)) {
                        drawLine(
                            walls,
                            Offset(wall, OrchardLimit),
                            Offset(wall, floor),
                            3f,
                            StrokeCap.Round
                        )
                    }
                    drawLine(
                        walls,
                        Offset(0f, floor),
                        Offset(OrchardWidth, floor),
                        3f,
                        StrokeCap.Round
                    )
                    drawLine(
                        color = if (run.danger > 0f) style.hazardColor else style.detailColor,
                        start = Offset(0f, OrchardLimit),
                        end = Offset(OrchardWidth, OrchardLimit),
                        strokeWidth = 1.5f,
                        pathEffect = OrchardDashes
                    )
                    if (run.danger > 0f) {
                        drawRect(
                            color = style.hazardColor.copy(alpha = 0.08f + run.danger * 0.16f),
                            topLeft = Offset(0f, OrchardLimit - 8f),
                            size = Size(OrchardWidth, 16f)
                        )
                    }
                    if (run.canDrop) {
                        val kind = OrchardKind.entries[run.current]
                        val landing = run.landing
                        drawLine(
                            color = style.detailColor.copy(alpha = 0.48f),
                            start = Offset(run.aimX, OrchardDrop + kind.radius),
                            end = Offset(run.aimX, maxOf(landing, OrchardDrop + kind.radius)),
                            strokeWidth = 1.3f,
                            pathEffect = OrchardDashes
                        )
                        if (landing > OrchardLimit) {
                            drawCircle(
                                color = kind.color.copy(alpha = 0.64f),
                                radius = kind.radius,
                                center = Offset(run.aimX, landing),
                                style = Stroke(1.4f)
                            )
                        }
                        orchardFruit(kind, Offset(run.aimX, OrchardDrop), kind.radius)
                    }
                    for (fruit in run.fruits) {
                        val kind = OrchardKind.entries[fruit.kind]
                        val growth = 1f - fruit.flash * 0.06f
                        rotate(fruit.angle, Offset(fruit.x, fruit.y)) {
                            orchardFruit(kind, Offset(fruit.x, fruit.y), kind.radius * growth)
                        }
                    }
                    run.effects.draw(this, style.accentColor)

                    for ((index, kind) in OrchardKind.entries.withIndex()) {
                        val radius = 4.6f + index * 0.22f
                        orchardFruit(kind, Offset(12f + index * 20f, floor + 12f), radius)
                    }
                }
            }
            val previewRadius = minOf(12f, run.topInset * 0.27f)
            orchardFruit(
                kind = OrchardKind.entries[run.next],
                center = Offset(15f + previewRadius, run.topInset / 2f),
                radius = previewRadius
            )
        }
    }
}

private fun DrawScope.orchardFruit(kind: OrchardKind, center: Offset, radius: Float) {
    val ink = Color(0xFF493131)
    val leaf = Color(0xFF40864F)
    translate(center.x, center.y) {
        scale(radius, Offset.Zero) {
            drawPath(
                path = kind.shape,
                brush = Brush.linearGradient(
                    listOf(
                        lerp(kind.color, Color.White, 0.23f),
                        kind.color,
                        lerp(kind.color, ink, 0.24f)
                    ),
                    start = Offset(-0.8f, -0.9f),
                    end = Offset(0.8f, 0.94f)
                )
            )
            drawPath(kind.shape, lerp(kind.color, ink, 0.43f), style = Stroke(0.085f))
            when (kind) {
                OrchardKind.Cherry -> {
                    drawArc(
                        color = leaf, startAngle = 205f, sweepAngle = 103f, useCenter = false,
                        topLeft = Offset(-0.13f, -1.49f), size = Size(1.08f, 1.28f),
                        style = Stroke(0.12f, cap = StrokeCap.Round)
                    )
                }

                OrchardKind.Strawberry -> {
                    for (index in -1..1) {
                        rotate(index * 41f, Offset(0f, -0.72f)) {
                            drawOval(leaf, Offset(-0.14f, -1.06f), Size(0.28f, 0.52f))
                        }
                    }
                    repeat(3) { row ->
                        for (col in -1..1) {
                            val seed = Offset(col * (0.42f - row * 0.1f), row * 0.29f - 0.38f)
                            drawOval(
                                Color(0xFFFFDF90),
                                seed - Offset(0.045f, 0.075f),
                                Size(0.09f, 0.15f)
                            )
                        }
                    }
                }

                OrchardKind.Grape -> {
                    for (index in 0..4) {
                        val angle = index * 72f
                        rotate(angle, Offset.Zero) {
                            drawCircle(Color.White.copy(alpha = 0.15f), 0.28f, Offset(0f, -0.57f))
                        }
                    }
                    drawCircle(Color.White.copy(alpha = 0.22f), 0.26f, Offset(-0.11f, -0.12f))
                }

                OrchardKind.Dekopon, OrchardKind.Apple, OrchardKind.Pear -> {
                    drawLine(ink, Offset(0f, -0.77f), Offset(0.10f, -1.09f), 0.10f, StrokeCap.Round)
                    rotate(-28f, Offset(0.18f, -0.91f)) {
                        drawOval(leaf, Offset(0.05f, -1.02f), Size(0.56f, 0.21f))
                    }
                    if (kind == OrchardKind.Pear) {
                        for (index in 0..3) {
                            val point = Offset(0.27f + index * 0.12f, 0.58f - index % 2 * 0.14f)
                            drawCircle(ink.copy(alpha = 0.31f), 0.034f, point)
                        }
                    }
                }

                OrchardKind.Orange -> {
                    drawCircle(leaf, 0.17f, Offset(0.29f, -0.74f))
                    for (index in 0..5) {
                        val point = Offset(0.22f + index % 3 * 0.20f, 0.32f + index / 3 * 0.17f)
                        drawCircle(ink.copy(alpha = 0.22f), 0.041f, point)
                    }
                }

                OrchardKind.Peach -> {
                    drawArc(
                        color = Color(0xFFCB5F75),
                        startAngle = -77f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(-0.26f, -0.78f),
                        size = Size(0.62f, 1.64f),
                        style = Stroke(0.065f, cap = StrokeCap.Round)
                    )
                    rotate(27f, Offset(-0.18f, -0.80f)) {
                        drawOval(leaf, Offset(-0.67f, -0.94f), Size(0.60f, 0.23f))
                    }
                }

                OrchardKind.Pineapple, OrchardKind.Melon -> {
                    val pineapple = kind == OrchardKind.Pineapple
                    val lines = if (pineapple) ink.copy(alpha = 0.29f) else Color(0xFFE8EBC3)
                    clipPath(kind.shape) {
                        for (index in -5..5) {
                            val x = index * 0.33f
                            drawLine(lines, Offset(x - 0.86f, -1f), Offset(x + 0.86f, 1f), 0.037f)
                            drawLine(lines, Offset(x + 0.86f, -1f), Offset(x - 0.86f, 1f), 0.037f)
                        }
                    }
                    if (pineapple) {
                        for (index in -2..2) {
                            rotate(index * 24f, Offset(0f, -0.68f)) {
                                drawOval(leaf, Offset(-0.115f, -1.3f), Size(0.23f, 0.68f))
                            }
                        }
                    }
                }

                OrchardKind.Watermelon -> {
                    clipPath(kind.shape) {
                        rotate(-12f, Offset.Zero) {
                            for (index in -1..1) {
                                translate(top = index * 0.55f) {
                                    drawPath(OrchardWatermelonStripe, Color(0xFF367F49))
                                }
                            }
                        }
                    }
                }
            }
            drawOval(Color.White.copy(alpha = 0.45f), Offset(-0.62f, -0.50f), Size(0.25f, 0.15f))
            if (radius >= 8f) {
                drawCircle(Color.White.copy(alpha = 0.36f), 0.049f, Offset(-0.66f, -0.34f))
                orchardFace(kind, lerp(kind.color, ink, 0.86f))
            }
        }
    }
}

private fun DrawScope.orchardFace(kind: OrchardKind, ink: Color) {
    val eyesY = if (kind == OrchardKind.Pear) 0.21f else 0.05f
    for (side in -1..1 step 2) {
        val eye = Offset(side * 0.26f, eyesY)
        when {
            kind == OrchardKind.Pineapple || kind == OrchardKind.Strawberry && side == 1 -> {
                drawArc(
                    color = ink,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = eye - Offset(0.095f, 0.07f),
                    size = Size(0.19f, 0.17f),
                    style = Stroke(0.065f, cap = StrokeCap.Round)
                )
            }

            kind == OrchardKind.Watermelon && side == -1 -> {
                val tip = eye + Offset(0.046f, 0f)
                drawLine(ink, eye - Offset(0.08f, 0.08f), tip, 0.075f, StrokeCap.Round)
                drawLine(ink, eye - Offset(0.08f, -0.07f), tip, 0.075f, StrokeCap.Round)
            }

            else -> drawOval(ink, eye - Offset(0.077f, 0.091f), Size(0.154f, 0.182f))
        }

        if (kind == OrchardKind.Peach || kind == OrchardKind.Strawberry || kind == OrchardKind.Pear) {
            drawOval(
                color = Color(0xFFCA4E66).copy(alpha = 0.35f),
                topLeft = eye + Offset(side * 0.13f - 0.09f, 0.14f),
                size = Size(0.18f, 0.10f)
            )
        }
    }
    when (kind) {
        OrchardKind.Dekopon, OrchardKind.Apple -> {
            drawOval(ink, Offset(-0.084f, 0.18f), Size(0.168f, 0.16f))
            drawCircle(Color.White.copy(alpha = 0.85f), 0.037f, Offset(0f, 0.22f))
        }

        OrchardKind.Peach, OrchardKind.Grape, OrchardKind.Pineapple -> {
            val mouth = Size(0.24f, 0.21f)
            drawArc(ink, 0f, 180f, true, Offset(-0.12f, 0.115f), mouth)
            drawOval(Color.White.copy(alpha = 0.86f), Offset(-0.054f, 0.225f), Size(0.108f, 0.05f))
        }

        OrchardKind.Orange, OrchardKind.Melon -> {
            drawPath(OrchardPout, ink, style = Stroke(0.06f, cap = StrokeCap.Round))
        }

        else -> {
            val y = eyesY + 0.11f
            drawArc(
                color = ink,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(-0.12f, y),
                size = Size(0.24f, 0.17f),
                style = Stroke(0.064f, cap = StrokeCap.Round)
            )
        }
    }
}

@Serializable
internal class OrchardFruit(
    val kind: Int,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var age: Float = 0f,
    var angle: Float = 0f,
    var flash: Float = 0f,
    var spin: Float = 0f
) {
    val radius: Float
        get() = OrchardKind.entries[kind].radius

    @Transient
    var supported = false
}

@Serializable
internal class OrchardPileRun : ArcadeRun() {
    val fruits = mutableListOf<OrchardFruit>()
    var current = 0
        private set
    var next = 0
        private set
    var aimX = OrchardWidth / 2f
        private set
    private var cooldown = 0f
    private var overflow = 0f
    private var drops = 0
    var floor = OrchardFloor
        private set
    private val bag = mutableListOf<Int>()

    @Transient
    private var aiming = false

    @Transient
    val effects = ArcadeEffects()

    val unit: Float
        get() = minOf((width - 18f) / OrchardWidth, (height - topInset - 8f) / (floor + 21f))
    val left: Float
        get() = (width - OrchardWidth * unit) / 2f
    val top: Float
        get() = topInset + (height - topInset - (floor + 21f) * unit) / 2f
    val canDrop: Boolean
        get() = cooldown <= 0f
    private val grace: Float
        get() = 1.75f - 0.70f * (drops.toFloat() / (drops + 70f))
    val danger: Float
        get() = (overflow / grace).coerceIn(0f, 1f)
    val landing: Float
        get() {
            val radius = OrchardKind.entries[current].radius
            var y = floor - radius
            for (fruit in fruits) {
                val dx = abs(aimX - fruit.x)
                val distance = radius + fruit.radius
                if (dx < distance) y = minOf(y, fruit.y - sqrt(distance * distance - dx * dx))
            }
            return y
        }

    override fun reset() {
        fruits.clear()
        bag.clear()
        drops = 0
        val fullWidthUnit = (width - 18f) / OrchardWidth
        floor = ((height - topInset - 8f) / fullWidthUnit - 21f).coerceIn(OrchardFloor, 460f)
        cooldown = 0f
        overflow = 0f
        aiming = false
        aimX = OrchardWidth / 2f
        current = pickFruit()
        next = pickFruit()
        effects.clear()
    }

    fun aim(point: Offset) {
        aiming = true
        val radius = OrchardKind.entries[current].radius
        aimX = ((point.x - left) / unit).coerceIn(radius, OrchardWidth - radius)
    }

    fun drop() {
        if (!aiming) return
        aiming = false
        if (!canDrop || landing < OrchardDrop) return
        fruits += OrchardFruit(current, aimX, OrchardDrop)
        drops++
        current = next
        next = pickFruit()
        val radius = OrchardKind.entries[current].radius
        aimX = aimX.coerceIn(radius, OrchardWidth - radius)
        cooldown = OrchardCooldown
    }

    override fun stopInput() {
        aiming = false
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        cooldown = (cooldown - seconds).coerceAtLeast(0f)
        for (fruit in fruits) {
            fruit.supported = false
            fruit.age += seconds
            fruit.flash = (fruit.flash - seconds * 5f).coerceAtLeast(0f)
            fruit.vx *= 1f - 0.50f * seconds
            fruit.vy = (fruit.vy + 530f * seconds).coerceAtMost(420f)
            fruit.spin *= 1f - 1.9f * seconds
            fruit.x += fruit.vx * seconds
            fruit.y += fruit.vy * seconds
            fruit.angle = (fruit.angle + fruit.spin * seconds * 57.29578f) % 360f
        }
        repeat(8) {
            for (fruit in fruits) contain(fruit)
            solveContacts()
        }
        for (fruit in fruits) {
            contain(fruit)
            if (fruit.supported && abs(fruit.vy) < 42f) {
                fruit.vy *= 1f - 28f * seconds
            }
        }
        val blocked = fruits.any { it.age > 0.68f && it.y - it.radius < OrchardLimit }
        overflow = if (blocked) overflow + seconds else 0f
        if (overflow >= grace) finish()
    }

    private fun solveContacts() {
        for (i in 0 until fruits.lastIndex) {
            val a = fruits[i]
            val radiusA = a.radius
            for (j in i + 1..fruits.lastIndex) {
                val b = fruits[j]
                val radiusB = b.radius
                val contact = radiusA + radiusB
                val dx = b.x - a.x
                val dy = b.y - a.y
                val reach = contact + 0.12f
                if (abs(dx) > reach || abs(dy) > reach) continue
                val squared = dx * dx + dy * dy
                if (squared > reach * reach) continue
                if (a.kind == b.kind && squared <= contact * contact) {
                    merge(i, j, a, b)
                    return
                }

                val distance = sqrt(squared)
                val nx = if (distance > 0.001f) dx / distance else 1f
                val ny = if (distance > 0.001f) dy / distance else 0f
                if (ny > 0.35f) a.supported = true
                if (ny < -0.35f) b.supported = true
                val massA = radiusA * radiusA
                val massB = radiusB * radiusB
                val shareA = massB / (massA + massB)
                val shareB = 1f - shareA
                val overlap = ((contact - distance - 0.06f) * 0.68f).coerceIn(0f, 1.8f)
                a.x -= nx * overlap * shareA
                a.y -= ny * overlap * shareA
                b.x += nx * overlap * shareB
                b.y += ny * overlap * shareB

                val dvx = b.vx - a.vx
                val dvy = b.vy - a.vy
                val normal = dvx * nx + dvy * ny
                if (normal >= 0f) continue
                val impulse = -normal * if (normal < -120f) 1.045f else 1f
                // Friction acts on the contact surfaces and transfers momentum into rotation.
                val slip = dvy * nx - dvx * ny - a.spin * radiusA - b.spin * radiusB
                val limit = impulse * 0.27f
                val friction = (-slip / 3f).coerceIn(-limit, limit)
                val ix = nx * impulse - ny * friction
                val iy = ny * impulse + nx * friction
                a.vx -= ix * shareA
                a.vy -= iy * shareA
                a.spin -= 2f * friction * shareA / radiusA
                b.vx += ix * shareB
                b.vy += iy * shareB
                b.spin -= 2f * friction * shareB / radiusB
            }
        }
    }

    private fun contain(fruit: OrchardFruit) {
        val radius = fruit.radius
        val right = OrchardWidth - radius
        fruit.x = fruit.x.coerceIn(radius, right)
        if (fruit.x == radius && fruit.vx < 0f || fruit.x == right && fruit.vx > 0f) {
            fruit.vx = if (abs(fruit.vx) > 120f) -fruit.vx * 0.04f else 0f
        }
        val bottom = floor - radius
        if (fruit.y < bottom) return

        fruit.y = bottom
        fruit.supported = true
        if (fruit.vy <= 0f) return
        val limit = fruit.vy * 0.35f
        val friction = ((fruit.vx - fruit.spin * radius) / 3f).coerceIn(-limit, limit)
        fruit.vx -= friction
        fruit.spin += 2f * friction / radius
        fruit.vy = if (fruit.vy > 120f) -fruit.vy * 0.045f else 0f
    }

    private fun merge(i: Int, j: Int, a: OrchardFruit, b: OrchardFruit) {
        val kind = a.kind + 1
        val x = (a.x + b.x) / 2f
        val y = (a.y + b.y) / 2f
        fruits.removeAt(j)
        if (kind == OrchardKind.entries.size) {
            fruits.removeAt(i)
            score += 100
        } else {
            val fruit = OrchardFruit(
                kind = kind,
                x = x,
                y = y,
                vx = (a.vx + b.vx) * 0.40f,
                vy = ((a.vy + b.vy) * 0.32f).coerceAtLeast(-22f),
                age = maxOf(a.age, b.age),
                angle = a.angle,
                flash = 1f,
                spin = (a.spin + b.spin) * 0.32f
            )
            contain(fruit)
            fruits[i] = fruit
            score += kind * (kind + 1) / 2
        }
        effects.burst(Offset(x, y), if (kind == OrchardKind.entries.size) 24 else 9)
    }

    private fun pickFruit(): Int {
        if (bag.isEmpty()) {
            val varieties = (3 + drops / 16).coerceAtMost(6)
            repeat(varieties * 2) { bag += it % varieties }
            bag.shuffle(Random)
        }
        return bag.removeAt(bag.lastIndex)
    }
}
