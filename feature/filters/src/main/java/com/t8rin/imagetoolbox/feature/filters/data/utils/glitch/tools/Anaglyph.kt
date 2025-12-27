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

package com.t8rin.imagetoolbox.feature.filters.data.utils.glitch.tools

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

internal interface Anaglyph {
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

    companion object {
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
    }
}