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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PhotoCrossStitchParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.ARTISTIC)
class UiPhotoCrossStitchFilter(
    override val value: PhotoCrossStitchParams = PhotoCrossStitchParams.Default
) : UiFilter<PhotoCrossStitchParams>(
    title = R.string.photo_cross_stitch,
    paramsInfo = listOf(
        FilterParam(R.string.just_size, 4f..38f, roundTo = 0),
        FilterParam(R.string.thread_colors, 2f..64f, roundTo = 0),
        FilterParam(R.string.thread_width, 0.05f..0.25f),
        FilterParam(R.string.light_angle, 0f..360f, roundTo = 0),
        FilterParam(R.string.relief_strength, 0f..1f),
        FilterParam(R.string.seed, 0f..10000f, roundTo = 0),
        FilterParam(R.string.strength, 0f..1f)
    ),
    value = value
), Filter.PhotoCrossStitch