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
import com.t8rin.gmic.filters.PoincareDisk
import com.t8rin.gmic.filters.PoincareFilling
import com.t8rin.gmic.filters.PoincareTiling
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PoincareDisk.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class PoincareDiskFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PoincareDisk {

    override fun createFilter(): GmicFilter = PoincareDisk(
        pValue = value.settings[0].toInt(),
        qValue = value.settings[1].toInt(),
        size = value.settings[2].toFloat(),
        iterations = value.settings[3].toInt(),
        angle = value.settings[4].toFloat(),
        tiling = PoincareTiling.valueOf(value.settings[5]),
        antialiasing = value.settings[6].toBoolean(),
        filling = PoincareFilling.valueOf(value.settings[7]),
        shiftX = value.settings[8].toFloat(),
        shiftY = value.settings[9].toFloat(),
        zoom = value.settings[10].toFloat(),
        imageAngle = value.settings[11].toFloat(),
        outline = value.settings[12].toInt(),
        outlineColor = value.settings[13].toInt(),
        firstColor = value.settings[14].toInt(),
        secondColor = value.settings[15].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_poincare_disk,
            R.string.filter_help_gmic_filter_poincare_disk
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_p_value, 5, 3f..16f),
                gmicInteger(R.string.gmic_param_q_value, 6, 3f..32f),
                gmicNumber(R.string.just_size, 100, 0f..200f),
                gmicInteger(R.string.iterations, 20, 0f..40f),
                gmicNumber(R.string.angle, 0, -180f..180f),
                gmicSelection(
                    R.string.gmic_param_tiling,
                    "Polygonal",
                    listOf("Triangular", "Polygonal")
                ),
                gmicToggle(R.string.antialias, true),
                gmicSelection(R.string.fill, "Image", listOf("Binary", "Image")),
                gmicNumber(R.string.gmic_param_x_shift, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_y_shift, 50, 0f..100f),
                gmicNumber(R.string.zoom, 0, -10f..10f),
                gmicNumber(R.string.gmic_param_image_angle, 0, -180f..180f),
                gmicInteger(R.string.gmic_param_outline, 2, 0f..8f),
                gmicColor(R.string.outline_color, -16777216),
                gmicColor(R.string.first_color, -16777216),
                gmicColor(R.string.second_color, -1)
            )
        }
    }
}