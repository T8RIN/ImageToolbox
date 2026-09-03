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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.lerp
import androidx.core.net.toUri
import coil3.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.transformation.GenericTransformation
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientMaker
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientType
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.UiGradientState
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.UiMeshGradientState
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.generateMesh
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.model.GradientMakerType
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.model.isMesh
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent.HistorySnapshot
import com.t8rin.palette.PaletteCoderException
import com.t8rin.palette.PaletteFormat
import com.t8rin.palette.decode
import com.t8rin.palette.getCoder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class GradientMakerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val gradientMaker: GradientMaker<Bitmap, ShaderBrush, Size, Color, TileMode, Offset>,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::setUris)
            resetState()
        }
    }

    private val _screenType: MutableState<GradientMakerType?> = mutableStateOf(null)
    val screenType by _screenType

    private val _showOriginal: MutableState<Boolean> = mutableStateOf(false)
    val showOriginal by _showOriginal

    private var _gradientState = UiGradientState()
    private val gradientState: UiGradientState get() = _gradientState

    private var _meshGradientState = UiMeshGradientState()
    val meshGradientState: UiMeshGradientState get() = _meshGradientState

    val meshResolutionX: Int get() = meshGradientState.resolutionX
    val meshResolutionY: Int get() = meshGradientState.resolutionY
    val meshResolutionMax: Int
        get() = when (meshGradientState.gridSize) {
            6 -> 24
            5 -> 32
            4 -> 40
            else -> 64
        }
    val meshPoints: List<List<Pair<Offset, Color>>> get() = meshGradientState.points

    val brush: ShaderBrush? get() = gradientState.brush
    val gradientType: GradientType get() = gradientState.gradientType
    val colorStops: List<Pair<Float, Color>> get() = gradientState.colorStops
    val selectedGradientPalette: GradientPalette?
        get() = GradientPalette.entries.firstOrNull { palette ->
            palette.colors.map { it.colorInt } == colorStops.map { it.second.toArgb() }
        }
    val selectedMeshGradientPalette: GradientPalette?
        get() {
            val colors = meshPoints.flatten().map { it.second.toArgb() }
            if (colors.isEmpty()) return null

            return GradientPalette.entries.firstOrNull { palette ->
                palette.sampleColors(colors.size).map { it.colorInt } == colors
            }
        }
    val tileMode: TileMode get() = gradientState.tileMode
    val angle: Float get() = gradientState.linearGradientAngle
    val centerFriction: Offset get() = gradientState.centerFriction
    val radiusFriction: Float get() = gradientState.radiusFriction

    private var _gradientAlpha: MutableState<Float> = mutableFloatStateOf(1f)
    val gradientAlpha by _gradientAlpha

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _selectedUri = mutableStateOf(Uri.EMPTY)
    val selectedUri: Uri by _selectedUri

    private val _colorPickerBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val colorPickerBitmap: Bitmap? by _colorPickerBitmap

    private val _uris = mutableStateOf(emptyList<Uri>())
    val uris by _uris

    private val _imageAspectRatio: MutableState<Float> = mutableFloatStateOf(1f)
    val imageAspectRatio by _imageAspectRatio

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _imageFormat = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _gradientSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(1000, 1000))
    val gradientSize by _gradientSize

    suspend fun createGradientBitmap(
        data: Any,
        integerSize: IntegerSize = gradientSize,
        useBitmapOriginalSizeIfAvailable: Boolean = false
    ): Bitmap? {
        return if (selectedUri == Uri.EMPTY) {
            if (screenType.isMesh()) {
                gradientMaker.createMeshGradient(
                    integerSize = integerSize,
                    gradientState = meshGradientState
                )
            } else {
                gradientMaker.createGradient(
                    integerSize = integerSize,
                    gradientState = gradientState
                )
            }
        } else {
            imageGetter.getImage(
                data = data,
                originalSize = useBitmapOriginalSizeIfAvailable
            )?.let {
                if (screenType.isMesh()) {
                    gradientMaker.createMeshGradient(
                        src = it,
                        gradientState = meshGradientState,
                        gradientAlpha = gradientAlpha
                    )
                } else {
                    gradientMaker.createGradient(
                        src = it,
                        gradientState = gradientState,
                        gradientAlpha = gradientAlpha
                    )
                }
            }
        }
    }

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _left.value = -1
            _isSaving.value = true
            if (uris.isEmpty()) {
                createGradientBitmap(
                    data = Unit,
                    useBitmapOriginalSizeIfAvailable = true
                )?.let { localBitmap ->
                    val imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = localBitmap.width,
                        height = localBitmap.height
                    )
                    parseSaveResult(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = "",
                                sequenceNumber = null,
                                data = imageCompressor.compressAndTransform(
                                    image = localBitmap,
                                    imageInfo = imageInfo
                                )
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        ).onSuccess(::registerSave)
                    )
                }
            } else {
                val results = mutableListOf<SaveResult>()
                _done.value = 0
                _left.value = uris.size
                uris.forEach { uri ->
                    createGradientBitmap(
                        data = uri,
                        useBitmapOriginalSizeIfAvailable = true
                    )?.let { localBitmap ->
                        val imageInfo = ImageInfo(
                            imageFormat = imageFormat,
                            width = localBitmap.width,
                            height = localBitmap.height,
                            originalUri = uri.toString()
                        )
                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = localBitmap,
                                        imageInfo = imageInfo
                                    )
                                ),
                                keepOriginalMetadata = keepExif,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                    } ?: results.add(
                        SaveResult.Error.Exception(Throwable())
                    )

                    _done.value += 1
                    updateProgress(
                        done = done,
                        total = left
                    )
                }
                parseSaveResults(results.onSuccess(::registerSave))
            }
            _isSaving.value = false
        }
    }

    fun shareBitmaps() {
        savingJob = trackProgress {
            _left.value = -1
            _isSaving.value = true
            if (uris.isEmpty()) {
                createGradientBitmap(
                    data = Unit,
                    useBitmapOriginalSizeIfAvailable = true
                )?.let {
                    shareProvider.shareImage(
                        image = it,
                        imageInfo = ImageInfo(
                            imageFormat = imageFormat,
                            width = it.width,
                            height = it.height
                        ),
                        onComplete = AppToastHost::showConfetti
                    )
                }
            } else {
                _done.value = 0
                _left.value = uris.size
                shareProvider.shareImages(
                    uris.map { it.toString() },
                    imageLoader = { uri ->
                        createGradientBitmap(
                            data = uri,
                            useBitmapOriginalSizeIfAvailable = true
                        )?.let {
                            it to ImageInfo(
                                width = it.width,
                                height = it.height,
                                imageFormat = imageFormat
                            )
                        }
                    },
                    onProgressChange = {
                        if (it == -1) {
                            AppToastHost.showConfetti()
                            _isSaving.value = false
                            _done.value = 0
                        } else {
                            _done.value = it
                        }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }
                )
            }
            _isSaving.value = false
            _left.value = -1
        }
    }

    fun cancelSaving() {
        savingJob = null
        _isSaving.value = false
        _left.value = -1
    }

    fun updateHeight(value: Int) {
        if (gradientSize.height != value) {
            updateWithHistory {
                _gradientSize.update {
                    it.copy(height = value)
                }
            }
        }
    }

    fun updateWidth(value: Int) {
        if (gradientSize.width != value) {
            updateWithHistory {
                _gradientSize.update {
                    it.copy(width = value)
                }
            }
        }
    }

    fun setGradientType(value: GradientType) {
        if (gradientType != value) {
            updateWithHistory {
                gradientState.gradientType = value
            }
        }
    }

    fun setPreviewSize(size: Size) {
        gradientState.size = size
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            updateWithHistory(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            ) {
                _imageFormat.update { imageFormat }
            }
        }
    }

    fun updateLinearAngle(angle: Float) {
        if (gradientState.linearGradientAngle != angle) {
            updateWithHistory {
                gradientState.linearGradientAngle = angle
            }
        }
    }

    fun setRadialProperties(
        center: Offset,
        radius: Float
    ) {
        if (centerFriction != center || radiusFriction != radius) {
            updateWithHistory {
                gradientState.centerFriction = center
                gradientState.radiusFriction = radius
            }
        }
    }

    fun setTileMode(tileMode: TileMode) {
        if (gradientState.tileMode != tileMode) {
            updateWithHistory {
                gradientState.tileMode = tileMode
            }
        }
    }

    fun setResolution(resolution: Float) {
        val coercedResolution = resolution
            .roundToInt()
            .coerceIn(1, meshResolutionMax)
        if (
            meshResolutionX != coercedResolution ||
            meshResolutionY != coercedResolution
        ) {
            updateWithHistory {
                meshGradientState.resolutionX = coercedResolution
                meshGradientState.resolutionY = coercedResolution
            }
        }
    }

    fun setMeshGridSize(size: Int) {
        val coercedSize = size.coerceIn(2, 6)
        if (meshGradientState.gridSize != coercedSize) {
            updateWithHistory {
                meshGradientState.points.apply {
                    clear()
                    addAll(generateMesh(coercedSize))
                }
                val resolution = lerp(1f, 16f, 2f / coercedSize)
                    .roundToInt()
                    .coerceIn(1, meshResolutionMax)
                meshGradientState.resolutionX = resolution
                meshGradientState.resolutionY = resolution
            }
        }
    }

    fun setScreenType(
        type: GradientMakerType?
    ) {
        _screenType.update { type }
    }

    fun addColorStop(
        pair: Pair<Float, Color>,
        isInitial: Boolean = false
    ) {
        if (isInitial) {
            gradientState.colorStops.add(pair)
            resetHistory()
        } else {
            updateWithHistory {
                gradientState.colorStops.add(pair)
            }
        }
    }

    fun setGradientPalette(palette: GradientPalette) {
        if (selectedGradientPalette != palette) {
            updateWithHistory {
                gradientState.colorStops.apply {
                    clear()
                    addAll(
                        palette.colors.mapIndexed { index, color ->
                            index / palette.colors.lastIndex.toFloat() to color.toColor()
                        }
                    )
                }
            }
        }
    }

    fun setMeshGradientPalette(palette: GradientPalette) {
        if (selectedMeshGradientPalette != palette) {
            updateWithHistory {
                val colors = palette
                    .sampleColors(meshPoints.sumOf { it.size })
                    .map { it.toColor() }
                    .iterator()

                meshGradientState.points.replaceAll { row ->
                    row.map { (offset, _) ->
                        offset to colors.next()
                    }
                }
            }
        }
    }

    fun importPalette(uri: Uri) {
        componentScope.launch {
            _isImageLoading.value = true

            try {
                val colors = withContext(defaultDispatcher) {
                    val formats = buildList {
                        PaletteFormat.fromFilename(uri.filename() ?: uri.toString())?.let(::add)
                        addAll(PaletteFormat.entries.filterNot(::contains))
                    }

                    formats.firstNotNullOfOrNull { format ->
                        runCatching {
                            format.getCoder().decode(
                                uri = uri,
                                context = appContext
                            ).allColors().map { it.toComposeColor() }
                        }.getOrNull()?.takeIf { it.isNotEmpty() }
                    }
                }

                if (colors == null) {
                    AppToastHost.showFailureToast(PaletteCoderException.InvalidFormat())
                } else {
                    updateWithHistory {
                        gradientState.colorStops.apply {
                            clear()
                            addAll(
                                colors.mapIndexed { index, color ->
                                    val position = if (colors.size == 1) {
                                        0f
                                    } else {
                                        index / (colors.size - 1f)
                                    }
                                    position to color
                                }
                            )
                        }
                    }
                }
            } finally {
                _isImageLoading.value = false
            }
        }
    }

    fun updateColorStop(
        index: Int,
        pair: Pair<Float, Color>
    ) {
        if (gradientState.colorStops.getOrNull(index) != pair) {
            updateWithHistory {
                gradientState.colorStops[index] = pair.copy()
            }
        }
    }

    fun removeColorStop(index: Int) {
        if (gradientState.colorStops.size > 2) {
            updateWithHistory {
                gradientState.colorStops.removeAt(index)
            }
        }
    }

    fun updateMeshPointPosition(
        oldOffset: Offset,
        newOffset: Offset
    ) {
        if (oldOffset != newOffset) {
            updateWithHistory {
                var found = false
                meshGradientState.points.replaceAll { row ->
                    row.map { point ->
                        if (point.first == oldOffset && !found) {
                            found = true
                            newOffset to point.second
                        } else {
                            point
                        }
                    }
                }
            }
        }
    }

    fun updateMeshPointColor(
        offset: Offset,
        color: Color
    ) {
        if (meshPoints.flatten().firstOrNull { it.first == offset }?.second == color) return

        updateWithHistory {
            var found = false
            meshGradientState.points.replaceAll { row ->
                row.map { point ->
                    if (point.first == offset && !found) {
                        found = true
                        point.first to color
                    } else {
                        point
                    }
                }
            }
        }
    }

    fun updateSelectedUri(uri: Uri) {
        componentScope.launch {
            _selectedUri.value = uri
            _colorPickerBitmap.value = null
            _isImageLoading.value = true
            imageGetter.getImageData(
                uri = uri.toString(),
                size = 2000,
                onFailure = {
                    _isImageLoading.value = false
                    AppToastHost.showFailureToast(it)
                }
            )?.let { imageData ->
                _colorPickerBitmap.value = imageData.image
                _imageAspectRatio.update {
                    imageData.image.safeAspectRatio
                }
                _isImageLoading.value = false
                setImageFormat(imageData.imageInfo.imageFormat)
            }
        }
    }

    fun updateGradientAlpha(value: Float) {
        if (gradientAlpha != value) {
            updateWithHistory {
                _gradientAlpha.update { value }
            }
        }
    }

    override fun resetState() {
        _selectedUri.update { Uri.EMPTY }
        _colorPickerBitmap.value = null
        _uris.update { emptyList() }
        _gradientAlpha.update { 1f }
        _gradientState = UiGradientState()
        _meshGradientState = UiMeshGradientState()
        setScreenType(null)
        resetHistory()
        registerChangesCleared()
    }

    fun updateUrisSilently(
        removedUri: Uri
    ) = componentScope.launch {
        if (selectedUri == removedUri) {
            val index = uris.indexOf(removedUri)
            val replacementUri = if (index == 0) {
                uris.getOrNull(1)
            } else {
                uris.getOrNull(index - 1)
            }
            if (replacementUri != null) {
                updateSelectedUri(replacementUri)
            } else {
                _selectedUri.value = Uri.EMPTY
                _colorPickerBitmap.value = null
            }
        }
        _uris.update {
            it.toMutableList().apply {
                remove(removedUri)
            }
        }
    }

    fun setUris(uris: List<Uri>) {
        _uris.update { uris }
        uris.firstOrNull()?.let(::updateSelectedUri)
    }

    fun getGradientTransformation(): Transformation =
        GenericTransformation<Bitmap>(
            Triple(brush, meshPoints, screenType.isMesh())
        ) { input ->
            createGradientBitmap(
                data = input,
                useBitmapOriginalSizeIfAvailable = false
            ) ?: input
        }.toCoil()

    fun toggleKeepExif(value: Boolean) {
        if (keepExif != value) {
            updateWithHistory {
                _keepExif.update { value }
            }
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true
            createGradientBitmap(
                data = selectedUri,
                useBitmapOriginalSizeIfAvailable = true
            )?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            val list = mutableListOf<Uri>()

            _left.value = -1
            _isSaving.value = true

            if (uris.isEmpty()) {
                createGradientBitmap(
                    data = Unit,
                    useBitmapOriginalSizeIfAvailable = true
                )?.let { localBitmap ->
                    val imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = localBitmap.width,
                        height = localBitmap.height
                    )
                    shareProvider.cacheImage(
                        image = localBitmap,
                        imageInfo = imageInfo
                    )?.toUri()?.let(list::add)
                }
            } else {
                _done.value = 0
                _left.value = uris.size
                uris.forEach { uri ->
                    createGradientBitmap(
                        data = uri,
                        useBitmapOriginalSizeIfAvailable = true
                    )?.let { localBitmap ->
                        val imageInfo = ImageInfo(
                            imageFormat = imageFormat,
                            width = localBitmap.width,
                            height = localBitmap.height,
                            originalUri = uri.toString()
                        )

                        shareProvider.cacheImage(
                            image = localBitmap,
                            imageInfo = imageInfo
                        )?.toUri()?.let(list::add)
                    }

                    _done.value += 1
                    updateProgress(
                        done = done,
                        total = left
                    )
                }
            }
            _isSaving.value = false

            onComplete(list)
            _isSaving.value = false
        }
    }

    fun selectLeftUri() {
        uris
            .indexOf(selectedUri)
            .takeIf { it >= 0 && uris.size > 1 }
            ?.let {
                uris.leftFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris
            .indexOf(selectedUri)
            .takeIf { it >= 0 && uris.size > 1 }
            ?.let {
                uris.rightFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun getFormatForFilenameSelection(): ImageFormat? = if (uris.size < 2) imageFormat
    else null

    fun setShowOriginal(value: Boolean) {
        _showOriginal.update { value }
    }

    private fun updateWithHistory(
        mode: PendingHistoryMode? = null,
        commitDelayMillis: Long = historyTransactionDebounce,
        action: () -> Unit
    ) {
        beginPendingHistoryTransaction(
            mode = mode,
            commitDelayMillis = commitDelayMillis
        )
        action()
        registerChanges()
        schedulePendingHistoryCommit()
    }

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        gradientSize = gradientSize,
        imageFormat = imageFormat,
        gradientAlpha = gradientAlpha,
        keepExif = keepExif,
        gradientType = gradientType,
        colorStops = colorStops.toList(),
        tileMode = tileMode,
        linearGradientAngle = angle,
        centerFriction = centerFriction,
        radiusFriction = radiusFriction,
        meshPoints = meshPoints.map { it.toList() },
        meshResolutionX = meshResolutionX,
        meshResolutionY = meshResolutionY
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _gradientSize.update { snapshot.gradientSize }
        _imageFormat.update { snapshot.imageFormat }
        _gradientAlpha.update { snapshot.gradientAlpha }
        _keepExif.update { snapshot.keepExif }
        gradientState.apply {
            gradientType = snapshot.gradientType
            colorStops.apply {
                clear()
                addAll(snapshot.colorStops)
            }
            tileMode = snapshot.tileMode
            linearGradientAngle = snapshot.linearGradientAngle
            centerFriction = snapshot.centerFriction
            radiusFriction = snapshot.radiusFriction
        }
        meshGradientState.apply {
            points.apply {
                clear()
                addAll(snapshot.meshPoints.map { it.toList() })
            }
            resolutionX = snapshot.meshResolutionX
            resolutionY = snapshot.meshResolutionY
        }
    }

    data class HistorySnapshot(
        val gradientSize: IntegerSize,
        val imageFormat: ImageFormat,
        val gradientAlpha: Float,
        val keepExif: Boolean,
        val gradientType: GradientType,
        val colorStops: List<Pair<Float, Color>>,
        val tileMode: TileMode,
        val linearGradientAngle: Float,
        val centerFriction: Offset,
        val radiusFriction: Float,
        val meshPoints: List<List<Pair<Offset, Color>>>,
        val meshResolutionX: Int,
        val meshResolutionY: Int
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): GradientMakerComponent
    }
}
