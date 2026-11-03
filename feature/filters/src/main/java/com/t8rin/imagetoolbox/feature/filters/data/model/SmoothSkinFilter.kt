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
import com.t8rin.gmic.filters.SkinEstimation
import com.t8rin.gmic.filters.SkinSmoothnessType
import com.t8rin.gmic.filters.SmoothSkin
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SmoothSkin.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class SmoothSkinFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SmoothSkin {

    override fun createFilter(): GmicFilter = SmoothSkin(
        skinEstimation = SkinEstimation.valueOf(value.settings[0]),
        tolerance = value.settings[1].toFloat(),
        maskSmoothness = value.settings[2].toFloat(),
        threshold = value.settings[3].toFloat(),
        preNormalize = value.settings[4].toBoolean(),
        manualX = value.settings[5].toFloat(),
        manualY = value.settings[6].toFloat(),
        manualRadius = value.settings[7].toFloat(),
        baseScale = value.settings[8].toFloat(),
        fineScale = value.settings[9].toFloat(),
        detailSmoothness = value.settings[10].toFloat(),
        smoothnessType = SkinSmoothnessType.valueOf(value.settings[11]),
        gain = value.settings[12].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_smooth_skin,
            R.string.filter_help_gmic_filter_smooth_skin
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_skin_estimation,
                    "Automatic",
                    listOf("None", "Manual", "Automatic")
                ),
                gmicNumber(R.string.tolerance, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_mask_smoothness, 1, 0f..5f),
                gmicNumber(R.string.threshold, 1, 0f..10f),
                gmicToggle(R.string.gmic_param_pre_normalize, true),
                gmicNumber(R.string.gmic_param_manual_x, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_manual_y, 50, 0f..100f),
                gmicNumber(R.string.radius, 5, 0f..25f),
                gmicNumber(R.string.gmic_param_base_scale, 2, 0f..10f),
                gmicNumber(R.string.gmic_param_fine_scale, 0.2f, 0f..0.8f),
                gmicNumber(R.string.gmic_param_detail_smoothness, 3, 0f..10f),
                gmicSelection(
                    R.string.gmic_param_smoothness_type,
                    "Bilateral",
                    listOf("Gaussian", "Bilateral")
                ),
                gmicNumber(R.string.gain, 0.05f, 0f..0.5f)
            )
        }
    }
}