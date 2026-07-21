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

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams

sealed class GmicUiFilter(
    @StringRes title: Int,
    value: GmicFilterParams,
    val gmicParamsInfo: List<GmicParameterInfo>
) : UiFilter<GmicFilterParams>(
    title = title,
    value = value
)

sealed interface GmicParameterInfo {
    @get:StringRes
    val title: Int

    data class Number(
        override val title: Int,
        val range: ClosedFloatingPointRange<Float>,
        val isInteger: Boolean,
        val roundTo: Int = if (isInteger) 0 else 2
    ) : GmicParameterInfo

    data class Toggle(
        override val title: Int
    ) : GmicParameterInfo

    data class Selection(
        override val title: Int,
        val entries: List<String>
    ) : GmicParameterInfo

    data class Color(
        override val title: Int
    ) : GmicParameterInfo
}
