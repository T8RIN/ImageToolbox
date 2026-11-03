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
import com.t8rin.gmic.filters.BufferError
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BufferError.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class BufferErrorFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.BufferError {

    override fun createFilter(): GmicFilter = BufferError(
        width = value.settings[0].toFloat(),
        height = value.settings[1].toFloat(),
        bufferSelectionStartPercent = value.settings[2].toFloat(),
        bufferSelectionLengthPercent = value.settings[3].toFloat(),
        bufferSelectionShiftPercent = value.settings[4].toFloat(),
        repeatBuffer = value.settings[5].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_buffer_error,
            R.string.filter_help_gmic_filter_buffer_error
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_width, 50, 0f..200f),
                gmicNumber(R.string.gmic_param_height, 50, 0f..200f),
                gmicNumber(R.string.gmic_param_buffer_selection_start_percent, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_buffer_selection_length_percent, 100, 0f..400f),
                gmicNumber(R.string.gmic_param_buffer_selection_shift_percent, 0, 0f..100f),
                gmicToggle(R.string.gmic_param_repeat_buffer, true)
            )
        }
    }
}