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
import android.graphics.Matrix
import android.graphics.Rect
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Mirror.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class MirrorFilter(
    override val value: Value,
) : FilterTransformation, Filter.Mirror {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = input.mirror(
        value = value.center,
        side = value.side
    )

    private fun Bitmap.mirror(
        value: Float = 0.5f,
        side: MirrorSide = MirrorSide.LeftToRight
    ): Bitmap {
        val input = this
        if (value <= 0f || value >= 1f) return input

        val horizontal = side == MirrorSide.LeftToRight || side == MirrorSide.RightToLeft
        val dimension = if (horizontal) width else height
        if (dimension < 2) return input
        val center = (dimension * value).toInt().coerceIn(1, dimension - 1)
        val half = minOf(center, dimension - center)
        val fromStart = side == MirrorSide.LeftToRight || side == MirrorSide.TopToBottom
        val start = if (fromStart) center - half else center
        val source = if (horizontal) {
            Rect(start, 0, start + half, height)
        } else Rect(0, start, width, start + half)

        val part =
            Bitmap.createBitmap(input, source.left, source.top, source.width(), source.height())
        try {
            val flipped = Bitmap.createBitmap(
                part, 0, 0, part.width, part.height,
                Matrix().apply {
                    preScale(
                        if (horizontal) -1f else 1f,
                        if (horizontal) 1f else -1f
                    )
                },
                true
            )
            try {
                return createBitmap(
                    width = if (horizontal) half * 2 else width,
                    height = if (horizontal) height else half * 2,
                    config = input.safeConfig
                ).applyCanvas {
                    drawBitmap(if (fromStart) part else flipped)
                    drawBitmap(
                        bitmap = if (fromStart) flipped else part,
                        left = if (horizontal) half.toFloat() else 0f,
                        top = if (horizontal) 0f else half.toFloat()
                    )
                }
            } finally {
                if (flipped !== part) flipped.recycle()
            }
        } finally {
            part.recycle()
        }
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.tile_mode_mirror, R.string.filter_help_tile_mode_mirror)

        override val parameters = structuredParameters {
            Value::center.float(R.string.center, 0.5f, 0f..1f)
            Value::side.enum(R.string.side, label = { it.label }) { MirrorSide.LeftToRight }
        }
    }
}