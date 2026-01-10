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
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.image_splitting.domain.ImageSplitter
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
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
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::updateUri)
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
        _uri.value = null
        _uri.value = uri
        updateUris()
    }

    fun updateParams(params: SplitParams) {
        if (params != this.params) {
            _params.update { params }
            registerChanges()
            updateUris()
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onComplete: (List<SaveResult>) -> Unit
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
            onComplete(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }


    fun shareBitmaps(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            shareProvider.shareUris(
                uris = uris.map { it.toString() }
            )
            onComplete()
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