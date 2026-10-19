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
import ai.onnxruntime.TensorInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.system.Os
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.NeuralTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.roundToInt

object LaMaProcessor : NeuralTool() {

    private const val MODEL_ALIGNMENT = 8
    private const val CONTEXT_PADDING = 256
    private const val PREVIEW_SIZE = 512
    private const val TILE_SIZE = 768
    private const val TILE_PADDING = 128
    private const val OVERVIEW_SIZE = 1024

    private val MODEL_DOWNLOAD_LINK
        get() = baseUrl.replace(
            oldValue = "*",
            newValue = "onnx/inpaint/lama/LaMa_dynamic.onnx"
        )

    val modelDownloadLink: String
        get() = MODEL_DOWNLOAD_LINK

    private val directory: File
        get() = File(context.filesDir, "onnx").apply {
            mkdirs()
        }

    private val modelFile
        get() = File(directory, "LaMa_dynamic.onnx")

    init {
        File(directory, "LaMa_512.onnx").delete()
    }

    private var sessionHolder: OrtSession? = null
    private var sessionThreads = 4
    private val session: OrtSession
        get() = sessionHolder ?: run {
            val options = OrtSession.SessionOptions().apply {
                if (Build.HARDWARE == "ranchu" || Build.HARDWARE == "goldfish") {
                    addConfigEntry("mlas.disable_kleidiai", "1")
                }
                runCatching { addCUDA() }
                runCatching { setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT) }
                runCatching { setInterOpNumThreads(1) }
                runCatching { setIntraOpNumThreads(sessionThreads) }
                runCatching { addConfigEntry("session.dynamic_block_base", "4") }
                runCatching { setMemoryPatternOptimization(true) }
            }
            options.use {
                OrtEnvironment.getEnvironment().createSession(modelFile.absolutePath, it)
            }
        }.also { sessionHolder = it }

    private val _isDownloaded = MutableStateFlow(modelFile.exists() && modelFile.length() > 0L)
    val isDownloaded: StateFlow<Boolean> = _isDownloaded

    fun startDownload(): Flow<DownloadProgress> = channelFlow {
        val destination = modelFile
        downloader.download(
            url = MODEL_DOWNLOAD_LINK,
            destinationPath = destination.absolutePath,
            onProgress = { send(it) }
        )
        check(destination.exists() && destination.length() > 0L)
        synchronized(this@LaMaProcessor) {
            sessionHolder?.close()
            sessionHolder = null
            _isDownloaded.update { modelFile.exists() && modelFile.length() > 0L }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun importModel(uri: Uri) = withContext(Dispatchers.IO) {
        val temporaryFile = File.createTempFile("lama_import_", ".onnx", directory)
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                temporaryFile.outputStream().use(input::copyTo)
            } ?: error("Unable to open model file")
            check(temporaryFile.length() > 0L) { "Model file is empty" }

            OrtEnvironment.getEnvironment().createSession(temporaryFile.absolutePath)
                .use { imported ->
                    val image = imported.inputInfo["image"]?.info as? TensorInfo
                    val mask = imported.inputInfo["mask"]?.info as? TensorInfo
                    check(
                        image?.shape?.size == 4 && image.shape.getOrNull(1) == 3L &&
                                mask?.shape?.size == 4 && mask.shape.getOrNull(1) == 1L &&
                                image.shape[2] < 0 && image.shape[3] < 0 &&
                                mask.shape[2] < 0 && mask.shape[3] < 0 &&
                                imported.outputInfo.keys.containsAll(listOf("output", "prediction"))
                    ) { "This is not a compatible LaMa model" }
                }

            synchronized(this@LaMaProcessor) {
                sessionHolder?.close()
                sessionHolder = null
                Os.rename(temporaryFile.absolutePath, modelFile.absolutePath)
                _isDownloaded.update { true }
            }
        } finally {
            temporaryFile.delete()
        }
    }

    suspend fun deleteModel() = withContext(Dispatchers.IO) {
        synchronized(this@LaMaProcessor) {
            sessionHolder?.close()
            sessionHolder = null
            check(!modelFile.exists() || modelFile.delete()) { "Unable to delete LaMa model" }
            _isDownloaded.update { false }
        }
    }

    @Synchronized
    fun inpaint(
        image: Bitmap,
        mask: Bitmap,
        runOptions: OrtSession.RunOptions? = null,
        isPreview: Boolean = false,
        threads: Int = 0,
        limitResolution: Boolean = false
    ): Bitmap? = runCatching {
        if (!modelFile.exists()) {
            _isDownloaded.update { false }
            return null
        }

        val fullMask = if (mask.width != image.width || mask.height != image.height) {
            mask.scale(image.width, image.height, false)
        } else mask

        try {
            val selection =
                maskBounds(fullMask) ?: return image.copy(Bitmap.Config.ARGB_8888, false)
            val threadCount = (threads.takeIf { it > 0 } ?: 4).coerceIn(
                1, Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
            )
            if (sessionThreads != threadCount) {
                sessionHolder?.close()
                sessionHolder = null
                sessionThreads = threadCount
            }
            val bounds = selection.bounds
            val region = bounds.expanded(
                maxOf(CONTEXT_PADDING, bounds.width(), bounds.height()), image
            )
            if (!isPreview && !limitResolution && maxOf(
                    region.width(),
                    region.height()
                ) > OVERVIEW_SIZE
            ) {
                return inpaintTiled(image, fullMask, selection, region, runOptions)
            }
            val scale = if (isPreview || limitResolution) {
                val size = if (isPreview) PREVIEW_SIZE else OVERVIEW_SIZE
                minOf(1f, size.toFloat() / maxOf(region.width(), region.height()))
            } else 1f
            val width = (region.width() * scale).roundToInt().coerceAtLeast(1)
            val height = (region.height() * scale).roundToInt().coerceAtLeast(1)
            val predicted =
                inpaintRegion(image, fullMask, region, width, height, "output", runOptions)
            val patch = if (width != region.width() || height != region.height()) {
                predicted.scale(region.width(), region.height())
            } else predicted
            try {
                return composite(image, fullMask, patch, region)
            } finally {
                if (patch !== predicted) patch.recycle()
                predicted.recycle()
            }
        } finally {
            if (fullMask !== mask) fullMask.recycle()
        }
    }.onFailure { Log.e("LaMaProcessor", "failure", it) }.getOrNull()

    private data class MaskBounds(val bounds: Rect, val pixels: Int)

    private fun maskBounds(
        mask: Bitmap,
        region: Rect = Rect(0, 0, mask.width, mask.height)
    ): MaskBounds? {
        val row = IntArray(region.width())
        val bounds = Rect(region.right, region.bottom, region.left, region.top)
        var pixels = 0
        for (y in region.top until region.bottom) {
            mask.getPixels(row, 0, row.size, region.left, y, row.size, 1)
            row.forEachIndexed { index, color ->
                if (Color.red(color) > 0 && Color.alpha(color) > 0) {
                    val x = region.left + index
                    bounds.left = minOf(bounds.left, x)
                    bounds.top = minOf(bounds.top, y)
                    bounds.right = maxOf(bounds.right, x + 1)
                    bounds.bottom = maxOf(bounds.bottom, y + 1)
                    pixels++
                }
            }
        }
        return bounds.takeUnless(Rect::isEmpty)?.let { MaskBounds(it, pixels) }
    }

    private fun Rect.expanded(padding: Int, image: Bitmap): Rect = Rect(
        (left - padding).coerceAtLeast(0),
        (top - padding).coerceAtLeast(0),
        (right + padding).coerceAtMost(image.width),
        (bottom + padding).coerceAtMost(image.height)
    )

    private fun inpaintRegion(
        image: Bitmap,
        mask: Bitmap,
        region: Rect,
        width: Int,
        height: Int,
        outputName: String,
        runOptions: OrtSession.RunOptions?
    ): Bitmap {
        val scaled = width != region.width() || height != region.height()
        val inputImage = if (scaled) scaleRegion(image, region, width, height, false) else image
        val inputMask = if (scaled) scaleRegion(mask, region, width, height, true) else mask
        val inputRegion = if (scaled) Rect(0, 0, width, height) else region
        val inputWidth = width.alignedSize()
        val inputHeight = height.alignedSize()
        try {
            bitmapToTensor(
                inputImage,
                inputRegion,
                inputWidth,
                inputHeight,
                false
            ).use { tensorImg ->
                bitmapToTensor(
                    inputMask,
                    inputRegion,
                    inputWidth,
                    inputHeight,
                    true
                ).use { tensorMask ->
                    val outputBuffer = floatBuffer(3 * inputWidth * inputHeight)
                    OnnxTensor.createTensor(
                        OrtEnvironment.getEnvironment(), outputBuffer,
                        longArrayOf(1, 3, inputHeight.toLong(), inputWidth.toLong())
                    ).use { output ->
                        session.run(
                            mapOf("image" to tensorImg, "mask" to tensorMask),
                            emptySet(), mapOf(outputName to output), runOptions
                        ).use {
                            return outputBufferToBitmap(
                                outputBuffer, width, height, inputWidth, inputHeight
                            )
                        }
                    }
                }
            }
        } finally {
            if (scaled) {
                inputImage.recycle()
                inputMask.recycle()
            }
        }
    }

    private fun inpaintTiled(
        image: Bitmap,
        mask: Bitmap,
        selection: MaskBounds,
        region: Rect,
        runOptions: OrtSession.RunOptions?
    ): Bitmap {
        val scale = minOf(1f, OVERVIEW_SIZE.toFloat() / maxOf(region.width(), region.height()))
        val overview = if (selection.pixels > 256 * 256) {
            val prediction = inpaintRegion(
                image, mask, region,
                (region.width() * scale).roundToInt().coerceAtLeast(1),
                (region.height() * scale).roundToInt().coerceAtLeast(1),
                "prediction", runOptions
            )
            prediction.scale(region.width(), region.height()).also {
                if (it !== prediction) prediction.recycle()
            }
        } else null
        val result = image.copy(Bitmap.Config.ARGB_8888, true)
        try {
            val bounds = selection.bounds
            for (y in bounds.top until bounds.bottom step TILE_SIZE) {
                for (x in bounds.left until bounds.right step TILE_SIZE) {
                    val core = Rect(
                        x,
                        y,
                        minOf(x + TILE_SIZE, bounds.right),
                        minOf(y + TILE_SIZE, bounds.bottom)
                    )
                    val tileMask = maskBounds(mask, core) ?: continue
                    val tile = tileMask.bounds.expanded(
                        if (maxOf(
                                tileMask.bounds.width(),
                                tileMask.bounds.height()
                            ) < 256
                        ) CONTEXT_PADDING else TILE_PADDING,
                        image
                    )
                    val prediction = inpaintRegion(
                        image, mask, tile, tile.width(), tile.height(), "prediction", runOptions
                    )
                    val low = if (overview != null) {
                        val small = prediction.scale(
                            (prediction.width * scale).roundToInt().coerceAtLeast(1),
                            (prediction.height * scale).roundToInt().coerceAtLeast(1)
                        )
                        small.scale(prediction.width, prediction.height).also {
                            if (small !== prediction && small !== it) small.recycle()
                        }
                    } else null
                    try {
                        compositeTile(
                            result,
                            mask,
                            prediction,
                            tileMask.bounds,
                            tile,
                            overview,
                            region,
                            low
                        )
                    } finally {
                        if (low !== prediction) low?.recycle()
                        prediction.recycle()
                    }
                }
            }
            return result
        } catch (throwable: Throwable) {
            result.recycle()
            throw throwable
        } finally {
            overview?.recycle()
        }
    }

    private fun compositeTile(
        result: Bitmap,
        mask: Bitmap,
        prediction: Bitmap,
        core: Rect,
        tile: Rect,
        overview: Bitmap?,
        region: Rect,
        low: Bitmap?
    ) {
        val original = IntArray(core.width())
        val maskRow = IntArray(core.width())
        val patch = IntArray(core.width())
        val guide = IntArray(core.width())
        val lowRow = IntArray(core.width())
        for (y in core.top until core.bottom) {
            result.getPixels(original, 0, original.size, core.left, y, original.size, 1)
            mask.getPixels(maskRow, 0, maskRow.size, core.left, y, maskRow.size, 1)
            prediction.getPixels(
                patch,
                0,
                patch.size,
                core.left - tile.left,
                y - tile.top,
                patch.size,
                1
            )
            overview?.getPixels(
                guide,
                0,
                guide.size,
                core.left - region.left,
                y - region.top,
                guide.size,
                1
            )
            low?.getPixels(
                lowRow,
                0,
                lowRow.size,
                core.left - tile.left,
                y - tile.top,
                lowRow.size,
                1
            )
            for (x in original.indices) {
                if (Color.red(maskRow[x]) > 0 && Color.alpha(maskRow[x]) > 0) {
                    val color = if (overview != null && low != null) {
                        Color.rgb(
                            (Color.red(guide[x]) + Color.red(patch[x]) - Color.red(lowRow[x])).coerceIn(
                                0,
                                255
                            ),
                            (Color.green(guide[x]) + Color.green(patch[x]) - Color.green(lowRow[x])).coerceIn(
                                0,
                                255
                            ),
                            (Color.blue(guide[x]) + Color.blue(patch[x]) - Color.blue(lowRow[x])).coerceIn(
                                0,
                                255
                            )
                        )
                    } else patch[x]
                    original[x] = (color and 0x00FFFFFF) or (original[x] and -0x1000000)
                }
            }
            result.setPixels(original, 0, original.size, core.left, y, original.size, 1)
        }
    }

    private fun scaleRegion(
        source: Bitmap,
        region: Rect,
        width: Int,
        height: Int,
        isMask: Boolean
    ): Bitmap = createBitmap(width, height).apply {
        Canvas(this).drawBitmap(
            source,
            region,
            Rect(0, 0, width, height),
            Paint().apply { isFilterBitmap = !isMask }
        )
    }

    private fun Int.alignedSize(): Int =
        ((this + MODEL_ALIGNMENT - 1) / MODEL_ALIGNMENT * MODEL_ALIGNMENT).coerceAtLeast(32)

    private fun reflect(coordinate: Int, size: Int): Int {
        if (size == 1) return 0
        val position = coordinate % (2 * (size - 1))
        return if (position < size) position else 2 * (size - 1) - position
    }

    private fun composite(source: Bitmap, mask: Bitmap, patch: Bitmap, region: Rect): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val originalRow = IntArray(region.width())
        val maskRow = IntArray(region.width())
        val patchRow = IntArray(region.width())
        for (y in 0 until region.height()) {
            source.getPixels(
                originalRow,
                0,
                region.width(),
                region.left,
                region.top + y,
                region.width(),
                1
            )
            mask.getPixels(
                maskRow,
                0,
                region.width(),
                region.left,
                region.top + y,
                region.width(),
                1
            )
            patch.getPixels(patchRow, 0, region.width(), 0, y, region.width(), 1)
            for (x in originalRow.indices) {
                if (Color.red(maskRow[x]) > 0 && Color.alpha(maskRow[x]) > 0) {
                    originalRow[x] = (patchRow[x] and 0x00FFFFFF) or (originalRow[x] and -0x1000000)
                }
            }
            result.setPixels(
                originalRow,
                0,
                region.width(),
                region.left,
                region.top + y,
                region.width(),
                1
            )
        }
        return result
    }

    private fun floatBuffer(size: Int): FloatBuffer = ByteBuffer
        .allocateDirect(size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private fun bitmapToTensor(
        bitmap: Bitmap,
        region: Rect,
        width: Int,
        height: Int,
        isMask: Boolean
    ): OnnxTensor {
        val channels = if (isMask) 1 else 3
        val channelSize = width * height
        val data = floatBuffer(channels * channelSize)
        val pixels = IntArray(region.width())
        val row = FloatArray(width)
        for (y in 0 until height) {
            if (isMask && y >= region.height()) continue
            bitmap.getPixels(
                pixels, 0, pixels.size, region.left,
                region.top + reflect(y, region.height()), pixels.size, 1
            )
            for (channel in 0 until channels) {
                for (x in 0 until width) {
                    val color = pixels[reflect(x, pixels.size)]
                    row[x] = if (isMask) {
                        if (x < pixels.size && Color.red(color) > 0 && Color.alpha(color) > 0) 1f else 0f
                    } else {
                        ((color shr (16 - channel * 8)) and 0xFF) / 255f
                    }
                }
                data.position(channel * channelSize + y * width)
                data.put(row)
            }
        }
        data.rewind()
        return OnnxTensor.createTensor(
            OrtEnvironment.getEnvironment(),
            data,
            longArrayOf(1, channels.toLong(), height.toLong(), width.toLong())
        )
    }

    private fun outputBufferToBitmap(
        buffer: FloatBuffer,
        width: Int,
        height: Int,
        inputWidth: Int,
        inputHeight: Int
    ): Bitmap {
        val channelSize = inputWidth * inputHeight
        val result = createBitmap(width, height)
        val red = FloatArray(width)
        val green = FloatArray(width)
        val blue = FloatArray(width)
        val pixels = IntArray(width)
        for (y in 0 until height) {
            buffer.position(y * inputWidth)
            buffer.get(red)
            buffer.position(channelSize + y * inputWidth)
            buffer.get(green)
            buffer.position(2 * channelSize + y * inputWidth)
            buffer.get(blue)
            for (x in pixels.indices) {
                pixels[x] = Color.rgb(
                    red[x].toInt().coerceIn(0, 255),
                    green[x].toInt().coerceIn(0, 255),
                    blue[x].toInt().coerceIn(0, 255)
                )
            }
            result.setPixels(pixels, 0, width, 0, y, width, 1)
        }
        return result
    }
}