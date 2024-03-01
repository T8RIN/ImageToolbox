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

package ru.tech.imageresizershrinker.feature.load_net_image.presentation.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LoadNetImageViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>
) : ViewModel() {

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap by _bitmap

    private val _tempUri: MutableState<Uri?> = mutableStateOf(null)
    val tempUri by _tempUri

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        link: String,
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            imageGetter.getImage(data = link)?.let { bitmap ->
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height
                            ),
                            originalUri = "_",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = ImageFormat.PngLossless,
                                quality = Quality.Base(100)
                            )
                        ),
                        keepOriginalMetadata = false
                    )
                )
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun cacheImage(
        image: Bitmap,
        imageInfo: ImageInfo
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _tempUri.value = shareProvider.cacheImage(image, imageInfo)?.toUri()
            _isSaving.value = false
        }
    }

    fun shareBitmap(
        onComplete: () -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { image ->
                shareProvider.shareImage(
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = ImageFormat.PngLossless
                    ),
                    image = image,
                    onComplete = {
                        _isSaving.value = false
                        onComplete()
                    }
                )
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        imageFormat = ImageFormat.PngLossless
                    ),
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}