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

package com.t8rin.opencv_tools.free_corners_crop.model

import android.graphics.PointF
import org.opencv.core.Point
import kotlin.math.pow
import kotlin.math.sqrt

class Quad(
    val topLeftCorner: PointF,
    val topRightCorner: PointF,
    val bottomRightCorner: PointF,
    val bottomLeftCorner: PointF
)

fun PointF.toOpenCVPoint(): Point {
    return Point(x.toDouble(), y.toDouble())
}

fun Point.distance(point: Point): Double {
    return sqrt((point.x - x).pow(2) + (point.y - y).pow(2))
}