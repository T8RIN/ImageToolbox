/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.utils.cast
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.TransferFunc
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.ColorModelTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.FloatPaletteImageModelTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberColorModelColorModelTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberColorModelPopArtTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberNumberBlurEdgeModeTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberNumberColorModelTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberTransferFuncBlurEdgeModeTripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components.NumberTripleItem

@Composable
internal fun TripleItem(
    value: Triple<*, *, *>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<*, *, *>) -> Unit,
    previewOnly: Boolean
) {
    when {
        value.first is Float && value.second is PaletteTransferSpace && value.third is ImageModel -> {
            FloatPaletteImageModelTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is Number && value.third is Number -> {
            NumberTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is Number && value.third is ColorModel -> {
            NumberNumberColorModelTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is ColorModel && value.third is ColorModel -> {
            NumberColorModelColorModelTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is Number && value.third is BlurEdgeMode -> {
            NumberNumberBlurEdgeModeTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is TransferFunc && value.third is BlurEdgeMode -> {
            NumberTransferFuncBlurEdgeModeTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is ColorModel && value.second is ColorModel && value.third is ColorModel -> {
            ColorModelTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }

        value.first is Number && value.second is ColorModel && value.third is PopArtBlendingMode -> {
            NumberColorModelPopArtTripleItem(
                value = value.cast(),
                filter = filter,
                onFilterChange = onFilterChange,
                previewOnly = previewOnly
            )
        }
    }
}