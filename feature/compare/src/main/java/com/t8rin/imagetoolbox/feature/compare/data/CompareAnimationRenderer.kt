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

package com.t8rin.imagetoolbox.feature.compare.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.t8rin.imagetoolbox.feature.compare.domain.CompareAnimationParams
import androidx.core.graphics.withSave

internal class CompareAnimationRenderer(
    private val before: Bitmap,
    private val after: Bitmap,
    val width: Int,
    val height: Int,
    private val beforeLabel: String,
    private val afterLabel: String
) {
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = minOf(width, height) * 0.045f
    }
    private val beforeRect = before.fitRect()
    private val afterRect = after.fitRect()

    fun draw(
        canvas: Canvas,
        position: Float,
        params: CompareAnimationParams
    ) {
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(before, null, beforeRect, bitmapPaint)
        if (params.showLabels) canvas.drawLabel(beforeLabel, atStart = false)

        val progress = position.coerceIn(0f, 1f)
        val edge = if (params.isVertical) height * progress else width * progress
        canvas.withSave {
            if (params.isVertical) {
                canvas.clipRect(0f, 0f, width.toFloat(), edge)
            } else {
                canvas.clipRect(0f, 0f, edge, height.toFloat())
            }
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(after, null, afterRect, bitmapPaint)
            if (params.showLabels) canvas.drawLabel(afterLabel, atStart = true)
        }

        if (progress > 0f && progress < 1f) {
            overlayPaint.color = Color.WHITE
            overlayPaint.strokeWidth = (minOf(width, height) * 0.004f).coerceAtLeast(1f)
            if (params.isVertical) {
                canvas.drawLine(0f, edge, width.toFloat(), edge, overlayPaint)
            } else {
                canvas.drawLine(edge, 0f, edge, height.toFloat(), overlayPaint)
            }
        }
    }

    private fun Bitmap.fitRect(): RectF {
        val scale = minOf(
            this@CompareAnimationRenderer.width.toFloat() / width,
            this@CompareAnimationRenderer.height.toFloat() / height
        )
        val left = (this@CompareAnimationRenderer.width - width * scale) / 2f
        val top = (this@CompareAnimationRenderer.height - height * scale) / 2f
        return RectF(left, top, left + width * scale, top + height * scale)
    }

    private fun Canvas.drawLabel(text: String, atStart: Boolean) {
        val padding = textPaint.textSize * 0.55f
        val labelWidth = textPaint.measureText(text) + 2 * padding
        val labelHeight = textPaint.descent() - textPaint.ascent() + 2 * padding
        val left =
            if (atStart) padding else this@CompareAnimationRenderer.width - padding - labelWidth
        val top =
            if (atStart) padding else this@CompareAnimationRenderer.height - padding - labelHeight
        overlayPaint.color = Color.argb(170, 0, 0, 0)
        drawRoundRect(
            left, top, left + labelWidth, top + labelHeight,
            padding, padding, overlayPaint
        )
        drawText(text, left + padding, top + padding - textPaint.ascent(), textPaint)
    }
}