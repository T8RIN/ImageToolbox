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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.TriTone.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.COLOR)
internal class TriToneFilter(
    override val value: Value
) : FilterTransformation, Filter.TriTone {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.tritone(
        input = input,
        shadowsColor = value.shadowColor.colorInt,
        middleColor = value.midtoneColor.colorInt,
        highlightsColor = value.highlightColor.colorInt
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.tri_tone, R.string.filter_help_tri_tone)

        override val parameters = structuredParameters {
            Value::shadowColor.color(R.string.first_color, ColorModel(0xFFFF003B.toInt()))
            Value::midtoneColor.color(R.string.second_color, ColorModel(0xFF831111.toInt()))
            Value::highlightColor.color(R.string.third_color, ColorModel(0xFFFF0099.toInt()))
        }
    }
}