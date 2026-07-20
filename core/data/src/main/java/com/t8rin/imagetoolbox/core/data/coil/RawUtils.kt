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

package com.t8rin.imagetoolbox.core.data.coil

import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.raw_coder.RawDemosaicQuality
import com.t8rin.raw_coder.RawDevelopSettings
import com.t8rin.raw_coder.RawHighlightRecovery
import com.t8rin.raw_coder.RawOutputColorSpace
import com.t8rin.raw_coder.RawWhiteBalance
import com.t8rin.imagetoolbox.core.settings.domain.model.RawDemosaicQuality as DomainRawDemosaicQuality
import com.t8rin.imagetoolbox.core.settings.domain.model.RawHighlightRecovery as DomainRawHighlightRecovery
import com.t8rin.imagetoolbox.core.settings.domain.model.RawOutputColorSpace as DomainRawOutputColorSpace
import com.t8rin.imagetoolbox.core.settings.domain.model.RawWhiteBalance as DomainRawWhiteBalance

internal fun SettingsState.rawDevelopSettings() = RawDevelopSettings(
    whiteBalance = rawDevelopSettings.whiteBalance.toRawWhiteBalance(),
    outputColorSpace = rawDevelopSettings.outputColorSpace.toRawOutputColorSpace(),
    highlightRecovery = rawDevelopSettings.highlightRecovery.toRawHighlightRecovery(),
    exposureCompensationEv = rawDevelopSettings.exposureCompensationEv,
    highlightPreservation = rawDevelopSettings.highlightPreservation,
    autoBrightness = rawDevelopSettings.autoBrightness,
    brightness = if (rawDevelopSettings.autoBrightness) 1f else rawDevelopSettings.brightness,
    quality = rawDevelopSettings.quality.toRawDemosaicQuality(),
    halfSize = rawDevelopSettings.halfSize,
    applyOrientation = rawDevelopSettings.applyOrientation
)

private fun DomainRawWhiteBalance.toRawWhiteBalance(): RawWhiteBalance = when (this) {
    DomainRawWhiteBalance.Camera -> RawWhiteBalance.Camera
    DomainRawWhiteBalance.Auto -> RawWhiteBalance.Auto
    DomainRawWhiteBalance.Daylight -> RawWhiteBalance.Daylight
    is DomainRawWhiteBalance.Custom -> RawWhiteBalance.Custom(
        redMultiplier = redMultiplier,
        greenMultiplier = greenMultiplier,
        blueMultiplier = blueMultiplier,
        secondGreenMultiplier = secondGreenMultiplier
    )
}

private fun DomainRawHighlightRecovery.toRawHighlightRecovery(): RawHighlightRecovery =
    when (this) {
        DomainRawHighlightRecovery.Clip -> RawHighlightRecovery.Clip
        DomainRawHighlightRecovery.Unclip -> RawHighlightRecovery.Unclip
        DomainRawHighlightRecovery.Blend -> RawHighlightRecovery.Blend
        is DomainRawHighlightRecovery.Reconstruct -> RawHighlightRecovery.Reconstruct(level)
    }

private fun DomainRawOutputColorSpace.toRawOutputColorSpace(): RawOutputColorSpace = when (this) {
    DomainRawOutputColorSpace.SRgb -> RawOutputColorSpace.SRgb
    DomainRawOutputColorSpace.AdobeRgb -> RawOutputColorSpace.AdobeRgb
    DomainRawOutputColorSpace.WideGamutRgb -> RawOutputColorSpace.WideGamutRgb
    DomainRawOutputColorSpace.ProPhotoRgb -> RawOutputColorSpace.ProPhotoRgb
    DomainRawOutputColorSpace.Xyz -> RawOutputColorSpace.Xyz
    DomainRawOutputColorSpace.Aces -> RawOutputColorSpace.Aces
}

private fun DomainRawDemosaicQuality.toRawDemosaicQuality(): RawDemosaicQuality = when (this) {
    DomainRawDemosaicQuality.Linear -> RawDemosaicQuality.Linear
    DomainRawDemosaicQuality.Vng -> RawDemosaicQuality.Vng
    DomainRawDemosaicQuality.Ppg -> RawDemosaicQuality.Ppg
    DomainRawDemosaicQuality.Ahd -> RawDemosaicQuality.Ahd
    DomainRawDemosaicQuality.Dcb -> RawDemosaicQuality.Dcb
    DomainRawDemosaicQuality.Dht -> RawDemosaicQuality.Dht
    DomainRawDemosaicQuality.Aahd -> RawDemosaicQuality.Aahd
}
