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
import androidx.exifinterface.media.ExifInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary
import ru.tech.imageresizershrinker.core.data.utils.toModel
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.UriModel
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter

internal class PaletteTransferFilter @AssistedInject constructor(
    @Assisted override val value: Pair<Float, UriModel> = 1f to "file:///android_asset/svg/emotions/aasparkles.svg".toModel(),
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : Transformation<Bitmap>, Filter.PaletteTransfer {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val reference = imageGetter.getImage(value.second.uri)?.image ?: return input

        return GPUImageNativeLibrary.transferPalette(
            source = reference,
            target = input,
            intensity = value.first
        )
    }

    @AssistedFactory
    interface Factory {

        operator fun invoke(
            @Assisted value: Pair<Float, UriModel> = 1f to "file:///android_asset/svg/emotions/aasparkles.svg".toModel()
        ): PaletteTransferFilter

    }

}