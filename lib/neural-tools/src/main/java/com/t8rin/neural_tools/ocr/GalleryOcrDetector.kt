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

package com.t8rin.neural_tools.ocr

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.t8rin.neural_tools.runCancellable
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.geometry.Geometry
import org.opencv.imgproc.Imgproc
import java.nio.FloatBuffer
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.round

internal class GalleryOcrDetector(
    private val session: OrtSession,
    private val config: GalleryOcrModels.Config
) {
    suspend fun detect(bgr: Mat): List<Region> {
        val scale = minOf(1.0, config.longSide.toDouble() / maxOf(bgr.cols(), bgr.rows()))
        val width = round(bgr.cols() * scale).toInt().coerceAtLeast(1)
        val height = round(bgr.rows() * scale).toInt().coerceAtLeast(1)
        val paddedWidth = (width + 31) / 32 * 32
        val paddedHeight = (height + 31) / 32 * 32
        val area = paddedWidth * paddedHeight
        val input = FloatArray(area * 3)
        val resized = Mat()
        try {
            Imgproc.resize(
                bgr,
                resized,
                Size(width.toDouble(), height.toDouble()),
                0.0,
                0.0,
                Imgproc.INTER_AREA
            )
            val pixels = ByteArray(width * height * 3)
            resized.get(0, 0, pixels)
            for (channel in 0..2) {
                val offset = channel * area
                val normalized = NormalizedPixels[channel]
                input.fill(normalized[255], offset, offset + area)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val value = pixels[(y * width + x) * 3 + channel].toInt() and 255
                        input[offset + y * paddedWidth + x] = normalized[value]
                    }
                }
            }
        } finally {
            resized.release()
        }
        return OnnxTensor.createTensor(
            OrtEnvironment.getEnvironment(), FloatBuffer.wrap(input),
            longArrayOf(1, 3, paddedHeight.toLong(), paddedWidth.toLong())
        ).use { tensor ->
            session.runCancellable(mapOf("x" to tensor)).use { result ->
                val output = (result[0] as OnnxTensor).floatBuffer
                val probabilities = FloatArray(area)
                output.get(probabilities)
                regions(probabilities, width, height, paddedWidth, bgr.cols(), bgr.rows(), scale)
            }
        }
    }

    private fun regions(
        probabilities: FloatArray,
        width: Int,
        height: Int,
        stride: Int,
        imageWidth: Int,
        imageHeight: Int,
        scale: Double
    ): List<Region> {
        val paddedHeight = probabilities.size / stride
        val scores = Mat(paddedHeight, stride, CvType.CV_32FC1)
        val mask = Mat(paddedHeight, stride, CvType.CV_8UC1)
        val hierarchy = Mat()
        val contours = mutableListOf<MatOfPoint>()
        try {
            scores.put(0, 0, probabilities)
            mask.put(
                0, 0, ByteArray(probabilities.size) {
                    if (it / stride < height && it % stride < width &&
                        probabilities[it] > config.maskThreshold
                    ) 1 else 0
                }
            )
            Imgproc.findContours(
                mask,
                contours,
                hierarchy,
                Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE
            )
            return contours.take(1000)
                .mapNotNull { region(it, scores, imageWidth, imageHeight, scale) }
        } finally {
            contours.forEach(Mat::release)
            scores.release()
            mask.release()
            hierarchy.release()
        }
    }

    private fun region(
        contour: MatOfPoint,
        scores: Mat,
        imageWidth: Int,
        imageHeight: Int,
        scale: Double
    ): Region? {
        val polygon = MatOfPoint2f(*contour.toArray())
        try {
            val rect = Geometry.minAreaRect(polygon)
            if (minOf(rect.size.width, rect.size.height) < 3 ||
                maxOf(rect.size.width, rect.size.height) < 7
            ) return null
            val bounds = Geometry.boundingRect(contour)
            val local = MatOfPoint(*contour.toArray().map {
                Point(it.x - bounds.x, it.y - bounds.y)
            }.toTypedArray())
            val mask = Mat.zeros(bounds.height, bounds.width, CvType.CV_8UC1)
            val crop = scores.submat(bounds)
            try {
                Imgproc.drawContours(mask, listOf(local), 0, Scalar(255.0), Imgproc.FILLED)
                val confidence = Core.mean(crop, mask).`val`[0]
                if (confidence < config.boxThreshold) return null
                val padding = config.unclipRatio * Geometry.contourArea(contour) /
                        Geometry.arcLength(polygon, true).coerceAtLeast(1.0)
                val left = ((bounds.x - padding) / scale).toInt().coerceAtLeast(0)
                val top = ((bounds.y - padding) / scale).toInt().coerceAtLeast(0)
                val right = ceil((bounds.x + bounds.width + padding) / scale)
                    .toInt().coerceAtMost(imageWidth)
                val bottom = ceil((bounds.y + bounds.height + padding) / scale)
                    .toInt().coerceAtMost(imageHeight)
                return Region(
                    rect = rect,
                    bounds = Rect(left, top, right - left, bottom - top),
                    confidence = confidence,
                    padding = padding,
                    scale = scale
                )
            } finally {
                local.release()
                mask.release()
                crop.release()
            }
        } finally {
            polygon.release()
        }
    }

    data class Region(
        val rect: RotatedRect,
        val bounds: Rect,
        val confidence: Double,
        val padding: Double,
        val scale: Double
    ) {
        fun crop(gray: Mat): Mat {
            val points = Array(4) { Point() }
            rect.points(points)
            val edges = points.indices.map { i ->
                val start = points[i]
                val end = points[(i + 1) % points.size]
                Point(end.x - start.x, end.y - start.y)
            }
            val edge = edges.maxBy { hypot(it.x, it.y) }
            val length = hypot(edge.x, edge.y)
            val sign = if (edge.x < 0) -1 else 1
            val dx = edge.x / length * sign
            val dy = edge.y / length * sign
            val width = (length + padding * 2) / scale
            val height = (minOf(rect.size.width, rect.size.height) + padding * 2) / scale
            val x = rect.center.x / scale
            val y = rect.center.y / scale
            val along = Point(dx * width / 2, dy * width / 2)
            val down = Point(-dy * height / 2, dx * height / 2)
            val source = MatOfPoint2f(
                Point(x - along.x - down.x, y - along.y - down.y),
                Point(x + along.x - down.x, y + along.y - down.y),
                Point(x + along.x + down.x, y + along.y + down.y),
                Point(x - along.x + down.x, y - along.y + down.y)
            )
            val targetHeight = 40
            val targetWidth = round(width / height * targetHeight).toInt().coerceIn(8, 3000)
            val right = targetWidth - 1.0
            val bottom = targetHeight - 1.0
            val target =
                MatOfPoint2f(Point(), Point(right, 0.0), Point(right, bottom), Point(0.0, bottom))
            val transform = Geometry.getPerspectiveTransform(source, target)
            val crop = Mat()
            try {
                Imgproc.warpPerspective(
                    gray, crop, transform, Size(targetWidth.toDouble(), targetHeight.toDouble()),
                    Imgproc.INTER_CUBIC, Core.BORDER_REPLICATE
                )
                return crop
            } catch (e: Exception) {
                crop.release()
                throw e
            } finally {
                source.release()
                target.release()
                transform.release()
            }
        }
    }

    private companion object {
        val Mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val Std = floatArrayOf(0.229f, 0.224f, 0.225f)
        val NormalizedPixels = Array(3) { channel ->
            FloatArray(256) { (it / 255f - Mean[channel]) / Std[channel] }
        }
    }
}