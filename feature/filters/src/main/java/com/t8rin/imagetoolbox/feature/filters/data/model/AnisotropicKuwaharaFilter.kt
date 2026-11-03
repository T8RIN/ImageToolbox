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
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.opencv_tools.photo_filters.ArtisticEffects

@FilterInject
internal class AnisotropicKuwaharaFilter(
    override val value: Quad<Float, Float, Float, Float> = Quad(5.5f, 0.82f, 10f, 1f)
) : Transformation<Bitmap>, Filter.AnisotropicKuwahara {

    override val cacheKey: String
        get() = "${javaClass.name}:${value.hashCode()}"

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ArtisticEffects.anisotropicKuwahara(
        bitmap = input,
        radius = value.first,
        anisotropy = value.second,
        selectivity = value.third,
        strength = value.fourth
    )

}