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
import com.jhlabs.JhFilter
import com.jhlabs.SparkleFilter
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Sparkle.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.max
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.LIGHT)
internal class SparkleFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Sparkle {

    override fun createFilter(): JhFilter = SparkleFilter(1, 1)

    override fun createFilter(image: Bitmap): JhFilter =
        SparkleFilter(
            (value.centreX * image.width).roundToInt(),
            (value.centreY * image.height).roundToInt()
        ).apply {
            amount = value.amount
            rays = value.rays
            radius = (value.radius * (max(image.width, image.height) / 2f)).roundToInt()
            randomness = value.randomness
            color = value.color.colorInt
        }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.sparkle, R.string.filter_help_sparkle)

        override val parameters = structuredParameters {
            Value::amount.int(R.string.amount, 50, 0..200)
            Value::rays.int(R.string.rays, 50, 0..200)
            Value::radius.float(R.string.radius, 0.3f, 0f..1f)
            Value::randomness.int(R.string.randomness, 25, 0..100)
            Value::centreX.float(R.string.center_x, 0.5f, 0f..1f)
            Value::centreY.float(R.string.center_y, 0.5f, 0f..1f)
            Value::color.color(R.string.color, ColorModel(0xFFFFFFFF.toInt()))
        }
    }
}