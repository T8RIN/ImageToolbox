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

package com.t8rin.imagetoolbox.core.ui.transformation

import android.graphics.Bitmap
import coil3.size.Size
import coil3.size.pxOrElse
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.ui.utils.helper.asCoil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.math.roundToInt
import coil3.transform.Transformation as CoilTransformation

class ImageInfoTransformation @AssistedInject internal constructor(
    @Assisted private val imageInfo: ImageInfo,
    @Assisted private val preset: Preset,
    @Assisted private val transformations: List<Transformation<Bitmap>>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>
) : CoilTransformation(), Transformation<Bitmap> {

    override val cacheKey: String
        get() = Triple(imageInfo, preset, transformations).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = transform(input, size.asCoil())

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap {
        val transformedInput = imageScaler.scaleImage(
            image = input,
            width = size.width.pxOrElse { imageInfo.width },
            height = size.height.pxOrElse { imageInfo.height },
            resizeType = ResizeType.Flexible
        )

        val originalUri = shareProvider.cacheImage(
            image = input,
            imageInfo = ImageInfo(
                width = input.width,
                height = input.height
            )
        )

        val presetValue = preset.value()
        val presetInfo = imageTransformer.applyPresetBy(
            image = transformedInput,
            preset = preset,
            currentInfo = imageInfo.copy(
                originalUri = originalUri
            )
        )

        val info = presetInfo.copy(
            originalUri = originalUri,
            resizeType = presetInfo.resizeType.withOriginalSizeIfCrop(
                if (presetValue != null) {
                    if (presetValue > 100) {
                        IntegerSize(
                            (transformedInput.width.toFloat() / (presetValue / 100f)).roundToInt(),
                            (transformedInput.height.toFloat() / (presetValue / 100f)).roundToInt()
                        )
                    } else {
                        IntegerSize(
                            transformedInput.width,
                            transformedInput.height
                        )
                    }
                } else {
                    IntegerSize(input.width, input.height)
                }
            )
        )

        return imagePreviewCreator.createPreview(
            image = transformedInput,
            imageInfo = info,
            transformations = transformations,
            onGetByteCount = {}
        ) ?: input
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            imageInfo: ImageInfo,
            preset: Preset = Preset.Original,
            transformations: List<Transformation<Bitmap>> = emptyList(),
        ): ImageInfoTransformation
    }
}