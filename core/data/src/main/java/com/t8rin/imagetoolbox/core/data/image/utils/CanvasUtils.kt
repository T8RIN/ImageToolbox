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

package com.t8rin.imagetoolbox.core.data.image.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.t8rin.imagetoolbox.core.domain.model.Position
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

fun Canvas.drawBitmap(
    bitmap: Bitmap,
    position: Position,
    paint: Paint? = null,
    horizontalPadding: Float = 0f,
    verticalPadding: Float = 0f
) {
    val left = when (position) {
        Position.TopLeft, Position.CenterLeft, Position.BottomLeft -> horizontalPadding
        Position.TopCenter, Position.Center, Position.BottomCenter -> (width - bitmap.width) / 2f
        Position.TopRight, Position.CenterRight, Position.BottomRight -> (width - bitmap.width - horizontalPadding)
    }

    val top = when (position) {
        Position.TopLeft, Position.TopCenter, Position.TopRight -> verticalPadding
        Position.CenterLeft, Position.Center, Position.CenterRight -> (height - bitmap.height) / 2f
        Position.BottomLeft, Position.BottomCenter, Position.BottomRight -> (height - bitmap.height - verticalPadding)
    }

    drawBitmap(
        bitmap,
        null,
        RectF(
            left,
            top,
            bitmap.width + left,
            bitmap.height + top
        ),
        paint
    )
}

fun Canvas.drawBitmap(
    bitmap: Bitmap,
    left: Float = 0f,
    top: Float = 0f
) = drawBitmap(bitmap, left, top, Paint(Paint.ANTI_ALIAS_FLAG))

fun Canvas.drawBitmap(
    bitmap: Bitmap,
    left: Float = 0f,
    top: Float = 0f,
    paint: Paint
) = drawBitmap(bitmap, left, top, paint)

suspend fun Bitmap.healAlpha(
    original: Bitmap
): Bitmap = coroutineScope {
    val processed = this@healAlpha

    val originalRow = IntArray(original.width)
    val processedRow = IntArray(original.width)
    val result = copy(Bitmap.Config.ARGB_8888, true)
    try {
        for (y in 0 until original.height) {
            ensureActive()
            original.getPixels(originalRow, 0, original.width, 0, y, original.width, 1)
            if (originalRow.all { it ushr 24 == 255 }) continue
            processed.getPixels(processedRow, 0, original.width, 0, y, original.width, 1)
            for (x in originalRow.indices) {
                val alpha = originalRow[x] ushr 24
                if (alpha < 255) {
                    processedRow[x] = (alpha shl 24) or (processedRow[x] and 0x00FFFFFF)
                }
            }
            result.setPixels(processedRow, 0, original.width, 0, y, original.width, 1)
        }
        result
    } catch (throwable: Throwable) {
        result.recycle()
        throw throwable
    }
}