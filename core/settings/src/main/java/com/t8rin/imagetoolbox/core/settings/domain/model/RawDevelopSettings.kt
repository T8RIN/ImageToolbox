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

package com.t8rin.imagetoolbox.core.settings.domain.model

data class RawDevelopSettings(
    val useEmbeddedPreview: Boolean = false,
    val whiteBalance: RawWhiteBalance = RawWhiteBalance.Camera,
    val outputColorSpace: RawOutputColorSpace = RawOutputColorSpace.SRgb,
    val highlightRecovery: RawHighlightRecovery = RawHighlightRecovery.Clip,
    val exposureCompensationEv: Float = 0f,
    val highlightPreservation: Float = 0f,
    val autoBrightness: Boolean = true,
    val brightness: Float = 1f,
    val quality: RawDemosaicQuality = RawDemosaicQuality.Ahd,
    val halfSize: Boolean = false,
    val applyOrientation: Boolean = true,
)

sealed interface RawWhiteBalance {
    data object Camera : RawWhiteBalance
    data object Auto : RawWhiteBalance
    data object Daylight : RawWhiteBalance

    data class Custom(
        val redMultiplier: Float = 1f,
        val greenMultiplier: Float = 1f,
        val blueMultiplier: Float = 1f,
        val secondGreenMultiplier: Float = greenMultiplier,
    ) : RawWhiteBalance
}

sealed interface RawHighlightRecovery {
    data object Clip : RawHighlightRecovery
    data object Unclip : RawHighlightRecovery
    data object Blend : RawHighlightRecovery

    data class Reconstruct(
        val level: Int = 5,
    ) : RawHighlightRecovery
}

enum class RawOutputColorSpace {
    SRgb,
    AdobeRgb,
    WideGamutRgb,
    ProPhotoRgb,
    Xyz,
    Aces;

    companion object {
        fun fromOrdinal(ordinal: Int?): RawOutputColorSpace? = entries.getOrNull(ordinal ?: -1)
    }
}

enum class RawDemosaicQuality {
    Linear,
    Vng,
    Ppg,
    Ahd,
    Dcb,
    Dht,
    Aahd;

    companion object {
        fun fromOrdinal(ordinal: Int?): RawDemosaicQuality? = entries.getOrNull(ordinal ?: -1)
    }
}
