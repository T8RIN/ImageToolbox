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

package com.t8rin.opencv_tools.auto_straight

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import com.t8rin.opencv_tools.auto_straight.model.Corners
import com.t8rin.opencv_tools.auto_straight.model.StraightenMode
import com.t8rin.opencv_tools.free_corners_crop.FreeCrop
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toBitmap
import com.t8rin.opencv_tools.utils.toMat
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object AutoStraighten : OpenCV() {

    fun process(
        input: Bitmap,
        mode: StraightenMode
    ): Bitmap = when (mode) {
        is StraightenMode.Perspective -> autoPerspective(input = input)

        is StraightenMode.Deskew -> autoDeskew(
            input = input,
            maxSkew = mode.maxSkew,
            allowCrop = mode.allowCrop
        )

        is StraightenMode.Manual -> perspectiveFromPoints(input = input, corners = mode.corners)
    }

    private fun autoDeskew(input: Bitmap, maxSkew: Int, allowCrop: Boolean): Bitmap {
        val srcMat = input.toMat()
        val gray = Mat()
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_BGR2GRAY)
        Photo.fastNlMeansDenoising(gray, gray, 3f)

        val binary = Mat()
        Imgproc.threshold(
            gray, binary, 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU
        )

        val lines = Mat()
        Imgproc.HoughLinesP(
            binary,
            lines,
            1.0,
            Math.PI / 180,
            200,
            srcMat.width() / 12.0,
            srcMat.width() / 150.0
        )

        if (lines.rows() == 0) return input

        val angles = mutableListOf<Double>()
        for (i in 0 until lines.rows()) {
            val l = lines.get(i, 0)
            val x1 = l[0]
            val y1 = l[1]
            val x2 = l[2]
            val y2 = l[3]
            angles += atan2(y2 - y1, x2 - x1)
        }

        val landscape = angles.count { abs(it) > Math.PI / 4 } > angles.size / 2

        val filtered = if (landscape) {
            angles.filter {
                val deg = abs(Math.toDegrees(it))
                deg > (90 - maxSkew) && deg < (90 + maxSkew)
            }
        } else {
            angles.filter { abs(Math.toDegrees(it)) < maxSkew }
        }

        if (filtered.size < 5) return input

        var angleDeg = Math.toDegrees(filtered.median())

        val rotated = Mat()

        if (landscape) {
            angleDeg = if (angleDeg < 0) {
                Core.rotate(srcMat, rotated, Core.ROTATE_90_CLOCKWISE)
                angleDeg + 90
            } else {
                Core.rotate(srcMat, rotated, Core.ROTATE_90_COUNTERCLOCKWISE)
                angleDeg - 90
            }
        } else {
            srcMat.copyTo(rotated)
        }

        val center = Point(rotated.width() / 2.0, rotated.height() / 2.0)
        val rotMat = Imgproc.getRotationMatrix2D(center, angleDeg, 1.0)

        val angleRad = Math.toRadians(angleDeg)
        val absSin = abs(sin(angleRad))
        val absCos = abs(cos(angleRad))
        val newWidth = (rotated.height() * absSin + rotated.width() * absCos).toInt()
        val newHeight = (rotated.height() * absCos + rotated.width() * absSin).toInt()

        rotMat.put(0, 2, rotMat.get(0, 2)[0] + (newWidth / 2.0 - center.x))
        rotMat.put(1, 2, rotMat.get(1, 2)[0] + (newHeight / 2.0 - center.y))

        val rotatedFull = Mat()
        Imgproc.warpAffine(
            rotated,
            rotatedFull,
            rotMat,
            Size(newWidth.toDouble(), newHeight.toDouble()),
            Imgproc.INTER_LINEAR,
            Core.BORDER_REPLICATE,
        )

        return if (allowCrop) {
            val cropRect = getLargestRotatedRect(
                width = rotated.width(),
                height = rotated.height(),
                angle = angleRad
            )

            Mat(rotatedFull, cropRect).clone()
        } else {
            rotatedFull
        }.toBitmap()
    }

    private fun autoPerspective(input: Bitmap): Bitmap {
        val srcMat = input.toMat()
        val gray = Mat()
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_BGR2GRAY)
        Photo.fastNlMeansDenoising(gray, gray, 3f)

        val binary = Mat()
        Imgproc.threshold(
            gray, binary, 0.0, 255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            binary.clone(),
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        val biggest = contours
            .mapNotNull { contour ->
                val approx = MatOfPoint2f()
                val c2f = MatOfPoint2f(*contour.toArray())
                Imgproc.approxPolyDP(c2f, approx, Imgproc.arcLength(c2f, true) * 0.02, true)
                if (approx.total() == 4L && Imgproc.isContourConvex(MatOfPoint(*approx.toArray())))
                    approx
                else null
            }
            .maxByOrNull { Imgproc.contourArea(MatOfPoint(*it.toArray())) }
            ?: return input

        val sorted = sortCorners(biggest.toArray())
        val widthA = distance(sorted[0], sorted[1])
        val widthB = distance(sorted[2], sorted[3])
        val maxWidth = maxOf(widthA, widthB).toInt()

        val heightA = distance(sorted[0], sorted[3])
        val heightB = distance(sorted[1], sorted[2])
        val maxHeight = maxOf(heightA, heightB).toInt()

        val dst = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(maxWidth.toDouble(), 0.0),
            Point(maxWidth.toDouble(), maxHeight.toDouble()),
            Point(0.0, maxHeight.toDouble())
        )

        val transform = Imgproc.getPerspectiveTransform(MatOfPoint2f(*sorted), dst)
        val out = Mat()
        Imgproc.warpPerspective(
            srcMat,
            out,
            transform,
            Size(maxWidth.toDouble(), maxHeight.toDouble())
        )

        return out.toBitmap()
    }

    private fun perspectiveFromPoints(input: Bitmap, corners: Corners): Bitmap {
        val width = input.width
        val height = input.height

        val absPoints = if (corners.isAbsolute) {
            corners.points.map {
                Offset(
                    x = it.x.toFloat(),
                    y = it.y.toFloat()
                )
            }
        } else {
            corners.points.map {
                Offset(
                    x = (it.x * width).toFloat(),
                    y = (it.y * height).toFloat()
                )
            }
        }

        return FreeCrop.crop(
            bitmap = input,
            points = absPoints
        )
    }

    private fun sortCorners(pts: Array<Point>): Array<Point> {
        val sum = pts.sortedBy { it.y + it.x }
        val diff = pts.sortedBy { it.y - it.x }
        return arrayOf(
            sum.first(),
            diff.first(),
            sum.last(),
            diff.last()
        )
    }

    private fun distance(p1: Point, p2: Point): Double =
        sqrt((p1.x - p2.x).pow(2.0) + (p1.y - p2.y).pow(2.0))

    private fun getLargestRotatedRect(width: Int, height: Int, angle: Double): Rect {
        val absSin = abs(sin(angle))
        val absCos = abs(cos(angle))

        val boundWidth = (width * absCos - height * absSin).coerceAtLeast(0.0)
        val boundHeight = (height * absCos - width * absSin).coerceAtLeast(0.0)

        val newWidth = (height * absSin + width * absCos)
        val newHeight = (height * absCos + width * absSin)

        val x = ((newWidth - boundWidth) / 2.0).toInt()
        val y = ((newHeight - boundHeight) / 2.0).toInt()

        return Rect(x, y, boundWidth.toInt(), boundHeight.toInt())
    }

    private fun List<Double>.median(): Double {
        if (isEmpty()) return 0.0
        val sorted = this.sorted()
        return if (size % 2 == 0)
            (sorted[size / 2 - 1] + sorted[size / 2]) / 2
        else
            sorted[size / 2]
    }

}