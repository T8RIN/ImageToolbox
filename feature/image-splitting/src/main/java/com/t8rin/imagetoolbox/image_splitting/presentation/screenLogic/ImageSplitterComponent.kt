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

package com.t8rin.imagetoolbox.image_splitting.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.image_splitting.domain.ImageSplitter
import com.t8rin.imagetoolbox.image_splitting.domain.SavableSplitParams
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
import com.t8rin.imagetoolbox.image_splitting.presentation.screenLogic.ImageSplitterComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class ImageSplitterComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageSplitter: ImageSplitter,
    private val shareProvider: ShareProvider,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {
    private var savableParams by fileController.savable(
        scope = componentScope,
        initial = SavableSplitParams()
    )

    init {
        debounce {
            initialUri?.let(::updateUri)
            _params.update {
                it.copy(
                    rowsCount = savableParams.rowsCount,
                    columnsCount = savableParams.columnsCount
                )
            }
        }
    }

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _params: MutableState<SplitParams> = mutableStateOf(SplitParams.Default)
    val params: SplitParams by _params

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done


    private fun updateUris() {
        if (uri == null) return

        debouncedImageCalculation {
            _uris.update {
                imageSplitter.split(
                    imageUri = uri!!.toString(),
                    params = params
                ).map { it.toUri() }
            }
        }
    }

    fun updateUri(uri: Uri?) {
        clearHistory()
        registerChangesCleared()
        _uri.value = null
        _uri.value = uri
        if (uri != null) {
            resetHistory()
        }
        updateUris()
    }

    fun updateParams(params: SplitParams) {
        if (params != this.params) {
            savableParams = SavableSplitParams(
                rowsCount = params.rowsCount,
                columnsCount = params.columnsCount
            )
            beginPendingHistoryTransaction()
            _params.update { params }
            registerChanges()
            updateUris()
            schedulePendingHistoryCommit()
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris.forEach { uri ->
                results.add(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                imageFormat = params.imageFormat,
                                quality = params.quality,
                                originalUri = uri.toString()
                            ),
                            metadata = null,
                            originalUri = uri.toString(),
                            sequenceNumber = _done.value + 1,
                            data = fileController.readBytes(uri.toString())
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                )

                _done.value += 1
                updateProgress(
                    done = done,
                    total = uris.size
                )
            }
            parseSaveResults(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }


    fun shareBitmaps() {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            shareProvider.shareUris(
                uris = uris.map { it.toString() }
            )
            AppToastHost.showConfetti()
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }


    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            onComplete(uris)
            _isSaving.value = false
        }
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        uri = uri,
        params = params,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _uri.update { snapshot.uri }
        _params.update { snapshot.params }
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        updateUris()
    }

    data class HistorySnapshot(
        val uri: Uri? = null,
        val params: SplitParams = SplitParams.Default,
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImageSplitterComponent
    }

}