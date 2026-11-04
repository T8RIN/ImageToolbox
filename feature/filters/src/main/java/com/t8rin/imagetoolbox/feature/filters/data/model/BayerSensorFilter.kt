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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BayerSensor.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.OpticalEffects

@FilterDefinition(group = FilterGroups.GLITCH)
internal class BayerSensorFilter(
    override val value: Value
) : FilterTransformation, Filter.BayerSensor {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = OpticalEffects.bayerSensor(
        bitmap = input,
        pattern = value.pattern,
        scale = value.scale,
        shotNoise = value.shotNoise,
        readNoise = value.readNoise,
        seed = value.seed,
        strength = value.strength,
        edgeAware = value.edgeAware
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.bayer_sensor, R.string.filter_help_bayer_sensor)

        override val parameters = structuredParameters {
            Value::pattern.choice(
                R.string.bayer_pattern, 0,
                R.string.rggb,
                R.string.bggr,
                R.string.grbg,
                R.string.gbrg
            )
            Value::scale.float(R.string.scale, 0.55f, 0.1f..1f)
            Value::shotNoise.float(R.string.shot_noise, 0.045f, 0f..0.25f, roundTo = 3)
            Value::readNoise.float(R.string.read_noise, 0.006f, 0f..0.15f, roundTo = 3)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
            Value::edgeAware.toggle(
                R.string.demosaicing,
                false,
                R.string.bilinear,
                R.string.edge_aware
            )
        }
    }
}