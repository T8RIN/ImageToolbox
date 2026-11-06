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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.t8rin.collages.CollageState
import com.t8rin.collages.CollageType
import com.t8rin.collages.public.CollageConstants
import com.t8rin.imagetoolbox.collage_maker.domain.CollageParams
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProject
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProjectRepository
import com.t8rin.imagetoolbox.core.domain.COLLAGE_PROJECT_EXT
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
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.getString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class CollageMakerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUris: List<Uri>?,
    @Assisted initialProjectUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val projectRepository: CollageProjectRepository,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
) : BaseHistoryComponent<CollageMakerComponent.HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

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

    private val _collageResetKey = mutableIntStateOf(0)
    val collageResetKey by _collageResetKey

    private val defaultParams = fileController.savable(
        scope = componentScope,
        initial = CollageParams()
    )
    private var projectParams by mutableStateOf<CollageParams?>(null)

    val params: CollageParams
        get() = (projectParams ?: defaultParams.get()).let { value ->
            value.copy(outputScaleRatio = value.outputScaleRatio.coerceIn(0.1f, 1f))
        }

    private fun updateParams(value: CollageParams) {
        if (projectParams != null) projectParams = value
        else defaultParams.set(value)
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat: ImageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality: Quality by _quality

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var requestedOperation: (suspend (Bitmap) -> Unit)? = null
    private var pendingLayoutCorrectionImageCount: Int? = null

    private var projectLoadingJob: Job? = null

    init {
        debounce {
            _imageFormat.value =
                settingsManager.settingsState.value.defaultImageFormat ?: imageFormat
            when {
                initialProjectUri != null -> openProject(initialProjectUri)
                initialUris.orEmpty().size > CollageConstants.MAX_IMAGE_COUNT ->
                    AppToastHost.showFailureToast(
                        getString(
                            R.string.pick_up_to_n_collage_images,
                            CollageConstants.MAX_IMAGE_COUNT
                        )
                    )

                else -> initialUris?.let(::updateUris)
            }
        }

        lifecycle.doOnDestroy {
            projectLoadingJob?.let { job ->
                job.cancel()
                job.invokeOnCompletion { projectRepository.clearCache() }
            } ?: projectRepository.clearCache()
        }
    }

    val canSaveProject: Boolean
        get() = !isSaving && !isImageLoading &&
                collageState?.let { state ->
                    state.matchesImages(uris.orEmpty()) && state.images.all {
                        it.matrixValues.size == 9 && it.viewWidth > 0f && it.viewHeight > 0f
                    }
                } == true

    fun createProjectFilename(): String = "Collage_${timestamp()}.$COLLAGE_PROJECT_EXT"

    fun saveProject(uri: Uri) {
        if (!canSaveProject) return
        finalizePendingHistoryTransaction()

        val project = CollageProject(
            current = currentHistorySnapshot().toProjectSnapshot(),
            history = history.dropLast(1).map { it.toProjectSnapshot() },
            redoHistory = redoHistory.map { it.toProjectSnapshot() }
        )
        _isSaving.value = true
        savingJob = trackProgress {
            try {
                fileController.writeBytes(uri.toString()) { output ->
                    projectRepository.save(output, project)
                }.onSuccess {
                    registerSave()
                    AppToastHost.showConfetti()
                }.let(::parseSaveResult)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun openProject(uri: Uri) {
        if (isImageLoading || isSaving) return
        _isImageLoading.value = true
        projectLoadingJob = componentScope.launch {
            try {
                runSuspendCatching {
                    val project = projectRepository.open(uri.toString())
                    val current = project.current.toHistorySnapshot()
                    val previous = project.history.map { it.toHistorySnapshot() }
                    val undone = project.redoHistory.map { it.toHistorySnapshot() }

                    projectParams = current.params
                    applyHistorySnapshot(current)
                    _collageResetKey.update { it + 1 }
                    restoreHistory(previous, undone)
                    registerChangesCleared()
                }.onFailure {
                    AppToastHost.showFailureToast(R.string.markup_project_open_failed)
                }
            } finally {
                _isImageLoading.value = false
            }
        }
    }

    fun setCollageType(collageType: CollageType) {
        if (collageType.imageCount != (uris?.size ?: 0)) return
        if (_collageType.value.layoutId == collageType.layoutId) return

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
        _collageCreationTrigger.value = false
        val operation = requestedOperation
        requestedOperation = null
        if (operation == null) {
            bitmap.recycle()
            return
        }
        savingJob = trackProgress {
            try {
                operation(bitmap)
            } finally {
                bitmap.recycle()
                _isSaving.value = false
            }
        }
    }

    fun onCollageCreationFailed(throwable: Throwable) {
        cancelLoading()
        AppToastHost.showFailureToast(throwable)
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
                    if (image.index == index) {
                        image.copy(
                            uri = uri.toString(),
                            matrixValues = emptyList(),
                            userAllowedEmptySpace = false
                        )
                    } else image
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
        updateParams(params.copy(outputScaleRatio = ratio))
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setDisableRotation(value: Boolean) {
        if (params.disableRotation == value) return
        beginPendingHistoryTransaction()
        updateParams(params.copy(disableRotation = value))
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setEnableSnapToBorders(value: Boolean) {
        if (params.enableSnapToBorders == value) return
        beginPendingHistoryTransaction()
        updateParams(params.copy(enableSnapToBorders = value))
        registerChanges()
        schedulePendingHistoryCommit()
    }

    private var savingJob: Job? by smartJob()

    private fun requestBitmap(operation: suspend (Bitmap) -> Unit) {
        if (isSaving || isImageLoading) return
        requestedOperation = operation
        _isSaving.value = true
        _collageCreationTrigger.value = true
    }

    fun saveBitmap(oneTimeSaveLocationUri: String?) = requestBitmap { image ->
        val result = fileController.save(
            saveTarget = ImageSaveTarget(
                imageInfo = image.outputInfo(),
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
    }

    fun performSharing() = requestBitmap { image ->
        shareProvider.cacheImage(image, image.outputInfo())?.let { uri ->
            shareProvider.shareUri(uri, onComplete = AppToastHost::showConfetti)
        }
    }

    fun cacheImage(onComplete: (Uri) -> Unit) = requestBitmap { image ->
        shareProvider.cacheImage(image, image.outputInfo())?.let { onComplete(it.toUri()) }
    }

    private fun Bitmap.outputInfo() = ImageInfo(
        width = width,
        height = height,
        quality = quality,
        imageFormat = imageFormat
    )

    fun cancelLoading() {
        requestedOperation = null
        _collageCreationTrigger.value = false
        projectLoadingJob?.cancel()
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
        updateParams(params.copy(spacing = value))
        registerChanges()
        schedulePendingHistoryCommit()
    }

    fun setCornerRadius(value: Float) {
        if (params.cornerRadius == value) return
        beginPendingHistoryTransaction()
        updateParams(params.copy(cornerRadius = value))
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
        if (state.layoutId == collageType.layoutId && state.matchesImages(uris.orEmpty())) {
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

    private fun CollageState.matchesImages(images: List<Uri>): Boolean =
        images.isNotEmpty() && this.images.size == images.size &&
                this.images.all {
                    it.uri != null && it.uri == images.getOrNull(it.index)?.toString()
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
        val needsReset = snapshot.collageState?.images?.any { it.matrixValues.isEmpty() }
            ?: (collageState != null)
        if (needsReset) _collageResetKey.update { it + 1 }
        _uris.update { snapshot.uris }
        _aspectRatio.update { snapshot.aspectRatio }
        _backgroundColor.update { snapshot.backgroundColor }
        backgroundGradient = snapshot.backgroundGradient
        _collageType.update { snapshot.collageType }
        _collageState.update { snapshot.collageState }
        updateParams(snapshot.params)
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
            initialProjectUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): CollageMakerComponent
    }

}