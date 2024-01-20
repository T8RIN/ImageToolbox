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

package ru.tech.imageresizershrinker.core.ui.transformation

import android.graphics.Bitmap
import coil.size.Size
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import coil.transform.Transformation as CoilTransformation

class ImageInfoTransformation @AssistedInject constructor(
    @Assisted private val imageInfo: ImageInfo,
    @Assisted private val preset: Preset = Preset.Numeric(100),
    @Assisted private val transformations: List<Transformation<Bitmap>> = emptyList(),
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>
) : CoilTransformation, Transformation<Bitmap> {

    override val cacheKey: String
        get() = (imageInfo to preset to imageTransformer to transformations).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val transformedInput =
            imageScaler.scaleImage(
                image = input,
                width = imageInfo.width,
                height = imageInfo.height,
                resizeType = ResizeType.Flexible,
                imageScaleMode = ImageScaleMode.NotPresent
            )!!
        var info = imageInfo
        if (!preset.isEmpty()) {
            val currentInfo = info.copy()
            info = imageTransformer.applyPresetBy(
                image = transformedInput,
                preset = preset,
                currentInfo = info
            ).let {
                if (it.quality != currentInfo.quality) {
                    it.copy(quality = currentInfo.quality)
                } else it
            }
        }
        return imagePreviewCreator.createPreview(
            image = transformedInput,
            imageInfo = info,
            transformations = transformations,
            onGetByteCount = {}
        )
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            imageInfo: ImageInfo,
            preset: Preset = Preset.Numeric(100),
            transformations: List<Transformation<Bitmap>> = emptyList(),
        ): ImageInfoTransformation
    }
}