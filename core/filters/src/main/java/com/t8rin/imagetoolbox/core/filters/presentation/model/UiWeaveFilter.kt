/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.qto
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.resources.R

class UiWeaveFilter(
    override val value: Quad<Float, Float, Float, Float> = 16f to 16f qto (6f to 6f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.weave,
    paramsInfo = listOf(
        R.string.x_width paramTo 0f..100f,
        R.string.y_wdth paramTo 0f..100f,
        R.string.x_gap paramTo 0f..100f,
        R.string.y_gap paramTo 0f..100f,
    ),
    value = value
), Filter.Weave