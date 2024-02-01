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

package ru.tech.imageresizershrinker.feature.filters.data

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import coil.size.Size
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.glitch.Glitcher

class GlitchFilter @AssistedInject internal constructor(
    @Assisted override val value: Triple<Float, Float, Float> = Triple(20f, 15f, 9f),
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : Transformation<Bitmap>, Filter.Glitch<Bitmap> {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = Glitcher(
        amount = value.first.toInt(),
        seed = value.second.toInt(),
        iterations = value.third.toInt()
    ).glitch(
        imageCompressor.compress(
            image = input,
            imageFormat = ImageFormat.Jpeg,
            quality = 100f,
        )
    ).let {
        imageGetter.getImage(it) ?: input
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            value: Triple<Float, Float, Float>
        ): GlitchFilter
    }

}