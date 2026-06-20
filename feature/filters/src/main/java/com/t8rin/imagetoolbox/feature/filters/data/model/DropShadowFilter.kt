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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DropShadowParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import kotlin.math.ceil

@FilterInject
internal class DropShadowFilter(
    override val value: DropShadowParams = DropShadowParams.Default
) : Transformation<Bitmap>, Filter.DropShadow {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val padding = calculateShadowPadding(value)
        val output = createBitmap(
            width = input.width + padding.left + padding.right,
            height = input.height + padding.top + padding.bottom
        )

        return output.applyCanvas {
            buildShadowBitmap(input, value).let { shadow ->
                drawBitmap(
                    shadow.bitmap,
                    padding.left + shadow.left,
                    padding.top + shadow.top,
                    null
                )
                shadow.bitmap.recycle()
            }
            drawBitmap(input, padding.left.toFloat(), padding.top.toFloat(), null)
        }
    }

    private fun calculateShadowPadding(
        shadow: DropShadowParams
    ): ShadowPadding {
        val blurRadius = shadow.blurRadius.coerceAtLeast(0f)

        return ShadowPadding(
            left = ceil((blurRadius - shadow.offsetX).coerceAtLeast(0f)).toInt(),
            top = ceil((blurRadius - shadow.offsetY).coerceAtLeast(0f)).toInt(),
            right = ceil((blurRadius + shadow.offsetX).coerceAtLeast(0f)).toInt(),
            bottom = ceil((blurRadius + shadow.offsetY).coerceAtLeast(0f)).toInt()
        )
    }

    private fun buildShadowBitmap(
        sourceBitmap: Bitmap,
        shadow: DropShadowParams
    ): ShadowBitmap {
        val blurRadius = shadow.blurRadius.coerceAtLeast(0f)
        val offset = IntArray(2)
        val alphaBitmap = sourceBitmap.extractAlpha(
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
            drawColor(shadow.color.colorInt, PorterDuff.Mode.SRC_IN)
        }

        alphaBitmap.recycle()

        return ShadowBitmap(
            bitmap = tintedBitmap,
            left = offset[0].toFloat() + shadow.offsetX,
            top = offset[1].toFloat() + shadow.offsetY
        )
    }

    private data class ShadowPadding(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    )

    private data class ShadowBitmap(
        val bitmap: Bitmap,
        val left: Float,
        val top: Float
    )
}
