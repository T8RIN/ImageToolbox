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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.ai_tools.data.model.ChunkInfo
import com.t8rin.imagetoolbox.feature.ai_tools.data.model.ModelInfo
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Float.floatToIntBits
import java.lang.Float.intBitsToFloat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.inject.Inject
import kotlin.math.ceil

internal class AiProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val service: KeepAliveService,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager
) : DispatchersHolder by dispatchersHolder, ResourceManager by resourceManager {

    private val chunksDir: File
        get() = File(context.cacheDir, "processing_chunks").apply(File::mkdirs)

    suspend fun processImage(
        session: OrtSession,
        inputBitmap: Bitmap,
        model: NeuralModel,
        params: NeuralParams,
        listener: AiProgressListener
    ): Bitmap? = withContext(defaultDispatcher) {
        service.track(
            onCancel = {
                "Cancelled".makeLog("AiProcessor")
                clearChunks()
            },
            onFailure = { e ->
                listener.onError(
                    e.localizedMessage ?: e.message ?: getString(R.string.something_went_wrong)
                )
            },
            action = {
                processBitmapImpl(
                    session = session,
                    inputBitmap = inputBitmap,
                    listener = object : AiProgressListener {
                        override fun onError(error: String) {
                            listener.onError(error)
                            stop()
                        }

                        override fun onProgress(currentChunkIndex: Int, totalChunks: Int) {
                            listener.onProgress(
                                currentChunkIndex = currentChunkIndex,
                                totalChunks = totalChunks
                            )

                            if (totalChunks > 0) {
                                updateProgress(
                                    done = currentChunkIndex,
                                    total = totalChunks
                                )
                            }
                        }
                    },
                    info = ModelInfo(
                        strength = params.strength,
                        session = session,
                        chunkSize = params.chunkSize,
                        overlap = params.overlap,
                        model = model,
                        disableChunking = !params.enableChunking
                    )
                )
            }
        )
    }

    private suspend fun processBitmapImpl(
        session: OrtSession,
        inputBitmap: Bitmap,
        listener: AiProgressListener,
        info: ModelInfo,
    ): Bitmap = withContext(defaultDispatcher) {
        // to handle edge artifacts, dunno if it's needed for *all* models, but it helped with SCUNet
        val inputBitmap = if (info.isScuNet) {
            addBlackBorder(inputBitmap)
        } else {
            inputBitmap
        }
        val width = inputBitmap.getWidth()
        val height = inputBitmap.getHeight()

        val processingConfig = Bitmap.Config.ARGB_8888
        // Use the smaller of chunkSize or model's fixed dimensions
        val effectiveMaxChunkSize = if (info.isNonChunkable) {
            Int.MAX_VALUE
        } else {
            if (info.expectedWidth != null && info.expectedHeight != null) {
                minOf(info.chunkSize, info.expectedWidth, info.expectedHeight)
            } else {
                info.chunkSize
            }
        }

        val processed = if (width > effectiveMaxChunkSize || height > effectiveMaxChunkSize) {
            processTiled(
                session = session,
                inputBitmap = inputBitmap,
                listener = listener,
                info = info,
                config = processingConfig,
                maxChunkSize = effectiveMaxChunkSize
            )
        } else {
            processChunk(
                session = session,
                chunk = inputBitmap.copy(processingConfig, true),
                config = processingConfig,
                info = info
            )
        }

        if (info.isScuNet) {
            removeBlackBorder(processed)
        } else {
            processed
        }
    }

    private fun addBlackBorder(bitmap: Bitmap, borderSize: Int = 8): Bitmap {
        val newWidth = bitmap.width + 2 * borderSize
        val newHeight = bitmap.height + 2 * borderSize
        val borderedBitmap = createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(borderedBitmap)
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(bitmap, borderSize.toFloat(), borderSize.toFloat(), null)
        return borderedBitmap
    }

    private fun removeBlackBorder(bitmap: Bitmap, borderSize: Int = 8): Bitmap {
        val croppedWidth = bitmap.width - 2 * borderSize
        val croppedHeight = bitmap.height - 2 * borderSize
        if (croppedWidth <= 0 || croppedHeight <= 0) {
            return bitmap
        }
        return Bitmap.createBitmap(bitmap, borderSize, borderSize, croppedWidth, croppedHeight)
    }

    private suspend fun processTiled(
        session: OrtSession,
        inputBitmap: Bitmap,
        listener: AiProgressListener,
        info: ModelInfo,
        config: Bitmap.Config,
        maxChunkSize: Int
    ): Bitmap {
        ensureActive()
        val width = inputBitmap.width
        val height = inputBitmap.height
        val overlap = info.overlap
        val stride = maxChunkSize - overlap
        val cols =
            if (width <= maxChunkSize) 1 else ceil((width - overlap).toFloat() / stride).toInt()
        val rows =
            if (height <= maxChunkSize) 1 else ceil((height - overlap).toFloat() / stride).toInt()
        "Processing tiled: image=${width}x${height}, chunkSize=$maxChunkSize, stride=$stride, grid=${cols}x${rows}, overlap=$overlap".makeLog(
            "AiProcessor"
        )
        val totalChunks = cols * rows

        val chunkInfoList = mutableListOf<ChunkInfo>()

        "Phase 1: Extracting $totalChunks chunks to disk".makeLog("AiProcessor")
        var chunkIndex = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                ensureActive()
                val chunkX = col * stride
                val chunkY = row * stride
                val chunkW = minOf(chunkX + maxChunkSize, width) - chunkX
                val chunkH = minOf(chunkY + maxChunkSize, height) - chunkY
                if (chunkW <= 0 || chunkH <= 0) continue
                val chunk = Bitmap.createBitmap(inputBitmap, chunkX, chunkY, chunkW, chunkH)
                val converted = if (chunk.config != config) {
                    val temp = chunk.copy(config, true)
                    chunk.recycle()
                    temp
                } else {
                    chunk
                }

                ensureActive()

                val inputChunkFile = File(chunksDir, "chunk_${chunkIndex}.png")
                val processedChunkFile = File(chunksDir, "chunk_${chunkIndex}_processed.png")
                withContext(Dispatchers.IO) {
                    FileOutputStream(inputChunkFile).use {
                        converted.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                }
                converted.recycle()
                chunkInfoList.add(
                    ChunkInfo(
                        index = chunkIndex,
                        inputFile = inputChunkFile,
                        processedFile = processedChunkFile,
                        x = chunkX,
                        y = chunkY,
                        width = chunkW,
                        height = chunkH,
                        col = col,
                        row = row
                    )
                )
                chunkIndex++
            }
        }
        "Saved ${chunkInfoList.size} chunks to ${chunksDir.absolutePath}".makeLog("AiProcessor")
        "Phase 2: Processing $totalChunks chunks".makeLog("AiProcessor")
        if (totalChunks > 1) {
            listener.onProgress(0, totalChunks)
        }
        for (chunkInfo in chunkInfoList) {
            ensureActive()

            val loadedChunk = BitmapFactory.decodeFile(chunkInfo.inputFile.absolutePath)

            val processed = processChunk(
                session = session,
                chunk = loadedChunk,
                config = config,
                info = info
            )

            loadedChunk.recycle()
            withContext(ioDispatcher) {
                FileOutputStream(chunkInfo.processedFile).use {
                    processed.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                chunkInfo.inputFile.delete()
            }

            ensureActive()

            processed.recycle()

            if (totalChunks > 1) {
                val nextChunkIndex = chunkInfo.index + 1
                listener.onProgress(nextChunkIndex, totalChunks)
            }
        }

        val resultWidth = width * info.scaleFactor
        val resultHeight = height * info.scaleFactor
        val result = createBitmap(resultWidth, resultHeight, config)
        for (chunkInfo in chunkInfoList) {
            ensureActive()
            val loadedProcessed = withContext(ioDispatcher) {
                BitmapFactory.decodeFile(chunkInfo.processedFile.absolutePath)
            } ?: throw Exception("Failed to load processed chunk ${chunkInfo.index}")

            mergeChunkWithBlending(result, loadedProcessed, chunkInfo, overlap, info.scaleFactor)
            loadedProcessed.recycle()
        }
        clearChunks()

        return result
    }

    private suspend fun processChunk(
        session: OrtSession,
        chunk: Bitmap,
        config: Bitmap.Config,
        info: ModelInfo
    ): Bitmap = withContext(defaultDispatcher) {
        this@AiProcessor.ensureActive()

        val originalW = chunk.width
        val originalH = chunk.height
        var w: Int
        var h: Int

        if (info.isScuNetColor) {
            val minModelSize = 256
            w = if (info.expectedWidth != null && info.expectedWidth > 0) {
                info.expectedWidth
            } else {
                val padFactor = 8
                val paddedW = ((originalW + padFactor - 1) / padFactor) * padFactor
                maxOf(paddedW, minModelSize)
            }
            h = if (info.expectedHeight != null && info.expectedHeight > 0) {
                info.expectedHeight
            } else {
                val padFactor = 8
                val paddedH = ((originalH + padFactor - 1) / padFactor) * padFactor
                maxOf(paddedH, minModelSize)
            }
        } else {
            w = if (info.expectedWidth != null && info.expectedWidth > 0) {
                info.expectedWidth
            } else {
                val padFactor = 8
                ((originalW + padFactor - 1) / padFactor) * padFactor
            }
            h = if (info.expectedHeight != null && info.expectedHeight > 0) {
                info.expectedHeight
            } else {
                val padFactor = 8
                ((originalH + padFactor - 1) / padFactor) * padFactor
            }
        }

        val needsPadding = w != originalW || h != originalH
        val paddedChunk = if (needsPadding) {
            "Padding chunk from ${originalW}x${originalH} to ${w}x${h}".makeLog("AiProcessor")
            val padded = createBitmap(w, h, config)
            val canvas = Canvas(padded)
            canvas.drawBitmap(chunk, 0f, 0f, null)
            if (w > originalW) {
                val rightStrip = Bitmap.createBitmap(chunk, originalW - 1, 0, 1, originalH)
                for (x in originalW until w) {
                    this@AiProcessor.ensureActive()
                    canvas.drawBitmap(rightStrip, x.toFloat(), 0f, null)
                }
                rightStrip.recycle()
            }
            if (h > originalH) {
                val bottomStrip = Bitmap.createBitmap(padded, 0, originalH - 1, w, 1)
                for (y in originalH until h) {
                    this@AiProcessor.ensureActive()
                    canvas.drawBitmap(bottomStrip, 0f, y.toFloat(), null)
                }
                bottomStrip.recycle()
            }
            padded
        } else {
            chunk
        }
        val hasAlpha = chunk.hasAlpha()
        val inputChannels = info.inputChannels
        val outputChannels = info.outputChannels
        val pixels = IntArray(w * h)
        paddedChunk.getPixels(pixels, 0, w, 0, 0, w, h)
        val inputArray = FloatArray(inputChannels * w * h)
        val alphaChannel = if (hasAlpha) FloatArray(w * h) else null
        for (i in 0 until w * h) {
            this@AiProcessor.ensureActive()

            val color = pixels[i]
            if (inputChannels == 1) {
                val gray = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
                inputArray[i] = gray / 255f
            } else {
                inputArray[i] = Color.red(color) / 255f
                inputArray[w * h + i] = Color.green(color) / 255f
                inputArray[2 * w * h + i] = Color.blue(color) / 255f
            }
            if (hasAlpha) {
                alphaChannel!![i] = Color.alpha(color) / 255f
            }
        }
        val env = OrtEnvironment.getEnvironment()
        val inputShape = longArrayOf(1, inputChannels.toLong(), h.toLong(), w.toLong())
        val inputs = mutableMapOf<String, OnnxTensor>()
        val inputTensor = if (info.isFp16) {
            val fp16Array = ShortArray(inputArray.size) { i -> floatToFloat16(inputArray[i]) }
            val byteBuffer =
                ByteBuffer.allocateDirect(fp16Array.size * 2).order(ByteOrder.nativeOrder())
            val shortBuffer = byteBuffer.asShortBuffer()
            shortBuffer.put(fp16Array)
            byteBuffer.rewind()
            OnnxTensor.createTensor(env, byteBuffer, inputShape, OnnxJavaType.FLOAT16)
        } else {
            OnnxTensor.createTensor(env, FloatBuffer.wrap(inputArray), inputShape)
        }
        inputs[info.inputName] = inputTensor
        for ((key, nodeInfo) in info.inputInfoMap) {
            this@AiProcessor.ensureActive()
            if (key == info.inputName) continue
            val tensorInfo = nodeInfo.info as? TensorInfo ?: continue
            if (tensorInfo.type == OnnxJavaType.FLOAT || tensorInfo.type == OnnxJavaType.FLOAT16) {
                val shape = tensorInfo.shape.clone()
                for (i in shape.indices) {
                    this@AiProcessor.ensureActive()
                    if (shape[i] == -1L) shape[i] = 1L
                }
                if (shape.size == 2 && shape[0] == 1L && shape[1] == 1L) {
                    this@AiProcessor.ensureActive()
                    val strengthTensor = if (tensorInfo.type == OnnxJavaType.FLOAT16) {
                        val strengthFp16 = floatToFloat16(info.strength / 100f)
                        val byteBuffer = ByteBuffer.allocateDirect(2).order(ByteOrder.nativeOrder())
                        byteBuffer.asShortBuffer().put(strengthFp16)
                        byteBuffer.rewind()
                        OnnxTensor.createTensor(env, byteBuffer, shape, OnnxJavaType.FLOAT16)
                    } else {
                        OnnxTensor.createTensor(
                            env,
                            FloatBuffer.wrap(floatArrayOf(info.strength / 100f)),
                            shape
                        )
                    }
                    inputs[key] = strengthTensor
                }
            }
        }

        try {
            this@AiProcessor.ensureActive()
            session.run(inputs).use { sessionResult ->
                val outputH = h * info.scaleFactor
                val outputW = w * info.scaleFactor
                val (outputArray, actualOutputChannels) = extractOutputArray(
                    sessionResult[0].value,
                    outputChannels,
                    outputH,
                    outputW
                )
                val fullResultBitmap =
                    createBitmap(width = outputW, height = outputH, config = config)
                val outPixels = IntArray(outputW * outputH)

                for (i in 0 until outputW * outputH) {
                    this@AiProcessor.ensureActive()
                    val alpha = if (hasAlpha) {
                        val srcY = ((i / outputW) / info.scaleFactor).coerceIn(0, h - 1)
                        val srcX = ((i % outputW) / info.scaleFactor).coerceIn(0, w - 1)
                        val srcIdx = srcY * w + srcX
                        clamp255(alphaChannel!![srcIdx] * 255f)
                    } else {
                        255
                    }

                    if (actualOutputChannels == 1) {
                        val gray = clamp255(outputArray[i] * 255f)
                        outPixels[i] = Color.argb(alpha, gray, gray, gray)
                    } else {
                        val r = clamp255(outputArray[i] * 255f)
                        val g = clamp255(outputArray[outputW * outputH + i] * 255f)
                        val b = clamp255(outputArray[2 * outputW * outputH + i] * 255f)
                        outPixels[i] = Color.argb(alpha, r, g, b)
                    }
                }
                fullResultBitmap.setPixels(outPixels, 0, outputW, 0, 0, outputW, outputH)
                if (needsPadding) {
                    val croppedW = originalW * info.scaleFactor
                    val croppedH = originalH * info.scaleFactor
                    val cropped = Bitmap.createBitmap(fullResultBitmap, 0, 0, croppedW, croppedH)
                    fullResultBitmap.recycle()
                    cropped
                } else {
                    fullResultBitmap
                }
            }
        } finally {
            inputs.values.forEach { it.close() }
            if (needsPadding) {
                paddedChunk.recycle()
            }
        }
    }

    private suspend fun mergeChunkWithBlending(
        result: Bitmap,
        processedChunk: Bitmap,
        chunkInfo: ChunkInfo,
        overlap: Int,
        scaleFactor: Int
    ) = withContext(defaultDispatcher) {
        val width = processedChunk.width
        val height = processedChunk.height
        val x = chunkInfo.x * scaleFactor
        val y = chunkInfo.y * scaleFactor
        val scaledOverlap = overlap * scaleFactor
        val needsLeftBlend = chunkInfo.col > 0
        val needsTopBlend = chunkInfo.row > 0
        if (!needsLeftBlend && !needsTopBlend) {
            val canvas = Canvas(result)
            canvas.drawBitmap(processedChunk, x.toFloat(), y.toFloat(), null)
            return@withContext
        }
        val existingPixels = IntArray(width * height)
        try {
            result.getPixels(existingPixels, 0, width, x, y, width, height)
        } catch (_: Throwable) {
            val canvas = Canvas(result)
            canvas.drawBitmap(processedChunk, x.toFloat(), y.toFloat(), null)
            return@withContext
        }
        val newPixels = IntArray(width * height)
        processedChunk.getPixels(newPixels, 0, width, 0, 0, width, height)
        for (localY in 0 until height) {
            for (localX in 0 until width) {
                ensureActive()
                val inLeftOverlap = needsLeftBlend && localX < scaledOverlap
                val inTopOverlap = needsTopBlend && localY < scaledOverlap
                if (!inLeftOverlap && !inTopOverlap) continue
                val idx = localY * width + localX
                var blendFactor = 1.0f
                if (inLeftOverlap) {
                    val t =
                        (localX.toFloat() / (scaledOverlap - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
                    blendFactor = minOf(blendFactor, t * t * (3f - 2f * t))
                }
                if (inTopOverlap) {
                    val t =
                        (localY.toFloat() / (scaledOverlap - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
                    blendFactor = minOf(blendFactor, t * t * (3f - 2f * t))
                }
                val existingColor = existingPixels[idx]
                val newColor = newPixels[idx]
                val r =
                    ((1 - blendFactor) * Color.red(existingColor) + blendFactor * Color.red(newColor)).toInt()
                val g = ((1 - blendFactor) * Color.green(existingColor) + blendFactor * Color.green(
                    newColor
                )).toInt()
                val b = ((1 - blendFactor) * Color.blue(existingColor) + blendFactor * Color.blue(
                    newColor
                )).toInt()
                val a = ((1 - blendFactor) * Color.alpha(existingColor) + blendFactor * Color.alpha(
                    newColor
                )).toInt()
                newPixels[idx] = Color.argb(a, r, g, b)
            }
        }
        result.setPixels(newPixels, 0, width, x, y, width, height)
    }

    private suspend fun extractOutputArray(
        outputValue: Any,
        channels: Int,
        h: Int,
        w: Int
    ): Pair<FloatArray, Int> {
        ensureActive()
        "Output type received: ${outputValue.javaClass.name}".makeLog("AiProcessor")
        return when (outputValue) {
            is FloatArray -> {
                "Output is FloatArray (FP32 or auto-converted from FP16)".makeLog("AiProcessor")
                outputValue to channels
            }

            is ShortArray -> {
                "Output is ShortArray (FP16) - converting to Float32".makeLog("AiProcessor")
                FloatArray(outputValue.size) { i -> float16ToFloat(outputValue[i]) } to channels
            }

            is Array<*> -> {
                try {
                    val arr = outputValue as Array<Array<Array<FloatArray>>>
                    "Output is multi-dimensional FloatArray".makeLog("AiProcessor")
                    val actualChannels = arr[0].size
                    "Expected channels: $channels, Actual channels: $actualChannels".makeLog("AiProcessor")

                    val out = FloatArray(channels * h * w)
                    val channelsToProcess = minOf(channels, actualChannels)
                    for (ch in 0 until channelsToProcess) {
                        for (y in 0 until h) {
                            for (x in 0 until w) {
                                ensureActive()
                                out[ch * h * w + y * w + x] = arr[0][ch][y][x]
                            }
                        }
                    }
                    out to actualChannels
                } catch (e: Throwable) {
                    try {
                        val arr = outputValue as Array<Array<Array<ShortArray>>>
                        "Output is multi-dimensional ShortArray (FP16)".makeLog("AiProcessor")
                        val actualChannels = arr[0].size
                        "Expected channels: $channels, Actual channels: $actualChannels".makeLog("AiProcessor")

                        val out = FloatArray(channels * h * w)
                        val channelsToProcess = minOf(channels, actualChannels)
                        for (ch in 0 until channelsToProcess) {
                            for (y in 0 until h) {
                                for (x in 0 until w) {
                                    ensureActive()
                                    out[ch * h * w + y * w + x] = float16ToFloat(arr[0][ch][y][x])
                                }
                            }
                        }
                        out to actualChannels
                    } catch (e2: Throwable) {
                        throw RuntimeException("Failed to extract output array: ${e.message}, ${e2.message}")
                    }
                }
            }

            else -> throw RuntimeException("Unexpected ONNX output type: ${outputValue.javaClass}")
        }
    }

    private fun clamp255(v: Float): Int = 0.coerceAtLeast(255.coerceAtMost(v.toInt()))

    private fun floatToFloat16(value: Float): Short {
        val bits = floatToIntBits(value)
        val sign = (bits ushr 16) and 0x8000
        val exponent = ((bits ushr 23) and 0xFF) - 127 + 15
        var mantissa = bits and 0x7FFFFF

        if (exponent <= 0) {
            if (exponent < -10) {
                return sign.toShort()
            }
            mantissa = mantissa or 0x800000
            mantissa = mantissa shr (1 - exponent)
            return (sign or (mantissa shr 13)).toShort()
        } else if (exponent >= 0x1F) {
            return (sign or 0x7C00).toShort()
        }

        return (sign or (exponent shl 10) or (mantissa shr 13)).toShort()
    }

    private fun float16ToFloat(fp16: Short): Float {
        val bits = fp16.toInt() and 0xFFFF
        val sign = (bits and 0x8000) shl 16
        val exponent = (bits and 0x7C00) ushr 10
        val mantissa = bits and 0x3FF
        if (exponent == 0) {
            if (mantissa == 0) {
                return intBitsToFloat(sign)
            }
            var e = -14
            var m = mantissa
            while ((m and 0x400) == 0) {
                m = m shl 1
                e--
            }
            m = m and 0x3FF
            return intBitsToFloat(sign or ((e + 127) shl 23) or (m shl 13))
        } else if (exponent == 0x1F) {
            return intBitsToFloat(sign or 0x7F800000 or (mantissa shl 13))
        }

        return intBitsToFloat(sign or ((exponent - 15 + 127) shl 23) or (mantissa shl 13))
    }

    private suspend fun ensureActive() = currentCoroutineContext().ensureActive()

    private fun clearChunks() = chunksDir.deleteRecursively()
}