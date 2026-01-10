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

package com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.WallpapersProvider
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.WallpapersResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class WallpapersExportComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val wallpapersProvider: WallpapersProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _imageFormat = mutableStateOf<ImageFormat>(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _quality = mutableStateOf<Quality>(Quality.Base())
    val quality by _quality

    private val _wallpapersState: MutableState<WallpapersResult> =
        mutableStateOf(WallpapersResult.Loading)
    val wallpapersState: WallpapersResult by _wallpapersState

    private var switchToLoading = true

    val wallpapers get() = (wallpapersState as? WallpapersResult.Success)?.wallpapers.orEmpty()

    private val _selectedImages: MutableState<List<Int>> = mutableStateOf(emptyList())
    val selectedImages: List<Int> by _selectedImages

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private var wallpapersJob: Job? by smartJob()

    fun loadWallpapers() {
        wallpapersJob = componentScope.launch {
            if (switchToLoading) _wallpapersState.value = WallpapersResult.Loading

            _wallpapersState.value = wallpapersProvider.getWallpapers().also { result ->
                val success = result is WallpapersResult.Success
                if (!success) {
                    _selectedImages.update { emptyList() }
                }
                if (switchToLoading && success) {
                    _selectedImages.update {
                        result.wallpapers.mapIndexedNotNull { index, wallpaper ->
                            index.takeIf { wallpaper.imageUri != null }
                        }
                    }
                }
                switchToLoading = !success
            }
        }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }

            val results = mutableListOf<SaveResult>()
            val uris = wallpapers.mapIndexedNotNull { index, wallpaper ->
                wallpaper.imageUri?.takeIf { index in selectedImages }
            }

            _done.value = 0
            _left.value = uris.size

            uris.forEach { url ->
                imageGetter.getImage(data = url)?.let { bitmap ->
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                                imageFormat = imageFormat,
                                quality = quality
                            ),
                            originalUri = "_",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = imageFormat,
                                quality = quality
                            )
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                }?.let(results::add) ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )
                _done.value++
                updateProgress(
                    done = done,
                    total = left
                )
            }
            onResult(results.onSuccess(::registerSave))
            _isSaving.update { false }
        }
    }

    fun performSharing(onComplete: () -> Unit) {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                onComplete()
            }
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true

            val uris = wallpapers.mapIndexedNotNull { index, wallpaper ->
                wallpaper.imageUri?.takeIf { index in selectedImages }
            }

            _done.value = 0
            _left.value = uris.size

            onComplete(
                uris.mapNotNull {
                    val image = imageGetter.getImage(data = it) ?: return@mapNotNull null

                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            imageFormat = imageFormat,
                            quality = quality
                        )
                    )?.toUri().also {
                        _done.value++
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }
                }
            )
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun toggleSelection(position: Int) {
        _selectedImages.update { it.toggle(position) }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): WallpapersExportComponent
    }
}