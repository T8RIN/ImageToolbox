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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiVHSNtscFilter(
    override val value: NtscParams = NtscParams()
) : UiFilter<NtscParams>(
    title = R.string.vhs_ntsc,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.amount,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.chroma_bleed,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.tape_wear,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.noise,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.tracking,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.seed,
            valueRange = 0f..10_000f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.luma_smear,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.sharpen,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.ringing,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.snow,
            valueRange = 0f..1f,
            roundTo = 2
        ),
        FilterParam(
            title = R.string.processing_downscale,
            valueRange = 0f..4f,
            roundTo = 0
        )
    )
), Filter.VHSNtsc
