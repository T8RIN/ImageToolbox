/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.filters.data.utils.glitch

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.coroutineScope
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

internal object Glitcher {

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

    suspend fun anaglyph(
        image: Bitmap,
        percentage: Int
    ): Bitmap = coroutineScope {
        val anaglyphPaint = Paint()
        val anaglyphShader = BitmapShader(image, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader

        val w = image.width
        val h = image.height

        val transX = (percentage)
        val transY = 0

        val colorMatrix = ColorMatrix()

        createBitmap(
            width = w,
            height = h
        ).applyCanvas {
            drawColor(0, PorterDuff.Mode.CLEAR)

            //left
            val matrix = Matrix()
            matrix.setTranslate((-transX).toFloat(), (transY).toFloat())
            anaglyphShader.setLocalMatrix(matrix)
            colorMatrix.set(leftArray)
            anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)

            //right
            val matrix2 = Matrix()
            matrix2.setTranslate((transX).toFloat(), transY.toFloat())
            anaglyphShader.setLocalMatrix(matrix2)
            colorMatrix.set(rightArray)
            anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)


            drawBitmap(image, 0f, 0f, anaglyphPaint)
        }
    }

    suspend fun glitch(
        bitmap: ByteArray,
        amount: Int = 20,
        seed: Int = 15,
        iterations: Int = 9
    ): ByteArray = coroutineScope {
        val imageByteArray = bitmap.clone()
        val jpgHeaderLength = getJpegHeaderSize(imageByteArray)
        repeat(
            times = iterations
        ) {
            glitchJpegBytes(
                pos = it,
                imageByteArray = imageByteArray,
                jpgHeaderLength = jpgHeaderLength,
                amount = amount,
                seed = seed,
                iterations = iterations
            )
        }
        imageByteArray
    }

    suspend fun glitchVariant(
        src: Bitmap,
        iterations: Int = 30,
        maxOffset: Float = 0.1f,   // 0f..1f
        channelShift: Float = 0.02f // 0f..1f
    ): Bitmap = coroutineScope {
        val w = src.width
        val h = src.height
        val result = src.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(w * h)
        val out = IntArray(w * h)
        result.getPixels(pixels, 0, w, 0, 0, w, h)
        pixels.copyInto(out)

        val maxOffset = ((w / 2) * maxOffset).roundToInt()
        val channelShift = ((w / 10) * channelShift).roundToInt()

        repeat(iterations) {
            val y = Random.nextInt(h)
            val height = Random.nextInt(1, 6)
            val offset = Random.nextInt(-maxOffset, maxOffset)

            for (dy in 0 until height) {
                val row = y + dy
                if (row !in 0 until h) continue

                for (x in 0 until w) {
                    val srcX = (x + offset).coerceIn(0, w - 1)
                    out[row * w + x] = pixels[row * w + srcX]
                }
            }
        }

        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = y * w + x

                val rSrc = ((x + channelShift).coerceIn(0, w - 1)) + y * w
                val bSrc = ((x - channelShift).coerceIn(0, w - 1)) + y * w

                val r = (pixels[rSrc] shr 16) and 0xFF
                val g = (out[i] shr 8) and 0xFF
                val b = pixels[bSrc] and 0xFF
                val a = (out[i] ushr 24) and 0xFF

                out[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        result.setPixels(out, 0, w, 0, 0, w, h)

        result
    }


    private fun glitchJpegBytes(
        pos: Int,
        imageByteArray: ByteArray,
        jpgHeaderLength: Int,
        amount: Int = 20,
        seed: Int = 15,
        iterations: Int = 9
    ) {
        val maxIndex = imageByteArray.size - jpgHeaderLength - 4f
        val pxMin = maxIndex / iterations * pos
        val pxMax = maxIndex / iterations * (pos + 1)
        val delta = pxMax - pxMin
        var pxIndex = pxMin + delta * seed / 100f
        if (pxIndex > maxIndex) {
            pxIndex = maxIndex
        }
        val index = floor((jpgHeaderLength + pxIndex).toDouble()).toInt()
        imageByteArray[index] = floor((amount / 100f * 256f).toDouble()).toInt().toByte()
    }

    private fun getJpegHeaderSize(imageByteArray: ByteArray): Int {
        var result = 417
        var i = 0
        val len = imageByteArray.size
        while (i < len) {
            if (imageByteArray[i].toInt() == 255 && imageByteArray[i + 1].toInt() == 218) {
                result = i + 2
                break
            }
            i++
        }
        return result
    }

}