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

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class ImagePreviewComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val shareProvider: ShareProvider,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

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
        uriList: List<Uri>?,
        onComplete: () -> Unit
    ) = componentScope.launch {
        uris?.let {
            shareProvider.shareUris(
                if (uriList.isNullOrEmpty()) {
                    getSelectedUris()!!
                } else {
                    uriList
                }.map { it.toString() }
            )
            onComplete()
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
        asyncUpdateUris {
            _uris.update { emptyList() }

            withContext(ioDispatcher) {
                fileController.listFilesInDirectoryAsFlow(uri.toString())
                    .mapNotNull { uri ->
                        val excluded = listOf(
                            "xml",
                            "mov",
                            "zip",
                            "apk",
                            "mp4",
                            "mp3",
                            "pdf",
                            "ldb",
                            "ttf",
                            "gz",
                            "rar"
                        )
                        if (excluded.any { uri.endsWith(".$it", true) }) return@mapNotNull null

                        imageGetter.getImage(
                            data = uri,
                            size = 10
                        )?.let { uri.toUri() }
                    }
                    .onEach { uri ->
                        _uris.update { it.orEmpty() + uri }
                    }
                    .toList()
            }
        }
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