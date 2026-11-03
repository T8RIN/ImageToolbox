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
import com.t8rin.opencv_tools.utils.OpenCV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.roundToInt

internal object PhotoFilterRenderer : OpenCV() {

    suspend fun render(
        bitmap: Bitmap,
        strength: Float,
        maxSide: Int = Int.MAX_VALUE,
        alphaAsInput: Boolean = false,
        effect: PhotoFilterScope.(Mat) -> Mat
    ): Bitmap = withContext(Dispatchers.Default) {
        val amount = if (strength.isFinite()) strength.coerceIn(0f, 1f).toDouble() else 1.0
        if (amount == 0.0) return@withContext bitmap

        PhotoFilterScope(coroutineContext).use { scope ->
            val rgba = scope.mat()
            val source = if (bitmap.config == Bitmap.Config.ARGB_8888) {
                bitmap
            } else {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            }
            try {
                Utils.bitmapToMat(source, rgba, source.isPremultiplied)
            } finally {
                if (source !== bitmap) source.recycle()
            }
            scope.checkpoint()

            val rgb = scope.convert(rgba, Imgproc.COLOR_RGBA2RGB)
            val base = if (alphaAsInput) scope.channel(rgba, 3) else rgb
            val longestSide = max(base.cols(), base.rows())
            val input = if (longestSide > maxSide) {
                val scale = maxSide.toDouble() / longestSide
                scope.resize(
                    src = base,
                    width = (base.cols() * scale).roundToInt().coerceAtLeast(1),
                    height = (base.rows() * scale).roundToInt().coerceAtLeast(1),
                    interpolation = Imgproc.INTER_AREA
                )
            } else base
            val processed = scope.effect(input)
            scope.checkpoint()
            if (processed === input) return@withContext bitmap

            val result = if (processed.size() == rgb.size()) {
                processed
            } else {
                scope.resize(processed, rgb.cols(), rgb.rows(), Imgproc.INTER_LINEAR)
            }
            if (amount < 1.0) {
                Core.addWeighted(result, amount, rgb, 1.0 - amount, 0.0, result)
            }
            val alpha = if (alphaAsInput) null else scope.channel(rgba, 3)
            Imgproc.cvtColor(result, rgba, Imgproc.COLOR_RGB2RGBA)
            if (alpha != null) Core.insertChannel(alpha, rgba, 3)

            scope.checkpoint()
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888).apply {
                density = bitmap.density
                Utils.matToBitmap(rgba, this, true)
            }
        }
    }
}

internal class PhotoFilterScope(private val context: CoroutineContext) : AutoCloseable {
    private val matrices = mutableListOf<Mat>()

    fun checkpoint() = context.ensureActive()

    fun mat(): Mat = Mat().also(matrices::add)

    fun mat(rows: Int, cols: Int, type: Int): Mat = Mat(rows, cols, type).also(matrices::add)

    fun roi(src: Mat, top: Int, bottom: Int, left: Int, right: Int): Mat =
        src.submat(top, bottom, left, right).also(matrices::add)

    fun <T> work(block: PhotoFilterScope.() -> T): T = PhotoFilterScope(context).use(block)

    fun convert(src: Mat, code: Int): Mat = mat().also { Imgproc.cvtColor(src, it, code) }

    fun gray(src: Mat): Mat = convert(src, Imgproc.COLOR_RGB2GRAY)

    fun floats(src: Mat): Mat = mat().also { src.convertTo(it, CvType.CV_32F, 1.0 / 255.0) }

    fun bytes(src: Mat): Mat = mat().also { src.convertTo(it, CvType.CV_8U, 255.0) }

    fun resize(src: Mat, width: Int, height: Int, interpolation: Int): Mat = mat().also {
        Imgproc.resize(src, it, Size(width.toDouble(), height.toDouble()), 0.0, 0.0, interpolation)
    }

    fun gaussian(src: Mat, sigma: Double): Mat =
        mat().also { Imgproc.GaussianBlur(src, it, Size(0.0, 0.0), sigma.coerceAtLeast(0.15)) }

    fun channel(src: Mat, index: Int): Mat = mat().also { Core.extractChannel(src, it, index) }

    fun box(src: Mat, radius: Int): Mat = mat().also {
        val window = (radius * 2 + 1).toDouble()
        Imgproc.blur(src, it, Size(window, window))
    }

    fun multiply(a: Mat, b: Mat): Mat = mat().also { Core.multiply(a, b, it) }

    fun subtract(a: Mat, b: Mat): Mat = mat().also { Core.subtract(a, b, it) }

    fun divide(a: Mat, b: Mat): Mat = mat().also { Core.divide(a, b, it) }

    fun scale(a: Mat, value: Double): Mat = mat().also { Core.multiply(a, Scalar.all(value), it) }

    fun add(a: Mat, b: Mat): Mat = mat().also { Core.add(a, b, it) }

    override fun close() {
        matrices.asReversed().forEach(Mat::release)
    }
}

internal fun Mat.referenceScale(): Double = max(rows(), cols()) / 1024.0

internal fun Mat.radius(value: Float): Int = (value * referenceScale()).roundToInt().coerceAtLeast(1)