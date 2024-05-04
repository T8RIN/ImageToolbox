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

package ru.tech.imageresizershrinker.feature.draw.data

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import kotlin.math.atan2

fun Canvas.drawRepeatedTextOnPath(
    text: String,
    path: Path,
    paint: Paint,
    intervalMultiplier: Float = 1f
) {
    val pathMeasure = PathMeasure(path, false)
    val pathLength = pathMeasure.length

    val textLength = paint.measureText(text)
    val interval = textLength * intervalMultiplier

    var distance = 0f

    while (distance < pathLength) {
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        pathMeasure.getPosTan(distance, pos, tan)

        val degree = Math.toDegrees(atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()

        save()
        translate(pos[0], pos[1])
        rotate(degree)
        drawText(text, -textLength / 2, 0f, paint)
        restore()

        distance += interval
    }
}
