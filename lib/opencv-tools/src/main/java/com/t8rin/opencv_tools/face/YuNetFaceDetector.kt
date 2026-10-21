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

package com.t8rin.opencv_tools.face

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import androidx.core.graphics.scale
import com.t8rin.opencv_tools.utils.OpenCV
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.FaceDetectorYN
import java.io.File
import kotlin.math.roundToInt

object YuNetFaceDetector : OpenCV() {
    private val mutex = Mutex()
    private var detector: FaceDetectorYN? = null

    suspend fun detect(image: Bitmap): List<RectF> = detectWithLandmarks(image).map { it.bounds }

    suspend fun detectWithLandmarks(image: Bitmap): List<Face> = mutex.withLock {
        val scale = minOf(1f, 960f / maxOf(image.width, image.height))
        val resized = image.scale(
            (image.width * scale).roundToInt().coerceAtLeast(1),
            (image.height * scale).roundToInt().coerceAtLeast(1)
        )
        val source = resized.toMat()
        if (resized !== image) resized.recycle()
        val faces = Mat()
        try {
            Imgproc.cvtColor(source, source, Imgproc.COLOR_RGBA2BGR)
            val model = detector ?: run {
                val file = File(context.cacheDir, "face_detection_yunet_2026may.onnx")
                if (!file.isFile || file.length() == 0L) {
                    context.assets.open(file.name).use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                FaceDetectorYN.create(
                    file.absolutePath, "",
                    Size(source.cols().toDouble(), source.rows().toDouble()),
                    0.7f, 0.3f, 5000
                )
                    .also { detector = it }
            }
            model.inputSize = Size(source.cols().toDouble(), source.rows().toDouble())
            model.detect(source, faces)
            buildList {
                for (row in 0 until faces.rows()) {
                    val x = faces.get(row, 0)[0].toFloat() / source.cols()
                    val y = faces.get(row, 1)[0].toFloat() / source.rows()
                    val width = faces.get(row, 2)[0].toFloat() / source.cols()
                    val height = faces.get(row, 3)[0].toFloat() / source.rows()
                    val box = RectF(
                        x.coerceIn(0f, 1f),
                        y.coerceIn(0f, 1f),
                        (x + width).coerceIn(0f, 1f),
                        (y + height).coerceIn(0f, 1f)
                    )
                    if (box.width() > 0f && box.height() > 0f) {
                        add(
                            Face(
                                bounds = box,
                                landmarks = List(5) { index ->
                                    PointF(
                                        faces.get(row, 4 + index * 2)[0].toFloat() / source.cols(),
                                        faces.get(row, 5 + index * 2)[0].toFloat() / source.rows()
                                    )
                                }
                            )
                        )
                    }
                }
            }
        } finally {
            source.release()
            faces.release()
        }
    }

    data class Face(
        val bounds: RectF,
        val landmarks: List<PointF>
    )
}