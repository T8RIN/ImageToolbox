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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.pdf.screenLogic

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.palette_tools.domain.PalettePdfExporter
import com.t8rin.imagetoolbox.feature.palette_tools.domain.model.PalettePdfParams
import com.t8rin.imagetoolbox.feature.palette_tools.domain.model.PalettePdfSourceType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PalettePdfToolComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri,
    @Assisted initialIsPaletteFile: Boolean,
    @Assisted initialPaletteFormat: String?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val shareProvider: ShareProvider,
    private val palettePdfExporter: PalettePdfExporter,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    var sourceUri by mutableStateOf(initialUri)
        private set

    val sourceType: PalettePdfSourceType = if (initialIsPaletteFile) {
        PalettePdfSourceType.PaletteFile
    } else {
        PalettePdfSourceType.Image
    }

    private var sourcePaletteFormat = initialPaletteFormat

    val hasSourceImage: Boolean
        get() = sourceType == PalettePdfSourceType.Image

    private val _params = mutableStateOf(
        PalettePdfParams(includeSourceImage = hasSourceImage)
    )
    val params by _params

    private val _isSaving = mutableStateOf(false)
    val isSaving by _isSaving

    val outputFilename: String
        get() {
            val name = sourceUri.filename()
                ?.substringBeforeLast('.')
                .orEmpty()
                .ifBlank { "Palette" }
            return if (sourceType == PalettePdfSourceType.PaletteFile) {
                "$name.pdf"
            } else {
                "${name}_${timestamp()}.pdf"
            }
        }

    private var savingJob by smartJob()

    fun updateParams(value: PalettePdfParams) {
        _params.value = value
    }

    fun updateSourceUri(uri: Uri) {
        sourceUri = uri
        sourcePaletteFormat = null
    }

    fun saveTo(uri: Uri) {
        savingJob = trackProgress {
            _isSaving.value = true
            fileController.writeBytes(
                uri = uri.toString(),
                block = ::export
            ).also(::parseFileSaveResult).onSuccess(::registerSave)
            _isSaving.value = false
        }
    }

    fun share() {
        savingJob = trackProgress {
            _isSaving.value = true
            val uri = cachePdf()

            if (uri == null) {
                _isSaving.value = false
                AppToastHost.showFailureToast(
                    appContext.getString(R.string.something_went_wrong)
                )
                return@trackProgress
            }

            shareProvider.shareUri(
                uri = uri,
                onComplete = {
                    _isSaving.value = false
                    AppToastHost.showConfetti()
                }
            )
        }
    }

    fun prepareForEditing(onSuccess: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            val uri = cachePdf()
            _isSaving.value = false

            if (uri != null) {
                onSuccess(uri.toUri())
            } else {
                AppToastHost.showFailureToast(
                    appContext.getString(R.string.something_went_wrong)
                )
            }
        }
    }

    private suspend fun cachePdf(): String? = shareProvider.cacheData(
        writeData = ::export,
        filename = outputFilename
    )

    private suspend fun export(writeable: Writeable) {
        palettePdfExporter.export(
            sourceUri = sourceUri.toString(),
            sourceType = sourceType,
            sourcePaletteFormat = sourcePaletteFormat,
            params = params,
            writeable = writeable
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri,
            initialIsPaletteFile: Boolean,
            initialPaletteFormat: String?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PalettePdfToolComponent
    }
}
