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

package com.t8rin.imagetoolbox.feature.markup_layers.data.utils

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.t8rin.imagetoolbox.feature.markup_layers.domain.TextShadow
import kotlin.math.roundToInt

internal data class PictureShadowRenderData(
    val bitmap: Bitmap,
    val left: Float,
    val top: Float,
    val rasterScale: Float
)

internal fun buildPictureShadowRenderData(
    sourceBitmap: Bitmap,
    shadow: TextShadow?,
    targetWidth: Float,
    targetHeight: Float,
    rasterScale: Float = 1f
): PictureShadowRenderData? {
    shadow ?: return null

    val safeRasterScale = rasterScale.coerceAtLeast(1f)
    val safeTargetWidth = (targetWidth.coerceAtLeast(1f) * safeRasterScale)
        .roundToInt()
        .coerceAtLeast(1)
    val safeTargetHeight = (targetHeight.coerceAtLeast(1f) * safeRasterScale)
        .roundToInt()
        .coerceAtLeast(1)
    val targetRect = RectF(0f, 0f, safeTargetWidth.toFloat(), safeTargetHeight.toFloat())

    val contentBitmap = createBitmap(
        width = safeTargetWidth,
        height = safeTargetHeight
    ).applyCanvas {
        withSave {
            drawBitmap(
                sourceBitmap,
                null,
                targetRect,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    isFilterBitmap = true
                }
            )
        }
    }

    val blurRadius = shadow.blurRadius.coerceAtLeast(0f) * safeRasterScale
    val offset = IntArray(2)
    val alphaBitmap = contentBitmap.extractAlpha(
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            if (blurRadius > 0f) {
                maskFilter = BlurMaskFilter(
                    blurRadius,
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        },
        offset
    )
    val tintedBitmap = createBitmap(
        width = alphaBitmap.width.coerceAtLeast(1),
        height = alphaBitmap.height.coerceAtLeast(1)
    ).applyCanvas {
        drawBitmap(
            alphaBitmap,
            0f,
            0f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                isFilterBitmap = true
            }
        )
        drawColor(shadow.color, PorterDuff.Mode.SRC_IN)
    }

    contentBitmap.recycle()
    alphaBitmap.recycle()

    return PictureShadowRenderData(
        bitmap = tintedBitmap,
        left = offset[0].toFloat() + shadow.offsetX * safeRasterScale,
        top = offset[1].toFloat() + shadow.offsetY * safeRasterScale,
        rasterScale = safeRasterScale
    )
}