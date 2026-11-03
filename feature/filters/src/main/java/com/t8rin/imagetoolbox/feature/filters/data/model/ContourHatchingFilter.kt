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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ContourHatching.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class ContourHatchingFilter(
    override val value: Value
) : FilterTransformation, Filter.ContourHatching {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.contourHatching(
        bitmap = input,
        spacing = value.spacing,
        width = value.width,
        angle = value.angle,
        contrast = value.contrast,
        ink = value.ink.colorInt,
        paper = value.paper.colorInt,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.contour_hatching, R.string.filter_help_contour_hatching)

        override val parameters = structuredParameters {
            Value::spacing.float(R.string.spacing, 7f, 2f..32f, roundTo = 1)
            Value::width.float(R.string.gmic_param_width, 0.65f, 0.1f..0.9f)
            Value::angle.float(R.string.angle, 35f, 0f..180f, roundTo = 1)
            Value::contrast.float(R.string.contrast, 1.35f, 0.5f..3f)
            Value::ink.color(R.string.ink_color, ColorModel(0xFF292429.toInt()))
            Value::paper.color(R.string.texture_paper_color, ColorModel(0xFFF7F1DD.toInt()))
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}