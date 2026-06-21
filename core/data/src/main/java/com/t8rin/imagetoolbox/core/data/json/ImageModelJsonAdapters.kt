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

package com.t8rin.imagetoolbox.core.data.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeAnchor
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.image.model.ScaleColorSpace
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position

internal class ImageFormatJsonAdapter {

    @FromJson
    fun fromJson(value: String?): ImageFormat = ImageFormat.entries.firstOrNull {
        it.title == value
    } ?: ImageFormat.Default

    @ToJson
    fun toJson(value: ImageFormat): String = value.title
}

internal class PresetJsonAdapter {

    @FromJson
    fun fromJson(value: PresetJson?): Preset = when (value?.type) {
        Telegram -> Preset.Telegram
        Percentage -> value.value?.let(Preset::Percentage) ?: Preset.None
        AspectRatio -> Preset.AspectRatio(
            ratio = value.ratio ?: 1f,
            isFit = value.isFit ?: false
        )

        else -> Preset.None
    }

    @ToJson
    fun toJson(value: Preset): PresetJson = when (value) {
        Preset.None -> PresetJson(type = None)
        Preset.Telegram -> PresetJson(type = Telegram)
        is Preset.Percentage -> PresetJson(
            type = Percentage,
            value = value.value
        )

        is Preset.AspectRatio -> PresetJson(
            type = AspectRatio,
            ratio = value.ratio,
            isFit = value.isFit
        )
    }

    private companion object {
        const val None = "none"
        const val Telegram = "telegram"
        const val Percentage = "percentage"
        const val AspectRatio = "aspect_ratio"
    }
}

internal class ResizeTypeJsonAdapter {

    @FromJson
    fun fromJson(value: ResizeTypeJson?): ResizeType = when (value?.type) {
        Flexible -> ResizeType.Flexible(
            resizeAnchor = value.resizeAnchor
        )

        CenterCrop -> ResizeType.CenterCrop(
            canvasColor = value.canvasColor,
            blurRadius = value.blurRadius,
            originalSize = value.originalSize,
            scaleFactor = value.scaleFactor,
            position = value.position
        )

        Fit -> ResizeType.Fit(
            canvasColor = value.canvasColor,
            blurRadius = value.blurRadius,
            position = value.position
        )

        else -> ResizeType.Explicit
    }

    @ToJson
    fun toJson(value: ResizeType): ResizeTypeJson = when (value) {
        ResizeType.Explicit -> ResizeTypeJson(type = Explicit)

        is ResizeType.Flexible -> ResizeTypeJson(
            type = Flexible,
            resizeAnchor = value.resizeAnchor
        )

        is ResizeType.CenterCrop -> ResizeTypeJson(
            type = CenterCrop,
            canvasColor = value.canvasColor,
            blurRadius = value.blurRadius,
            originalSize = value.originalSize,
            scaleFactor = value.scaleFactor,
            position = value.position
        )

        is ResizeType.Fit -> ResizeTypeJson(
            type = Fit,
            canvasColor = value.canvasColor,
            blurRadius = value.blurRadius,
            position = value.position
        )
    }

    private companion object {
        const val Explicit = "explicit"
        const val Flexible = "flexible"
        const val CenterCrop = "center_crop"
        const val Fit = "fit"
    }
}

internal class ImageScaleModeJsonAdapter {

    @FromJson
    fun fromJson(value: ImageScaleModeJson?): ImageScaleMode = ImageScaleMode
        .fromInt(value?.value ?: ImageScaleMode.Default.value)
        .copy(
            scaleColorSpace = ScaleColorSpace.fromOrdinal(
                value?.scaleColorSpace ?: ScaleColorSpace.Default.ordinal
            )
        )

    @ToJson
    fun toJson(value: ImageScaleMode): ImageScaleModeJson = ImageScaleModeJson(
        value = value.value,
        scaleColorSpace = value.scaleColorSpace.ordinal
    )
}

internal data class PresetJson(
    val type: String = "none",
    val value: Int? = null,
    val ratio: Float? = null,
    val isFit: Boolean? = null
)

internal data class ResizeTypeJson(
    val type: String = "explicit",
    val resizeAnchor: ResizeAnchor = ResizeAnchor.Default,
    val canvasColor: Int? = 0,
    val blurRadius: Int = 35,
    val originalSize: IntegerSize = IntegerSize.Undefined,
    val scaleFactor: Float = 1f,
    val position: Position = Position.Center
)

internal data class ImageScaleModeJson(
    val value: Int = ImageScaleMode.Default.value,
    val scaleColorSpace: Int = ScaleColorSpace.Default.ordinal
)
