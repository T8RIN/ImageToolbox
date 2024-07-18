/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.image_preview.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val shareProvider: ShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

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
    ) = viewModelScope.launch(defaultDispatcher) {
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

}