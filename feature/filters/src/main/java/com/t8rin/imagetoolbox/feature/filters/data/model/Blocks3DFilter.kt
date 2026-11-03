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

import android.graphics.Color
import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.RawGmicFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Blocks3D.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class Blocks3DFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Blocks3D {

    override fun createFilter(): GmicFilter {
        val outlineColor = value.settings[16].toInt()

        return RawGmicFilter(
            command = listOf(
                value.settings[0],
                value.settings[1],
                value.settings[2],
                value.settings[3],
                value.settings[4],
                value.settings[5],
                value.settings[6],
                value.settings[7],
                value.settings[8],
                value.settings[9],
                value.settings[10],
                value.settings[11],
                value.settings[12],
                value.settings[13],
                value.settings[14].toGmicBoolean(),
                value.settings[15].toGmicBoolean(),
                Color.red(outlineColor).toString(),
                Color.green(outlineColor).toString(),
                Color.blue(outlineColor).toString(),
                Color.alpha(outlineColor).toString()
            ).joinToString(
                separator = ",",
                prefix = "fx_blocks3d "
            )
        )
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_blocks_3d, R.string.filter_help_gmic_filter_blocks_3d)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.resolution, 32, 1f..128f),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..40f),
                gmicNumber(R.string.gmic_param_elevation, 4, -10f..10f),
                gmicNumber(R.string.just_size, 1.5f, 0f..3f),
                gmicNumber(R.string.angle, 30, 0f..360f),
                gmicNumber(R.string.gmic_param_tilt, 60, 0f..90f),
                gmicNumber(R.string.gmic_param_fov, 45, 1f..90f),
                gmicNumber(R.string.gmic_param_x_centering, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_y_centering, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_x_light, 0, -100f..100f),
                gmicNumber(R.string.gmic_param_y_light, -50, -100f..100f),
                gmicNumber(R.string.gmic_param_z_light, -100, -100f..0f),
                gmicNumber(R.string.gmic_param_specular_lightness, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_specular_shininess, 0.7f, 0f..3f),
                gmicToggle(R.string.gmic_param_use_light, true),
                gmicToggle(R.string.antialias, true),
                gmicColor(R.string.outline_color, -2147483648)
            )
        }
    }
}

private fun String.toGmicBoolean(): String = if (toBoolean()) "1" else "0"