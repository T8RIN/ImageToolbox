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
import com.t8rin.gmic.filters.ArrayInitialization
import com.t8rin.gmic.filters.MirroredArray
import com.t8rin.gmic.filters.MirroredArrayMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MirroredArray.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class MirroredArrayFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.MirroredArray {

    override fun createFilter(): GmicFilter = MirroredArray(
        iterations = value.settings[0].toInt(),
        xOffset = value.settings[1].toFloat(),
        yOffset = value.settings[2].toFloat(),
        mode = MirroredArrayMode.valueOf(value.settings[3]),
        initialization = ArrayInitialization.valueOf(value.settings[4]),
        expandSize = value.settings[5].toBoolean(),
        crop = value.settings[6].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_mirrored_array,
            R.string.filter_help_gmic_filter_mirrored_array
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.iterations, 1, 1f..10f),
                gmicNumber(R.string.gmic_param_x_offset, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_y_offset, 0, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_mode,
                    "BothAxes",
                    listOf("XAxis", "YAxis", "BothAxes", "DoubleBothAxes")
                ),
                gmicSelection(
                    R.string.gmic_param_initialization,
                    "Original",
                    listOf(
                        "Original",
                        "MirrorX",
                        "MirrorY",
                        "Rotate90",
                        "Rotate180",
                        "Rotate270"
                    )
                ),
                gmicToggle(R.string.gmic_param_expand_size, false),
                gmicInteger(R.string.crop, 0, 0f..100f)
            )
        }
    }
}