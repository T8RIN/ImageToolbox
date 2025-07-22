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

package com.t8rin.imagetoolbox.feature.filters.data.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide


internal fun Bitmap.mirror(
    value: Float = 0.5f,
    side: MirrorSide = MirrorSide.LeftToRight
): Bitmap {
    val input = this
    if (value <= 0f || value >= 1f) return input

    val width = input.width
    val height = input.height

    return when (side) {
        MirrorSide.LeftToRight, MirrorSide.RightToLeft -> {
            val centerX = (width * value).toInt().coerceIn(1, width - 1)
            val leftWidth = centerX
            val rightWidth = width - centerX
            val halfWidth = minOf(leftWidth, rightWidth)
            val outputWidth = halfWidth * 2

            val output = createBitmap(outputWidth, height, input.safeConfig)
            val canvas = Canvas(output)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            if (side == MirrorSide.LeftToRight) {
                val leftPart = Bitmap.createBitmap(input, centerX - halfWidth, 0, halfWidth, height)
                val flipped =
                    Bitmap.createBitmap(leftPart, 0, 0, halfWidth, height, Matrix().apply {
                        preScale(-1f, 1f)
                    }, true)
                canvas.drawBitmap(leftPart, 0f, 0f, paint)
                canvas.drawBitmap(flipped, halfWidth.toFloat(), 0f, paint)
            } else {
                val rightPart = Bitmap.createBitmap(input, centerX, 0, halfWidth, height)
                val flipped =
                    Bitmap.createBitmap(rightPart, 0, 0, halfWidth, height, Matrix().apply {
                        preScale(-1f, 1f)
                    }, true)
                canvas.drawBitmap(flipped, 0f, 0f, paint)
                canvas.drawBitmap(rightPart, halfWidth.toFloat(), 0f, paint)
            }

            output
        }

        MirrorSide.TopToBottom, MirrorSide.BottomToTop -> {
            val centerY = (height * value).toInt().coerceIn(1, height - 1)
            val topHeight = centerY
            val bottomHeight = height - centerY
            val halfHeight = minOf(topHeight, bottomHeight)
            val outputHeight = halfHeight * 2

            val output = createBitmap(width, outputHeight, input.safeConfig)
            val canvas = Canvas(output)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            if (side == MirrorSide.TopToBottom) {
                val topPart = Bitmap.createBitmap(input, 0, centerY - halfHeight, width, halfHeight)
                val flipped = Bitmap.createBitmap(topPart, 0, 0, width, halfHeight, Matrix().apply {
                    preScale(1f, -1f)
                }, true)
                canvas.drawBitmap(topPart, 0f, 0f, paint)
                canvas.drawBitmap(flipped, 0f, halfHeight.toFloat(), paint)
            } else {
                val bottomPart = Bitmap.createBitmap(input, 0, centerY, width, halfHeight)
                val flipped =
                    Bitmap.createBitmap(bottomPart, 0, 0, width, halfHeight, Matrix().apply {
                        preScale(1f, -1f)
                    }, true)
                canvas.drawBitmap(flipped, 0f, 0f, paint)
                canvas.drawBitmap(bottomPart, 0f, halfHeight.toFloat(), paint)
            }

            output
        }
    }
}
