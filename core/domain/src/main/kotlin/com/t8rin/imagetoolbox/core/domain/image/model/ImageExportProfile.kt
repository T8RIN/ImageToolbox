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

package com.t8rin.imagetoolbox.core.domain.image.model

import com.t8rin.imagetoolbox.core.domain.model.ColorModel

data class ImageExportProfiles(
    val presets: List<ImageExportProfile> = emptyList()
)

data class ImageExportProfile(
    val name: String = "",
    val imageInfo: ImageInfo = ImageInfo(),
    val preset: Preset = Preset.None,
    val keepExif: Boolean? = null,
    val backgroundColorForNoAlphaFormats: Int? = null,
    val version: Int = 1
) {

    fun toImageInfo(current: ImageInfo): ImageInfo = imageInfo.copy(
        width = if (preset.isEmpty()) imageInfo.width else current.width,
        height = if (preset.isEmpty()) imageInfo.height else current.height,
        quality = imageInfo.quality.coerceIn(imageInfo.imageFormat),
        originalUri = current.originalUri,
        sizeInBytes = 0
    )

    fun applyExportSettingsTo(imageInfo: ImageInfo): ImageInfo = imageInfo.copy(
        imageFormat = this.imageInfo.imageFormat,
        quality = this.imageInfo.quality.coerceIn(this.imageInfo.imageFormat),
        resizeType = this.imageInfo.resizeType,
        imageScaleMode = this.imageInfo.imageScaleMode,
        rotationDegrees = this.imageInfo.rotationDegrees,
        isFlipped = this.imageInfo.isFlipped,
        sizeInBytes = 0
    )

    fun backgroundColorModel(): ColorModel? = backgroundColorForNoAlphaFormats?.let(::ColorModel)

    companion object {
        fun from(
            name: String,
            imageInfo: ImageInfo,
            preset: Preset,
            keepExif: Boolean? = null,
            backgroundColorForNoAlphaFormats: ColorModel? = null
        ): ImageExportProfile = ImageExportProfile(
            name = name.trim(),
            imageInfo = imageInfo.copy(
                originalUri = null,
                sizeInBytes = 0
            ),
            preset = preset,
            keepExif = keepExif,
            backgroundColorForNoAlphaFormats = backgroundColorForNoAlphaFormats?.colorInt
        )
    }
}
