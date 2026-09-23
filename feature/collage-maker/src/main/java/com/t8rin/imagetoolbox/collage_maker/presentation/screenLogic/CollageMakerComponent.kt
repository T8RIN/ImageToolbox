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

package com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.collages.CollageState
import com.t8rin.collages.CollageType
import com.t8rin.collages.public.CollageConstants
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageParams
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class CollageMakerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
) : BaseHistoryComponent<CollageMakerComponent.HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    init {
        debounce {
            _imageFormat.value =
                settingsManager.settingsState.value.defaultImageFormat ?: imageFormat
            if (!uris.isNullOrEmpty()) {
                resetHistory()
                registerChangesCleared()
            }

            initialUris?.let(::updateUris)
        }
    }

    private val _aspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Numeric(1f, 1f))
    val aspectRatio by _aspectRatio

    private val _backgroundColor = mutableStateOf(Color.White)
    val backgroundColor: Color by _backgroundColor
    var backgroundGradient by mutableStateOf<GradientFill?>(null)
        private set

    private val _collageCreationTrigger = mutableStateOf(false)
    val collageCreationTrigger by _collageCreationTrigger

    private val _collageType: MutableState<CollageType> = mutableStateOf(CollageType.Empty)
    val collageType by _collageType

    private val _collageState: MutableState<CollageState?> = mutableStateOf(null)
    val collageState by _collageState

    private val _collageResetKey = mutableStateOf(0)
    val collageResetKey by _collageResetKey

    private val _collageBitmap = mutableStateOf<Bitmap?>(null)
    private val collageBitmap by _collageBitmap

    private val _params = fileController.savable(
        scope = componentScope,
        initial = CollageParams()
    )
    val params by _params

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var requestedOperation: () -> Unit = {}
    private var pendingLayoutCorrectionImageCount: Int? = null

    fun setCollageType(collageType: CollageType) {
        if (collageType.imageCount != (uris?.size ?: 0)) return
        if (_collageType.value == collageType) return

        val isInitialSelection = _collageType.value == CollageType.Empty && !haveChanges
        val isExpectedLayoutCorrection =
            pendingLayoutCorrectionImageCount == collageType.imageCount
        val isLayoutCorrection = _collageType.value.imageCount != (uris?.size ?: 0)
        if (!isInitialSelection && !isLayoutCorrection) {
            beginPendingHistoryTransaction()
        }
        _collageType.update { collageType }
        _collageState.update { null }
        registerChanges()
        if (isInitialSelection) {
            resetHistory()
            registerChangesCleared()
        } else if (isExpectedLayoutCorrection) {
            pendingLayoutCorrectionImageCount = null
            finalizePendingHistoryTransaction()
        } else {
            schedulePendingHistoryCommit()
        }
    }

    fun updateCollageBitmap(bitmap: Bitmap) {
        _collageCreationTrigger.update { false }
        _collageBitmap.update { bitmap }
        requestedOperation()
    }

    fun updateUris(uris: List<Uri>?) {
        if (_uris.value == uris) return

        componentScope.launch {
            _isImageLoading.update { true }
            val hadImages = !_uris.value.isNullOrEmpty()
            if (hadImages) {
                finalizePendingHistoryTransaction()
                beginPendingHistoryTransaction()
            }
            _uris.update { uris }
            _collageState.update { null }
            _collageType.update { CollageType.Empty }
            pendingLayoutCorrectionImageCount = uris
                ?.size
                ?.takeIf { hadImages && it > 0 }
            _isImageLoading.update { false }
            if (hadImages) {
                registerChanges()
                if (uris.isNullOrEmpty()) {
                    finalizePendingHistoryTransaction()
                }
            } else if (!uris.isNullOrEmpty()) {
                resetHistory()
                registerChangesCleared()
            } else {
                clearHistory()
            }
        }
    }

    fun replaceImageAt(index: Int, uri: Uri) {
        val current = _uris.value ?: return
        if (index !in current.indices || current[index] == uri) return

        beginPendingHistoryTransaction()
        _uris.update { current ->
            val list = current?.toMutableList() ?: return@update current
            if (index in list.indices) {
                list[index] = uri
                list
            } else current
        }
        _collageState.update { state ->
            state?.copy(
                images = state.images.map { image ->
                    if (image.index == index) image.copy(uri = uri.toString()) else image
                }
            )
        }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun addImage(uri: Uri) {
        val current = _uris.value.orEmpty()
        if (current.size >= CollageConstants.MAX_IMAGE_COUNT) return
        val updated = current + uri

        finalizePendingHistoryTransaction()
        beginPendingHistoryTransaction()
        _uris.update { updated }
        _collageState.update { null }
        pendingLayoutCorrectionImageCount = updated.size
        registerChanges()
    }

    fun removeImageAt(index: Int) {
        val current = _uris.value ?: return
        if (index !in current.indices) return
        val updated = current.toMutableList().apply {
            removeAt(index)
        }

        finalizePendingHistoryTransaction()
        beginPendingHistoryTransaction()
        _uris.update { updated }
        _collageState.update { null }
        pendingLayoutCorrectionImageCount = updated.size.takeIf { it > 0 }
        if (updated.isEmpty()) {
            _collageType.update { CollageType.Empty }
        }
        registerChanges()
        if (updated.isEmpty()) {
            finalizePendingHistoryTransaction()
        }
    }

    fun setQuality(quality: Quality) {
        if (_quality.value == quality) return
        beginPendingHistoryTransaction()
        _quality.update { quality }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value == imageFormat) return
        beginPendingHistoryTransaction()
        _imageFormat.update { imageFormat }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setOutputScaleRatio(ratio: Float) {
        if (params.outputScaleRatio == ratio) return
        beginPendingHistoryTransaction()
        _params.update { it.copy(outputScaleRatio = ratio) }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setDisableRotation(value: Boolean) {
        if (params.disableRotation == value) return
        beginPendingHistoryTransaction()
        _params.update { it.copy(disableRotation = value) }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setEnableSnapToBorders(value: Boolean) {
        if (params.enableSnapToBorders == value) return
        beginPendingHistoryTransaction()
        _params.update { it.copy(enableSnapToBorders = value) }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            savingJob = trackProgress {
                collageBitmap?.let { image ->
                    _isSaving.update { true }
                    val imageInfo = ImageInfo(
                        width = image.width,
                        height = image.height,
                        quality = quality,
                        imageFormat = imageFormat
                    )
                    val result = fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            originalUri = "",
                            sequenceNumber = null,
                            data = imageCompressor.compress(
                                image = image,
                                imageFormat = imageFormat,
                                quality = quality
                            )
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )

                    parseSaveResult(result.onSuccess(::registerSave))
                    _isSaving.update { false }
                }
            }
        }
    }

    fun performSharing() {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = trackProgress {
                    _isSaving.update { true }
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            quality = quality,
                            imageFormat = imageFormat
                        )
                    )?.let { uri ->
                        shareProvider.shareUri(
                            uri = uri,
                            onComplete = AppToastHost::showConfetti
                        )
                    }
                    _isSaving.update { false }
                }
            }
        }
    }

    fun cacheImage(
        onComplete: (Uri) -> Unit,
    ) {
        _isSaving.update { true }
        _collageCreationTrigger.update { true }
        requestedOperation = {
            collageBitmap?.let { image ->
                savingJob = trackProgress {
                    _isSaving.update { true }
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            width = image.width,
                            height = image.height,
                            quality = quality,
                            imageFormat = imageFormat
                        )
                    )?.let { uri ->
                        onComplete(uri.toUri())
                    }
                    _isSaving.update { false }
                }
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun updateBackgroundGradient(gradient: GradientFill) {
        if (backgroundGradient == gradient) return
        beginPendingHistoryTransaction()
        backgroundGradient = gradient
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setBackgroundColor(color: Color) {
        if (_backgroundColor.value == color && backgroundGradient == null) return
        beginPendingHistoryTransaction()
        _backgroundColor.update { color }
        backgroundGradient = null
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setSpacing(value: Float) {
        if (params.spacing == value) return
        beginPendingHistoryTransaction()
        _params.update { it.copy(spacing = value) }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setCornerRadius(value: Float) {
        if (params.cornerRadius == value) return
        beginPendingHistoryTransaction()
        _params.update { it.copy(cornerRadius = value) }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun setAspectRatio(aspect: DomainAspectRatio) {
        if (_aspectRatio.value == aspect) return
        beginPendingHistoryTransaction()
        _aspectRatio.update { aspect }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun updateCollageState(state: CollageState) {
        if (state.layoutId == collageType.layoutId) {
            _collageState.update { state }
            val current = currentHistorySnapshot()
            if (
                history.size == 1 &&
                redoHistory.isEmpty() &&
                history.single().copy(collageState = null) == current.copy(collageState = null)
            ) {
                resetHistory()
            }
        }
    }

    fun onCollageStateChange(
        before: CollageState,
        after: CollageState
    ) {
        if (before == after || after.layoutId != collageType.layoutId) return

        finalizePendingHistoryTransaction()
        val currentUris = uris
        val beforeUris = before.images.mapNotNull { it.uri?.toUri() }
        val afterUris = after.images.mapNotNull { it.uri?.toUri() }
        _collageState.update { after }
        if (afterUris.size == after.images.size) {
            _uris.update { afterUris }
        }
        commitHistoryFrom(
            currentHistorySnapshot().copy(
                uris = if (beforeUris.size == before.images.size) beforeUris else currentUris,
                collageState = before
            )
        )
    }

    fun resetCollage() {
        beginPendingHistoryTransaction()
        _collageState.update { null }
        _collageResetKey.update { it + 1 }
        registerChanges()
        schedulePendingHistoryCommit()
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        uris = uris,
        aspectRatio = aspectRatio,
        backgroundColor = backgroundColor,
        backgroundGradient = backgroundGradient,
        collageType = collageType,
        collageState = collageState,
        params = params,
        imageFormat = imageFormat,
        quality = quality
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        pendingLayoutCorrectionImageCount = null
        _uris.update { snapshot.uris }
        _aspectRatio.update { snapshot.aspectRatio }
        _backgroundColor.update { snapshot.backgroundColor }
        backgroundGradient = snapshot.backgroundGradient
        _collageType.update { snapshot.collageType }
        _collageState.update { snapshot.collageState }
        _params.update { snapshot.params }
        _imageFormat.update { snapshot.imageFormat }
        _quality.update { snapshot.quality }
    }

    override fun hasSameUndoState(
        first: HistorySnapshot,
        second: HistorySnapshot
    ): Boolean = first == second

    data class HistorySnapshot(
        val uris: List<Uri>? = null,
        val aspectRatio: DomainAspectRatio = DomainAspectRatio.Numeric(1f, 1f),
        val backgroundColor: Color = Color.White,
        val backgroundGradient: GradientFill? = null,
        val collageType: CollageType = CollageType.Empty,
        val collageState: CollageState? = null,
        val params: CollageParams = CollageParams(),
        val imageFormat: ImageFormat = ImageFormat.Png.Lossless,
        val quality: Quality = Quality.Base()
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): CollageMakerComponent
    }

}
