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

package com.t8rin.collages.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import androidx.core.graphics.withClip
import com.t8rin.collages.utils.ImageDecoder
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.max

internal class FrameImageRender(
    private val context: Context,
    private val uri: Uri?,
    private val preview: Bitmap,
    private val matrix: Matrix,
    private val path: Path,
    private val backgroundPath: Path,
    private val clearPath: Path,
    private val backgroundColor: Int
) {
    suspend fun draw(canvas: Canvas, sourceSize: Int) {
        val values = FloatArray(9).also(matrix::getValues)
        val decodeSize = ceil(max(preview.width, preview.height) * hypot(values[0], values[3]))
            .toInt().coerceIn(1, sourceSize.coerceAtLeast(1))
        val image = uri?.let {
            ImageDecoder.decodeFileToBitmap(context, it, decodeSize, cache = false)
        } ?: preview
        try {
            val imageMatrix = Matrix(matrix).apply {
                preScale(
                    preview.width.toFloat() / image.width,
                    preview.height.toFloat() / image.height
                )
            }
            drawCollageImage(
                canvas, path, backgroundPath, clearPath, image, imageMatrix,
                backgroundColor, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
        } finally {
            if (image !== preview) image.recycle()
        }
    }
}

internal fun drawCollageImage(
    canvas: Canvas,
    path: Path,
    backgroundPath: Path,
    clearPath: Path,
    image: Bitmap?,
    matrix: Matrix,
    backgroundColor: Int,
    paint: Paint,
    previousImage: Bitmap? = null,
    previousMatrix: Matrix? = null,
    progress: Float = 1f
) {
    paint.color = backgroundColor
    canvas.drawPath(backgroundPath, paint)
    val clip =
        if (clearPath.isEmpty) path else Path(path).apply { op(clearPath, Path.Op.DIFFERENCE) }
    canvas.withClip(clip) {
        paint.alpha = 255
        if (previousImage != null && !previousImage.isRecycled &&
            previousMatrix != null && progress < 1f
        ) {
            val bounds = clipBounds
            val layer = saveLayer(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.bottom.toFloat(), null
            )
            try {
                paint.alpha = ((1f - progress) * 255).toInt()
                drawBitmap(previousImage, previousMatrix, paint)
                paint.alpha = (progress * 255).toInt()
                paint.xfermode = imageBlendMode
                if (image != null && !image.isRecycled) drawBitmap(image, matrix, paint)
            } finally {
                paint.alpha = 255
                paint.xfermode = null
                restoreToCount(layer)
            }
        } else if (image != null && !image.isRecycled) {
            drawBitmap(image, matrix, paint)
        }
    }
}

private val imageBlendMode = PorterDuffXfermode(PorterDuff.Mode.ADD)