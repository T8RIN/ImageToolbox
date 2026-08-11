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

package com.t8rin.imagetoolbox.feature.image_preview.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.loadableImagesFromFolder
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class ImagePreviewComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _folderLoadingCount: MutableState<Int?> = mutableStateOf(null)
    val folderLoadingCount by _folderLoadingCount

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(
        ImageFrames.ManualSelection(
            emptyList()
        )
    )
    val imageFrames by _imageFrames

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
    }

    fun shareImages(
        uriList: List<Uri>?
    ) = componentScope.launch {
        uris?.let {
            shareProvider.shareUris(
                if (uriList.isNullOrEmpty()) {
                    getSelectedUris()!!
                } else {
                    uriList
                }.map { it.toString() }
            )
            AppToastHost.showConfetti()
        }
    }

    fun getSelectedUris(): List<Uri>? {
        val targetUris = uris ?: return null

        val positions = imageFrames.getFramePositions(targetUris.size)

        return targetUris.mapIndexedNotNull { index, uri ->
            if (index + 1 in positions) uri
            else null
        }
    }

    fun removeUri(
        uri: Uri
    ) = _uris.update { it?.minus(uri) }

    fun updateImageFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
    }

    fun updateUrisFromTree(uri: Uri) {
        _folderLoadingCount.value = 0

        asyncUpdateUris { previousUris ->
            try {
                withContext(ioDispatcher) {
                    uri.loadableImagesFromFolder(
                        context = appContext,
                        onProgress = { count ->
                            withContext(uiDispatcher) {
                                _folderLoadingCount.value = count
                            }
                        }
                    ).ifEmpty { previousUris.orEmpty() }
                }
            } finally {
                withContext(NonCancellable + uiDispatcher) {
                    _folderLoadingCount.value = null
                }
            }
        }
    }

    fun cancelLoading() {
        _folderLoadingCount.value = null
        cancelImageLoading()
    }

    fun asyncUpdateUris(
        onFinish: suspend () -> Unit = {},
        action: suspend (List<Uri>?) -> List<Uri>
    ) {
        debouncedImageCalculation(delay = 100, onFinish = onFinish) {
            _uris.value = action(_uris.value)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImagePreviewComponent
    }

}
