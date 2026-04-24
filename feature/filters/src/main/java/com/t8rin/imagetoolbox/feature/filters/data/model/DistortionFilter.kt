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
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.opencv_tools.seam_carving.SeamCarver
import kotlin.math.roundToInt

@FilterInject
internal class DistortionFilter(
    override val value: Float = 50f
) : Transformation<Bitmap>, Filter.Distortion {

    override val cacheKey: String
        get() = value.toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val amount = value.coerceIn(0f, 95f)
        if (amount <= 0f) return input

        val targetScale = 1f - amount / 100f
        val targetWidth = (input.width * targetScale).roundToInt().coerceAtLeast(1)
        val targetHeight = (input.height * targetScale).roundToInt().coerceAtLeast(1)

        if (targetWidth == input.width && targetHeight == input.height) return input

        return SeamCarver
            .carve(
                bitmap = input,
                desiredWidth = targetWidth,
                desiredHeight = targetHeight
            )
            .scale(
                width = input.width,
                height = input.height
            )
    }

}
