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
import com.t8rin.gmic.filters.Quadtree
import com.t8rin.gmic.filters.QuadtreeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Quadtree.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class QuadtreeFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Quadtree {

    override fun createFilter(): GmicFilter = Quadtree(
        mode = QuadtreeMode.valueOf(value.settings[0]),
        precision = value.settings[1].toInt(),
        homogeneity = value.settings[2].toFloat(),
        outline = value.settings[3].toInt(),
        primaryRadius = value.settings[4].toFloat(),
        secondaryRadius = value.settings[5].toFloat(),
        anisotropy = value.settings[6].toFloat(),
        onlyLeafs = value.settings[7].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_quadtree, R.string.filter_help_gmic_filter_quadtree)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_mode,
                    "Squares",
                    listOf("Squares", "Sierpinski", "EllipsePainting")
                ),
                gmicInteger(R.string.gmic_param_precision, 1024, 2f..4096f),
                gmicNumber(R.string.gmic_param_homogeneity, 0.5f, 0f..2f),
                gmicInteger(R.string.gmic_param_outline, 0, 0f..4f),
                gmicNumber(R.string.gmic_param_primary_radius, 3, 0f..5f),
                gmicNumber(R.string.second_radius, 1.5f, 0f..5f),
                gmicNumber(R.string.texture_anisotropy, 1, 0f..4f),
                gmicToggle(R.string.gmic_param_only_leafs, true)
            )
        }
    }
}