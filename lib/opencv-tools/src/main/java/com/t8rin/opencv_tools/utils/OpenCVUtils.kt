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

@file:Suppress("unused")

package com.t8rin.opencv_tools.utils

import android.app.Application
import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

fun Bitmap.toMat(): Mat {
    val bitmap = if (config == Bitmap.Config.ARGB_8888) this
    else requireNotNull(copy(Bitmap.Config.ARGB_8888, false))
    val mat = Mat()
    try {
        Utils.bitmapToMat(bitmap, mat)
        return mat
    } catch (t: Throwable) {
        mat.release()
        throw t
    } finally {
        if (bitmap !== this) bitmap.recycle()
    }
}

fun Mat.toBitmap(): Bitmap = try {
    createBitmap(cols(), rows()).also { bitmap ->
        try {
            Utils.matToBitmap(this, bitmap)
        } catch (t: Throwable) {
            bitmap.recycle()
            throw t
        }
    }
} finally {
    release()
}

fun Mat.multiChannelMean(): Double =
    Core.mean(this).`val`.average()

fun Mat.singleChannelMean(): Double =
    Core.mean(this).`val`.first()

fun Mat.resizeAndCrop(targetSize: Size): Mat {
    val aspectRatio = this.width().toDouble() / this.height()
    val targetAspectRatio = targetSize.width / targetSize.height

    val resizedMat = Mat()
    if (aspectRatio > targetAspectRatio) {
        val newWidth = (this.height() * targetAspectRatio).toInt()
        Imgproc.resize(this, resizedMat, Size(newWidth.toDouble(), this.height().toDouble()))
    } else {
        val newHeight = (this.width() / targetAspectRatio).toInt()
        Imgproc.resize(this, resizedMat, Size(this.width().toDouble(), newHeight.toDouble()))
    }

    val xOffset = (resizedMat.width() - targetSize.width).toInt() / 2
    val yOffset = (resizedMat.height() - targetSize.height).toInt() / 2
    return Mat(
        resizedMat,
        Rect(xOffset, yOffset, targetSize.width.toInt(), targetSize.height.toInt())
    )
}

fun Mat.resizeAndPad(targetSize: Size): Mat {
    val aspectRatio = this.width().toDouble() / this.height()
    val targetAspectRatio = targetSize.width / targetSize.height

    val resizedMat = Mat()
    val paddedMat = Mat()
    try {
        if (aspectRatio > targetAspectRatio) {
            val newHeight = (targetSize.width / aspectRatio).toInt().coerceAtLeast(1)
            Imgproc.resize(this, resizedMat, Size(targetSize.width, newHeight.toDouble()))
        } else {
            val newWidth = (targetSize.height * aspectRatio).toInt().coerceAtLeast(1)
            Imgproc.resize(this, resizedMat, Size(newWidth.toDouble(), targetSize.height))
        }
        Core.copyMakeBorder(
            resizedMat, paddedMat,
            0, targetSize.height.toInt() - resizedMat.rows(),
            0, targetSize.width.toInt() - resizedMat.cols(),
            Core.BORDER_CONSTANT, Scalar.all(0.0)
        )
        return paddedMat
    } catch (t: Throwable) {
        paddedMat.release()
        throw t
    } finally {
        resizedMat.release()
    }
}

fun Int.toScalar(): Scalar {
    val alpha = (this shr 24 and 0xFF).toDouble()
    val red = (this shr 16 and 0xFF).toDouble()
    val green = (this shr 8 and 0xFF).toDouble()
    val blue = (this and 0xFF).toDouble()

    return Scalar(red, green, blue, alpha)
}

abstract class OpenCV {
    init {
        OpenCVLoader.initLocal()
    }

    protected val context get() = application

    companion object {
        private var _context: Application? = null
        internal val application: Application
            get() = _context
                ?: throw NullPointerException("Call OpenCV.init() in Application onCreate to use this feature")

        fun init(context: Application) {
            _context = context
        }
    }
}