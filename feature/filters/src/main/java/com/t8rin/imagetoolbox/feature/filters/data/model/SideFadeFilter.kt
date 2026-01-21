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
import androidx.core.graphics.applyCanvas
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.data.getPaint
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import kotlin.math.roundToInt

@FilterInject
internal class SideFadeFilter(
    override val value: SideFadeParams = SideFadeParams.Relative(FadeSide.Start, 0.5f),
) : Transformation<Bitmap>, Filter.SideFade {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val bitmap = input.copy(input.safeConfig, true).apply { setHasAlpha(true) }
        val fadeSize: Int = when (value) {
            is SideFadeParams.Absolute -> value.size
            is SideFadeParams.Relative -> {
                when (value.side) {
                    FadeSide.Start, FadeSide.End -> {
                        bitmap.width * value.scale
                    }

                    FadeSide.Bottom, FadeSide.Top -> {
                        bitmap.height * value.scale
                    }
                }.roundToInt()
            }
        }
        val strength = when (value) {
            is SideFadeParams.Absolute -> value.strength
            is SideFadeParams.Relative -> 1f
        }

        return bitmap.applyCanvas {
            drawPaint(
                value.side.getPaint(
                    bmp = input,
                    length = fadeSize,
                    strength = strength
                )
            )
        }
    }

}