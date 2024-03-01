/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter

internal class ShuffleBlurFilter(
    override val value: Pair<Int, Float> = 35 to 1f
) : Transformation<Bitmap>, Filter.ShuffleBlur<Bitmap> {

    private val radiusMapping = listOf(0f, RAD_1) + List(200) {
        RAD_2 + STEP * it
    }

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = input.copy(input.config, true).apply {
        GPUImageNativeLibrary.shuffle(
            this, value.second,
            radiusMapping.getOrNull(value.first) ?: 0f
        )
    }

    private companion object {
        private const val RAD_1 = 0.001f
        private const val RAD_2 = 0.005f
        private const val STEP = 0.005f
    }

}