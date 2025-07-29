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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.jhlabs.JhFilter
import com.jhlabs.WeaveFilter
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.qto
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

internal class WeaveFilter(
    override val value: Quad<Float, Float, Float, Float> = 16f to 16f qto (6f to 6f)
) : JhFilterTransformation(), Filter.Weave {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): JhFilter = WeaveFilter().apply {
        xWidth = value.first
        yWidth = value.second
        xGap = value.third
        yGap = value.fourth
    }

}