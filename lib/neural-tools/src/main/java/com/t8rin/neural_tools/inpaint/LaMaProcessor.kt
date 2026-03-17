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

package com.t8rin.neural_tools.inpaint

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.util.Log
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.awxkee.aire.ScaleColorSpace
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.NeuralTool
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer

object LaMaProcessor : NeuralTool() {

    private const val TRAINED_SIZE = 512

    private var isFastModel: Boolean = false

    private val MODEL_DOWNLOAD_LINK
        get() = baseUrl.replace(
            oldValue = "*",
            newValue = if (isFastModel) {
                "onnx/inpaint/lama/LaMa_512_FAST.onnx"
            } else {
                "onnx/inpaint/lama/LaMa_512.onnx"
            }
        )

    private val directory: File
        get() = File(context.filesDir, "onnx").apply {
            mkdirs()
        }

    private val modelFile
        get() = File(
            directory,
            MODEL_DOWNLOAD_LINK.substringAfterLast('/')
        )

    private var sessionHolder: OrtSession? = null
    private val session: OrtSession
        get() = sessionHolder ?: run {
            val options = OrtSession.SessionOptions().apply {
                runCatching { addCUDA() }
                runCatching { setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT) }
                runCatching { setInterOpNumThreads(8) }
                runCatching { setIntraOpNumThreads(8) }
                runCatching { setMemoryPatternOptimization(true) }
            }
            OrtEnvironment.getEnvironment().createSession(modelFile.absolutePath, options)
        }.also { sessionHolder = it }

    private val _isDownloaded = MutableStateFlow(modelFile.exists())
    val isDownloaded: StateFlow<Boolean> = _isDownloaded

    fun setIsFastModel(boolean: Boolean) {
        isFastModel = boolean
        _isDownloaded.update { modelFile.exists() }
        sessionHolder?.close()
        sessionHolder = null
    }

    fun startDownload(): Flow<DownloadProgress> = callbackFlow {
        httpClient.prepareGet(MODEL_DOWNLOAD_LINK).execute { response ->
            val total = response.contentLength() ?: -1L

            val tmp = File(modelFile.parentFile, modelFile.name + ".tmp")

            val channel = response.bodyAsChannel()
            var downloaded = 0L

            FileOutputStream(tmp).use { fos ->
                try {
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            downloaded += bytes.size
                            fos.write(bytes)
                            trySend(
                                DownloadProgress(
                                    currentPercent = if (total > 0) downloaded.toFloat() / total else 0f,
                                    currentTotalSize = downloaded
                                )
                            )
                        }
                    }

                    tmp.renameTo(modelFile)
                    _isDownloaded.update { true }
                    close()
                } catch (e: Throwable) {
                    tmp.delete()
                    close(e)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    fun inpaint(
        image: Bitmap,
        mask: Bitmap
    ): Bitmap? = runCatching {
        if (!modelFile.exists()) {
            _isDownloaded.update { false }
            return null
        }

        val inputImage = if (image.width != TRAINED_SIZE || image.height != TRAINED_SIZE) {
            Aire.scale(
                bitmap = image,
                dstWidth = TRAINED_SIZE,
                dstHeight = TRAINED_SIZE,
                scaleMode = ResizeFunction.Lanczos3,
                colorSpace = ScaleColorSpace.LAB
            )
        } else {
            image
        }

        val inputMask = if (mask.width != TRAINED_SIZE || mask.height != TRAINED_SIZE) {
            Aire.scale(
                bitmap = mask,
                dstWidth = TRAINED_SIZE,
                dstHeight = TRAINED_SIZE,
                scaleMode = ResizeFunction.Nearest,
                colorSpace = ScaleColorSpace.SRGB
            )
        } else {
            mask
        }

        val tensorImg = bitmapToOnnxTensor(bitmap = inputImage)
        val tensorMask = bitmapToMaskTensor(bitmap = inputMask)

        val inputs = mapOf("image" to tensorImg, "mask" to tensorMask)

        session.run(inputs).use { res ->
            val outValue = res[0]
            val outTensor = outValue as? OnnxTensor
                ?: throw IllegalStateException("Model output is not OnnxTensor")

            val resultBitmap = outputTensorToBitmap(outTensor)

            tensorImg.close()
            tensorMask.close()

            if (image.width != TRAINED_SIZE || image.height != TRAINED_SIZE) {
                return Aire.scale(
                    bitmap = resultBitmap,
                    dstWidth = image.width,
                    dstHeight = image.height,
                    scaleMode = ResizeFunction.Lanczos3,
                    colorSpace = ScaleColorSpace.LAB
                )
            }
            return resultBitmap
        }
    }.onFailure { Log.e("LaMaProcessor", "failure", it) }.getOrNull()

    private fun bitmapToMaskTensor(
        bitmap: Bitmap
    ): OnnxTensor {
        val env = OrtEnvironment.getEnvironment()
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val data = FloatArray(w * h)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            data[i] = if (r > 0) 1f else 0f
        }

        return OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(data),
            longArrayOf(1, 1, h.toLong(), w.toLong())
        )
    }

    private fun bitmapToOnnxTensor(
        bitmap: Bitmap
    ): OnnxTensor {
        val env = OrtEnvironment.getEnvironment()
        val w = bitmap.width
        val h = bitmap.height

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val size = 3 * w * h
        val data = FloatArray(size)

        val channelSize = w * h
        for (i in 0 until channelSize) {
            val p = pixels[i]
            val r = ((p shr 16) and 0xFF) / 255f
            val g = ((p shr 8) and 0xFF) / 255f
            val b = (p and 0xFF) / 255f

            data[i] = r
            data[channelSize + i] = g
            data[2 * channelSize + i] = b
        }

        return OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(data),
            longArrayOf(1, 3, h.toLong(), w.toLong())
        )
    }

    private fun outputTensorToBitmap(
        tensor: OnnxTensor
    ): Bitmap {
        val buffer = tensor.floatBuffer
        val data = FloatArray(buffer.capacity())
        buffer.get(data)

        val width = TRAINED_SIZE
        val height = TRAINED_SIZE
        val size = width * height

        val pixels = IntArray(size)

        val amp = if (isFastModel) 255 else 1

        for (i in 0 until size) {
            val r = (data[i] * amp).toInt().coerceIn(0, 255)
            val g = (data[size + i] * amp).toInt().coerceIn(0, 255)
            val b = (data[2 * size + i] * amp).toInt().coerceIn(0, 255)

            pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}