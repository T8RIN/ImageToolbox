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

class UiTwirlFilter(
    override val value: Quad<Float, Float, Float, Float> = 45f to 0.5f qto (0.5f to 0.5f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.twirl,
    paramsInfo = listOf(
        R.string.angle paramTo -360f..360f,
        R.string.center_x paramTo 0f..1f,
        R.string.center_y paramTo 0f..1f,
        R.string.radius paramTo 0f..1f,
    ),
    value = value
), Filter.Twirl