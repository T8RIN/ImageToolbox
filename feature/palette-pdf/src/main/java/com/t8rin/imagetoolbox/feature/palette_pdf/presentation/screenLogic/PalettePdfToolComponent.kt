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

package com.t8rin.imagetoolbox.feature.palette_pdf.presentation.screenLogic

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
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.PalettePdfExporter
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.model.PalettePdfParams
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.model.PalettePdfSourceType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class PalettePdfToolComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri?,
    @Assisted initialIsPaletteFile: Boolean?,
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

    var sourceType by mutableStateOf(initialIsPaletteFile?.toSourceType())
        private set

    private var sourcePaletteFormat = initialPaletteFormat

    val hasSource: Boolean
        get() = sourceUri != null && sourceType != null

    val hasSourceImage: Boolean
        get() = sourceType == PalettePdfSourceType.Image

    val sourceFilename: String
        get() = sourceUri?.filename()
            ?: sourceUri?.lastPathSegment
            ?: "Palette"

    private val _params = mutableStateOf(
        PalettePdfParams(includeSourceImage = sourceType != PalettePdfSourceType.PaletteFile)
    )
    val params by _params

    private val _isSaving = mutableStateOf(false)
    val isSaving by _isSaving

    val outputFilename: String
        get() {
            val name = sourceUri?.filename()
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
    private var sourceJob: Job? by smartJob {
        _isImageLoading.value = false
    }

    init {
        if (initialUri != null && sourceType == null) {
            updateSourceUri(initialUri)
        }
    }

    fun updateParams(value: PalettePdfParams) {
        _params.value = value
    }

    fun updateSourceUri(
        uri: Uri,
        type: PalettePdfSourceType? = null
    ) {
        if (type != null) {
            applySource(
                uri = uri,
                type = type
            )
            return
        }

        sourceJob = componentScope.launch {
            _isImageLoading.value = true
            val detectedType = palettePdfExporter.detectSourceType(uri.toString())
            _isImageLoading.value = false

            if (detectedType != null) {
                applySource(
                    uri = uri,
                    type = detectedType
                )
            } else {
                AppToastHost.showFailureToast(
                    appContext.getString(R.string.file_is_corrupted_or_unsupported)
                )
            }
        }
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
        val uri = requireNotNull(sourceUri)
        val type = requireNotNull(sourceType)

        palettePdfExporter.export(
            sourceUri = uri.toString(),
            sourceType = type,
            sourcePaletteFormat = sourcePaletteFormat,
            params = params,
            writeable = writeable
        )
    }

    private fun applySource(
        uri: Uri,
        type: PalettePdfSourceType
    ) {
        sourceUri = uri
        sourceType = type
        sourcePaletteFormat = null
        _params.value = params.copy(
            includeSourceImage = type == PalettePdfSourceType.Image,
            showSourceFilename = type == PalettePdfSourceType.PaletteFile
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            initialIsPaletteFile: Boolean?,
            initialPaletteFormat: String?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PalettePdfToolComponent
    }

    private fun Boolean.toSourceType(): PalettePdfSourceType = if (this) {
        PalettePdfSourceType.PaletteFile
    } else {
        PalettePdfSourceType.Image
    }
}
