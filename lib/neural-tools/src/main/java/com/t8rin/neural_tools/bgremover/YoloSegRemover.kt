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

@file:Suppress("UNCHECKED_CAST")

package com.t8rin.neural_tools.bgremover

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import java.nio.FloatBuffer
import kotlin.math.exp

internal object YoloSegRemover : GenericBackgroundRemover(
    path = "yolo11x-seg.onnx",
    trainedSize = 1024
) {
    override fun removeBackground(
        image: Bitmap,
        modelPath: String,
        trainedSize: Int?
    ): Bitmap {
        val session = session ?: env.createSession(
            modelFile.absolutePath,
            OrtSession.SessionOptions()
        ).also {
            this.session = it
        }

        val resized = image.scale(
            width = trainedSize!!,
            height = trainedSize
        )

        val input = bitmapToFloatBuffer(resized)

        val tensor = OnnxTensor.createTensor(
            env,
            input,
            longArrayOf(
                1,
                3,
                trainedSize.toLong(),
                trainedSize.toLong()
            )
        )

        val outputs = session.run(
            mapOf(session.inputNames.first() to tensor)
        )

        val det =
            outputs[0].value as Array<Array<FloatArray>>

        val proto =
            outputs[1].value as Array<Array<Array<FloatArray>>>

        val resultMask = processSegmentation(
            det = det[0],
            proto = proto[0],
            width = image.width,
            height = image.height
        )

        tensor.close()
        outputs.close()

        return applyMask(image, resultMask)
    }

    private data class Detection(
        val cx: Float,
        val cy: Float,
        val w: Float,
        val h: Float,
        val score: Float,
        val classId: Int,
        val coeffs: FloatArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Detection

            if (cx != other.cx) return false
            if (cy != other.cy) return false
            if (w != other.w) return false
            if (h != other.h) return false
            if (score != other.score) return false
            if (classId != other.classId) return false
            if (!coeffs.contentEquals(other.coeffs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = cx.hashCode()
            result = 31 * result + cy.hashCode()
            result = 31 * result + w.hashCode()
            result = 31 * result + h.hashCode()
            result = 31 * result + score.hashCode()
            result = 31 * result + classId
            result = 31 * result + coeffs.contentHashCode()
            return result
        }
    }

    private fun processSegmentation(
        det: Array<FloatArray>,
        proto: Array<Array<FloatArray>>,
        width: Int,
        height: Int
    ): Bitmap {
        val protoChannels = 32
        val protoH = proto[0].size
        val protoW = proto[0][0].size

        val candidates = mutableListOf<Detection>()
        val count = det[0].size

        for (i in 0 until count) {
            var bestClass = -1
            var bestScore = 0f

            for (c in 4 until 84) {
                val score = det[c][i]
                if (score > bestScore) {
                    bestScore = score
                    bestClass = c - 4
                }
            }

            if (bestScore < 0.45f) continue

            candidates += Detection(
                cx = det[0][i],
                cy = det[1][i],
                w = det[2][i],
                h = det[3][i],
                score = bestScore,
                classId = bestClass,
                coeffs = FloatArray(32) { k -> det[84 + k][i] }
            )
        }

        val selected = nms(candidates, 0.5f).take(8)

        val mask = FloatArray(protoW * protoH)

        selected.forEach { d ->
            val left = ((d.cx - d.w / 2f) / trainedSize!! * protoW)
                .toInt()
                .coerceIn(0, protoW - 1)

            val top = ((d.cy - d.h / 2f) / trainedSize * protoH)
                .toInt()
                .coerceIn(0, protoH - 1)

            val right = ((d.cx + d.w / 2f) / trainedSize * protoW)
                .toInt()
                .coerceIn(0, protoW - 1)

            val bottom = ((d.cy + d.h / 2f) / trainedSize * protoH)
                .toInt()
                .coerceIn(0, protoH - 1)

            for (y in top..bottom) {
                for (x in left..right) {
                    var sum = 0f

                    for (k in 0 until protoChannels) {
                        sum += d.coeffs[k] * proto[k][y][x]
                    }

                    val v = 1f / (1f + exp(-sum))
                    val idx = y * protoW + x

                    if (v > mask[idx]) mask[idx] = v
                }
            }
        }

        val pixels = IntArray(protoW * protoH)

        for (i in pixels.indices) {
            val alpha = when {
                mask[i] >= 0.55f -> 255
                mask[i] <= 0.35f -> 0
                else -> ((mask[i] - 0.35f) / 0.2f * 255f).toInt()
            }.coerceIn(0, 255)

            pixels[i] = Color.argb(alpha, 255, 255, 255)
        }

        val maskBmp = createBitmap(protoW, protoH, Bitmap.Config.ARGB_8888)

        maskBmp.setPixels(
            pixels,
            0,
            protoW,
            0,
            0,
            protoW,
            protoH
        )

        return maskBmp.scale(width, height)
    }

    private fun nms(
        detections: List<Detection>,
        iouThreshold: Float
    ): List<Detection> {
        val sorted = detections
            .sortedByDescending { it.score }
            .toMutableList()

        val result = mutableListOf<Detection>()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            result += best

            sorted.removeAll {
                it.classId == best.classId && iou(best, it) > iouThreshold
            }
        }

        return result
    }

    private fun iou(a: Detection, b: Detection): Float {
        val ax1 = a.cx - a.w / 2f
        val ay1 = a.cy - a.h / 2f
        val ax2 = a.cx + a.w / 2f
        val ay2 = a.cy + a.h / 2f

        val bx1 = b.cx - b.w / 2f
        val by1 = b.cy - b.h / 2f
        val bx2 = b.cx + b.w / 2f
        val by2 = b.cy + b.h / 2f

        val x1 = maxOf(ax1, bx1)
        val y1 = maxOf(ay1, by1)
        val x2 = minOf(ax2, bx2)
        val y2 = minOf(ay2, by2)

        val inter = maxOf(0f, x2 - x1) * maxOf(0f, y2 - y1)
        val areaA = maxOf(0f, ax2 - ax1) * maxOf(0f, ay2 - ay1)
        val areaB = maxOf(0f, bx2 - bx1) * maxOf(0f, by2 - by1)

        return inter / (areaA + areaB - inter + 1e-6f)
    }

    private fun applyMask(
        original: Bitmap,
        mask: Bitmap
    ): Bitmap {

        val w = original.width
        val h = original.height

        val src = IntArray(w * h)
        val msk = IntArray(w * h)

        original.getPixels(src, 0, w, 0, 0, w, h)
        mask.getPixels(msk, 0, w, 0, 0, w, h)

        for (i in src.indices) {

            val alpha = Color.alpha(msk[i])

            val c = src[i]

            src[i] = Color.argb(
                alpha,
                Color.red(c),
                Color.green(c),
                Color.blue(c)
            )
        }

        return Bitmap.createBitmap(
            src,
            w,
            h,
            Bitmap.Config.ARGB_8888
        )
    }

    private fun bitmapToFloatBuffer(
        bitmap: Bitmap
    ): FloatBuffer {

        val pixels = IntArray(
            trainedSize!! * trainedSize
        )

        bitmap.getPixels(
            pixels,
            0,
            trainedSize,
            0,
            0,
            trainedSize,
            trainedSize
        )

        val buffer = FloatBuffer.allocate(
            3 * trainedSize * trainedSize
        )

        var r = 0
        var g = trainedSize * trainedSize
        var b = trainedSize * trainedSize * 2

        for (p in pixels) {

            buffer.put(
                r++,
                Color.red(p) / 255f
            )

            buffer.put(
                g++,
                Color.green(p) / 255f
            )

            buffer.put(
                b++,
                Color.blue(p) / 255f
            )
        }

        return buffer
    }

}