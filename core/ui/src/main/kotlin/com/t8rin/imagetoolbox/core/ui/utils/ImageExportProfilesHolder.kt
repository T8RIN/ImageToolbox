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

package com.t8rin.imagetoolbox.core.ui.utils

import android.net.Uri
import androidx.compose.runtime.Stable
import com.t8rin.imagetoolbox.core.domain.image.ImageExportProfilesUseCase
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
interface ImageExportProfilesHolder {

    val imageProfiles: StateFlow<List<ImageExportProfile>>

    val currentProfileKeepExif: Boolean?
        get() = null

    fun updateProfile(profile: Preset)

    fun applyProfile(profile: ImageExportProfile)

    fun saveProfile(name: String)

    fun deleteProfile(profile: ImageExportProfile)

    fun exportProfile(
        preset: ImageExportProfile,
        uri: Uri
    )

    fun shareProfile(preset: ImageExportProfile)

    fun importProfile(uri: Uri)

    companion object {
        operator fun invoke(
            imageExportProfilesUseCase: ImageExportProfilesUseCase,
            componentScope: CoroutineScope
        ): ImageExportProfilesHolder = ImageExportProfilesHolderImpl(
            imageExportProfilesUseCase = imageExportProfilesUseCase,
            componentScope = componentScope
        )
    }

}

private class ImageExportProfilesHolderImpl(
    private val imageExportProfilesUseCase: ImageExportProfilesUseCase,
    private val componentScope: CoroutineScope
) : ImageExportProfilesHolder {

    override val imageProfiles: StateFlow<List<ImageExportProfile>> =
        imageExportProfilesUseCase.profiles
        .stateIn(componentScope, SharingStarted.Eagerly, emptyList())

    override fun updateProfile(profile: Preset) = Unit

    override fun applyProfile(profile: ImageExportProfile) = Unit

    override fun saveProfile(name: String) = Unit

    override fun deleteProfile(profile: ImageExportProfile) {
        componentScope.launch {
            imageExportProfilesUseCase.delete(profile)
        }
    }

    override fun exportProfile(
        preset: ImageExportProfile,
        uri: Uri
    ) {
        componentScope.launch {
            imageExportProfilesUseCase.export(
                profile = preset,
                uri = uri.toString()
            )
        }
    }

    override fun shareProfile(preset: ImageExportProfile) {
        componentScope.launch {
            imageExportProfilesUseCase.share(preset)
        }
    }

    override fun importProfile(uri: Uri) {
        componentScope.launch {
            imageExportProfilesUseCase.importProfile(uri.toString())
        }
    }

}
