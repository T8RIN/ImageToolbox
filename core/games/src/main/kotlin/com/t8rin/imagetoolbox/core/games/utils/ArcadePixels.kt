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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import kotlin.math.abs
import kotlin.math.ceil
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

internal fun DrawScope.clipPixelCircle(radius: Float, center: Offset, block: DrawScope.() -> Unit) {
    if (radius <= 0f || !radius.isFinite()) return
    val path = pixelCircleContour(radius, 0f)
    translate(left = center.x, top = center.y) {
        clipPath(path) {
            translate(left = -center.x, top = -center.y, block = block)
        }
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
    val unit = (radius / 8f).roundToInt().coerceIn(1, 2).toFloat()
    val rows = ceil(radius / unit).toInt()
    val innerRadius =
        if (stroke > 0f) (radius - stroke.coerceAtLeast(unit)).coerceAtLeast(0f) else 0f

    fun strip(left: Float, top: Float, width: Float) {
        if (width > 0f) path.addRect(Rect(left, top, left + width, top + unit))
    }
    for (row in -rows until rows) {
        val dy = (row + 0.5f) * unit
        if (abs(dy) >= radius) continue
        val half = (sqrt(radius * radius - dy * dy) / unit).roundToInt().coerceAtLeast(1) * unit
        val inner = if (abs(dy) < innerRadius) {
            (sqrt(innerRadius * innerRadius - dy * dy) / unit).roundToInt() * unit
        } else 0f
        if (inner > 0f) {
            strip(-half, row * unit, half - inner)
            strip(inner, row * unit, half - inner)
        } else strip(-half, row * unit, half * 2f)
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