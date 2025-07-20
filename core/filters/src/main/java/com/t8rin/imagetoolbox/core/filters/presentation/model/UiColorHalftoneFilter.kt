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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.resources.R

class UiColorHalftoneFilter(
    override val value: Quad<Float, Float, Float, Float> = Quad(
        first = 2f,
        second = 108f,
        third = 162f,
        fourth = 90f
    )
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.color_halftone,
    paramsInfo = listOf(
        FilterParam(R.string.radius, 0f..50f, 2),
        FilterParam(R.string.cyan, 0f..360f, 0),
        FilterParam(R.string.magenta, 0f..360f, 0),
        FilterParam(R.string.yellow, 0f..360f, 0),
    ),
    value = value
), Filter.ColorHalftone