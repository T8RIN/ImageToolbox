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
import com.t8rin.gmic.filters.RebuildFromSimilarBlocks
import com.t8rin.gmic.filters.SimilarBlockNorm
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RebuildFromSimilarBlocks.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class RebuildFromSimilarBlocksFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.RebuildFromSimilarBlocks {

    override fun createFilter(): GmicFilter = RebuildFromSimilarBlocks(
        blockSize = value.settings[0].toFloat(),
        regularization = value.settings[1].toFloat(),
        luminance = value.settings[2].toFloat(),
        norm = SimilarBlockNorm.valueOf(value.settings[3])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_rebuild_from_similar_blocks,
            R.string.filter_help_gmic_filter_rebuild_from_similar_blocks
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.block_size, 5, 2f..50f),
                gmicNumber(R.string.gmic_param_regularization, 10, 0f..20f),
                gmicNumber(R.string.gmic_param_luminance, 0.75f, 0f..3f),
                gmicSelection(R.string.gmic_param_norm, "L2", listOf("L1", "L2"))
            )
        }
    }
}