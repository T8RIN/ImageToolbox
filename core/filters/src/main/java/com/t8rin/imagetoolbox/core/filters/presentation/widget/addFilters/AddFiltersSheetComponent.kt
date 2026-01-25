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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResources
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesStore
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.filters.domain.FilterParamsInteractor
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.toCoil
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class AddFiltersSheetComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val favoriteInteractor: FilterParamsInteractor,
    private val imageGetter: ImageGetter<Bitmap>,
    private val remoteResourcesStore: RemoteResourcesStore,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _previewData: MutableState<List<UiFilter<*>>?> = mutableStateOf(null)
    val previewData by _previewData

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _cubeLutRemoteResources: MutableState<RemoteResources> =
        mutableStateOf(RemoteResources.CubeLutDefault)
    val cubeLutRemoteResources by _cubeLutRemoteResources

    private val _cubeLutDownloadProgress: MutableState<DownloadProgress?> =
        mutableStateOf(null)
    val cubeLutDownloadProgress by _cubeLutDownloadProgress

    init {
        updateCubeLuts(
            startDownloadIfNeeded = false,
            forceUpdate = false,
            onFailure = {},
            downloadOnlyNewData = false
        )
    }

    fun setFilterPreviewModel(uri: String) {
        componentScope.launch {
            favoriteInteractor.setFilterPreviewModel(uri)
            favoriteInteractor.setCanSetDynamicFilterPreview(false)
        }
    }

    fun setCanSetDynamicFilterPreview(value: Boolean) {
        componentScope.launch {
            favoriteInteractor.setCanSetDynamicFilterPreview(value)
        }
    }

    fun updateCubeLuts(
        startDownloadIfNeeded: Boolean,
        forceUpdate: Boolean,
        onFailure: (Throwable) -> Unit,
        downloadOnlyNewData: Boolean = false
    ) {
        componentScope.launch {
            remoteResourcesStore.getResources(
                name = RemoteResources.CUBE_LUT,
                forceUpdate = forceUpdate,
                onDownloadRequest = { name ->
                    if (startDownloadIfNeeded) {
                        remoteResourcesStore.downloadResources(
                            name = name,
                            onProgress = { progress ->
                                _cubeLutDownloadProgress.update { progress }
                            },
                            onFailure = onFailure,
                            downloadOnlyNewData = downloadOnlyNewData
                        )
                    } else null
                }
            )?.let { data ->
                _cubeLutRemoteResources.update { data }
            }
            _cubeLutDownloadProgress.update { null }
        }
    }

    fun setPreviewData(data: UiFilter<*>?) {
        _previewData.update {
            data?.let { filter ->
                listOf(
                    filter.copy(filter.value).apply { isVisible = true }
                )
            }
        }
    }

    fun setPreviewData(data: List<Filter<*>>) {
        _previewData.update { data.map { it.toUiFilter() } }
    }

    fun filterToTransformation(
        filter: UiFilter<*>
    ): Transformation = filterProvider.filterToTransformation(filter).toCoil()

    fun updatePreview(previewBitmap: Bitmap) {
        debouncedImageCalculation {
            _previewBitmap.update {
                imageTransformer.transform(
                    image = previewBitmap,
                    transformations = previewData?.map {
                        filterProvider.filterToTransformation(it)
                    } ?: emptyList(),
                    size = IntegerSize(2000, 2000)
                )
            }
        }
    }

    fun removeFilterAtIndex(index: Int) {
        _previewData.update {
            it?.toMutableList()?.apply {
                removeAt(index)
            }
        }
    }

    fun <T : Any> updateFilter(
        value: T,
        index: Int
    ) {
        val list = (previewData ?: emptyList()).toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _previewData.update { list }
        }.onFailure {
            list[index] = list[index].newInstance()
            _previewData.update { list }
        }
    }

    fun shareImage(
        bitmap: Bitmap,
        onComplete: () -> Unit
    ) {
        componentScope.launch {
            shareProvider.shareImage(
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat.Png.Lossless
                ),
                image = bitmap,
                onComplete = onComplete
            )
        }
    }

    fun saveImage(
        bitmap: Bitmap,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        componentScope.launch {
            val imageInfo = ImageInfo(
                width = bitmap.width,
                height = bitmap.height,
                imageFormat = ImageFormat.Png.Lossless
            )
            onComplete(
                fileController.save(
                    saveTarget = ImageSaveTarget(
                        imageInfo = imageInfo,
                        originalUri = "",
                        sequenceNumber = null,
                        data = imageCompressor.compress(
                            image = bitmap,
                            imageFormat = imageInfo.imageFormat,
                            quality = Quality.Base()
                        )
                    ),
                    keepOriginalMetadata = true
                )
            )
        }
    }

    fun saveContentTo(
        content: String,
        fileUri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        componentScope.launch {
            fileController.writeBytes(
                uri = fileUri.toString(),
                block = { it.writeBytes(content.toByteArray()) }
            ).also(onResult).onSuccess(::registerSave)
        }
    }

    fun shareContent(
        content: String,
        filename: String,
        onComplete: () -> Unit
    ) {
        componentScope.launch {
            shareProvider.shareData(
                writeData = { it.writeBytes(content.toByteArray()) },
                filename = filename,
                onComplete = onComplete
            )
        }
    }

    fun createTemplateFilename(templateFilter: TemplateFilter): String {
        return "template(${templateFilter.name})${timestamp()}.imtbx_template"
    }

    fun reorderFavoriteFilters(value: List<UiFilter<*>>) {
        componentScope.launch {
            favoriteInteractor.reorderFavoriteFilters(value)
        }
    }

    val favoritesFlow: Flow<List<Filter<*>>>
        get() = favoriteInteractor.getFavoriteFilters()

    val templatesFlow: Flow<List<TemplateFilter>>
        get() = favoriteInteractor.getTemplateFilters()

    fun toggleFavorite(filter: UiFilter<*>) {
        componentScope.launch {
            favoriteInteractor.toggleFavorite(filter)
        }
    }

    fun removeTemplateFilter(templateFilter: TemplateFilter) {
        componentScope.launch {
            favoriteInteractor.removeTemplateFilter(templateFilter)
        }
    }

    suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter
    ): String = favoriteInteractor.convertTemplateFilterToString(templateFilter)

    fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) {
        componentScope.launch {
            favoriteInteractor.addTemplateFilterFromString(
                string = string,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) {
        componentScope.launch {
            favoriteInteractor.addTemplateFilterFromUri(
                uri = uri,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun cacheNeutralLut(onComplete: (Uri) -> Unit) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let {
                shareProvider.cacheImage(
                    image = it,
                    imageInfo = ImageInfo(
                        width = 512,
                        height = 512,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
        }
    }

    fun shareNeutralLut(onComplete: () -> Unit) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        width = 512,
                        height = 512,
                        imageFormat = ImageFormat.Png.Lossless
                    ),
                    onComplete = onComplete
                )
            }
        }
    }

    fun saveNeutralLut(
        oneTimeSaveLocationUri: String? = null,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let { bitmap ->
                val imageInfo = ImageInfo(
                    width = 512,
                    height = 512,
                    imageFormat = ImageFormat.Png.Lossless
                )
                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            originalUri = "",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = bitmap,
                                imageFormat = imageInfo.imageFormat,
                                quality = Quality.Base()
                            )
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                )
            }
        }
    }

    override fun resetState() {
        _previewData.update { null }
        _previewBitmap.update { null }
        cancelImageLoading()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): AddFiltersSheetComponent
    }

}