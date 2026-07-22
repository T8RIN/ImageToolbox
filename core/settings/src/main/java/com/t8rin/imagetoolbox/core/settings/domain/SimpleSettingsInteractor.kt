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

package com.t8rin.imagetoolbox.core.settings.domain

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.RawDemosaicQuality
import com.t8rin.imagetoolbox.core.settings.domain.model.RawHighlightRecovery
import com.t8rin.imagetoolbox.core.settings.domain.model.RawOutputColorSpace
import com.t8rin.imagetoolbox.core.settings.domain.model.RawWhiteBalance

interface SimpleSettingsInteractor {

    suspend fun toggleRawUseEmbeddedPreview()

    suspend fun toggleMagnifierEnabled()

    suspend fun toggleCropOverlayDraggable()

    suspend fun setOneTimeSaveLocations(value: List<OneTimeSaveLocation>)

    suspend fun toggleRecentColor(
        color: ColorModel,
        forceExclude: Boolean = false
    )

    suspend fun toggleFavoriteColor(
        color: ColorModel,
        forceExclude: Boolean = true
    )

    fun isInstalledFromPlayStore(): Boolean

    suspend fun toggleSettingsGroupVisibility(
        key: Int,
        value: Boolean
    )

    suspend fun clearRecentColors()

    suspend fun updateFavoriteColors(
        colors: List<ColorModel>
    )

    suspend fun setBackgroundColorForNoAlphaFormats(
        color: ColorModel
    )

    suspend fun setAspectRatios(aspectRatios: List<DomainAspectRatio.Custom>)

    suspend fun toggleCustomAsciiGradient(gradient: String)

    suspend fun toggleOverwriteFiles()

    suspend fun toggleSaveToOriginalFolder()

    suspend fun toggleDeleteOriginalsAfterSave()

    suspend fun setSpotHealMode(mode: Int)

    suspend fun setBorderWidth(width: Float)

    suspend fun setRawWhiteBalance(whiteBalance: RawWhiteBalance)

    suspend fun setRawOutputColorSpace(colorSpace: RawOutputColorSpace)

    suspend fun setRawHighlightRecovery(recovery: RawHighlightRecovery)

    suspend fun setRawExposureCompensation(value: Float)

    suspend fun setRawHighlightPreservation(value: Float)

    suspend fun toggleRawAutoBrightness()

    suspend fun setRawBrightness(value: Float)

    suspend fun setRawDemosaicQuality(quality: RawDemosaicQuality)

    suspend fun toggleRawHalfSize()

    suspend fun toggleRawApplyOrientation()

}