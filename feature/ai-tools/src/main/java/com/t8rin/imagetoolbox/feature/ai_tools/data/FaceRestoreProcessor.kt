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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.feature.ai_tools.data.utils.readModelInput
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.neural_tools.runCancellable
import com.t8rin.opencv_tools.face.YuNetFaceDetector
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import java.nio.FloatBuffer
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class FaceRestoreProcessor @Inject constructor() {

    suspend fun process(
        source: Bitmap,
        session: OrtSession,
        strength: Float,
        listener: AiProgressListener
    ): Bitmap = coroutineScope {
        val amount = (strength / 100f).coerceIn(0f, 1f)
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        try {
            if (amount == 0f) return@coroutineScope result

            val faces = YuNetFaceDetector.detectWithLandmarks(source)
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
            }
            listener.onProgress(0, faces.size)
            faces.forEachIndexed { index, face ->
                ensureActive()
                val transform = alignment(face.landmarks, source.width, source.height)
                val inverse = Matrix()
                if (transform != null && transform.invert(inverse)) {
                    val aligned = createBitmap(FACE_SIZE, FACE_SIZE)
                    try {
                        val shader =
                            BitmapShader(source, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
                        shader.setLocalMatrix(transform)
                        Canvas(aligned).drawRect(
                            0f,
                            0f,
                            FACE_SIZE.toFloat(),
                            FACE_SIZE.toFloat(),
                            Paint(Paint.FILTER_BITMAP_FLAG).apply { this.shader = shader }
                        )
                        val restored = restoreFace(aligned, session)
                        try {
                            applyMask(restored, amount)
                            canvas.drawBitmap(restored, inverse, paint)
                        } finally {
                            restored.recycle()
                        }
                    } finally {
                        aligned.recycle()
                    }
                }
                listener.onProgress(index + 1, faces.size)
            }
            result
        } catch (error: Throwable) {
            result.recycle()
            throw error
        }
    }

    private suspend fun restoreFace(source: Bitmap, session: OrtSession): Bitmap {
        val values = source.readModelInput(channels = 3, alpha = null)
        OnnxTensor.createTensor(
            OrtEnvironment.getEnvironment(),
            FloatBuffer.wrap(values),
            longArrayOf(1, 3, FACE_SIZE.toLong(), FACE_SIZE.toLong())
        ).use { tensor ->
            session.runCancellable(mapOf(session.inputNames.single() to tensor)).use { output ->
                val restored = output[0] as OnnxTensor
                check(
                    restored.info.shape.contentEquals(
                        longArrayOf(1, 3, FACE_SIZE.toLong(), FACE_SIZE.toLong())
                    )
                )
                restored.floatBuffer.get(values)
            }
        }
        val size = FACE_SIZE * FACE_SIZE
        val pixels = IntArray(size) { index ->
            Color.rgb(
                (values[index] * 255f).roundToInt().coerceIn(0, 255),
                (values[size + index] * 255f).roundToInt().coerceIn(0, 255),
                (values[size * 2 + index] * 255f).roundToInt().coerceIn(0, 255)
            )
        }
        return createBitmap(FACE_SIZE, FACE_SIZE).apply {
            setPixels(pixels, 0, FACE_SIZE, 0, 0, FACE_SIZE, FACE_SIZE)
        }
    }

    private suspend fun applyMask(bitmap: Bitmap, amount: Float) = coroutineScope {
        val row = IntArray(FACE_SIZE)
        for (y in 0 until FACE_SIZE) {
            ensureActive()
            bitmap.getPixels(row, 0, FACE_SIZE, 0, y, FACE_SIZE, 1)
            for (x in row.indices) {
                val dx = (x - FACE_SIZE * 0.5f) / (FACE_SIZE * 0.46f)
                val dy = (y - FACE_SIZE * 0.55f) / (FACE_SIZE * 0.48f)
                val distance = sqrt(dx * dx + dy * dy)
                val fade = ((1f - distance) / 0.15f).coerceIn(0f, 1f)
                val edge = (minOf(x, y, FACE_SIZE - 1 - x, FACE_SIZE - 1 - y) / 20f)
                    .coerceIn(0f, 1f)
                val alpha = (255f * amount * fade * fade * (3f - 2f * fade) * edge).roundToInt()
                row[x] = (row[x] and 0x00FFFFFF) or (alpha shl 24)
            }
            bitmap.setPixels(row, 0, FACE_SIZE, 0, y, FACE_SIZE, 1)
        }
    }

    private fun alignment(points: List<PointF>, width: Int, height: Int): Matrix? {
        if (points.size != FACE_TEMPLATE.size) return null

        val sourceX = points.map { it.x.toDouble() * width }
        val sourceY = points.map { it.y.toDouble() * height }
        val meanX = sourceX.average()
        val meanY = sourceY.average()
        val targetX = FACE_TEMPLATE.map { it.x.toDouble() }.average()
        val targetY = FACE_TEMPLATE.map { it.y.toDouble() }.average()
        var denominator = 0.0
        var real = 0.0
        var imaginary = 0.0
        points.indices.forEach { index ->
            val x = sourceX[index] - meanX
            val y = sourceY[index] - meanY
            val tx = FACE_TEMPLATE[index].x - targetX
            val ty = FACE_TEMPLATE[index].y - targetY
            denominator += x * x + y * y
            real += x * tx + y * ty
            imaginary += x * ty - y * tx
        }
        if (denominator <= 0.0) return null

        val a = real / denominator
        val b = imaginary / denominator
        val values = floatArrayOf(
            a.toFloat(), -b.toFloat(), (targetX - a * meanX + b * meanY).toFloat(),
            b.toFloat(), a.toFloat(), (targetY - b * meanX - a * meanY).toFloat(),
            0f, 0f, 1f
        )
        return if (values.all { it.isFinite() }) Matrix().apply { setValues(values) } else null
    }

    private companion object {
        const val FACE_SIZE = 512

        val FACE_TEMPLATE = listOf(
            PointF(192.98138f, 239.94708f),
            PointF(318.90277f, 240.1936f),
            PointF(256.63416f, 314.01935f),
            PointF(201.26117f, 371.41043f),
            PointF(313.08905f, 371.15118f)
        )
    }
}