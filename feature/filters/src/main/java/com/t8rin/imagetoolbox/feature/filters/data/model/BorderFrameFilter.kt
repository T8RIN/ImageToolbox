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
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BorderFrame.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.FRAMES)
internal class BorderFrameFilter(
    override val value: Value
) : FilterTransformation, Filter.BorderFrame {
    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val horizontal = value.horizontalBorderThickness.roundToInt()
        val vertical = value.verticalBorderThickness.roundToInt()

        return createBitmap(
            width = input.width + horizontal * 2,
            height = input.height + vertical * 2
        ).applyCanvas {
            drawColor(value.borderColor.colorInt)

            drawBitmap(
                bitmap = input,
                position = Position.Center
            )
        }
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.border_frame, R.string.filter_help_border_frame)

        override val parameters = structuredParameters {
            Value::horizontalBorderThickness.float(
                R.string.horizontal_border_thickness,
                20f,
                0f..500f,
                roundTo = 0
            )
            Value::verticalBorderThickness.float(
                R.string.vertical_border_thickness,
                40f,
                0f..500f,
                roundTo = 0
            )
            Value::borderColor.color(
                R.string.border_color,
                ColorModel(0xFFFFFFFF.toInt()),
                allowAlpha = true
            )
        }
    }
}