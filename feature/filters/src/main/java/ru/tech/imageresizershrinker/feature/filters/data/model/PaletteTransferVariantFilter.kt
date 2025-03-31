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
import com.awxkee.aire.Aire
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.PaletteTransferSpace
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.feature.filters.data.utils.toSpace

internal class PaletteTransferVariantFilter @AssistedInject internal constructor(
    @Assisted override val value: Triple<Float, PaletteTransferSpace, ImageModel> = Triple(
        first = 1f,
        second = PaletteTransferSpace.OKLAB,
        third = ImageModel(R.drawable.filter_preview_source_2)
    ),
    private val imageGetter: ImageGetter<Bitmap>
) : Transformation<Bitmap>, Filter.PaletteTransferVariant {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val reference = imageGetter.getImage(
            data = value.third.data,
            size = IntegerSize(1000, 1000)
        ) ?: return input

        return Aire.copyPalette(
            source = reference,
            destination = input,
            colorSpace = value.second.toSpace(),
            intensity = value.first
        )
    }

    @AssistedFactory
    interface Factory {

        operator fun invoke(
            @Assisted value: Triple<Float, PaletteTransferSpace, ImageModel> = Triple(
                first = 1f,
                second = PaletteTransferSpace.OKLAB,
                third = ImageModel(R.drawable.filter_preview_source_2)
            )
        ): PaletteTransferVariantFilter

    }

}