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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BayerSensorParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiBayerSensorFilter(
    override val value: BayerSensorParams = BayerSensorParams.Default
) : UiFilter<BayerSensorParams>(
    title = R.string.bayer_sensor,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.bayer_pattern,
            valueRange = 0f..3f,
            roundTo = 0,
            options = listOf(
                R.string.rggb,
                R.string.bggr,
                R.string.grbg,
                R.string.gbrg
            )
        ),
        FilterParam(R.string.scale, 0.1f..1f),
        FilterParam(R.string.shot_noise, 0f..0.25f, roundTo = 3),
        FilterParam(R.string.read_noise, 0f..0.15f, roundTo = 3),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f),
        FilterParam(
            title = R.string.demosaicing,
            valueRange = 0f..1f,
            roundTo = 0,
            options = listOf(R.string.bilinear, R.string.edge_aware)
        )
    ),
    value = value
), Filter.BayerSensor