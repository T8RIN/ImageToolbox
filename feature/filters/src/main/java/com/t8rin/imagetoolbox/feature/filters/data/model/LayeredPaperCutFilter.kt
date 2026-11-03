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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LayeredPaperCut.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class LayeredPaperCutFilter(
    override val value: Value
) : FilterTransformation, Filter.LayeredPaperCut {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.layeredPaperCut(
        bitmap = input,
        layers = value.layers,
        smoothness = value.smoothness,
        depth = value.depth,
        lightAngle = value.lightAngle,
        grain = value.grain,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.layered_paper_cut, R.string.filter_help_layered_paper_cut)

        override val parameters = structuredParameters {
            Value::layers.int(R.string.texture_layers, 6, 2..12)
            Value::smoothness.float(R.string.smoothness, 3f, 0.2f..12f, roundTo = 1)
            Value::depth.float(R.string.texture_depth, 5f, 1f..22f, roundTo = 1)
            Value::lightAngle.float(R.string.light_angle, 45f, 0f..360f, roundTo = 1)
            Value::grain.float(R.string.grain, 0.3f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}