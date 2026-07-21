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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.PIXELATION)
class UiShufflePatchesFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Shuffle", "Colors", "64", "0", "20", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_shuffle_patches,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_mode,
            listOf("Shuffle", "Rotate", "ShuffleAndRotate")
        ),
        GmicParameterInfo.Selection(
            R.string.raw_highlight_recovery_reconstruct,
            listOf("Colors", "Gradients", "Laplacians")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_patch_size, 4f..512f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_overlap, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_overlap_deviation,
            0f..100f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.seed, 0f..65535f, isInteger = true)
    )
), Filter.ShufflePatches
