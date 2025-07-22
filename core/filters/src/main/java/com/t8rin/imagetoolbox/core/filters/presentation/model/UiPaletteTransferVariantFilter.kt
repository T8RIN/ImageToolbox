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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PaletteTransferSpace
import com.t8rin.imagetoolbox.core.resources.R

class UiPaletteTransferVariantFilter(
    override val value: Triple<Float, PaletteTransferSpace, ImageModel> = Triple(
        first = 1f,
        second = PaletteTransferSpace.OKLAB,
        third = ImageModel(R.drawable.filter_preview_source_2)
    )
) : UiFilter<Triple<Float, PaletteTransferSpace, ImageModel>>(
    title = R.string.palette_transfer_variant,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.strength,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.tag_color_space,
            valueRange = 0f..1f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.target_image,
            valueRange = 0f..0f,
            roundTo = 0
        )
    )
), Filter.PaletteTransferVariant