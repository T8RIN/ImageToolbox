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

package com.t8rin.opencv_tools.photo_filters

import android.graphics.Bitmap
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects.structureTensor
import org.opencv.core.CvType
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object DrawingEffects {

    suspend fun directionalEmbroidery(
        bitmap: Bitmap,
        stitchLength: Float,
        density: Float,
        thickness: Float,
        relief: Float,
        colorLevels: Int,
        seed: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength, maxSide = 2000) { rgb ->
        val width = rgb.cols()
        val height = rgb.rows()
        val length = max(3.0, stitchLength.coerceIn(3f, 45f) * rgb.referenceScale())
        val pitch = max(2.0, length * (1.25 - density.coerceIn(0.1f, 1f) * 0.75))
        val thread = max(1, (length * thickness.coerceIn(0.08f, 0.55f)).roundToInt())
        val fibers = max(1.5, pitch * 0.28)
        val heightScale = relief.coerceIn(0f, 1f)
        val quantize = colorLevels.coerceIn(2, 16) - 1
        val tensor = structureTensor(rgb, max(0.6, pitch * 0.75))
        val result = mat(height, width, CvType.CV_8UC3)
        val random = Random(seed)
        val row = ByteArray(width * 3)
        for (y in 0 until height) {
            checkpoint()
            val vertical = cos(2 * PI * y / fibers)
            for (x in 0 until width) {
                val grain = vertical * cos(2 * PI * x / fibers) * 5.0 + random.nextInt(-3, 4)
                row[x * 3] = (224 + grain).roundToInt().toByte()
                row[x * 3 + 1] = (212 + grain).roundToInt().toByte()
                row[x * 3 + 2] = (189 + grain).roundToInt().toByte()
            }
            result.put(y, 0, row)
        }
        val step = pitch.roundToInt().coerceAtLeast(2)
        val color = ByteArray(3)
        val value = FloatArray(1)
        for (y in 0 until height step step) {
            checkpoint()
            for (x in 0 until width step step) {
                val centerX = (x + pitch * (0.15 + 0.7 * random.nextDouble())).roundToInt()
                    .coerceAtMost(width - 1)
                val centerY = (y + pitch * (0.15 + 0.7 * random.nextDouble())).roundToInt()
                    .coerceAtMost(height - 1)
                tensor[0].get(centerY, centerX, value)
                val a = value[0]
                tensor[1].get(centerY, centerX, value)
                val b = value[0]
                tensor[2].get(centerY, centerX, value)
                val c = value[0]
                val coherence =
                    (hypot((a - c).toDouble(), 2.0 * b) / (a + c + 1e-6)).coerceIn(0.0, 1.0)
                val angle = if (coherence < 0.06) PI * (0.23 + random.nextDouble() * 0.10)
                else 0.5 * atan2(2.0 * b, (a - c).toDouble()) + PI / 2
                val half = length * (0.38 + 0.2 * random.nextDouble())
                val dx = cos(angle) * half
                val dy = sin(angle) * half
                val start = Point(centerX - dx, centerY - dy)
                val end = Point(centerX + dx, centerY + dy)
                rgb.get(centerY, centerX, color)
                fun shade(index: Int): Double {
                    val normalized = (color[index].toInt() and 255) / 255.0
                    return (normalized * quantize).roundToInt() * 235.0 / quantize + 10.0
                }

                val r = shade(0)
                val g = shade(1)
                val bl = shade(2)
                val shadow = 0.62 - heightScale * 0.2
                if (heightScale > 0f) {
                    Imgproc.line(
                        result,
                        Point(start.x + 0.6, start.y + 1.0),
                        Point(end.x + 0.6, end.y + 1.0),
                        Scalar(r * shadow, g * shadow, bl * shadow),
                        thread + 1,
                        Imgproc.LINE_AA,
                        0
                    )
                }
                Imgproc.line(result, start, end, Scalar(r, g, bl), thread, Imgproc.LINE_AA, 0)
                if (thread > 1 && heightScale > 0f) {
                    val shine = heightScale * 27
                    Imgproc.line(
                        result, Point(start.x + dx * 0.18, start.y + dy * 0.18 - 0.55),
                        Point(end.x - dx * 0.18, end.y - dy * 0.18 - 0.55),
                        Scalar(r + shine, g + shine, bl + shine), 1, Imgproc.LINE_AA, 0
                    )
                }
            }
        }
        result
    }

    suspend fun singleLineDrawing(
        bitmap: Bitmap,
        pointCount: Int,
        densityCurve: Float,
        lineWidth: Float,
        ink: Int,
        paper: Int,
        optimization: Int,
        seed: Int,
        strength: Float
    ): Bitmap = PhotoFilterRenderer.render(bitmap, strength) { rgb ->
        val longest = max(rgb.cols(), rgb.rows())
        val scale = (480.0 / longest).coerceAtMost(1.0)
        val sample = if (scale == 1.0) gray(rgb) else resize(
            gray(rgb),
            (rgb.cols() * scale).roundToInt().coerceAtLeast(1),
            (rgb.rows() * scale).roundToInt().coerceAtLeast(1), Imgproc.INTER_AREA
        )
        val width = sample.cols()
        val height = sample.rows()
        val values = ByteArray(width * height)
        sample.get(0, 0, values)
        val count = min(pointCount.coerceIn(60, 3500), values.size)
        val curve = densityCurve.coerceIn(0.35f, 4f).toDouble()
        val weights = DoubleArray(256) { (1.0 - it / 255.0).pow(curve) }
        val cumulative = DoubleArray(values.size)
        var total = 0.0
        for (i in cumulative.indices) {
            if (i and 4095 == 0) checkpoint()
            total += weights[values[i].toInt() and 255]
            cumulative[i] = total
        }
        val result = mat(rgb.rows(), rgb.cols(), CvType.CV_8UC3)
        result.setTo(rgbColor(paper))
        if (total < 1e-6 || count < 2) return@render result
        val random = Random(seed)
        // Stratified sampling reduces empty bands without placing points in pure highlights.
        val points = Array(count) { i ->
            val position = (i + random.nextDouble()) / count * total
            var index = cumulative.binarySearch(position)
            if (index < 0) index = -index - 1
            index = index.coerceIn(0, values.lastIndex)
            Point(
                (index % width + random.nextDouble()) * rgb.cols() / width,
                (index / width + random.nextDouble()) * rgb.rows() / height
            )
        }

        fun distance(a: Point, b: Point): Double {
            val x = a.x - b.x
            val y = a.y - b.y
            return x * x + y * y
        }
        for (i in 1 until points.size) {
            checkpoint()
            val from = points[i - 1]
            var closest = i
            var score = distance(from, points[i])
            for (j in i + 1 until points.size) {
                val candidate = distance(from, points[j])
                if (candidate < score) {
                    closest = j
                    score = candidate
                }
            }
            val old = points[i]
            points[i] = points[closest]
            points[closest] = old
        }
        repeat(optimization.coerceIn(0, 4)) {
            for (i in 0 until points.size - 3) {
                checkpoint()
                for (j in i + 2 until min(i + 30, points.lastIndex)) {
                    val a = points[i]
                    val b = points[i + 1]
                    val c = points[j]
                    val d = points[j + 1]
                    if (hypot(a.x - c.x, a.y - c.y) + hypot(b.x - d.x, b.y - d.y) <
                        hypot(a.x - b.x, a.y - b.y) + hypot(c.x - d.x, c.y - d.y)
                    ) {
                        var left = i + 1
                        var right = j
                        while (left < right) {
                            val old = points[left]
                            points[left++] = points[right]
                            points[right--] = old
                        }
                    }
                }
            }
        }
        val path = MatOfPoint(*points)
        try {
            Imgproc.polylines(
                result, listOf(path), false, rgbColor(ink),
                rgb.radius(lineWidth.coerceIn(0.4f, 5f)), Imgproc.LINE_AA, 0
            )
        } finally {
            path.release()
        }
        result
    }

    private fun rgbColor(color: Int): Scalar = Scalar(
        ((color ushr 16) and 255).toDouble(),
        ((color ushr 8) and 255).toDouble(),
        (color and 255).toDouble()
    )
}
