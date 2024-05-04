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

package ru.tech.imageresizershrinker.feature.draw.presentation.components

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure

fun Canvas.drawRepeatedTextOnPath(
    text: String,
    path: Path,
    paint: Paint,
    intervalMultiplier: Float = 1f
) {
    val pathMeasure = PathMeasure(path, false)
    val pathLength = pathMeasure.length

    val textWidth = paint.measureText(text)

    val fullRepeats = (pathLength / textWidth).toInt()

    val remainingLength = pathLength - fullRepeats * textWidth

    var distance = 0f

    repeat(fullRepeats - 1) {
        drawTextOnPath(text, path, distance, 0f, paint)
        distance += textWidth * intervalMultiplier
    }

    if (remainingLength > 0f) {
        //drawTextOnPath(text, path, distance, 0f, paint)
    }
}