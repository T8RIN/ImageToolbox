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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PaperQuilling.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class PaperQuillingFilter(
    override val value: Value
) : FilterTransformation, Filter.PaperQuilling {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.paperQuilling(
        input = input,
        cellSize = value.cellSize,
        turns = value.turns,
        stripWidth = value.stripWidth,
        relief = value.relief,
        background = value.background.colorInt,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.paper_quilling,
            R.string.filter_help_paper_quilling
        )

        override val parameters = structuredParameters {
            Value::cellSize.float(R.string.cell_size, 40f, 12f..96f)
            Value::turns.float(R.string.turns, 3.5f, 1f..8f)
            Value::stripWidth.float(R.string.strip_width, 0.06f, 0.025f..0.18f)
            Value::relief.float(R.string.relief, 0.8f, 0f..1f)
            Value::background.color(R.string.background_color, ColorModel(0xFFF5F0E4.toInt()))
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}