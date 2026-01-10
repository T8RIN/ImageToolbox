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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.utils.getFilename
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.PaletteType
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedPalette
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.PaletteFormatHelper
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.toNamed
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.toPalette
import com.t8rin.palette.PaletteFormat
import com.t8rin.palette.decode
import com.t8rin.palette.encode
import com.t8rin.palette.getCoder
import com.t8rin.palette.use
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PaletteToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    private val _paletteType: MutableState<PaletteType?> = mutableStateOf(null)
    val paletteType by _paletteType

    private val _paletteFormat: MutableState<PaletteFormat?> = mutableStateOf(null)
    val paletteFormat by _paletteFormat

    private val _palette: MutableState<NamedPalette> = mutableStateOf(NamedPalette())
    val palette by _palette

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _uri = mutableStateOf<Uri?>(null)

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var savingJob by smartJob()

    fun setUri(uri: Uri?) {
        _uri.value = uri
        if (uri == null) {
            _paletteType.update { null }
            _paletteFormat.update { null }
            _palette.update { NamedPalette() }
            _bitmap.value = null
            return
        }

        componentScope.launch {
            _isImageLoading.value = true

            _bitmap.value = imageScaler.scaleUntilCanShow(
                imageGetter.getImage(
                    data = uri.toString(),
                    originalSize = false
                )
            )

            if (bitmap == null || paletteType == PaletteType.Edit) {
                _bitmap.update { null }
                val data = fileController.readBytes(uri.toString())
                val entries = PaletteFormatHelper.entriesFor(uri.getFilename() ?: uri.toString())

                for (format in entries) {
                    format.getCoder().use { decode(data) }.onSuccess { palette ->
                        palette.toNamed()?.let { named ->
                            _palette.update { named }
                            updatePaletteFormat(format)
                            break
                        }
                    }
                }
            }

            _isImageLoading.value = false
        }
    }

    fun savePaletteTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        val format = paletteFormat ?: PaletteFormatHelper.entries.first()

        savingJob = trackProgress {
            _isSaving.value = true
            fileController.writeBytes(
                uri = uri.toString(),
                block = {
                    it.writeBytes(
                        format.getCoder().encode(palette.toPalette())
                    )
                }
            ).also(onResult).onSuccess(::registerSave)
            _isSaving.value = false
        }
    }

    fun sharePalette(
        onComplete: () -> Unit
    ) {
        val format = paletteFormat ?: PaletteFormatHelper.entries.first()

        savingJob = trackProgress {
            _isSaving.value = true
            val data = format.getCoder().use {
                encode(palette.toPalette())
            }.getOrNull()

            if (data == null) {
                _isSaving.update { false }
                return@trackProgress
            }

            shareProvider.shareByteArray(
                byteArray = data,
                filename = createPaletteFilename(),
                onComplete = {
                    _isSaving.value = false
                    onComplete()
                }
            )
        }
    }

    fun updatePalette(palette: NamedPalette) {
        _palette.update { palette }
    }

    fun updatePaletteFormat(format: PaletteFormat) {
        _paletteFormat.update { format }
    }

    fun setPaletteType(type: PaletteType) {
        _paletteType.update { type }
        if (type != PaletteType.Edit) {
            _palette.update { NamedPalette() }
            _paletteFormat.update { null }
        }
    }

    fun createPaletteFilename(): String {
        val name = palette.name.ifBlank { "Palette_Export" }
        val format = paletteFormat ?: PaletteFormatHelper.entries.first()
        val extension = format.fileExtension.maxBy { it.length }

        return "${name}_${timestamp()}.$extension"
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
        ): PaletteToolsComponent
    }
}