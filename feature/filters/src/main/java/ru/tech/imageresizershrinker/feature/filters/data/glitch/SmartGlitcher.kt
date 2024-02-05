/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.filters.data.glitch

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random
import androidx.compose.ui.graphics.Color as ComposeColor

internal object SmartGlitcher {

    private val leftArray = floatArrayOf(
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f
    )
    private val rightArray = floatArrayOf(
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f
    )
    private val redMatrix = floatArrayOf(
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )
    private val blueMatrix = floatArrayOf(
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )
    private val greenMatrix = floatArrayOf(
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f
    )

    suspend fun noise(image: Bitmap, threshold: Int): Bitmap = withContext(Dispatchers.IO) {
        val w = image.width
        val h = image.height
        val arrayLen = (w * h)

        val intArray = IntArray(arrayLen)
        image.getPixels(intArray, 0, w, 0, 0, w, h)

        val intArrayM = IntArray(intArray.size) { i ->
            intArray[i].or(
                Color.rgb(
                    Random().nextInt(threshold),
                    Random().nextInt(threshold),
                    Random().nextInt(threshold)
                )
            )
        }

        val vv = image.copy(image.config, true)
        vv!!.setPixels(intArrayM, 0, w, 0, 0, w, h)

        vv
    }

    suspend fun shuffle(image: Bitmap): Bitmap = withContext(Dispatchers.IO) {
        generateBitmap(image) { shuffleRow(it) }
    }

    suspend fun pixelSort(image: Bitmap): Bitmap = withContext(Dispatchers.IO) {
        generateBitmap(image) { it.sorted() }
    }

    suspend fun anaglyph(image: Bitmap, percentage: Int): Bitmap = withContext(Dispatchers.IO) {
        val anaglyphPaint = Paint()
        val anaglyphShader = BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader

        val w = image.width
        val h = image.height

        val transX = (percentage)
        val transY = 0

        val colorMatrix = ColorMatrix()

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(0, PorterDuff.Mode.CLEAR)

        //left
        val matrix = Matrix()
        matrix.setTranslate((-transX).toFloat(), (transY).toFloat())
        anaglyphShader.setLocalMatrix(matrix)
        colorMatrix.set(leftArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)

        //right
        val matrix2 = Matrix()
        matrix2.setTranslate((transX).toFloat(), transY.toFloat())
        anaglyphShader.setLocalMatrix(matrix2)
        colorMatrix.set(rightArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)


        c.drawBitmap(image, 0f, 0f, anaglyphPaint)

        bitmap
    }

    private fun shuffleRow(row: List<Int>): List<Int> {
        val offset = Random().nextInt(row.size / 2)
        return List(row.size) { ri -> row[(ri + offset) % row.size] }
    }

    private suspend fun generateBitmap(
        image: Bitmap,
        action: (List<Int>) -> List<Int>
    ): Bitmap = withContext(Dispatchers.IO) {
        val r = List(image.width) { row ->
            List(
                image.height
            ) { col ->
                image.getPixel(row, col)
            }
        }

        val rShuffle = List(image.width) { row -> action(r[row]) }

        val rr = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)

        for (i in 0 until rr.height - 1) {
            for (j in 0 until rr.width - 1) {
                rr.setPixel(j, i, rShuffle[j][i])
            }
        }

        return@withContext rr
    }


    suspend fun monochrome(
        input: Bitmap,
        intensity: Float,
        filterColor: ComposeColor
    ): Bitmap = withContext(Dispatchers.IO) {
        if (intensity > 0f) {
            generateBitmap(input) { pixels ->
                pixels.map {
                    val pixel = ComposeColor(it)
                    val luma = pixel.luminance()
                    val outColor = ComposeColor(
                        red = if (luma < 0.5f) (2f * luma * filterColor.red) else (1f - 2f * (1f - luma) * (1f - filterColor.red)),
                        green = if (luma < 0.5f) (2f * luma * filterColor.green) else (1f - 2f * (1f - luma) * (1f - filterColor.green)),
                        blue = if (luma < 0.5f) (2f * luma * filterColor.blue) else (1f - 2f * (1f - luma) * (1f - filterColor.blue)),
                        alpha = pixel.alpha
                    )

                    outColor.blend(pixel, intensity)
                }
            }
        } else input
    }

    private fun ComposeColor.blend(
        color: ComposeColor,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float
    ) = ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction)

}