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
import com.t8rin.gmic.filters.BlurredFrame
import com.t8rin.gmic.filters.FrameNormalization
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BlurredFrame.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.FRAMES)
internal class BlurredFrameFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.BlurredFrame {

    override fun createFilter(): GmicFilter = BlurredFrame(
        horizontalSize = value.settings[0].toFloat(),
        verticalSize = value.settings[1].toFloat(),
        crop = value.settings[2].toFloat(),
        blur = value.settings[3].toFloat(),
        roundness = value.settings[4].toFloat(),
        applyColorBalance = value.settings[5].toBoolean(),
        balanceColor = value.settings[6].toInt(),
        normalization = FrameNormalization.valueOf(value.settings[7]),
        outlineSize = value.settings[8].toFloat(),
        outlineColor = value.settings[9].toInt(),
        xShadow = value.settings[10].toFloat(),
        yShadow = value.settings[11].toFloat(),
        shadowSmoothness = value.settings[12].toFloat(),
        shadowContrast = value.settings[13].toFloat(),
        xCentering = value.settings[14].toFloat(),
        yCentering = value.settings[15].toFloat(),
        angle = value.settings[16].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_blurred_frame,
            R.string.filter_help_gmic_filter_blurred_frame
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_horizontal_size, 30, 0f..100f),
                gmicNumber(R.string.gmic_param_vertical_size, 30, 0f..100f),
                gmicNumber(R.string.crop, 0, 0f..100f),
                gmicNumber(R.string.blur, 5, 0f..10f),
                gmicNumber(R.string.gmic_param_roundness, 0, 0f..1f),
                gmicToggle(R.string.color_balance, false),
                gmicColor(R.string.gmic_param_balance_color, -8355712),
                gmicSelection(
                    R.string.gmic_param_normalization,
                    "None",
                    listOf("None", "Stretch", "Equalize")
                ),
                gmicNumber(R.string.outline_size, 5, 0f..50f),
                gmicColor(R.string.outline_color, -1),
                gmicNumber(R.string.gmic_param_x_shadow, 2, -10f..10f),
                gmicNumber(R.string.gmic_param_y_shadow, 2, -10f..10f),
                gmicNumber(R.string.softness, 1, 0f..5f),
                gmicNumber(R.string.contrast, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_x_centering, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_y_centering, 0.5f, 0f..1f),
                gmicNumber(R.string.angle, 0, -180f..180f)
            )
        }
    }
}