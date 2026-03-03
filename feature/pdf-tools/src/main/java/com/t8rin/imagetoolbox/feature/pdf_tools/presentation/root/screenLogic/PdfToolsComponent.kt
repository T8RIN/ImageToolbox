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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfToImagesAction
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.PdfToImageState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion

class PdfToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.PdfTools.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val pdfManager: PdfManager,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }

        doOnDestroy {
            pdfManager.setMasterPassword(null)
        }
    }

    private val _pdfToImageState: MutableState<PdfToImageState?> = mutableStateOf(null)
    val pdfToImageState by _pdfToImageState

    private val _pdfType: MutableState<Screen.PdfTools.Type?> = mutableStateOf(null)
    val pdfType: Screen.PdfTools.Type? by _pdfType

    private val _outputPdfUri = mutableStateOf<String?>(null)

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _presetSelected: MutableState<Preset.Percentage> =
        mutableStateOf(Preset.Percentage(100))
    val presetSelected by _presetSelected

    private val _quality: MutableState<Int> = mutableIntStateOf(85)
    val quality by _quality

    private val _showOOMWarning: MutableState<Boolean> = mutableStateOf(false)
    val showOOMWarning by _showOOMWarning

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(1)
    val left by _left

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private fun resetCalculatedData() {
        _outputPdfUri.value = null
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun setType(type: Screen.PdfTools.Type) {
        when (type) {
            is Screen.PdfTools.Type.PdfToImages -> setPdfToImagesUri(type.pdfUri)
        }
        registerChanges()
        resetCalculatedData()
    }

    fun setPdfToImagesUri(newUri: Uri?) {
        _pdfToImageState.update { null }
        _pdfType.update {
            it as? Screen.PdfTools.Type.PdfToImages ?: Screen.PdfTools.Type.PdfToImages(newUri)
        }
        componentScope.launch {
            newUri?.let {
                val pages = pdfManager.getPdfPages(
                    uri = newUri.toString()
                )

                _pdfToImageState.update {
                    PdfToImageState(
                        uri = newUri,
                        pagesCount = pages.size.coerceAtLeast(1),
                        selectedPages = pages
                    )
                }
                checkForOOM()
            }
        }
        resetCalculatedData()
    }

    fun updatePdfPassword(password: String?) {
        pdfManager.setMasterPassword(password)
    }

    fun clearAll() {
        _pdfType.update { null }
        _pdfToImageState.update { null }
        _presetSelected.update { Preset.Original }
        _showOOMWarning.value = false
        _imageInfo.value = ImageInfo()
        _quality.value = 85
        pdfManager.setMasterPassword(null)
        resetCalculatedData()
        registerChangesCleared()
    }

    fun savePdfToImages(
        oneTimeSaveLocationUri: String?,
        onComplete: (List<SaveResult>) -> Unit
    ) {
        _done.value = 0
        _left.value = 1
        val results = mutableListOf<SaveResult>()
        savingJob = trackProgress {
            pdfManager.convertPdfToImages(
                uri = _pdfToImageState.value?.uri.toString(),
                pages = _pdfToImageState.value?.selectedPages,
                preset = presetSelected
            ).onCompletion {
                _isSaving.value = false
                onComplete(results.onSuccess(::registerSave))
            }.catch {
                onComplete(listOf(SaveResult.Error.Exception(it)))
            }.collect { action ->
                when (action) {
                    is PdfToImagesAction.PagesCount -> {
                        _left.update { action.count }
                        _isSaving.value = true
                    }

                    is PdfToImagesAction.Progress -> {
                        val bitmap = imageGetter.getImage(action.image) ?: return@collect

                        val imageInfo = imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = _presetSelected.value,
                            currentInfo = imageInfo
                        )

                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    metadata = null,
                                    originalUri = _pdfToImageState.value?.uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = bitmap,
                                        imageInfo = imageInfo
                                    )
                                ),
                                keepOriginalMetadata = false,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.value += 1
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }
                }
            }
        }
    }

    fun performSharing(
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        prepareForSharing(
            onSuccess = {
                shareProvider.shareUris(it.map(Uri::toString))
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    fun prepareForSharing(
        onSuccess: suspend (List<Uri>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            when (_pdfType.value) {
                is Screen.PdfTools.Type.PdfToImages -> {
                    _done.value = 0
                    _left.value = 1
                    _isSaving.value = false
                    val uris: MutableList<String?> = mutableListOf()

                    pdfManager.convertPdfToImages(
                        uri = _pdfToImageState.value?.uri.toString(),
                        pages = _pdfToImageState.value?.selectedPages,
                        preset = presetSelected
                    ).onCompletion {
                        _isSaving.value = false
                        onSuccess(uris.mapNotNull { it?.toUri() })
                    }.catch {
                        onFailure(it)
                    }.collect { action ->
                        when (action) {
                            is PdfToImagesAction.PagesCount -> {
                                _left.update { action.count }
                                _isSaving.value = true
                            }

                            is PdfToImagesAction.Progress -> {
                                val bitmap = imageGetter.getImage(action.image) ?: return@collect

                                imageInfo.copy(
                                    originalUri = _pdfToImageState.value?.uri?.toString()
                                ).let {
                                    imageTransformer.applyPresetBy(
                                        image = bitmap,
                                        preset = _presetSelected.value,
                                        currentInfo = it
                                    )
                                }.apply {
                                    uris.add(
                                        shareProvider.cacheImage(
                                            imageInfo = this,
                                            image = bitmap
                                        )
                                    )
                                }
                                _done.value += 1
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            }
                        }
                    }
                }

                else -> Unit
            }
            _isSaving.value = false
        }
    }

    private var presetSelectionJob: Job? by smartJob()

    private fun checkForOOM() {
        val preset = _presetSelected.value
        presetSelectionJob = componentScope.launch {
            runSuspendCatching {
                _pdfToImageState.value?.let { (uri, _, selectedPages) ->
                    val pagesSize = pdfManager.getPdfPageSizes(
                        uri = uri.toString()
                    ).filterIndexed { index, _ -> index in selectedPages }

                    _showOOMWarning.update {
                        pagesSize.maxOf { size ->
                            size.width * (preset.value / 100f) * size.height * (preset.value / 100f) * 4
                        } >= 10_000 * 10_000 * 3
                    }
                }
            }.getOrNull() ?: _showOOMWarning.update { false }
        }
        registerChanges()
    }

    fun selectPreset(preset: Preset.Percentage) {
        _presetSelected.update { preset }
        preset.value()?.takeIf { it <= 100f }?.let { quality ->
            _imageInfo.update {
                it.copy(
                    quality = when (val q = it.quality) {
                        is Quality.Base -> q.copy(qualityValue = quality)
                        is Quality.Jxl -> q.copy(qualityValue = quality)
                        else -> q
                    }
                )
            }
        }
        checkForOOM()
    }

    fun updatePdfToImagePagesCount(pagesCount: Int) {
        _pdfToImageState.update { state ->
            if (state != null) checkForOOM()
            state?.copy(pagesCount = pagesCount)
        }
    }

    fun updatePdfToImageSelection(ints: List<Int>) {
        _pdfToImageState.update { state ->
            if (state != null) checkForOOM()
            state?.copy(selectedPages = ints.filter { it < state.pagesCount }.sorted())
        }
    }

    fun updateImageFormat(imageFormat: ImageFormat) {
        _imageInfo.update {
            it.copy(imageFormat = imageFormat)
        }
        registerChanges()
    }

    fun setQuality(quality: Quality) {
        _imageInfo.update {
            it.copy(quality = quality)
        }
        registerChanges()
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.PdfTools.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): PdfToolsComponent
    }

}