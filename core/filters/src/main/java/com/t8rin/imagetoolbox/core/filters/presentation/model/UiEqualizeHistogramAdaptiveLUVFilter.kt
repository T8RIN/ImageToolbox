/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.resources.R

class UiEqualizeHistogramAdaptiveLUVFilter(
    override val value: Triple<Float, Float, Float> = Triple(3f, 3f, 128f)
) : UiFilter<Triple<Float, Float, Float>>(
    title = R.string.equalize_histogram_adaptive_luv,
    paramsInfo = listOf(
        FilterParam(R.string.grid_size_x, 1f..100f, 0),
        FilterParam(R.string.grid_size_y, 1f..100f, 0),
        FilterParam(R.string.bins_count, 2f..256f, 0)
    ),
    value = value
), Filter.EqualizeHistogramAdaptiveLUV