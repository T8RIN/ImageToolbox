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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.core.graphics.withSave
import kotlin.math.atan2

fun Canvas.drawRepeatedTextOnPath(
    text: String,
    path: Path,
    paint: Paint,
    interval: Float = 0f
) {
    val pathMeasure = PathMeasure(path, false)
    val pathLength = pathMeasure.length

    val textWidth = paint.measureText(text)

    val fullRepeats = (pathLength / (textWidth + interval)).toInt()

    val remainingLength = pathLength - fullRepeats * (textWidth + interval)

    var distance = 0f

    repeat(fullRepeats) {
        drawTextOnPath(text, path, distance, 0f, paint)
        distance += (textWidth + interval)
    }

    if (remainingLength > 0f) {
        val ratio = (textWidth + interval - (remainingLength)) / (textWidth + interval)
        val endOffset = (text.length - (text.length * ratio).toInt()).coerceAtLeast(0)
        drawTextOnPath(text.substring(0, endOffset), path, distance, 0f, paint)
    }
}

fun Canvas.drawRepeatedBitmapOnPath(
    bitmap: Bitmap,
    path: Path,
    paint: Paint,
    interval: Float = 0f
) {
    val pathMeasure = PathMeasure(path, false)
    val pathLength = pathMeasure.length

    val bitmapWidth = bitmap.width.toFloat()
    val bitmapHeight = bitmap.height.toFloat()

    var distance = 0f
    val matrix = Matrix()

    while (distance < pathLength) {
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        pathMeasure.getPosTan(distance, pos, tan)

        val degree = Math.toDegrees(atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()

        withSave {
            translate(pos[0], pos[1])
            rotate(degree)

            matrix.reset()
            matrix.postTranslate(-bitmapWidth / 2, -bitmapHeight / 2)
            matrix.postRotate(degree)
            matrix.postTranslate(bitmapWidth / 2, bitmapHeight / 2)
            drawBitmap(bitmap, matrix, paint)
        }

        if (interval < 0 && distance + bitmapWidth < 0) break
        else {
            distance += (bitmapWidth + interval)
        }
    }
}