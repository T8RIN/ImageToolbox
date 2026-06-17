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

@file:Suppress("TooManyFunctions", "MagicNumber")

package com.t8rin.neural_tools.ocr

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
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
import java.util.ArrayDeque
import java.util.zip.ZipInputStream
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

object PaddleOCR : NeuralTool() {

    enum class Model(
        val title: String,
        val fileName: String
    ) {
        CJK("CJK", "paddleocr_v5.zip"),
        Korean("Korean", "paddleocr_v5_korean.zip"),
        Latin("Latin", "paddleocr_v5_latin.zip"),
        EastSlavic("East Slavic", "paddleocr_v5_eslav.zip"),
        Thai("Thai", "paddleocr_v5_th.zip"),
        Greek("Greek", "paddleocr_v5_el.zip"),
        English("English", "paddleocr_v5_en.zip"),
        Cyrillic("Cyrillic", "paddleocr_v5_cyrillic.zip"),
        Arabic("Arabic", "paddleocr_v5_arabic.zip"),
        Devanagari("Devanagari", "paddleocr_v5_devanagari.zip"),
        Tamil("Tamil", "paddleocr_v5_ta.zip"),
        Telugu("Telugu", "paddleocr_v5_te.zip"),
        UniversalV6("PaddleOCRv6", "paddleocr_v6_medium.zip")
    }

    private val env: OrtEnvironment by lazy { OrtEnvironment.getEnvironment() }
    private var processor: OcrProcessor? = null
    private var processorModel: Model? = null

    private val directory: File
        get() = File(context.filesDir, "ai_models/paddleocr").apply(File::mkdirs)

    private fun modelDirectory(model: Model): File = File(directory, model.name).apply(File::mkdirs)

    private fun modelUrl(model: Model): String =
        "https://huggingface.co/T8RIN/imagetoolbox-models/resolve/main/paddleocr/${model.fileName}?download=true"

    private fun modelFiles(model: Model): ModelFiles? {
        fun resolveFrom(folder: File): ModelFiles? {
            val files = folder.walkTopDown().filter(File::isFile).toList()
            val det = files.firstOrNull {
                it.name == "det.onnx" || it.name.contains(
                    "det",
                    true
                ) && it.extension == "onnx"
            }
            val rec = files.firstOrNull {
                it.name == "rec.onnx" || it.name.contains(
                    "rec",
                    true
                ) && it.extension == "onnx"
            }
            val cls = files.firstOrNull {
                it.name == "cls.onnx" || it.name.contains(
                    "cls",
                    true
                ) && it.extension == "onnx"
            }
            val dict = files.firstOrNull {
                it.name == "ppocrv5_dict.txt" || it.name.contains(
                    "dict",
                    true
                ) && it.extension == "txt"
            }

            return if (det != null && rec != null && cls != null && dict != null) {
                ModelFiles(
                    detectionModel = det,
                    recognitionModel = rec,
                    classificationModel = cls,
                    dictionaryFile = dict
                )
            } else {
                null
            }
        }

        return resolveFrom(modelDirectory(model))
    }

    private val _isDownloaded = MutableStateFlow(modelFiles(Model.CJK) != null)
    val isDownloaded: StateFlow<Boolean> = _isDownloaded

    fun isModelDownloaded(model: Model = Model.CJK): Boolean = modelFiles(model) != null

    fun checkModel(model: Model = Model.CJK): Boolean =
        (modelFiles(model) != null).also { downloaded ->
            _isDownloaded.update { downloaded }
            if (!downloaded) close()
        }

    fun deleteModel(model: Model) {
        if (processorModel == model) close()
        modelDirectory(model).deleteRecursively()
        _isDownloaded.update { isModelDownloaded(Model.CJK) }
    }

    fun startDownload(
        model: Model = Model.CJK,
        forced: Boolean = false
    ): Flow<DownloadProgress> = callbackFlow {
        if (!forced && checkModel(model)) {
            trySend(DownloadProgress(currentPercent = 1f, currentTotalSize = 0L))
            close()
            return@callbackFlow
        }

        val targetDirectory = modelDirectory(model)
        val zipFile = File(targetDirectory, "${model.fileName}.tmp")
        httpClient.prepareGet(modelUrl(model)).execute { response ->
            val total = response.contentLength() ?: -1L
            val channel = response.bodyAsChannel()
            var downloaded = 0L

            FileOutputStream(zipFile).use { fos ->
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

                    unzipModels(zipFile, targetDirectory)
                    _isDownloaded.update { checkModel(model) }
                    close()
                } catch (e: Throwable) {
                    close(e)
                } finally {
                    zipFile.delete()
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    fun recognize(
        image: Bitmap,
        model: Model = Model.CJK
    ): PaddleOCRResult? {
        if (!checkModel(model)) return null

        val files = modelFiles(model) ?: return null
        val ocrProcessor = processor?.takeIf { processorModel == model } ?: OcrProcessor(
            modelFiles = files,
            ortEnv = env
        ).also {
            close()
            processor = it
            processorModel = model
        }

        val result = ocrProcessor.processImage(image)
        return PaddleOCRResult(
            text = result.texts.joinToString(separator = "\n"),
            accuracy = if (result.scores.isEmpty()) 0 else (result.scores.average() * 100).roundToInt(),
            hocr = result.toHocr(image.width, image.height)
        )
    }

    fun close() {
        processor?.close()
        processor = null
        processorModel = null
    }

    private fun unzipModels(
        zipFile: File,
        targetDirectory: File
    ) {
        ZipInputStream(zipFile.inputStream()).use { zip ->
            generateSequence { zip.nextEntry }.forEach { entry ->
                if (!entry.isDirectory) {
                    val outFile = File(targetDirectory, entry.name.substringAfterLast('/'))
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { output ->
                        zip.copyTo(output)
                    }
                }
                zip.closeEntry()
            }
        }
    }
}

data class PaddleOCRResult(
    val text: String,
    val accuracy: Int,
    val hocr: String
)

private data class ModelFiles(
    val detectionModel: File,
    val recognitionModel: File,
    val classificationModel: File,
    val dictionaryFile: File
)

private data class OcrResult(
    val boxes: List<TextBox>,
    val texts: List<String>,
    val scores: List<Float>
) {
    fun toHocr(
        imageWidth: Int,
        imageHeight: Int
    ): String = buildString {
        append("""<div class="ocr_page" id="page_1" title="bbox 0 0 $imageWidth $imageHeight">""")
        append('\n')
        boxes.forEachIndexed { index, box ->
            val rect = box.boundingRect()
            val text = texts.getOrNull(index).orEmpty().escapeHocr()
            append(
                """  <span class="ocr_line" id="line_${index + 1}" title="bbox ${rect.left} ${rect.top} ${rect.right} ${rect.bottom}">"""
            )
            append(
                """<span class="ocrx_word" id="word_${index + 1}" title="bbox ${rect.left} ${rect.top} ${rect.right} ${rect.bottom}">$text</span>"""
            )
            append("</span>")
            append('\n')
        }
        append("</div>")
    }
}

private data class TextBox(
    val points: List<PointF>
) {
    fun boundingRect(): BoundingRect {
        val minX = points.minOfOrNull { it.x }?.floorToInt() ?: 0
        val minY = points.minOfOrNull { it.y }?.floorToInt() ?: 0
        val maxX = points.maxOfOrNull { it.x }?.ceilToInt() ?: 0
        val maxY = points.maxOfOrNull { it.y }?.ceilToInt() ?: 0
        return BoundingRect(minX, minY, maxX, maxY)
    }
}

private data class BoundingRect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

private fun Float.floorToInt(): Int = floor(toDouble()).toInt()

private fun Float.ceilToInt(): Int = ceil(toDouble()).toInt()

private fun String.escapeHocr(): String = buildString {
    this@escapeHocr.forEach { char ->
        append(
            when (char) {
                '&' -> "&amp;"
                '<' -> "&lt;"
                '>' -> "&gt;"
                '"' -> "&quot;"
                '\'' -> "&#39;"
                else -> char
            }
        )
    }
}

private class OcrProcessor(
    private val modelFiles: ModelFiles,
    private val ortEnv: OrtEnvironment
) {

    private val sessionOptions = OrtSession.SessionOptions().apply {
        setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT)
    }

    private val detectionSession = ortEnv.createSession(
        modelFiles.detectionModel.absolutePath,
        sessionOptions
    )
    private val recognitionSession = ortEnv.createSession(
        modelFiles.recognitionModel.absolutePath,
        sessionOptions
    )
    private val classificationSession = ortEnv.createSession(
        modelFiles.classificationModel.absolutePath,
        sessionOptions
    )
    private val characterDict = modelFiles.dictionaryFile.inputStream().use { stream ->
        stream.bufferedReader().readLines().toMutableList().apply {
            add(" ")
        }.let { listOf("blank") + it }
    }

    fun processImage(bitmap: Bitmap): OcrResult {
        val detectionResult = TextDetector(detectionSession, ortEnv).detect(bitmap)
        if (detectionResult.isEmpty()) {
            return OcrResult(emptyList(), emptyList(), emptyList())
        }

        val croppedImages = detectionResult.map { box ->
            ImageUtils.cropTextRegion(bitmap, ImageUtils.orderPointsClockwise(box.points))
        }.toMutableList()

        val classificationMask = BooleanArray(croppedImages.size)
        val rotationStates = BooleanArray(croppedImages.size)
        val aspectCandidates = croppedImages.mapIndexedNotNull { index, image ->
            val aspectRatio = image.width.toFloat() / image.height
            if (aspectRatio < 0.5f) index else null
        }
        classifyAndRotateIndices(
            croppedImages,
            aspectCandidates,
            classificationMask,
            rotationStates
        )

        val recognitionResults = TextRecognizer(
            session = recognitionSession,
            ortEnv = ortEnv,
            characterDict = characterDict
        ).recognize(croppedImages).toMutableList()

        val lowConfidenceIndices = recognitionResults.mapIndexedNotNull { index, result ->
            if (!classificationMask[index] && result.confidence < 0.65f) index else null
        }
        if (lowConfidenceIndices.isNotEmpty()) {
            classifyAndRotateIndices(
                croppedImages,
                lowConfidenceIndices,
                classificationMask,
                rotationStates
            )
            val refreshed = TextRecognizer(recognitionSession, ortEnv, characterDict).recognize(
                lowConfidenceIndices.map { croppedImages[it] }
            )
            lowConfidenceIndices.forEachIndexed { refreshedIndex, originalIndex ->
                val current = recognitionResults[originalIndex]
                val updated = refreshed[refreshedIndex]
                recognitionResults[originalIndex] =
                    if (updated.confidence > current.confidence) updated else current
            }
        }

        val boxes = mutableListOf<TextBox>()
        val texts = mutableListOf<String>()
        val scores = mutableListOf<Float>()

        recognitionResults.forEachIndexed { index, recognition ->
            if (recognition.confidence >= 0.5f && recognition.text.isNotBlank()) {
                boxes.add(detectionResult[index])
                texts.add(recognition.text)
                scores.add(recognition.confidence)
            }
        }

        return OcrResult(boxes, texts, scores)
    }

    private fun classifyAndRotateIndices(
        images: MutableList<Bitmap>,
        indices: List<Int>,
        classificationMask: BooleanArray,
        rotationStates: BooleanArray
    ) {
        if (indices.isEmpty()) return

        val outputs = TextClassifier(
            session = classificationSession,
            ortEnv = ortEnv
        ).classifyAndRotate(indices.map { images[it] })

        indices.forEachIndexed { idx, imageIndex ->
            classificationMask[imageIndex] = true
            val output = outputs[idx]
            val old = images[imageIndex]
            if (output.rotated) {
                rotationStates[imageIndex] = !rotationStates[imageIndex]
            }
            images[imageIndex] = output.bitmap
            if (output.rotated && output.bitmap !== old && !old.isRecycled) {
                old.recycle()
            }
        }
    }

    fun close() {
        detectionSession.close()
        recognitionSession.close()
        classificationSession.close()
    }
}

private data class DetectionCandidate(
    val box: TextBox,
    val score: Float
)

private class TextDetector(
    private val session: OrtSession,
    private val ortEnv: OrtEnvironment
) {

    fun detect(bitmap: Bitmap): List<TextBox> {
        val boxes = mutableListOf<TextBox>()
        runDetection(bitmap) { box, _ ->
            boxes.add(box)
            false
        }
        return sortBoxes(boxes)
    }

    private fun runDetection(
        bitmap: Bitmap,
        handler: (TextBox, Float) -> Boolean
    ) {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val (inputTensor, resizedWidth, resizedHeight) = preprocessImage(bitmap)
        var output: OnnxTensor? = null

        try {
            output = session.run(mapOf("x" to inputTensor))[0] as OnnxTensor
            postprocessDetection(
                output = output,
                originalWidth = originalWidth,
                originalHeight = originalHeight,
                resizedWidth = resizedWidth,
                resizedHeight = resizedHeight,
                handler = handler
            )
        } finally {
            output?.close()
            inputTensor.close()
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Triple<OnnxTensor, Int, Int> {
        val (resizedWidth, resizedHeight) = calculateResizeDimensions(bitmap.width, bitmap.height)
        val resizedBitmap = bitmap.scale(resizedWidth, resizedHeight)
        val inputArray = FloatArray(3 * resizedHeight * resizedWidth)
        val pixels = IntArray(resizedWidth * resizedHeight)
        resizedBitmap.getPixels(pixels, 0, resizedWidth, 0, 0, resizedWidth, resizedHeight)

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)
        var pixelIndex = 0

        for (y in 0 until resizedHeight) {
            for (x in 0 until resizedWidth) {
                val pixel = pixels[y * resizedWidth + x]
                val b = (pixel and 0xFF) / 255f
                val g = ((pixel shr 8) and 0xFF) / 255f
                val r = ((pixel shr 16) and 0xFF) / 255f

                inputArray[pixelIndex] = (b - mean[0]) / std[0]
                inputArray[pixelIndex + resizedHeight * resizedWidth] = (g - mean[1]) / std[1]
                inputArray[pixelIndex + 2 * resizedHeight * resizedWidth] = (r - mean[2]) / std[2]
                pixelIndex++
            }
        }

        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) {
            resizedBitmap.recycle()
        }

        return Triple(
            OnnxTensor.createTensor(
                ortEnv,
                FloatBuffer.wrap(inputArray),
                longArrayOf(1, 3, resizedHeight.toLong(), resizedWidth.toLong())
            ),
            resizedWidth,
            resizedHeight
        )
    }

    private fun calculateResizeDimensions(width: Int, height: Int): Pair<Int, Int> {
        val ratio = if (max(width, height) > 960) {
            960f / max(width, height)
        } else {
            1f
        }
        val resizedWidth = max((((width * ratio).roundToInt() + 31) / 32) * 32, 32)
        val resizedHeight = max((((height * ratio).roundToInt() + 31) / 32) * 32, 32)
        return resizedWidth to resizedHeight
    }

    private fun postprocessDetection(
        output: OnnxTensor,
        originalWidth: Int,
        originalHeight: Int,
        resizedWidth: Int,
        resizedHeight: Int,
        handler: (TextBox, Float) -> Boolean
    ) {
        val outputArray = ImageUtils.toFloatArray(output.floatBuffer)
        val probMap = Array(resizedHeight) { y ->
            FloatArray(resizedWidth) { x -> outputArray[y * resizedWidth + x] }
        }
        val binaryMap = Array(resizedHeight) { y ->
            BooleanArray(resizedWidth) { x -> probMap[y][x] > 0.3f }
        }
        val scaleX = originalWidth.toFloat() / resizedWidth
        val scaleY = originalHeight.toFloat() / resizedHeight

        extractConnectedComponents(binaryMap)
            .sortedByDescending { it.size }
            .take(1000)
            .forEach { component ->
                if (component.size < 4) return@forEach

                val hull = convexHull(component)
                if (hull.size < 3) return@forEach

                val rect = minimumAreaRectangle(hull, true)
                if (rect.isEmpty()) return@forEach

                val score = calculateBoxScore(probMap, rect)
                if (score < 0.6f) return@forEach

                val expanded = unclipBox(rect, 1.5f)
                val expandedRect = minimumAreaRectangle(expanded, false)
                if (expandedRect.isEmpty() || getMinSide(expandedRect) < 3f) return@forEach

                val points = ImageUtils.orderPointsClockwise(
                    ImageUtils.clipBoxToImageBounds(expandedRect, resizedWidth, resizedHeight)
                        .map { point -> PointF(point.x * scaleX, point.y * scaleY) }
                )

                if (handler(TextBox(points), score)) return
            }
    }

    private fun extractConnectedComponents(binaryMap: Array<BooleanArray>): List<List<PointF>> {
        val height = binaryMap.size
        val width = binaryMap.firstOrNull()?.size ?: 0
        val visited = Array(height) { BooleanArray(width) }
        val components = mutableListOf<List<PointF>>()
        val stack = ArrayDeque<Pair<Int, Int>>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (!binaryMap[y][x] || visited[y][x]) continue

                val points = mutableListOf<PointF>()
                stack.clear()
                stack.add(x to y)
                visited[y][x] = true

                while (stack.isNotEmpty()) {
                    val (cx, cy) = stack.removeLast()
                    points.add(PointF(cx.toFloat(), cy.toFloat()))

                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            val nx = cx + dx
                            val ny = cy + dy
                            if ((dx != 0 || dy != 0) && nx in 0 until width && ny in 0 until height &&
                                binaryMap[ny][nx] && !visited[ny][nx]
                            ) {
                                visited[ny][nx] = true
                                stack.add(nx to ny)
                            }
                        }
                    }
                }
                components.add(points)
            }
        }
        return components
    }

    private fun calculateBoxScore(probMap: Array<FloatArray>, polygon: List<PointF>): Float {
        var minX = floor(polygon.minOf { it.x.toDouble() }).toInt()
        var maxX = ceil(polygon.maxOf { it.x.toDouble() }).toInt()
        var minY = floor(polygon.minOf { it.y.toDouble() }).toInt()
        var maxY = ceil(polygon.maxOf { it.y.toDouble() }).toInt()

        minX = min(max(minX, 0), probMap[0].size - 1)
        maxX = min(max(maxX, 0), probMap[0].size - 1)
        minY = min(max(minY, 0), probMap.size - 1)
        maxY = min(max(maxY, 0), probMap.size - 1)

        var sum = 0f
        var count = 0
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (isPointInsideQuad(x + 0.5f, y + 0.5f, polygon)) {
                    sum += probMap[y][x]
                    count++
                }
            }
        }
        return if (count > 0) sum / count else 0f
    }

    private fun minimumAreaRectangle(points: List<PointF>, pointsAreConvex: Boolean): List<PointF> {
        val hull = if (pointsAreConvex) points else convexHull(points)
        if (hull.size < 3) return emptyList()

        var bestRect = emptyList<PointF>()
        var minArea = Float.MAX_VALUE

        for (i in hull.indices) {
            val p1 = hull[i]
            val p2 = hull[(i + 1) % hull.size]
            val edgeVec = normalizeVector(p1, p2) ?: continue
            val normal = PointF(-edgeVec.y, edgeVec.x)
            var minProj = Float.MAX_VALUE
            var maxProj = -Float.MAX_VALUE
            var minOrth = Float.MAX_VALUE
            var maxOrth = -Float.MAX_VALUE

            for (pt in hull) {
                val relX = pt.x - p1.x
                val relY = pt.y - p1.y
                val projection = relX * edgeVec.x + relY * edgeVec.y
                val orthProjection = relX * normal.x + relY * normal.y
                minProj = min(minProj, projection)
                maxProj = max(maxProj, projection)
                minOrth = min(minOrth, orthProjection)
                maxOrth = max(maxOrth, orthProjection)
            }

            val area = (maxProj - minProj) * (maxOrth - minOrth)
            if (area < minArea && maxProj - minProj > 1e-3f && maxOrth - minOrth > 1e-3f) {
                minArea = area
                bestRect = listOf(
                    PointF(
                        p1.x + edgeVec.x * minProj + normal.x * minOrth,
                        p1.y + edgeVec.y * minProj + normal.y * minOrth
                    ),
                    PointF(
                        p1.x + edgeVec.x * maxProj + normal.x * minOrth,
                        p1.y + edgeVec.y * maxProj + normal.y * minOrth
                    ),
                    PointF(
                        p1.x + edgeVec.x * maxProj + normal.x * maxOrth,
                        p1.y + edgeVec.y * maxProj + normal.y * maxOrth
                    ),
                    PointF(
                        p1.x + edgeVec.x * minProj + normal.x * maxOrth,
                        p1.y + edgeVec.y * minProj + normal.y * maxOrth
                    )
                )
            }
        }
        return bestRect.ifEmpty { axisAlignedBoundingBox(hull) }
    }

    private fun normalizeVector(from: PointF, to: PointF): PointF? {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val length = sqrt(dx * dx + dy * dy)
        return if (length < 1e-6f) null else PointF(dx / length, dy / length)
    }

    private fun axisAlignedBoundingBox(points: List<PointF>): List<PointF> {
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        return listOf(
            PointF(minX, minY),
            PointF(maxX, minY),
            PointF(maxX, maxY),
            PointF(minX, maxY)
        )
    }

    private fun isPointInsideQuad(x: Float, y: Float, quad: List<PointF>): Boolean {
        var hasPositive = false
        var hasNegative = false
        for (i in quad.indices) {
            val p1 = quad[i]
            val p2 = quad[(i + 1) % quad.size]
            val cross = (p2.x - p1.x) * (y - p1.y) - (p2.y - p1.y) * (x - p1.x)
            if (cross > 0) hasPositive = true else if (cross < 0) hasNegative = true
            if (hasPositive && hasNegative) return false
        }
        return true
    }

    private fun convexHull(points: List<PointF>): List<PointF> {
        if (points.size < 3) return points

        val sorted = points.sortedWith(compareBy({ it.x }, { it.y }))
        val lower = mutableListOf<PointF>()
        val upper = mutableListOf<PointF>()

        sorted.forEach { point ->
            while (lower.size >= 2 && crossProduct(
                    lower[lower.size - 2],
                    lower.last(),
                    point
                ) <= 0
            ) {
                lower.removeAt(lower.lastIndex)
            }
            lower.add(point)
        }
        sorted.asReversed().forEach { point ->
            while (upper.size >= 2 && crossProduct(
                    upper[upper.size - 2],
                    upper.last(),
                    point
                ) <= 0
            ) {
                upper.removeAt(upper.lastIndex)
            }
            upper.add(point)
        }

        lower.removeAt(lower.lastIndex)
        upper.removeAt(upper.lastIndex)
        return lower + upper
    }

    private fun crossProduct(o: PointF, a: PointF, b: PointF): Float =
        (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)

    private fun distance(p1: PointF, p2: PointF): Float {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return sqrt(dx * dx + dy * dy)
    }

    private fun sortBoxes(boxes: List<TextBox>): List<TextBox> {
        val sortedByTop = boxes.sortedBy { box -> box.points.minOf { it.y } }
        val ordered = mutableListOf<TextBox>()
        var index = 0
        while (index < sortedByTop.size) {
            val current = sortedByTop[index]
            val referenceY = current.points.minOf { it.y }
            val group = mutableListOf<TextBox>()
            var j = index
            while (j < sortedByTop.size && abs(sortedByTop[j].points.minOf { it.y } - referenceY) <= 10f) {
                group.add(sortedByTop[j])
                j++
            }
            group.sortBy { box -> box.points.minOf { it.x } }
            ordered.addAll(group)
            index = j
        }
        return ordered
    }

    private fun unclipBox(box: List<PointF>, unclipRatio: Float): List<PointF> {
        val perimeter = polygonPerimeter(box)
        if (perimeter <= 1e-6f) return emptyList()

        val offset = abs(polygonSignedArea(box)) * unclipRatio / perimeter
        return offsetPolygon(box, offset).takeIf { it.size >= 3 } ?: emptyList()
    }

    private fun getMinSide(box: List<PointF>): Float =
        box.indices.minOf { i -> distance(box[i], box[(i + 1) % box.size]) }

    private fun polygonSignedArea(points: List<PointF>): Float {
        var area = 0f
        for (i in points.indices) {
            val j = (i + 1) % points.size
            area += points[i].x * points[j].y - points[j].x * points[i].y
        }
        return area / 2f
    }

    private fun polygonPerimeter(points: List<PointF>): Float =
        points.indices.sumOf { i -> distance(points[i], points[(i + 1) % points.size]).toDouble() }
            .toFloat()

    private fun offsetPolygon(points: List<PointF>, offset: Float): List<PointF> {
        if (points.size < 3) return emptyList()

        val isCounterClockwise = polygonSignedArea(points) > 0f
        return points.indices.mapNotNull { i ->
            val prev = points[(i - 1 + points.size) % points.size]
            val curr = points[i]
            val next = points[(i + 1) % points.size]
            val dir1 = normalize(PointF(curr.x - prev.x, curr.y - prev.y)) ?: return@mapNotNull null
            val dir2 = normalize(PointF(next.x - curr.x, next.y - curr.y)) ?: return@mapNotNull null
            val normal1 =
                if (isCounterClockwise) PointF(dir1.y, -dir1.x) else PointF(-dir1.y, dir1.x)
            val normal2 =
                if (isCounterClockwise) PointF(dir2.y, -dir2.x) else PointF(-dir2.y, dir2.x)
            intersectLines(
                PointF(curr.x + normal1.x * offset, curr.y + normal1.y * offset),
                dir1,
                PointF(curr.x + normal2.x * offset, curr.y + normal2.y * offset),
                dir2
            ) ?: PointF(curr.x, curr.y)
        }
    }

    private fun normalize(vector: PointF): PointF? {
        val length = sqrt(vector.x * vector.x + vector.y * vector.y)
        return if (length < 1e-6f) null else PointF(vector.x / length, vector.y / length)
    }

    private fun intersectLines(
        point: PointF,
        direction: PointF,
        otherPoint: PointF,
        otherDirection: PointF
    ): PointF? {
        val cross = direction.x * otherDirection.y - direction.y * otherDirection.x
        if (abs(cross) < 1e-6f) return null

        val diffX = otherPoint.x - point.x
        val diffY = otherPoint.y - point.y
        val t = (diffX * otherDirection.y - diffY * otherDirection.x) / cross
        return PointF(point.x + direction.x * t, point.y + direction.y * t)
    }
}

private data class ClassificationOutput(
    val bitmap: Bitmap,
    val rotated: Boolean
)

private class TextClassifier(
    private val session: OrtSession,
    private val ortEnv: OrtEnvironment
) {

    fun classifyAndRotate(images: List<Bitmap>): List<ClassificationOutput> =
        images.chunked(6).flatMap { batch ->
            val flags = classifyBatch(batch)
            batch.indices.map { index ->
                if (flags[index]) {
                    ClassificationOutput(rotateImage180(batch[index]), true)
                } else {
                    ClassificationOutput(batch[index], false)
                }
            }
        }

    private fun classifyBatch(batchImages: List<Bitmap>): List<Boolean> {
        val inputArray = FloatArray(batchImages.size * 3 * 48 * 192)
        batchImages.forEachIndexed { index, image ->
            preprocessImage(image, inputArray, index)
        }

        val inputTensor = OnnxTensor.createTensor(
            ortEnv,
            FloatBuffer.wrap(inputArray),
            longArrayOf(batchImages.size.toLong(), 3, 48, 192)
        )
        val output = session.run(mapOf(session.inputNames.first() to inputTensor))[0] as OnnxTensor
        val outputArray = ImageUtils.toFloatArray(output.floatBuffer)
        val result = List(batchImages.size) { index ->
            val baseOffset = index * 2
            outputArray[baseOffset + 1] > outputArray[baseOffset] && outputArray[baseOffset + 1] > 0.9f
        }

        output.close()
        inputTensor.close()
        return result
    }

    private fun preprocessImage(bitmap: Bitmap, outputArray: FloatArray, batchIndex: Int) {
        val resizedWidth =
            min(ceil(48f * bitmap.width / bitmap.height).toInt().coerceAtLeast(1), 192)
        val resizedBitmap = bitmap.scale(resizedWidth, 48)
        val pixels = IntArray(resizedWidth * 48)
        resizedBitmap.getPixels(pixels, 0, resizedWidth, 0, 0, resizedWidth, 48)
        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) resizedBitmap.recycle()

        val baseOffset = batchIndex * 3 * 48 * 192
        val channelStride = 48 * 192
        for (y in 0 until 48) {
            val rowOffset = y * 192
            val sourceRowOffset = y * resizedWidth
            for (x in 0 until 192) {
                val pixelIndex = rowOffset + x
                if (x < resizedWidth) {
                    val pixel = pixels[sourceRowOffset + x]
                    outputArray[baseOffset + pixelIndex] = (((pixel and 0xFF) / 255f) - 0.5f) / 0.5f
                    outputArray[baseOffset + channelStride + pixelIndex] =
                        ((((pixel shr 8) and 0xFF) / 255f) - 0.5f) / 0.5f
                    outputArray[baseOffset + 2 * channelStride + pixelIndex] =
                        ((((pixel shr 16) and 0xFF) / 255f) - 0.5f) / 0.5f
                }
            }
        }
    }

    private fun rotateImage180(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { postRotate(180f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

private data class RecognitionResult(
    val text: String,
    val confidence: Float
)

private class TextRecognizer(
    private val session: OrtSession,
    private val ortEnv: OrtEnvironment,
    private val characterDict: List<String>
) {

    fun recognize(images: List<Bitmap>): List<RecognitionResult> {
        if (images.isEmpty()) return emptyList()

        val sortedIndices =
            images.indices.sortedBy { images[it].width.toFloat() / images[it].height }
        val orderedResults = MutableList(images.size) { RecognitionResult("", 0f) }

        sortedIndices.chunked(6).forEach { batchIndices ->
            val batchResults = processBatch(batchIndices.map { images[it] })
            batchIndices.forEachIndexed { idx, originalIndex ->
                orderedResults[originalIndex] = batchResults[idx]
            }
        }
        return orderedResults
    }

    private fun processBatch(batchImages: List<Bitmap>): List<RecognitionResult> {
        var maxWhRatio = 320f / 48f
        batchImages.forEach { image ->
            maxWhRatio = max(maxWhRatio, image.width.toFloat() / image.height)
        }
        val targetWidth = ceil(48f * maxWhRatio).toInt().coerceAtLeast(1)
        val inputArray = FloatArray(batchImages.size * 3 * 48 * targetWidth)
        batchImages.forEachIndexed { index, image ->
            preprocessImage(image, inputArray, index, targetWidth)
        }

        val inputTensor = OnnxTensor.createTensor(
            ortEnv,
            FloatBuffer.wrap(inputArray),
            longArrayOf(batchImages.size.toLong(), 3, 48, targetWidth.toLong())
        )
        val output = session.run(mapOf(session.inputNames.first() to inputTensor))[0] as OnnxTensor
        val results = decodeOutput(output, batchImages.size)

        output.close()
        inputTensor.close()
        return results
    }

    private fun preprocessImage(
        bitmap: Bitmap,
        outputArray: FloatArray,
        batchIndex: Int,
        targetWidth: Int
    ) {
        val resizedWidth =
            min(ceil(48f * bitmap.width / bitmap.height).toInt().coerceAtLeast(1), targetWidth)
        val resizedBitmap = bitmap.scale(resizedWidth, 48)
        val pixels = IntArray(resizedWidth * 48)
        resizedBitmap.getPixels(pixels, 0, resizedWidth, 0, 0, resizedWidth, 48)
        if (resizedBitmap !== bitmap && !resizedBitmap.isRecycled) resizedBitmap.recycle()

        val baseOffset = batchIndex * 3 * 48 * targetWidth
        val channelStride = 48 * targetWidth
        for (y in 0 until 48) {
            val rowOffset = y * targetWidth
            val sourceRowOffset = y * resizedWidth
            for (x in 0 until targetWidth) {
                val pixelIndex = rowOffset + x
                if (x < resizedWidth) {
                    val pixel = pixels[sourceRowOffset + x]
                    outputArray[baseOffset + pixelIndex] = (((pixel and 0xFF) / 255f) - 0.5f) / 0.5f
                    outputArray[baseOffset + channelStride + pixelIndex] =
                        ((((pixel shr 8) and 0xFF) / 255f) - 0.5f) / 0.5f
                    outputArray[baseOffset + 2 * channelStride + pixelIndex] =
                        ((((pixel shr 16) and 0xFF) / 255f) - 0.5f) / 0.5f
                }
            }
        }
    }

    private fun decodeOutput(output: OnnxTensor, batchSize: Int): List<RecognitionResult> {
        val outputArray = ImageUtils.toFloatArray(output.floatBuffer)
        val shape = output.info.shape
        val seqLen = shape[1].toInt()
        val vocabSize = shape[2].toInt()

        return List(batchSize) { batch ->
            val batchOffset = batch * seqLen * vocabSize
            val charIndices = IntArray(seqLen)
            val probs = FloatArray(seqLen)

            for (t in 0 until seqLen) {
                val timeOffset = batchOffset + t * vocabSize
                var maxProb = outputArray[timeOffset]
                var maxIndex = 0
                for (c in 1 until vocabSize) {
                    val prob = outputArray[timeOffset + c]
                    if (prob > maxProb) {
                        maxProb = prob
                        maxIndex = c
                    }
                }
                charIndices[t] = maxIndex
                probs[t] = maxProb
            }
            ctcDecode(charIndices, probs)
        }
    }

    private fun ctcDecode(charIndices: IntArray, probs: FloatArray): RecognitionResult {
        val decodedChars = mutableListOf<String>()
        val decodedProbs = mutableListOf<Float>()
        var t = 0

        while (t < charIndices.size) {
            val currentIndex = charIndices[t]
            if (currentIndex == 0) {
                t++
                continue
            }

            var end = t + 1
            var probSum = probs[t]
            var count = 1
            while (end < charIndices.size && charIndices[end] == currentIndex) {
                probSum += probs[end]
                end++
                count++
            }

            if (currentIndex < characterDict.size) {
                decodedChars.add(characterDict[currentIndex])
                decodedProbs.add(probSum / count)
            }
            t = end
        }

        return RecognitionResult(
            text = decodedChars.joinToString(""),
            confidence = decodedProbs.takeIf { it.isNotEmpty() }?.average()?.toFloat() ?: 0f
        )
    }
}

private object ImageUtils {

    fun toFloatArray(buffer: FloatBuffer): FloatArray {
        val duplicate = buffer.duplicate()
        duplicate.rewind()
        return FloatArray(duplicate.remaining()).also(duplicate::get)
    }

    fun cropTextRegion(bitmap: Bitmap, points: List<PointF>): Bitmap {
        val width = max(distance(points[0], points[1]), distance(points[2], points[3]))
            .toInt()
            .coerceAtLeast(1)
        val height = max(distance(points[0], points[3]), distance(points[1], points[2]))
            .toInt()
            .coerceAtLeast(1)

        val matrix = Matrix().apply {
            setPolyToPoly(
                floatArrayOf(
                    points[0].x,
                    points[0].y,
                    points[1].x,
                    points[1].y,
                    points[2].x,
                    points[2].y,
                    points[3].x,
                    points[3].y
                ),
                0,
                floatArrayOf(
                    0f,
                    0f,
                    width.toFloat(),
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    0f,
                    height.toFloat()
                ),
                0,
                4
            )
        }
        val croppedBitmap = createBitmap(width, height)
        Canvas(croppedBitmap).drawBitmap(
            bitmap,
            matrix,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
        )

        return if (height.toFloat() / width >= 1.5f) {
            val rotationMatrix = Matrix().apply { postRotate(90f) }
            Bitmap.createBitmap(
                croppedBitmap,
                0,
                0,
                croppedBitmap.width,
                croppedBitmap.height,
                rotationMatrix,
                true
            )
        } else {
            croppedBitmap
        }
    }

    fun orderPointsClockwise(points: List<PointF>): List<PointF> {
        if (points.size != 4) return points

        val centerX = points.sumOf { it.x.toDouble() }.toFloat() / 4
        val centerY = points.sumOf { it.y.toDouble() }.toFloat() / 4
        val sortedPoints = points.sortedBy { point ->
            atan2((point.y - centerY).toDouble(), (point.x - centerX).toDouble())
        }
        val topLeftIndex = sortedPoints.indices.minBy {
            sortedPoints[it].x + sortedPoints[it].y
        }
        return List(4) { sortedPoints[(topLeftIndex + it) % 4] }
    }

    fun clipBoxToImageBounds(
        points: List<PointF>,
        imageWidth: Int,
        imageHeight: Int
    ): List<PointF> =
        points.map { point ->
            PointF(
                point.x.coerceIn(0f, imageWidth - 1f),
                point.y.coerceIn(0f, imageHeight - 1f)
            )
        }

    private fun distance(p1: PointF, p2: PointF): Float {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return sqrt(dx * dx + dy * dy)
    }
}