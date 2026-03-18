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

@file:Suppress("unused")

package com.t8rin.ascii

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.alpha
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.blue
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.scale
import kotlinx.coroutines.coroutineScope
import kotlin.math.min
import kotlin.math.roundToInt

typealias GradientMap = Map<String, Float>

/**
 * Convert Bitmap into its ASCII equivalent [Bitmap] or [String].
 */
class ASCIIConverter(
    private val fontSize: Float = 18.0f,
    private val reverseLuma: Boolean = false,
    private val mapper: AsciiMapper = AsciiMapper()
) {
    private var columns: Int = 0

    suspend fun convertToAscii(
        bitmap: Bitmap,
    ): String = convert(bitmap) { grid ->
        grid.rotate90AndMirror().joinToString("\n") { row ->
            row.joinToString(" ") {
                it.luma()?.let(mapper::mapToAscii) ?: " "
            }
        }
    }

    suspend fun convertToAsciiBitmap(
        bitmap: Bitmap,
        typeface: Typeface = Typeface.DEFAULT,
        backgroundColor: Int = Color.TRANSPARENT,
        isGrayscale: Boolean = false,
    ): Bitmap = convert(bitmap) { grid ->
        prepareCanvas(
            bitmap = bitmap,
            backgroundColor = backgroundColor,
            typeface = typeface
        ) { paint ->
            grid.forEachIndexed { row, blocks ->
                blocks.forEachIndexed { col, color ->
                    val luma = color.luma()

                    if (isGrayscale && luma != null) {
                        paint.alpha = (luma * 255.0f).toInt()
                        paint.color = Color.GRAY
                    } else {
                        paint.color = color
                    }

                    drawText(
                        luma?.let(mapper::mapToAscii) ?: " ",
                        row * fontSize,
                        col * fontSize,
                        paint
                    )
                }
            }
        }
    }

    private fun Grid.transpose(): Grid =
        first().indices.map { y ->
            indices.map { x -> this[x][y] }
        }

    private fun Grid.rotate90AndMirror(): Grid =
        first().indices.map { x ->
            indices.map { y ->
                this[y][x]
            }.reversed()
        }.map { it.reversed() }

    private inline fun prepareCanvas(
        bitmap: Bitmap,
        backgroundColor: Int,
        typeface: Typeface,
        action: Canvas.(Paint) -> Unit
    ): Bitmap = createBitmap(bitmap.width, bitmap.height).applyCanvas {
        if (backgroundColor != Color.TRANSPARENT) drawColor(backgroundColor)
        action(
            Paint().apply {
                setTypeface(typeface)
                textSize = fontSize
            }
        )
    }

    private suspend inline fun <T> convert(
        bitmap: Bitmap,
        crossinline action: suspend (Grid) -> T
    ) = coroutineScope {
        checkColumns(bitmap)
        action(bitmap.resize().toGrid())
    }

    private fun checkColumns(bitmap: Bitmap) {
        columns = bitmap.toColumns()
        require(columns >= 5) { "Columns count is very small. Font size needs to be reduced" }
    }

    private fun Bitmap.toColumns(): Int =
        if (columns < 5) (width / fontSize).roundToInt() else columns

    private fun Bitmap.toGrid(): Grid {
        require(width > 0 && height > 0) { "Width and height must be positive values." }

        return List(width) { x ->
            List(height) { y ->
                this[x, y]
            }
        }
    }

    private fun Bitmap.resize(): Bitmap {
        if (columns <= 1) return this

        val min = min(height, width)
        val scaleFactor = if (columns > min) min else columns
        val ratio = scaleFactor.toFloat() / width.toFloat()

        return copy(Bitmap.Config.ARGB_8888, false).scale(
            width = scaleFactor,
            height = (ratio * height).toInt()
        )
    }

    private fun Int.luma(): Float? {
        if (alpha <= 0) return null

        val luminance = (0.2126 * (red / 255f))
            .plus(0.7152 * (green / 255f))
            .plus(0.0722 * (blue / 255f))

        return (if (reverseLuma) 1 - luminance else luminance).toFloat()
    }
}

fun interface AsciiMapper {
    fun mapToAscii(luma: Float): String
}

@JvmInline
value class Gradient(val value: String) {

    fun toMap(): GradientMap {
        val length = value.length
        return value.mapIndexed { index, char ->
            char.toString() to (1.0f - index.toFloat() / (length - 1).coerceAtLeast(1))
        }.toMap()
    }

    companion object {
        val NORMAL = Gradient(".:-=+*#%@")
        val NORMAL2 = Gradient(" `.,-~+<>o=*%X@")
        val ARROWS = Gradient("↖←↙↓↘→↗↑")
        val OLD = Gradient("░▒▓█")
        val EXTENDED_HIGH = Gradient(".:-~=+*^><)(][}{#%@")
        val MINIMAL = Gradient(".-+#")
        val MATH = Gradient("π√∞≈≠=÷×-+")
        val NUMERICAL = Gradient("7132546980")
    }
}

fun String.toGradientMap(): GradientMap = Gradient(this).toMap()

fun GradientMap.toMapper(): AsciiMapper = AsciiMapperImpl(map = this)

fun Gradient.toMapper(): AsciiMapper = toMap().toMapper()

fun AsciiMapper(map: GradientMap): AsciiMapper = map.toMapper()

fun AsciiMapper(gradient: Gradient = Gradient.NORMAL): AsciiMapper = gradient.toMapper()


private class AsciiMapperImpl(map: GradientMap) : AsciiMapper {
    private val metrics: List<Pair<String, Float>> = map.toList().sortedByDescending { it.second }

    override fun mapToAscii(
        luma: Float
    ): String = metrics.find {
        luma >= it.second
    }?.first ?: metrics.firstOrNull()?.first.orEmpty()
}

private typealias Grid = List<List<Int>>