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
import com.t8rin.raw_coder.RawOutputColorSpace
import com.t8rin.imagetoolbox.core.settings.domain.model.RawDemosaicQuality as DomainRawDemosaicQuality
import com.t8rin.imagetoolbox.core.settings.domain.model.RawOutputColorSpace as DomainRawOutputColorSpace

internal fun SettingsState.rawDevelopSettings() = RawDevelopSettings(
    useCameraWhiteBalance = rawDevelopSettings.useCameraWhiteBalance,
    useAutoWhiteBalance = rawDevelopSettings.useAutoWhiteBalance,
    outputColorSpace = rawDevelopSettings.outputColorSpace.toRawOutputColorSpace(),
    highlightRecovery = rawDevelopSettings.highlightRecovery,
    quality = rawDevelopSettings.quality.toRawDemosaicQuality(),
    halfSize = rawDevelopSettings.halfSize,
    applyOrientation = rawDevelopSettings.applyOrientation
)

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
