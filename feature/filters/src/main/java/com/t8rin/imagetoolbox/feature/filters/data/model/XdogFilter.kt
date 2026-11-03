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
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.XdogParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterInject
internal class XdogFilter(
    override val value: XdogParams = XdogParams.Default
) : Transformation<Bitmap>, Filter.Xdog {

    override val cacheKey: String
        get() = "${javaClass.name}:${value.hashCode()}"

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

}