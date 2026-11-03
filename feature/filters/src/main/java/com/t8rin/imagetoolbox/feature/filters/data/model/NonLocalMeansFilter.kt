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

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.NonLocalMeans.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.RestorationFilters
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class NonLocalMeansFilter(
    override val value: Value
) : FilterTransformation, Filter.NonLocalMeans {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = RestorationFilters.nonLocalMeans(
        bitmap = input,
        luminanceNoise = value.luminanceNoise,
        chromaNoise = value.ntscChromaNoise,
        searchWindow = value.searchWindow.roundToInt(),
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.non_local_means, R.string.filter_help_non_local_means)

        override val parameters = structuredParameters {
            Value::luminanceNoise.float(R.string.luminance_noise, 7f, 0f..25f, roundTo = 1)
            Value::ntscChromaNoise.float(R.string.ntsc_chroma_noise, 9f, 0f..25f, roundTo = 1)
            Value::searchWindow.float(R.string.search_window, 15f, 7f..23f, roundTo = 0)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}