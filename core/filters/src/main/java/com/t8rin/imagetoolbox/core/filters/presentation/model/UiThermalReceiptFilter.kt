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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ThermalReceiptParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiThermalReceiptFilter(
    override val value: ThermalReceiptParams = ThermalReceiptParams.Default
) : UiFilter<ThermalReceiptParams>(
    title = R.string.thermal_receipt,
    paramsInfo = listOf(
        FilterParam(R.string.just_size, 1f..22f, roundTo = 0),
        FilterParam(R.string.heat, 0.4f..2.5f),
        FilterParam(R.string.heat_retention, 0f..0.9f),
        FilterParam(R.string.defects, 0f..0.15f, roundTo = 3),
        FilterParam(R.string.fading, 0f..0.8f),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.ThermalReceipt