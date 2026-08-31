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

import android.graphics.Color
import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.RawGmicFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class GridFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "10", "0", "0", "0.5", "-16777216")
    )
) : GMICFilterTransformation(), Filter.Grid {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter {
        val color = value[5].toInt()

        return RawGmicFilter(
            command = listOf(
                "${value[0]}%",
                "${value[1]}%",
                "${value[2]}%",
                "${value[3]}%",
                value[4],
                Color.red(color).toString(),
                Color.green(color).toString(),
                Color.blue(color).toString(),
                Color.alpha(color).toString()
            ).joinToString(
                separator = ",",
                prefix = "grid "
            )
        )
    }

}
