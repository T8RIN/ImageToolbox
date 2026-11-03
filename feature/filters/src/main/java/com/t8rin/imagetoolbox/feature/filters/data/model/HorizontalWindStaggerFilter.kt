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
import com.awxkee.aire.Aire
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.toAbgr
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.toColor
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HorizontalWindStagger.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class HorizontalWindStaggerFilter(
    override val value: Value
) : FilterTransformation, Filter.HorizontalWindStagger {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.horizontalWindStagger(
        bitmap = input,
        windStrength = value.strength,
        streamsCount = value.amount,
        clearColor = value.color.toColor().toAbgr()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.horizontal_wind_stagger,
            R.string.filter_help_horizontal_wind_stagger
        )

        override val parameters = structuredParameters {
            Value::strength.float(R.string.strength, 0.2f, 0f..100f, roundTo = 1)
            Value::amount.int(R.string.amount, 90, 10..200)
            Value::color.color(R.string.color, ColorModel(0), allowAlpha = true)
        }
    }
}
