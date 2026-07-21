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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class QuadtreeFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Squares", "1024", "0.5", "0", "3", "1.5", "1", "true")
    )
) : GMICFilterTransformation(), Filter.Quadtree {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Quadtree(
        mode = QuadtreeMode.valueOf(value[0]),
        precision = value[1].toInt(),
        homogeneity = value[2].toFloat(),
        outline = value[3].toInt(),
        primaryRadius = value[4].toFloat(),
        secondaryRadius = value[5].toFloat(),
        anisotropy = value[6].toFloat(),
        onlyLeafs = value[7].toBoolean()
    )

}
