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
import com.t8rin.imagetoolbox.core.domain.image.ImagePresetsUseCase
import com.t8rin.imagetoolbox.core.domain.image.model.ImagePreset
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
interface ImagePresetsHolder {

    val imagePresets: StateFlow<List<ImagePreset>>

    val currentImagePresetKeepExif: Boolean?
        get() = null

    fun updatePreset(preset: Preset)

    fun applyImagePreset(preset: ImagePreset)

    fun saveImagePreset(name: String)

    fun deleteImagePreset(preset: ImagePreset)

    fun exportImagePreset(
        preset: ImagePreset,
        uri: Uri
    )

    fun shareImagePreset(preset: ImagePreset)

    fun importImagePreset(uri: Uri)

    companion object {
        operator fun invoke(
            imagePresetsUseCase: ImagePresetsUseCase,
            componentScope: CoroutineScope
        ): ImagePresetsHolder = ImagePresetsHolderImpl(
            imagePresetsUseCase = imagePresetsUseCase,
            componentScope = componentScope
        )
    }

}

private class ImagePresetsHolderImpl(
    private val imagePresetsUseCase: ImagePresetsUseCase,
    private val componentScope: CoroutineScope
) : ImagePresetsHolder {

    override val imagePresets: StateFlow<List<ImagePreset>> = imagePresetsUseCase.presets
        .stateIn(componentScope, SharingStarted.Eagerly, emptyList())

    override fun updatePreset(preset: Preset): Unit = missingScreenOverride()

    override fun applyImagePreset(preset: ImagePreset): Unit = missingScreenOverride()

    override fun saveImagePreset(name: String): Unit = missingScreenOverride()

    override fun deleteImagePreset(preset: ImagePreset) {
        componentScope.launch {
            imagePresetsUseCase.delete(preset)
        }
    }

    override fun exportImagePreset(
        preset: ImagePreset,
        uri: Uri
    ) {
        componentScope.launch {
            imagePresetsUseCase.export(
                preset = preset,
                uri = uri.toString()
            )
        }
    }

    override fun shareImagePreset(preset: ImagePreset) {
        componentScope.launch {
            imagePresetsUseCase.share(preset)
        }
    }

    override fun importImagePreset(uri: Uri) {
        componentScope.launch {
            imagePresetsUseCase.importPreset(uri.toString())
        }
    }

    private fun missingScreenOverride(): Nothing = error(
        "Screen-specific ImagePresetsHolder method must be overridden by the component"
    )

}
