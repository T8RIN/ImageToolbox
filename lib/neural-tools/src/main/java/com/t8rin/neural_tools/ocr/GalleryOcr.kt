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

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import androidx.core.graphics.createBitmap
import com.googlecode.tesseract.android.TessBaseAPI
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode.PSM_SINGLE_LINE
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.Closeable
import java.io.File
import java.text.Normalizer
import kotlin.math.roundToInt

class GalleryOcr(private val models: GalleryOcrModels) : Closeable {

    private val config = models.config
    private val labels = models.labels
    private val detectorSession = openSession(models.detector)
    private val routerSession = try {
        openSession(models.router)
    } catch (e: Exception) {
        detectorSession.close()
        throw e
    }
    private val detector = GalleryOcrDetector(detectorSession, config)
    private val router = GalleryOcrRouter(routerSession, labels, config)
    private var tess: TessBaseAPI? = null
    private var profile: GalleryOcrModels.Profile? = null
    private var stopRecognition: () -> Boolean = { false }
    private var lineAborted = false

    suspend fun recognize(image: Bitmap, extended: Boolean): Result {
        val coroutine = currentCoroutineContext()
        val multiplier = if (extended) 2 else 1
        val deadline = SystemClock.elapsedRealtime() + config.maxMillis * multiplier
        val maxRegions = config.maxRegions * multiplier
        val source = Mat()
        val gray = Mat()
        val crops = mutableListOf<Mat>()
        var partial = false
        val lines = sortedMapOf<Int, String>()
        fun remaining() = deadline - SystemClock.elapsedRealtime()
        fun result() = Result(lines.values.joinToString("\n"), partial)
        try {
            // Flatten transparent images on white and cap decoders that return a larger raster.
            val scale = minOf(1.0, MAX_IMAGE_SIZE.toDouble() / maxOf(image.width, image.height))
            val width = (image.width * scale).roundToInt().coerceAtLeast(1)
            val height = (image.height * scale).roundToInt().coerceAtLeast(1)
            if (scale == 1.0 && image.config == Bitmap.Config.ARGB_8888 &&
                !image.hasAlpha() && image.colorSpace?.isSrgb == true
            ) {
                Utils.bitmapToMat(image, source)
            } else {
                val bitmap = createBitmap(width, height)
                try {
                    Canvas(bitmap).apply {
                        drawColor(Color.WHITE)
                        drawBitmap(
                            image,
                            null,
                            Rect(0, 0, width, height),
                            Paint(Paint.FILTER_BITMAP_FLAG)
                        )
                    }
                    Utils.bitmapToMat(bitmap, source)
                } finally {
                    bitmap.recycle()
                }
            }
            val bgr = Mat()
            val detected = try {
                Imgproc.cvtColor(source, bgr, Imgproc.COLOR_RGBA2BGR)
                detector.detect(bgr)
            } finally {
                bgr.release()
            }
            coroutine.ensureActive()
            if (detected.isEmpty()) return result()
            if (remaining() <= 0) return Result("", partial = true)
            Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGBA2GRAY)
            partial = detected.size > maxRegions
            val regions = (if (partial) {
                detected.sortedByDescending { it.confidence * it.bounds.height }.take(maxRegions)
            } else detected).sortedWith(
                compareBy(
                    { it.bounds.y + it.bounds.height / 2.0 },
                    { it.bounds.x })
            )
            for (region in regions) {
                coroutine.ensureActive()
                crops.add(region.crop(gray))
            }
            if (remaining() <= 0) return Result("", partial = true)
            val routes = router.classify(crops)
            val groups = routes.indices.groupBy { routes[it].profile }.entries
                .sortedByDescending { it.value.size }
            partial = partial || groups.size > config.maxProfiles
            for ((profile, indices) in groups.take(config.maxProfiles)) {
                if (remaining() <= 0) {
                    partial = true
                    break
                }
                val api = useProfile(profile)
                for (index in indices) {
                    coroutine.ensureActive()
                    val budget = minOf(remaining(), config.maxLineMillis)
                    if (budget <= 0) {
                        partial = true
                        break
                    }
                    val lineDeadline = SystemClock.elapsedRealtime() + budget
                    stopRecognition =
                        { !coroutine.isActive || SystemClock.elapsedRealtime() >= lineDeadline }
                    lineAborted = false
                    val line = recognizeLine(
                        api = api,
                        crop = crops[index],
                        unclassifiedSingleLine = detected.size == 1 && routes[index].script == null
                    )
                    coroutine.ensureActive()
                    partial =
                        partial || lineAborted || SystemClock.elapsedRealtime() >= lineDeadline
                    if (line != null) lines[index] = line
                }
            }
            return result()
        } finally {
            stopRecognition = { false }
            crops.forEach(Mat::release)
            source.release()
            gray.release()
        }
    }

    private fun useProfile(next: GalleryOcrModels.Profile): TessBaseAPI {
        val api = tess ?: TessBaseAPI {
            if (stopRecognition()) {
                lineAborted = true
                tess?.stop()
            }
        }.also { tess = it }
        if (profile != next) {
            check(
                api.init(
                    models.tessdata(next).absolutePath,
                    next.codes.joinToString("+"),
                    TessBaseAPI.OEM_LSTM_ONLY
                )
            ) { "Could not open OCR languages ${next.codes.joinToString("+")}" }
            api.setVariable("user_defined_dpi", "150")
            profile = next
        }
        return api
    }

    private fun recognizeLine(
        api: TessBaseAPI,
        crop: Mat,
        unclassifiedSingleLine: Boolean
    ): String? {
        val bordered = Mat()
        val bitmap =
            Bitmap.createBitmap(crop.cols() + 16, crop.rows() + 16, Bitmap.Config.ARGB_8888)
        try {
            Core.copyMakeBorder(crop, bordered, 8, 8, 8, 8, Core.BORDER_CONSTANT, Scalar(255.0))
            Utils.matToBitmap(bordered, bitmap)
            api.pageSegMode = PSM_SINGLE_LINE
            api.setImage(bitmap)
            val text = api.utF8Text?.trim().orEmpty()
            val characters = text.count(Char::isLetterOrDigit)
            val minimumConfidence = if (unclassifiedSingleLine && characters < 4) {
                maxOf(config.minimumConfidence, 72)
            } else config.minimumConfidence
            return text.takeIf {
                !lineAborted && characters >= 2 && api.meanConfidence() >= minimumConfidence
            }?.let { Normalizer.normalize(it, Normalizer.Form.NFC) }
        } finally {
            api.clear()
            bordered.release()
            bitmap.recycle()
        }
    }

    override fun close() {
        try {
            tess?.recycle()
        } finally {
            detectorSession.use {
                routerSession.close()
            }
        }
    }

    data class Result(val text: String, val partial: Boolean)

    companion object {
        const val MAX_IMAGE_SIZE = 2048
        const val PIPELINE_VERSION = 3

        private val openCvReady by lazy(OpenCVLoader::initLocal)

        private fun openSession(model: File): OrtSession {
            check(openCvReady) { "Could not load OpenCV" }
            return OrtSession.SessionOptions().use {
                it.setIntraOpNumThreads(1)
                it.setInterOpNumThreads(1)
                it.setCPUArenaAllocator(false)
                it.addConfigEntry("mlas.disable_kleidiai", "1")
                OrtEnvironment.getEnvironment().createSession(model.absolutePath, it)
            }
        }
    }
}