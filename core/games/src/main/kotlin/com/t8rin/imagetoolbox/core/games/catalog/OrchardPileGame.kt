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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.ColorUtils
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AvocadoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object OrchardPileGame : ArcadeGameDefinition {
    override val title = R.string.arcade_orchardpile
    override val description = R.string.arcade_orchardpile_sub
    override val icon get() = Icons.Outlined.AvocadoBean
    override val order = 39

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val fruits = remember(style.playerColor) {
            OrchardKind.entries.map { kind -> orchardSprite(kind, style.playerColor) }
        }
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::OrchardPileRun
        )
        LaunchedEffect(Unit) {
            val outlines = OrchardKind.entries.map { it.outline }
            withContext(Dispatchers.Default) {
                for (outline in outlines) outline.prepare()
            }
        }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::aim,
                onDrag = run::aim,
                onRelease = run::drop
            ) {
                drawOrchardBackground(run, style)
                drawRoundRect(
                    brush = style.background,
                    topLeft = Offset(run.left, run.top + OrchardLimit * run.unit),
                    size = Size(OrchardWidth * run.unit, (run.floor - OrchardLimit) * run.unit),
                    cornerRadius = CornerRadius(4f * run.unit)
                )
                translate(left = run.left, top = run.top) {
                    scale(scale = run.unit, pivot = Offset.Zero) {
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
                                color = walls,
                                start = Offset(wall, OrchardLimit),
                                end = Offset(wall, floor),
                                strokeWidth = 3f,
                                cap = StrokeCap.Round
                            )
                        }
                        drawLine(
                            color = walls,
                            start = Offset(0f, floor),
                            end = Offset(OrchardWidth, floor),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
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
                            drawLine(
                                color = style.highScoreColor.copy(alpha = 0.44f),
                                start = Offset(run.aimX, OrchardDrop + kind.outline.bottom),
                                end = Offset(run.aimX, floor),
                                strokeWidth = 1.4f
                            )
                            orchardFruit(
                                fruits[kind.ordinal],
                                Offset(run.aimX, OrchardDrop),
                                kind.radius
                            )
                        }
                        for (fruit in run.fruits) {
                            val kind = OrchardKind.entries[fruit.kind]
                            rotate(
                                degrees = fruit.angle,
                                pivot = Offset(x = fruit.x, y = fruit.y)
                            ) {
                                orchardFruit(
                                    fruits[kind.ordinal],
                                    Offset(fruit.x, fruit.y),
                                    kind.radius
                                )
                            }
                        }
                        run.effects.draw(
                            scope = this,
                            color = style.accentColor
                        )

                        for ((index, _) in OrchardKind.entries.withIndex()) {
                            val radius = 4.6f + index * 0.22f
                            orchardFruit(
                                fruits[index],
                                Offset(12f + index * 20f, floor + 12f),
                                radius
                            )
                        }
                    }
                }
                val previewRadius = minOf(12f, run.topInset * 0.27f)
                orchardFruit(
                    sprite = fruits[run.next],
                    center = Offset(15f + previewRadius, run.topInset / 2f),
                    radius = previewRadius
                )
            }
        }
    }
}

private const val OrchardWidth = 224f
private const val OrchardFloor = 249f
private const val OrchardDrop = 20f
private const val OrchardLimit = 52f
private const val OrchardCooldown = 0.43f
private val OrchardInk = Color(0xFF493131)
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
        radius = 12f,
        color = Color(0xFFFF5D65),
        silhouette = "M0,-0.71 C-0.58,-1 -1,-0.69 -0.94,-0.16 C-0.89,0.23 -0.32,1 0,1 C0.32,1 0.89,0.23 " +
                "0.94,-0.16 C1,-0.69 0.58,-1 0,-0.71 Z"
    ),
    Grape(
        radius = 15f,
        color = Color(0xFF8C61CB),
        silhouette = "M0,-1 C0.32,-1 0.48,-0.82 0.5,-0.63 C0.92,-0.72 1.09,-0.36 0.94,-0.08 C1.08,0.21 " +
                "0.8,0.58 0.55,0.55 C0.5,0.81 0.29,1 0,1 C-0.29,1 -0.5,0.81 -0.55,0.55 C-0.8,0.58 " +
                "-1.08,0.21 -0.94,-0.08 C-1.09,-0.36 -0.92,-0.72 -0.5,-0.63 C-0.48,-0.82 -0.32,-1 0,-1 Z"
    ),
    Dekopon(
        radius = 19f,
        color = Color(0xFFFFC756),
        silhouette = "M-0.32,-0.7 Q-0.26,-1 0,-1 Q0.3,-1 0.33,-0.69 C1.22,-0.36 1.22,0.97 0,1 C-1.15,0.98 " +
                "-1.24,-0.34 -0.32,-0.7 Z"
    ),
    Orange(23f, Color(0xFFFB8A38)),
    Apple(
        radius = 27f,
        color = Color(0xFFE04D3B),
        silhouette = "M0,-0.72 C0.53,-1.03 1.04,-0.74 0.99,-0.07 C0.94,0.67 0.48,1.05 0.1,0.86 Q0,0.82 " +
                "-0.1,0.86 C-0.48,1.05 -0.94,0.67 -0.99,-0.07 C-1.04,-0.74 -0.53,-1.03 0,-0.72 Z"
    ),
    Pear(
        radius = 33f,
        color = Color(0xFFF2CB67),
        silhouette = "M0,-1 C0.45,-1 0.43,-0.47 0.68,-0.15 C1.21,0.46 0.85,0.97 0,1 C-0.85,0.97 -1.21,0.46 " +
                "-0.68,-0.15 C-0.43,-0.47 -0.45,-1 0,-1 Z"
    ),
    Peach(
        radius = 38f,
        color = Color(0xFFFFAFA0),
        silhouette = "M0,-0.78 C0.62,-1.02 1.07,-0.54 0.99,0.06 C0.94,0.53 0.4,0.94 0,1 C-0.4,0.94 -0.94,0.53 " +
                "-0.99,0.06 C-1.07,-0.54 -0.62,-1.02 0,-0.78 Z"
    ),
    Pineapple(
        radius = 44f,
        color = Color(0xFFF2B536),
        silhouette = "M0,-0.86 C0.54,-0.86 0.82,-0.45 0.82,0.08 C0.82,0.65 0.54,1 0,1 C-0.54,1 -0.82,0.65 " +
                "-0.82,0.08 C-0.82,-0.45 -0.54,-0.86 0,-0.86 Z"
    ),
    Melon(51f, Color(0xFFB9D78B)),
    Watermelon(59f, Color(0xFF76BF55));

    val shape = PathParser().parsePathString(silhouette).toPath()
    val outline by lazy { OrchardOutline(shape, radius) }
}

private fun Color.orchardColor(primary: Color): Color =
    Color(ColorUtils.blendARGB(toArgb(), primary.toArgb(), 0.2f))

private fun orchardSprite(kind: OrchardKind, primary: Color): ImageBitmap {
    val image = ImageBitmap(width = 80, height = 100)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(image),
        size = Size(80f, 100f)
    ) {
        drawOrchardFruit(kind, Offset(40f, 60f), 34f, primary)
    }
    val color = kind.color.orchardColor(primary)
    val ink = OrchardInk.orchardColor(primary)
    val palette = listOf(
        color,
        lerp(color, Color.White, 0.12f),
        lerp(color, Color.White, 0.23f),
        lerp(color, ink, 0.12f),
        lerp(color, ink, 0.24f),
        lerp(color, ink, 0.43f),
        lerp(color, ink, 0.86f),
        ink,
        Color.White.orchardColor(primary),
        Color(0xFF40864F).orchardColor(primary),
        Color(0xFF367F49).orchardColor(primary),
        Color(0xFFFFDF90).orchardColor(primary),
        Color(0xFFCB5F75).orchardColor(primary),
        Color(0xFFE8EBC3).orchardColor(primary),
        lerp(color, Color(0xFFCA4E66).orchardColor(primary), 0.35f)
    ).map(Color::toArgb)
    val bitmap = image.asAndroidBitmap()
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (index in pixels.indices) {
        val pixel = pixels[index]
        if (pixel ushr 24 < 128) {
            pixels[index] = 0
            continue
        }
        val red = pixel shr 16 and 255
        val green = pixel shr 8 and 255
        val blue = pixel and 255
        pixels[index] = palette.minBy { candidate ->
            val dr = red - (candidate shr 16 and 255)
            val dg = green - (candidate shr 8 and 255)
            val db = blue - (candidate and 255)
            dr * dr + dg * dg + db * db
        }
    }
    bitmap.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    return image
}

private fun DrawScope.orchardFruit(sprite: ImageBitmap, center: Offset, radius: Float) {
    translate(left = center.x, top = center.y) {
        scale(scale = radius / 34f, pivot = Offset.Zero) {
            translate(left = -40f, top = -60f) {
                drawImage(image = sprite, filterQuality = FilterQuality.None)
            }
        }
    }
}

private fun DrawScope.drawOrchardFruit(
    kind: OrchardKind,
    center: Offset,
    radius: Float,
    primary: Color
) {
    val ink = OrchardInk.orchardColor(primary)
    val leaf = Color(0xFF40864F).orchardColor(primary)
    val color = kind.color.orchardColor(primary)
    translate(left = center.x, top = center.y) {
        scale(scale = radius, pivot = Offset.Zero) {
            drawPath(kind.shape, lerp(color, ink, 0.18f))
            clipPath(kind.shape) {
                translate(left = -0.09f, top = -0.10f) {
                    drawPath(kind.shape, color)
                }
                drawOval(
                    lerp(color, Color.White, 0.12f),
                    Offset(-0.75f, -0.83f),
                    Size(1.18f, 0.92f)
                )
            }
            drawPath(kind.shape, lerp(color, ink, 0.3f), style = Stroke(0.055f))
            when (kind) {
                OrchardKind.Cherry -> {
                    drawArc(
                        color = leaf,
                        startAngle = 205f,
                        sweepAngle = 103f,
                        useCenter = false,
                        topLeft = Offset(-0.13f, -1.49f),
                        size = Size(1.08f, 1.28f),
                        style = Stroke(0.12f, cap = StrokeCap.Round)
                    )
                }

                OrchardKind.Strawberry -> {
                    for (index in -1..1) {
                        rotate(degrees = index * 41f, pivot = Offset(0f, -0.72f)) {
                            drawOval(leaf, Offset(-0.14f, -1.06f), Size(0.28f, 0.52f))
                        }
                    }
                    repeat(3) { row ->
                        for (col in -1..1) {
                            val seed = Offset(col * (0.42f - row * 0.1f), row * 0.29f - 0.38f)
                            drawOval(
                                color = Color(0xFFFFDF90).orchardColor(primary),
                                topLeft = seed - Offset(0.045f, 0.075f),
                                size = Size(0.09f, 0.15f)
                            )
                        }
                    }
                }

                OrchardKind.Grape -> {
                    for (index in 0..4) {
                        val angle = index * 72f
                        rotate(degrees = angle, pivot = Offset.Zero) {
                            drawCircle(Color.White.copy(alpha = 0.15f), 0.28f, Offset(0f, -0.57f))
                        }
                    }
                    drawCircle(Color.White.copy(alpha = 0.22f), 0.26f, Offset(-0.11f, -0.12f))
                }

                OrchardKind.Dekopon, OrchardKind.Apple, OrchardKind.Pear -> {
                    drawLine(ink, Offset(0f, -0.77f), Offset(0.10f, -1.09f), 0.10f, StrokeCap.Round)
                    rotate(degrees = -28f, pivot = Offset(0.18f, -0.91f)) {
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
                        color = Color(0xFFCB5F75).orchardColor(primary),
                        startAngle = -77f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(-0.26f, -0.78f),
                        size = Size(0.62f, 1.64f),
                        style = Stroke(0.065f, cap = StrokeCap.Round)
                    )
                    rotate(degrees = 27f, pivot = Offset(-0.18f, -0.80f)) {
                        drawOval(leaf, Offset(-0.67f, -0.94f), Size(0.60f, 0.23f))
                    }
                }

                OrchardKind.Pineapple, OrchardKind.Melon -> {
                    val pineapple = kind == OrchardKind.Pineapple
                    val lines =
                        if (pineapple) ink.copy(alpha = 0.29f) else Color(0xFFE8EBC3).orchardColor(
                            primary
                        )
                    clipPath(kind.shape) {
                        for (index in -5..5) {
                            val x = index * 0.33f
                            drawLine(lines, Offset(x - 0.86f, -1f), Offset(x + 0.86f, 1f), 0.037f)
                            drawLine(lines, Offset(x + 0.86f, -1f), Offset(x - 0.86f, 1f), 0.037f)
                        }
                    }
                    if (pineapple) {
                        for (index in -2..2) {
                            rotate(degrees = index * 24f, pivot = Offset(0f, -0.68f)) {
                                drawOval(leaf, Offset(-0.115f, -1.3f), Size(0.23f, 0.68f))
                            }
                        }
                    }
                }

                OrchardKind.Watermelon -> {
                    clipPath(kind.shape) {
                        rotate(degrees = -12f, pivot = Offset.Zero) {
                            for (index in -1..1) {
                                translate(top = index * 0.55f) {
                                    drawPath(
                                        OrchardWatermelonStripe,
                                        Color(0xFF367F49).orchardColor(primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            drawOval(Color.White.copy(alpha = 0.45f), Offset(-0.62f, -0.50f), Size(0.25f, 0.15f))
            if (radius >= 8f) {
                drawCircle(Color.White.copy(alpha = 0.36f), 0.049f, Offset(-0.66f, -0.34f))
                orchardFace(kind, lerp(color, ink, 0.86f), primary)
            }
        }
    }
}

private fun DrawScope.orchardFace(kind: OrchardKind, ink: Color, primary: Color) {
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
                color = Color(0xFFCA4E66).orchardColor(primary).copy(alpha = 0.35f),
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

private fun DrawScope.drawOrchardBackground(run: OrchardPileRun, style: ArcadeStyle) {
    val width = run.width
    val height = run.height
    val ink = style.detailColor.copy(alpha = 0.16f)
    val canopy = minOf(height * 0.23f, 79f)
    val stripe = width / 15f
    repeat(15) { index ->
        val x = index * stripe
        val cloth = Path().apply {
            moveTo(x, canopy - 17f)
            lineTo(x + stripe, canopy - 17f)
            lineTo(x + stripe, canopy)
            lineTo(x + stripe - 3f, canopy + 3f)
            lineTo(x + 3f, canopy + 3f)
            lineTo(x, canopy)
            close()
        }
        drawPath(cloth, style.detailColor.copy(alpha = if (index % 2 == 0) 0.09f else 0.04f))
        drawLine(ink.copy(alpha = 0.1f), Offset(x, canopy - 17f), Offset(x, canopy), 0.7f)
    }
    drawLine(ink, Offset(0f, canopy - 18f), Offset(width, canopy - 18f), 1f)
    for (x in listOf(7f, width - 8f)) {
        pixelRect(ink.copy(alpha = 0.12f), x, 0f, 2f, height)
        pixelRect(ink.copy(alpha = 0.06f), x + 3f, 0f, 1f, height)
    }
    val counter = height - 9f
    drawLine(ink.copy(alpha = 0.11f), Offset(0f, counter), Offset(width, counter), 1f)
    drawLine(ink.copy(alpha = 0.07f), Offset(0f, counter + 4f), Offset(width, counter + 4f), 1f)

    fun crate(x: Float, y: Float, size: Float) {
        val depth = size * 0.24f
        val bodyHeight = size * 0.7f
        val lid = Path().apply {
            moveTo(x, y - bodyHeight)
            lineTo(x + size * 0.7f, y - bodyHeight - depth)
            lineTo(x + size, y - bodyHeight)
            lineTo(x + size * 0.3f, y - bodyHeight + depth)
            close()
        }
        drawPath(lid, style.detailColor.copy(alpha = 0.15f))
        drawPath(lid, ink.copy(alpha = 0.18f), style = Stroke(0.7f))
        val front = Path().apply {
            moveTo(x, y - bodyHeight)
            lineTo(x + size * 0.3f, y - bodyHeight + depth)
            lineTo(x + size * 0.3f, y + depth)
            lineTo(x, y)
            close()
        }
        drawPath(front, style.detailColor.copy(alpha = 0.09f))
        val side = Path().apply {
            moveTo(x + size * 0.3f, y - bodyHeight + depth)
            lineTo(x + size, y - bodyHeight)
            lineTo(x + size, y)
            lineTo(x + size * 0.3f, y + depth)
            close()
        }
        drawPath(side, style.detailColor.copy(alpha = 0.19f))
        for (slat in 1..2) {
            val lineY = y - bodyHeight + bodyHeight * slat / 3f
            drawLine(
                ink.copy(alpha = 0.24f),
                Offset(x, lineY),
                Offset(x + size * 0.3f, lineY + depth),
                0.8f
            )
            drawLine(
                ink.copy(alpha = 0.21f),
                Offset(x + size * 0.3f, lineY + depth),
                Offset(x + size, lineY),
                0.8f
            )
        }
        drawLine(
            ink.copy(alpha = 0.2f), Offset(x + size * 0.3f, y - bodyHeight + depth),
            Offset(x + size * 0.3f, y + depth), 0.7f
        )
    }

    val shelfRows = ceil((height - canopy - 12f) / 83f).toInt().coerceAtLeast(1)
    val columns = ceil(width / 69f).toInt()
    for (row in 0 until shelfRows) {
        val y = canopy + 68f + row * 83f
        pixelRect(ink.copy(alpha = 0.16f), 0f, y + 6f, width, 3f)
        pixelRect(ink.copy(alpha = 0.07f), 0f, y + 10f, width, 6f)
        for (column in 0 until columns) {
            val x = column * 69f + 12f + row % 2 * 9f
            val size = 35f + (column + row) % 2 * 7f
            crate(x, y - 2f, size)
            for (fruit in 0..2) {
                val point = Offset(x + 11f + fruit * 9f, y - size * 0.75f - 3f - fruit % 2 * 2f)
                drawCircle(style.detailColor.copy(alpha = 0.21f), 4f, point, style = Stroke(0.8f))
                pixelRect(style.detailColor.copy(alpha = 0.22f), point.x, point.y - 6f, 1f, 2f)
            }
        }
    }

}
@Serializable
private class OrchardFruit(
    val kind: Int,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var age: Float = 0f,
    var angle: Float = 0f,
    var spin: Float = 0f
) {
    val radius: Float
        get() = OrchardKind.entries[kind].radius

    @Transient
    val collider = OrchardCollider(OrchardKind.entries[kind].outline).apply {
        rotate(this@OrchardFruit.angle)
    }

    @Transient
    var onFloor = false

    @Transient
    private var supportLeft = Float.POSITIVE_INFINITY

    @Transient
    private var supportRight = Float.NEGATIVE_INFINITY

    @Transient
    private var bracedLeft = false

    @Transient
    private var bracedRight = false

    @Transient
    var asleep = false
        private set

    @Transient
    private var settleTime = 0f

    val inverseMass: Float
        get() = if (asleep) 0f else 1f / (radius * radius)
    val inverseInertia: Float
        get() = 2f * inverseMass * inverseMass

    fun contactVelocity(point: Offset) = Offset(vx - spin * point.y, vy + spin * point.x)

    fun impulse(direction: Offset, point: Offset, amount: Float) {
        vx += direction.x * amount * inverseMass
        vy += direction.y * amount * inverseMass
        spin += point.cross(direction) * amount * inverseInertia
    }

    fun effectiveInverseMass(direction: Offset, point: Offset): Float {
        val lever = point.cross(direction)
        return inverseMass + lever * lever * inverseInertia
    }

    fun wake() {
        asleep = false
        settleTime = 0f
    }

    fun resetSupport() {
        onFloor = false
        supportLeft = Float.POSITIVE_INFINITY
        supportRight = Float.NEGATIVE_INFINITY
        bracedLeft = false
        bracedRight = false
    }

    fun support(point: Offset, normal: Offset) {
        if (normal.y > 0.35f) {
            supportLeft = minOf(supportLeft, point.x)
            supportRight = maxOf(supportRight, point.x)
        } else if (normal.x < -0.72f) {
            bracedLeft = true
        } else if (normal.x > 0.72f) {
            bracedRight = true
        }
    }

    fun settle(seconds: Float) {
        if (!onFloor && !supportLeft.isFinite()) {
            wake()
            return
        }
        val tolerance = radius * 0.08f
        val stable = onFloor ||
                supportLeft <= tolerance && supportRight >= -tolerance &&
                supportRight - supportLeft >= radius * 0.18f ||
                bracedLeft && supportRight >= -tolerance ||
                bracedRight && supportLeft <= tolerance
        if (!stable) {
            wake()
            // Let a fruit roll off an exposed point of contact, even when it landed dead center.
            if (abs(vx) < 3f && abs(spin) * radius < 7f) {
                val support = (supportLeft + supportRight) / 2f
                val downhill = when {
                    bracedLeft -> 1f
                    bracedRight -> -1f
                    abs(support) > 0.08f -> if (support > 0f) -1f else 1f
                    vx < -0.1f -> -1f
                    else -> 1f
                }
                vx += downhill * 24f * seconds
            }
            return
        }
        if (asleep) return
        if (abs(vy) < 42f) vy *= 1f - 28f * seconds
        if (abs(vx) >= 10f || abs(vy) >= 14f || abs(spin) * radius >= 14f) {
            settleTime = 0f
            return
        }
        spin *= 1f - 26f * seconds
        if (abs(spin) < 0.06f) spin = 0f
        settleTime += seconds
        if (settleTime >= 0.38f) {
            vx = 0f
            vy = 0f
            spin = 0f
            asleep = true
        }
    }
}

@Serializable
private class OrchardPileRun : ArcadeRun() {
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

    @Transient
    private val contact = OrchardContact()

    @Transient
    private val contacts = mutableListOf<OrchardContact>()

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

    override fun reset() {
        fruits.clear()
        contacts.clear()
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
        aimX = clampAim((point.x - left) / unit)
    }

    private fun clampAim(x: Float): Float {
        val kind = OrchardKind.entries[current]
        return x.coerceIn(-kind.outline.left, OrchardWidth - kind.outline.right)
    }

    fun drop() {
        if (!aiming) return
        aiming = false
        if (!canDrop) return
        val dropped = OrchardFruit(current, aimX, OrchardDrop)
        if (fruits.any {
                contact.find(
                    dropped.collider,
                    it.collider,
                    Offset(it.x - dropped.x, it.y - dropped.y)
                ) && contact.separation < 0f
            }) return
        fruits += dropped
        drops++
        current = next
        next = pickFruit()
        aimX = clampAim(aimX)
        cooldown = OrchardCooldown
    }

    override fun stopInput() {
        aiming = false
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        cooldown = (cooldown - seconds).coerceAtLeast(0f)
        for (fruit in fruits) {
            fruit.resetSupport()
            fruit.age += seconds
            if (fruit.asleep) continue
            fruit.vx *= 1f - 0.50f * seconds
            fruit.vy = (fruit.vy + 530f * seconds).coerceAtMost(420f)
            fruit.spin *= 1f - 1.9f * seconds
            fruit.x += fruit.vx * seconds
            fruit.y += fruit.vy * seconds
            fruit.angle = (fruit.angle + fruit.spin * seconds * 57.29578f) % 360f
            fruit.collider.rotate(fruit.angle)
        }
        var activeContacts = -1
        repeat(8) { pass ->
            for (fruit in fruits) contain(fruit)
            if (pass == 4 || activeContacts < 0) {
                activeContacts = findContacts(checkSleeping = activeContacts < 0)
            }
            for (index in 0 until activeContacts) resolveContact(contacts[index])
        }
        for (fruit in fruits) {
            contain(fruit)
            fruit.settle(seconds)
        }
        val blocked = fruits.any { it.age > 0.68f && it.y + it.collider.top < OrchardLimit }
        overflow = if (blocked) overflow + seconds else 0f
        if (overflow >= grace) finish()
    }

    private fun findContacts(checkSleeping: Boolean): Int {
        var count = 0
        for (i in 0 until fruits.lastIndex) {
            val a = fruits[i]
            for (j in i + 1..fruits.lastIndex) {
                val b = fruits[j]
                if (a.asleep && b.asleep && !checkSleeping) continue
                if (!contact.find(a.collider, b.collider, Offset(b.x - a.x, b.y - a.y))) continue
                if (a.kind == b.kind && contact.separation <= 0f) {
                    merge(i, j, a, b)
                    return -1
                }
                if (count == contacts.size) contacts += OrchardContact()
                contacts[count++].set(a, b, contact)
            }
        }
        return count
    }

    private fun resolveContact(contact: OrchardContact) {
        val a = contact.a
        val b = contact.b
        val delta = Offset(b.x - a.x, b.y - a.y)
        val shift = delta - contact.delta
        // Shapes only rotate between steps. Reuse the contact plane for small corrections.
        if (shift.getDistanceSquared() > 0.64f) {
            if (!contact.find(a.collider, b.collider, delta)) return
        }
        val normal = contact.normal
        val separation = contact.separation + (delta - contact.delta).dot(normal)
        if (separation > OrchardContactMargin) return
        val armA = contact.point
        val armB = armA - contact.delta
        a.support(armA, normal)
        b.support(armB, -normal)
        val velocity = (b.contactVelocity(armB) - a.contactVelocity(armA)).dot(normal)
        if (velocity < -23f || separation < -1.25f) {
            if (a.asleep) a.wake()
            if (b.asleep) b.wake()
        }
        if (a.asleep && b.asleep) return
        val overlap = ((-separation - 0.06f) * 0.68f).coerceIn(0f, 1.8f)
        val inverseA = a.inverseMass
        val inverseB = b.inverseMass
        val correction = normal * (overlap / (inverseA + inverseB))
        a.x -= correction.x * inverseA
        a.y -= correction.y * inverseA
        b.x += correction.x * inverseB
        b.y += correction.y * inverseB

        if (velocity >= 0f) return
        val impact = -velocity * if (velocity < -120f) 1.045f else 1f
        val impulse = impact / (
                a.effectiveInverseMass(normal, armA) + b.effectiveInverseMass(normal, armB)
                )
        a.impulse(normal, armA, -impulse)
        b.impulse(normal, armB, impulse)

        val tangent = Offset(-normal.y, normal.x)
        val slip = (b.contactVelocity(armB) - a.contactVelocity(armA)).dot(tangent)
        val limit = impulse * 0.31f
        val friction = (-slip / (
                a.effectiveInverseMass(tangent, armA) + b.effectiveInverseMass(tangent, armB)
                )).coerceIn(-limit, limit)
        a.impulse(tangent, armA, -friction)
        b.impulse(tangent, armB, friction)
    }

    private fun contain(fruit: OrchardFruit) {
        val shape = fruit.collider
        val left = -shape.left.x
        val right = OrchardWidth - shape.right.x
        if (fruit.x <= left) {
            fruit.x = left
            fruit.support(shape.left, Offset(-1f, 0f))
            wallImpulse(fruit, shape.left, Offset(1f, 0f))
        } else if (fruit.x >= right) {
            fruit.x = right
            fruit.support(shape.right, Offset(1f, 0f))
            wallImpulse(fruit, shape.right, Offset(-1f, 0f))
        }
        val bottom = floor - shape.bottom.y
        if (fruit.y >= bottom) {
            fruit.y = bottom
            fruit.onFloor = true
            wallImpulse(fruit, shape.bottom, Offset(0f, -1f))
        }
    }

    private fun wallImpulse(fruit: OrchardFruit, point: Offset, normal: Offset) {
        val velocity = fruit.contactVelocity(point).dot(normal)
        if (velocity >= 0f) return
        val impact = -velocity * if (velocity < -120f) 1.045f else 1f
        val impulse = impact / fruit.effectiveInverseMass(normal, point)
        fruit.impulse(normal, point, impulse)
        val tangent = Offset(-normal.y, normal.x)
        val limit = impulse * 0.35f
        val slip = fruit.contactVelocity(point).dot(tangent)
        val friction = (-slip / fruit.effectiveInverseMass(tangent, point)).coerceIn(-limit, limit)
        fruit.impulse(tangent, point, friction)
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
                age = 0f,
                angle = a.angle,
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

private const val OrchardContactMargin = 0.10f
private const val OrchardFieldSide = 65
private const val OrchardContourPoints = 56

private class OrchardOutline(path: Path, radius: Float) {
    private val limit = radius * 1.16f
    private val spacing = limit * 2f / (OrchardFieldSide - 1)
    private val inverseSpacing = 1f / spacing
    private val vertices = FloatArray(OrchardContourPoints * 2)
    val left: Float
    val right: Float
    val bottom: Float

    private val field by lazy {
        val dx = FloatArray(OrchardContourPoints)
        val dy = FloatArray(OrchardContourPoints)
        val inverseLength = FloatArray(OrchardContourPoints)
        for (index in dx.indices) {
            val next = (index + 1) % OrchardContourPoints * 2
            dx[index] = vertices[next] - vertices[index * 2]
            dy[index] = vertices[next + 1] - vertices[index * 2 + 1]
            inverseLength[index] = 1f / (dx[index] * dx[index] + dy[index] * dy[index])
        }
        FloatArray(OrchardFieldSide * OrchardFieldSide) { index ->
            val x = index % OrchardFieldSide * spacing - limit
            val y = index / OrchardFieldSide * spacing - limit
            var inside = false
            var closest = Float.POSITIVE_INFINITY
            for (edge in dx.indices) {
                val px = x - vertices[edge * 2]
                val py = y - vertices[edge * 2 + 1]
                val vx = dx[edge]
                val vy = dy[edge]
                if ((py < 0f) != (py < vy) && px < py * vx / vy) inside = !inside
                val along = ((px * vx + py * vy) * inverseLength[edge]).coerceIn(0f, 1f)
                val nx = px - vx * along
                val ny = py - vy * along
                closest = minOf(closest, nx * nx + ny * ny)
            }
            val distance = sqrt(closest)
            if (inside) -distance else distance
        }
    }

    init {
        val scaled = Path().apply {
            addPath(path)
            transform(Matrix().apply { scale(64f, 64f) })
        }
        val measure = PathMeasure().apply { setPath(scaled, forceClosed = true) }
        var left = Float.POSITIVE_INFINITY
        var right = Float.NEGATIVE_INFINITY
        var bottom = Float.NEGATIVE_INFINITY
        for (index in 0 until OrchardContourPoints) {
            val point = measure.getPosition(measure.length * index / OrchardContourPoints) *
                    (radius * 1.012f / 64f)
            vertices[index * 2] = point.x
            vertices[index * 2 + 1] = point.y
            left = minOf(left, point.x)
            right = maxOf(right, point.x)
            bottom = maxOf(bottom, point.y)
        }
        this.left = left
        this.right = right
        this.bottom = bottom
    }

    fun prepare() {
        distance(Offset.Zero)
    }

    fun vertex(index: Int) = Offset(vertices[index * 2], vertices[index * 2 + 1])

    fun distance(point: Offset): Float {
        val x = (point.x + limit) * inverseSpacing
        val y = (point.y + limit) * inverseSpacing
        if (x < 0f || y < 0f || x >= OrchardFieldSide - 1 || y >= OrchardFieldSide - 1) {
            return Float.POSITIVE_INFINITY
        }
        val col = x.toInt()
        val row = y.toInt()
        val index = row * OrchardFieldSide + col
        val dx = x - col
        val dy = y - row
        val distances = field
        val top = distances[index] + (distances[index + 1] - distances[index]) * dx
        val bottom = distances[index + OrchardFieldSide] +
                (distances[index + OrchardFieldSide + 1] - distances[index + OrchardFieldSide]) * dx
        return top + (bottom - top) * dy
    }

    fun normal(point: Offset): Offset {
        val step = spacing * 0.5f
        val x = Offset(step, 0f)
        val y = Offset(0f, step)
        val delta = Offset(
            distance(point + x) - distance(point - x),
            distance(point + y) - distance(point - y)
        )
        val length = delta.getDistance()
        return if (length.isFinite() && length > 0.0001f) delta / length else Offset.Zero
    }
}

private class OrchardCollider(private val outline: OrchardOutline) {
    private val points = FloatArray(OrchardContourPoints * 2)

    private var angle = Float.NaN
    private var cosine = 1f
    private var sine = 0f
    var left = Offset.Zero
        private set
    var right = Offset.Zero
        private set
    var bottom = Offset.Zero
        private set
    var top = 0f
        private set

    fun vertex(index: Int) = Offset(points[index * 2], points[index * 2 + 1])

    private fun toLocal(point: Offset) =
        Offset(point.x * cosine + point.y * sine, point.y * cosine - point.x * sine)

    private fun toWorld(point: Offset) =
        Offset(point.x * cosine - point.y * sine, point.x * sine + point.y * cosine)

    fun distance(point: Offset): Float = outline.distance(toLocal(point))

    fun normal(point: Offset): Offset = toWorld(outline.normal(toLocal(point)))

    fun rotate(degrees: Float) {
        if (angle == degrees) return
        angle = degrees
        val radians = degrees * 0.017453292f
        cosine = cos(radians)
        sine = sin(radians)
        for (index in 0 until OrchardContourPoints) {
            val point = toWorld(outline.vertex(index))
            points[index * 2] = point.x
            points[index * 2 + 1] = point.y
            if (index == 0 || point.x < left.x) left = point
            if (index == 0 || point.x > right.x) right = point
            if (index == 0 || point.y > bottom.y) bottom = point
            if (index == 0 || point.y < top) top = point.y
        }
    }
}

private class OrchardContact {
    lateinit var a: OrchardFruit
        private set
    lateinit var b: OrchardFruit
        private set
    var delta = Offset.Zero
        private set
    var separation = 0f
        private set
    var normal = Offset.Zero
        private set
    var point = Offset.Zero
        private set

    fun set(a: OrchardFruit, b: OrchardFruit, contact: OrchardContact) {
        this.a = a
        this.b = b
        delta = contact.delta
        separation = contact.separation
        normal = contact.normal
        point = contact.point
    }

    fun find(a: OrchardCollider, b: OrchardCollider, delta: Offset): Boolean {
        val left = maxOf(a.left.x, delta.x + b.left.x) - OrchardContactMargin
        val right = minOf(a.right.x, delta.x + b.right.x) + OrchardContactMargin
        val top = maxOf(a.top, delta.y + b.top) - OrchardContactMargin
        val bottom = minOf(a.bottom.y, delta.y + b.bottom.y) + OrchardContactMargin
        if (right - left < OrchardContactMargin || bottom - top < OrchardContactMargin) return false

        // Check both contours against the other fruit's signed distance field. The actual
        // indentations between berries and at the neck of a pear remain empty.
        var separation = Float.POSITIVE_INFINITY
        var point = Offset.Zero
        var fromA = false
        for (index in 0 until OrchardContourPoints) {
            val vertex = a.vertex(index)
            if (vertex.x !in left..right || vertex.y < top || vertex.y > bottom) continue
            val distance = b.distance(vertex - delta)
            if (distance < separation) {
                separation = distance
                point = vertex
                fromA = true
            }
        }
        for (index in 0 until OrchardContourPoints) {
            val vertex = b.vertex(index) + delta
            if (vertex.x !in left..right || vertex.y < top || vertex.y > bottom) continue
            val distance = a.distance(vertex)
            if (distance < separation) {
                separation = distance
                point = vertex
                fromA = false
            }
        }
        if (separation > OrchardContactMargin) return false
        normal = if (fromA) -b.normal(point - delta) else a.normal(point)
        if (normal == Offset.Zero) {
            val distance = delta.getDistance()
            normal = if (distance > 0.001f) delta / distance else Offset(1f, 0f)
        }
        this.point = point + normal * (if (fromA) separation * 0.5f else -separation * 0.5f)
        this.separation = separation
        this.delta = delta
        return true
    }
}

private fun Offset.dot(other: Offset): Float = x * other.x + y * other.y

private fun Offset.cross(other: Offset): Float = x * other.y - y * other.x