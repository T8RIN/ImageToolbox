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

package com.t8rin.imagetoolbox.core.games.utils

import android.util.LruCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun DrawScope.pixelCircle(
    color: Color,
    radius: Float = size.minDimension / 2f,
    center: Offset = this.center,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
    blendMode: BlendMode = BlendMode.SrcOver
) {
    if (radius <= 0f || !radius.isFinite()) return
    val stroke = if (style is Stroke) style.width else 0f
    val path = pixelCircleContour(radius, stroke)
    translate(left = center.x, top = center.y) {
        drawPath(path = path, color = color, alpha = alpha, blendMode = blendMode)
    }
}

private fun pixelCircleContour(radius: Float, stroke: Float): Path {
    val key = PixelCircleKey(radius, stroke)
    return PixelCircles[key] ?: pixelCirclePath(radius, stroke).also { PixelCircles.put(key, it) }
}

private data class PixelCircleKey(val radius: Float, val stroke: Float)

private val PixelCircles = LruCache<PixelCircleKey, Path>(128)

private fun pixelCirclePath(radius: Float, stroke: Float): Path {
    val path = Path()
    val rows = radius.roundToInt().coerceIn(2, 64)
    val unit = radius / rows

    fun contour(count: Int) {
        if (count <= 0) return
        val r = count * unit
        val widths = FloatArray(count * 2) { row ->
            val dy = (row - count + 0.5f) * unit
            (sqrt((r * r - dy * dy).coerceAtLeast(0f)) / unit)
                .roundToInt().coerceIn(1, count) * unit
        }
        path.moveTo(-widths.first(), -r)
        path.lineTo(widths.first(), -r)
        for (row in widths.indices) {
            val bottom = (row - count + 1) * unit
            path.lineTo(widths[row], bottom)
            if (row < widths.lastIndex) path.lineTo(widths[row + 1], bottom)
        }
        path.lineTo(-widths.last(), r)
        for (row in widths.indices.reversed()) {
            val top = (row - count) * unit
            path.lineTo(-widths[row], top)
            if (row > 0) path.lineTo(-widths[row - 1], top)
        }
        path.close()
    }

    contour(rows)
    if (stroke > 0f) {
        path.fillType = PathFillType.EvenOdd
        contour(rows - (stroke / unit).roundToInt().coerceAtLeast(1))
    }
    return path
}

internal fun DrawScope.pixelPanel(
    color: Color,
    ink: Color,
    topLeft: Offset,
    size: Size,
    inset: Boolean = false
) {
    if (size.width <= 0f || size.height <= 0f) return
    val cut = minOf(2f, size.minDimension / 5f)
    val width = size.width
    val height = size.height
    val path = Path().apply {
        moveTo(cut, 0f)
        lineTo(width - cut, 0f)
        lineTo(width, cut)
        lineTo(width, height - cut)
        lineTo(width - cut, height)
        lineTo(cut, height)
        lineTo(0f, height - cut)
        lineTo(0f, cut)
        close()
    }
    translate(left = topLeft.x, top = topLeft.y) {
        drawPath(path, color)
        val light = ink.copy(alpha = ink.alpha * if (inset) 0.12f else 0.34f)
        val shade = ink.copy(alpha = ink.alpha * if (inset) 0.34f else 0.12f)
        path.rewind()
        path.moveTo(cut, 1.5f)
        path.lineTo(width - cut, 1.5f)
        path.moveTo(1.5f, cut)
        path.lineTo(1.5f, height - cut)
        drawPath(path, light, style = Stroke(1f))
        path.rewind()
        path.moveTo(cut, height - 1.5f)
        path.lineTo(width - cut, height - 1.5f)
        path.moveTo(width - 1.5f, cut)
        path.lineTo(width - 1.5f, height - cut)
        drawPath(path, shade, style = Stroke(1f))
    }
}

internal fun DrawScope.pixelGem(color: Color, light: Color, center: Offset, radius: Float = 8f) {
    scale(radius / 8f, center) {
        drawArcadeSprite(PixelGemBody, color, center.x - 7f, center.y - 8f)
        drawArcadeSprite(PixelGemShade, lerp(color, light, 0.16f), center.x - 7f, center.y - 8f)
        drawArcadeSprite(PixelGemGlint, lerp(color, light, 0.68f), center.x - 7f, center.y - 8f)
    }
}

private val PixelGemBody = ArcadeSprite(
    width = 14, height = 16,
    data = "M6 0H8V2H10V4H12V6H14V10H12V12H10V14H8V16H6V14H4V12H2V10H0V6H2V4H4V2H6Z"
)
private val PixelGemShade = ArcadeSprite(
    width = 14, height = 16,
    data = "M10 6H12V10H10V12H8V14H6V12H8V10H10Z"
)
private val PixelGemGlint = ArcadeSprite(
    width = 14, height = 16,
    data = "M6 2H8V4H6V6H4V8H2V6H4V4H6Z"
)

internal fun DrawScope.reliefSprite(
    sprite: ArcadeSprite,
    color: Color,
    shade: Color,
    light: Color,
    x: Float,
    y: Float
) {
    val edges = SpriteEdges[sprite] ?: run {
        val inset = Path().apply { addPath(sprite.path, Offset(2f, 2f)) }
        val outset = Path().apply { addPath(sprite.path, Offset(-2f, -2f)) }
        (Path.combine(PathOperation.Difference, sprite.path, inset) to
                Path.combine(PathOperation.Difference, sprite.path, outset)).also {
            SpriteEdges.put(
                sprite,
                it
            )
        }
    }
    translate(x, y) {
        drawPath(sprite.path, color)
        drawPath(edges.second, shade)
        drawPath(edges.first, light)
    }
}

private val SpriteEdges = LruCache<ArcadeSprite, Pair<Path, Path>>(64)

internal fun DrawScope.pixelAsteroid(
    color: Color,
    ink: Color,
    light: Color,
    center: Offset,
    radius: Float,
    angle: Float = 0f
) {
    translate(center.x, center.y) {
        rotate(angle, Offset.Zero) {
            scale(radius / 24f, Offset.Zero) {
                translate(-24f, -24f) {
                    reliefSprite(
                        AsteroidBody,
                        color,
                        lerp(color, ink, 0.38f),
                        lerp(color, light, 0.24f),
                        0f,
                        0f
                    )
                    drawPath(AsteroidFacet, lerp(color, ink, 0.2f))
                    for ((point, r) in listOf(
                        Offset(16f, 17f) to 5f,
                        Offset(32f, 28f) to 4f,
                        Offset(17f, 35f) to 2f
                    )) {
                        pixelCircle(lerp(color, light, 0.28f), r, point + Offset(0f, 1.5f))
                        pixelCircle(lerp(color, ink, 0.42f), r, point)
                        pixelCircle(lerp(color, ink, 0.18f), r * 0.58f, point + Offset(0.5f, 0.5f))
                    }
                }
            }
        }
    }
}

private val AsteroidBody = ArcadeSprite(
    width = 48, height = 48,
    data = "M14 2H30V5H37V10H43V19H46V30H42V37H35V43H24V46H13V42H7V35H3V24H5V14H9V7H14Z"
)
private val AsteroidFacet = Path.combine(
    PathOperation.Intersect,
    AsteroidBody.path,
    ArcadeSprite(
        width = 48, height = 48,
        data = "M34 9H37V12H41V23H37V30H32V35H26V42H17V44H24V46H35V43H42V37H46V30H43V19H41V12H37V10H34Z"
    ).path
)