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

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.RandomPatchArray
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RandomPatchArray.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class RandomPatchArrayFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.RandomPatchArray {

    override fun createFilter(): GmicFilter = RandomPatchArray(
        sourceXTiles = value.settings[0].toInt(),
        sourceYTiles = value.settings[1].toInt(),
        destinationXTiles = value.settings[2].toInt(),
        destinationYTiles = value.settings[3].toInt(),
        randomSeed = value.settings[4].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_random_patch_array,
            R.string.filter_help_gmic_filter_random_patch_array
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_source_x_tiles, 5, 1f..128f),
                gmicInteger(R.string.gmic_param_source_y_tiles, 5, 1f..128f),
                gmicInteger(R.string.gmic_param_destination_x_tiles, 7, 1f..128f),
                gmicInteger(R.string.gmic_param_destination_y_tiles, 7, 1f..128f),
                gmicSeed(R.string.seed, 0, 0f..65535f)
            )
        }
    }
}