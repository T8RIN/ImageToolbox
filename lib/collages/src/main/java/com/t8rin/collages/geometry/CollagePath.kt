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

package com.t8rin.collages.geometry

import android.graphics.Path
import android.graphics.PathMeasure
import kotlin.math.abs

internal fun Path.maximumInset(): Float {
    val measure = PathMeasure(this, true)
    val point = FloatArray(2)
    var doubleArea = 0.0
    var perimeter = 0f
    do {
        val length = measure.length
        if (length <= 0f) continue
        perimeter += length
        measure.getPosTan(0f, point, null)
        val firstX = point[0]
        val firstY = point[1]
        var previousX = firstX
        var previousY = firstY
        for (step in 1..128) {
            measure.getPosTan(length * step / 128, point, null)
            doubleArea += previousX.toDouble() * point[1] - point[0].toDouble() * previousY
            previousX = point[0]
            previousY = point[1]
        }
        doubleArea += previousX.toDouble() * firstY - firstX.toDouble() * previousY
    } while (measure.nextContour())
    return if (perimeter > 0f) (abs(doubleArea) * .375 / perimeter).toFloat() else 0f
}