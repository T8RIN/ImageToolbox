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

package com.websitebeaver.documentscanner.extensions

import android.graphics.PointF
import org.opencv.core.Point
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * converts an OpenCV point to Android point
 *
 * @return Android point
 */
fun Point.toPointF(): PointF {
    return PointF(x.toFloat(), y.toFloat())
}

/**
 * calculates the distance between 2 OpenCV points
 *
 * @param point the 2nd OpenCV point
 * @return the distance between this point and the 2nd point
 */
fun Point.distance(point: Point): Double {
    return sqrt((point.x - x).pow(2) + (point.y - y).pow(2))
}

/**
 * offset the OpenCV point by (dx, dy)
 *
 * @param dx horizontal offset
 * @param dy vertical offset
 * @return the OpenCV point after moving it (dx, dy)
 */
fun Point.move(dx: Double, dy: Double): Point {
    return Point(x + dx, y + dy)
}

/**
 * converts an Android point to OpenCV point
 *
 * @return OpenCV point
 */
fun PointF.toOpenCVPoint(): Point {
    return Point(x.toDouble(), y.toDouble())
}

/**
 * multiply an Android point by magnitude
 *
 * @return Android point after multiplying by magnitude
 */
fun PointF.multiply(magnitude: Float): PointF {
    return PointF(magnitude * x, magnitude * y)
}

/**
 * offset the Android point by (dx, dy)
 *
 * @param dx horizontal offset
 * @param dy vertical offset
 * @return the Android point after moving it (dx, dy)
 */
fun PointF.move(dx: Float, dy: Float): PointF {
    return PointF(x + dx, y + dy)
}

/**
 * calculates the distance between 2 Android points
 *
 * @param point the 2nd Android point
 * @return the distance between this point and the 2nd point
 */
fun PointF.distance(point: PointF): Float {
    return sqrt((point.x - x).pow(2) + (point.y - y).pow(2))
}