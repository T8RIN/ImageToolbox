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
import com.t8rin.gmic.filters.MineralMosaic
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MineralMosaic.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class MineralMosaicFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.MineralMosaic {

    override fun createFilter(): GmicFilter = MineralMosaic(
        density = value.settings[0].toFloat(),
        area = value.settings[1].toFloat(),
        smoothness = value.settings[2].toFloat(),
        shadeStrength = value.settings[3].toFloat(),
        shadeAngle = value.settings[4].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_mineral_mosaic,
            R.string.filter_help_gmic_filter_mineral_mosaic
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.density, 1, 0f..3f),
                gmicNumber(R.string.area, 2, 0f..32f),
                gmicNumber(R.string.gmic_param_smoothness, 1, 0f..10f),
                gmicNumber(R.string.strength, 100, 0f..255f),
                gmicNumber(R.string.angle, 0, 0f..360f)
            )
        }
    }
}