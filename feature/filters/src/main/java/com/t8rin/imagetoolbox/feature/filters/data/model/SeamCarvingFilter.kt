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
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SeamCarvingParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.utils.image.loadBitmap
import com.t8rin.opencv_tools.seam_carving.SeamCarver

@FilterInject
internal class SeamCarvingFilter(
    override val value: SeamCarvingParams = SeamCarvingParams()
) : Transformation<Bitmap>, Filter.SeamCarving {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = if (value.size.isZero()) {
        input
    } else {
        val mask = value.maskFile.uri
            .takeIf { it.isNotEmpty() }
            ?.loadBitmap()
            ?.let { bitmap ->
                if (bitmap.width == input.width && bitmap.height == input.height) {
                    bitmap
                } else {
                    bitmap.scale(
                        width = input.width,
                        height = input.height
                    )
                }.copy(input.safeConfig, false)
            }

        SeamCarver.carve(
            bitmap = input,
            desiredWidth = value.size.width,
            desiredHeight = value.size.height,
            protectionMask = mask,
            useBackwardEnergy = value.useBackwardEnergy,
            useMaskAsRemoval = value.useMaskAsRemoval,
        )
    }

}