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
import com.t8rin.gmic.filters.PaintingFrame
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PaintingFrame.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.FRAMES)
internal class PaintingFrameFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PaintingFrame {

    override fun createFilter(): GmicFilter = PaintingFrame(
        size = value.settings[0].toFloat(),
        contrast = value.settings[1].toFloat(),
        smoothness = value.settings[2].toFloat(),
        color = value.settings[3].toInt(),
        vignetteSize = value.settings[4].toFloat(),
        vignetteContrast = value.settings[5].toFloat(),
        defectsContrast = value.settings[6].toFloat(),
        defectsDensity = value.settings[7].toFloat(),
        defectsSize = value.settings[8].toFloat(),
        defectsSmoothness = value.settings[9].toFloat(),
        serialNumber = value.settings[10].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_painting_frame,
            R.string.filter_help_gmic_filter_painting_frame
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.just_size, 10, 0f..100f),
                gmicNumber(R.string.contrast, 0.4f, 0f..1f),
                gmicNumber(R.string.gmic_param_smoothness, 6, 0f..30f),
                gmicColor(R.string.color, -1980296),
                gmicNumber(R.string.gmic_param_vignette_size, 2, 0f..50f),
                gmicNumber(R.string.gmic_param_vignette_contrast, 400, 0f..1000f),
                gmicNumber(R.string.gmic_param_defects_contrast, 50, 0f..512f),
                gmicNumber(R.string.density, 10, 0f..100f),
                gmicNumber(R.string.gmic_param_defects_size, 1, 0f..10f),
                gmicNumber(R.string.gmic_param_defects_smoothness, 0.5f, 0f..20f),
                gmicInteger(R.string.gmic_param_serial_number, 123456, 0f..1000000f)
            )
        }
    }
}