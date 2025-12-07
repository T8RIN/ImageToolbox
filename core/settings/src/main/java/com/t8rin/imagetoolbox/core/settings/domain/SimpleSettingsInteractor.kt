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

package com.t8rin.imagetoolbox.core.settings.domain

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation

interface SimpleSettingsInteractor {

    suspend fun toggleMagnifierEnabled()

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

    suspend fun toggleCustomAsciiGradient(gradient: String)

    suspend fun toggleOverwriteFiles()

    suspend fun setSpotHealMode(mode: Int)

}