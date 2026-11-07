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

package com.t8rin.collages.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import androidx.core.graphics.withClip
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
    private val backgroundColor: Int,
    private val borderWidth: Float = 0f,
    private val borderColor: Int = -1,
    private val shadowRadius: Float = 0f,
    private val shadowColor: Int = 0x55000000,
    private val borderShader: Shader? = null
) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    fun drawShadow(canvas: Canvas) = drawCollageShadow(
        canvas, path, clearPath, shadowRadius, shadowColor, paint = paint
    )

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
                backgroundColor, paint,
                borderWidth = borderWidth, borderColor = borderColor, borderShader = borderShader
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
    progress: Float = 1f,
    borderWidth: Float = 0f,
    borderColor: Int = -1,
    borderShader: Shader? = null
) {
    paint.color = backgroundColor
    canvas.drawPath(backgroundPath, paint)
    val clip =
        if (clearPath.isEmpty) path else Path(path).apply { op(clearPath, Path.Op.DIFFERENCE) }
    canvas.withClip(clip) {
        paint.alpha = 255
        if (progress < 1f) {
            val bounds = clipBounds
            val layer = saveLayer(
                bounds.left.toFloat(), bounds.top.toFloat(),
                bounds.right.toFloat(), bounds.bottom.toFloat(), null
            )
            try {
                if (previousImage != null && !previousImage.isRecycled && previousMatrix != null) {
                    paint.alpha = ((1f - progress) * 255).toInt()
                    drawBitmap(previousImage, previousMatrix, paint)
                }
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
        if (borderWidth > 0f) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth * 2
            paint.color = if (borderShader == null) borderColor else android.graphics.Color.WHITE
            paint.shader = borderShader
            drawPath(clip, paint)
            paint.shader = null
            paint.style = Paint.Style.FILL
        }
    }
}

internal fun drawCollageShadow(
    canvas: Canvas,
    path: Path,
    clearPath: Path,
    radius: Float,
    color: Int,
    alpha: Float = 1f,
    paint: Paint
) {
    if (radius <= 0f || android.graphics.Color.alpha(color) == 0 || alpha <= 0f) return
    val clip = if (clearPath.isEmpty) path else Path(path).apply {
        op(clearPath, Path.Op.DIFFERENCE)
    }
    val bounds = RectF()
    clip.computeBounds(bounds, true)
    bounds.inset(-radius * 2, -radius * 2)
    val layer = canvas.saveLayer(bounds, null)
    paint.color = android.graphics.Color.BLACK
    val opacity = (android.graphics.Color.alpha(color) * alpha).toInt().coerceIn(0, 255)
    paint.setShadowLayer(radius, 0f, radius / 3, (color and 0x00ffffff) or (opacity shl 24))
    canvas.drawPath(clip, paint)
    paint.clearShadowLayer()
    paint.xfermode = shadowCutoutMode
    canvas.drawPath(clip, paint)
    paint.xfermode = null
    canvas.restoreToCount(layer)
}

private val imageBlendMode = PorterDuffXfermode(PorterDuff.Mode.ADD)
private val shadowCutoutMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)