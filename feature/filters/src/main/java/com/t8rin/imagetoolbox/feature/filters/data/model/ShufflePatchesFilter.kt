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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.PatchReconstruction
import com.t8rin.gmic.filters.PatchShuffleMode
import com.t8rin.gmic.filters.ShufflePatches
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ShufflePatches.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class ShufflePatchesFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ShufflePatches {

    override fun createFilter(): GmicFilter = ShufflePatches(
        mode = PatchShuffleMode.valueOf(value.settings[0]),
        reconstruction = PatchReconstruction.valueOf(value.settings[1]),
        patchSize = value.settings[2].toInt(),
        overlap = value.settings[3].toFloat(),
        overlapDeviation = value.settings[4].toFloat(),
        randomSeed = value.settings[5].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_shuffle_patches,
            R.string.filter_help_gmic_filter_shuffle_patches
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_mode,
                    "Shuffle",
                    listOf("Shuffle", "Rotate", "ShuffleAndRotate")
                ),
                gmicSelection(
                    R.string.raw_highlight_recovery_reconstruct,
                    "Colors",
                    listOf("Colors", "Gradients", "Laplacians")
                ),
                gmicInteger(R.string.gmic_param_patch_size, 64, 4f..512f),
                gmicNumber(R.string.gmic_param_overlap, 0, 0f..50f),
                gmicNumber(R.string.gmic_param_overlap_deviation, 20, 0f..100f),
                gmicSeed(R.string.seed, 0, 0f..65535f)
            )
        }
    }
}