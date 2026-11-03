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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Xdog.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class XdogFilter(
    override val value: Value
) : FilterTransformation, Filter.Xdog {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.xdog(
        bitmap = input,
        sigma = value.sigma,
        ratio = value.ratio,
        weight = value.weight,
        threshold = value.threshold,
        sharpness = value.sharpness,
        ink = value.ink.colorInt,
        paper = value.paper.colorInt,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.xdog, R.string.filter_help_xdog)

        override val parameters = structuredParameters {
            Value::sigma.float(R.string.sigma, 2.5f, 0.5f..12f, roundTo = 1)
            Value::ratio.float(R.string.gmic_param_ratio, 1.6f, 1.1f..4f)
            Value::weight.float(R.string.weight, 12f, 1f..30f, roundTo = 1)
            Value::threshold.float(R.string.threshold, 0.42f, -0.5f..1f)
            Value::sharpness.float(R.string.tag_sharpness, 14f, 1f..60f, roundTo = 1)
            Value::ink.color(R.string.ink_color, ColorModel(0xFF251E1B.toInt()))
            Value::paper.color(R.string.texture_paper_color, ColorModel(0xFFFAF0DF.toInt()))
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}