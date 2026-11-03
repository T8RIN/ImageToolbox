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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SignedDistanceField.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class SignedDistanceFieldFilter(
    override val value: Value
) : FilterTransformation, Filter.SignedDistanceField {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.signedDistanceField(
        bitmap = input,
        threshold = value.threshold,
        distance = value.distance,
        mode = value.mode,
        spacing = value.spacing,
        insideColor = value.insideColor.colorInt,
        outsideColor = value.outsideColor.colorInt,
        invert = value.invert,
        alphaMask = value.alphaMask
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.signed_distance_field, R.string.filter_help_signed_distance_field)

        override val parameters = structuredParameters {
            Value::alphaMask.toggle(R.string.mask_source, false, R.string.luminance, R.string.alpha)
            Value::threshold.float(R.string.threshold, 0.48f, 0f..1f)
            Value::distance.float(R.string.distance, 44f, 1f..180f, roundTo = 1)
            Value::mode.choice(
                R.string.gmic_param_mode, 0,
                R.string.gradient,
                R.string.glow,
                R.string.isolines
            )
            Value::spacing.float(R.string.spacing, 8f, 1f..60f, roundTo = 1)
            Value::insideColor.color(R.string.inside_color, ColorModel(0xFFF6EFDE.toInt()))
            Value::outsideColor.color(R.string.outside_color, ColorModel(0xFF101D36.toInt()))
            Value::invert.toggle(
                R.string.gmic_param_invert,
                false,
                R.string.normal,
                R.string.inverted
            )
        }
    }
}