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

package com.t8rin.imagetoolbox.feature.compression_lab.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.opencv_tools.image_comparison.ImageDiffTool
import com.t8rin.opencv_tools.image_comparison.model.ComparisonType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlin.time.TimeSource

class CompressionLabComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val fileController: FileController,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    private val _uris: MutableState<List<Uri>> = mutableStateOf(emptyList())
    val uris: List<Uri> by _uris

    private val _benchmarkUri: MutableState<Uri?> = mutableStateOf(null)
    val benchmarkUri: Uri? by _benchmarkUri

    private val _sourceBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val sourceBitmap: Bitmap? by _sourceBitmap

    private val _selectedFormats = mutableStateOf(DefaultFormats)
    val selectedFormats: List<ImageFormat> by _selectedFormats

    private val _searchMode = mutableStateOf(CompressionSearchMode.Manual)
    val searchMode: CompressionSearchMode by _searchMode

    private val _manualQuality = mutableIntStateOf(85)
    val manualQuality: Int by _manualQuality

    private val _targetQuality = mutableIntStateOf(95)
    val targetQuality: Int by _targetQuality

    private val _targetSizeKb = mutableIntStateOf(500)
    val targetSizeKb: Int by _targetSizeKb

    private val _results = mutableStateOf<List<CompressionLabResult>>(emptyList())
    val results: List<CompressionLabResult> by _results

    private val _failedFormats = mutableStateOf<List<String>>(emptyList())
    val failedFormats: List<String> by _failedFormats

    private val _selectedResultIndex = mutableIntStateOf(0)
    val selectedResultIndex: Int by _selectedResultIndex
    val selectedResult: CompressionLabResult?
        get() = results.getOrNull(selectedResultIndex)
    val selectedUri: Uri?
        get() = selectedResult?.uri ?: benchmarkUri

    private val _done = mutableIntStateOf(0)
    val done: Int by _done

    private var analysisJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private val _isSaving = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    init {
        debounce {
            initialUri?.let { setUris(listOf(it)) }
        }
    }

    fun setUris(newUris: List<Uri>?) {
        val distinctUris = newUris.orEmpty()
            .filterNot { it == Uri.EMPTY }
            .distinct()

        _uris.update { distinctUris }

        if (distinctUris.isEmpty()) {
            cancelRunning()
            _benchmarkUri.update { null }
            _sourceBitmap.update { null }
            clearResults()
            return
        }

        val selectedBenchmarkUri = benchmarkUri
            ?.takeIf { it in distinctUris }
            ?: distinctUris.first()

        loadBenchmark(selectedBenchmarkUri)
    }

    fun selectBenchmark(uri: Uri) {
        if (uri !in uris || uri == benchmarkUri) return
        loadBenchmark(
            uri = uri,
            runAnalysis = false
        )
    }

    fun removeUri(uri: Uri) {
        val index = uris.indexOf(uri)
        if (index < 0) return

        val updatedUris = uris.toMutableList().apply { removeAt(index) }
        _uris.update { updatedUris }

        if (updatedUris.isEmpty()) {
            setUris(emptyList())
        } else if (benchmarkUri == uri) {
            loadBenchmark(
                uri = updatedUris[index.coerceAtMost(updatedUris.lastIndex)],
                runAnalysis = false
            )
        }
    }

    fun selectPreviousBenchmark() = selectBenchmarkByOffset(-1)

    fun selectNextBenchmark() = selectBenchmarkByOffset(1)

    fun toggleFormat(format: ImageFormat) {
        _selectedFormats.update { formats ->
            if (format in formats) {
                formats.filterNot { it == format }.ifEmpty { formats }
            } else {
                (formats + format).sortedBy(AvailableFormats::indexOf)
            }
        }
        clearResults()
    }

    fun setSearchMode(mode: CompressionSearchMode) {
        if (_searchMode.value == mode) return
        _searchMode.update { mode }
        clearResults()
    }

    fun setManualQuality(value: Int) {
        _manualQuality.update { value.coerceIn(0, 100) }
        clearResults()
    }

    fun setTargetQuality(value: Int) {
        _targetQuality.update { value.coerceIn(1, 100) }
        clearResults()
    }

    fun setTargetSizeKb(value: Int) {
        _targetSizeKb.update { value.coerceIn(1, 100_000) }
        clearResults()
    }

    fun runLab() {
        val bitmap = sourceBitmap ?: return
        analysisJob = componentScope.launch(defaultDispatcher) {
            _isImageLoading.update { true }
            analyze(bitmap)
            _isImageLoading.update { false }
        }
    }

    fun selectResult(index: Int) {
        val newIndex = index.coerceIn(0, results.lastIndex.coerceAtLeast(0))
        if (newIndex != selectedResultIndex) {
            _selectedResultIndex.update { newIndex }
            registerChanges()
        }
    }

    fun saveResults(oneTimeSaveLocationUri: String?) {
        val profile = selectedResult ?: return
        val inputUris = uris
        if (inputUris.isEmpty()) return

        savingJob = trackProgress {
            _isSaving.update { true }
            _done.update { 0 }
            val saveResults = mutableListOf<SaveResult>()

            inputUris.forEachIndexed { index, uri ->
                runSuspendCatching {
                    encodeWithProfile(uri, profile)
                }.onSuccess { output ->
                    saveResults += fileController.save(
                        saveTarget = output.saveTarget(
                            uri = uri,
                            profile = profile,
                            sequenceNumber = index + 1,
                            data = fileController.readBytes(output.uri.toString())
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                }.onFailure {
                    saveResults += SaveResult.Error.Exception(it)
                }
                _done.update { index + 1 }
                updateProgress(done = done, total = inputUris.size)
            }

            parseSaveResults(saveResults.onSuccess(::registerSave))
            _isSaving.update { false }
        }
    }

    fun shareResults() {
        val profile = selectedResult ?: return
        val inputUris = uris
        if (inputUris.isEmpty()) return

        savingJob = trackProgress {
            _isSaving.update { true }
            _done.update { 0 }
            val cachedUris = mutableListOf<String>()
            var firstFailure: Throwable? = null

            inputUris.forEachIndexed { index, uri ->
                runSuspendCatching {
                    encodeWithProfile(uri, profile).uri.toString()
                }.onSuccess { cachedUri ->
                    cachedUris += cachedUri
                }.onFailure {
                    if (firstFailure == null) firstFailure = it
                }
                _done.update { index + 1 }
                updateProgress(done = done, total = inputUris.size)
            }

            firstFailure?.let(AppToastHost::showFailureToast)
            if (cachedUris.isNotEmpty()) {
                shareProvider.shareUris(cachedUris)
                AppToastHost.showConfetti()
            }
            _isSaving.update { false }
        }
    }

    fun cacheResults(onComplete: (List<Uri>) -> Unit) {
        val profile = selectedResult ?: return
        val inputUris = uris
        if (inputUris.isEmpty()) return

        savingJob = trackProgress {
            _isSaving.update { true }
            _done.update { 0 }
            val cachedUris = mutableListOf<Uri>()
            var firstFailure: Throwable? = null

            inputUris.forEachIndexed { index, uri ->
                runSuspendCatching {
                    encodeWithProfile(uri, profile).uri
                }.onSuccess(cachedUris::add).onFailure {
                    if (firstFailure == null) firstFailure = it
                }
                _done.update { index + 1 }
                updateProgress(done = done, total = inputUris.size)
            }

            firstFailure?.let(AppToastHost::showFailureToast)
            onComplete(cachedUris)
            _isSaving.update { false }
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat? = selectedResult?.format

    fun cancelRunning() {
        analysisJob?.cancel()
        analysisJob = null
        _isImageLoading.update { false }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    private fun loadBenchmark(
        uri: Uri,
        runAnalysis: Boolean = true
    ) {
        _benchmarkUri.update { uri }
        clearResults()

        analysisJob = componentScope.launch(defaultDispatcher) {
            _isImageLoading.update { true }
            runSuspendCatching {
                imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image ?: error("Unable to decode image")
            }.onSuccess { bitmap ->
                _sourceBitmap.update { bitmap }
                if (runAnalysis) analyze(bitmap)
            }.onFailure(AppToastHost::showFailureToast)
            _isImageLoading.update { false }
        }
    }

    private fun selectBenchmarkByOffset(offset: Int) {
        if (uris.size < 2) return
        val currentIndex = uris.indexOf(benchmarkUri).coerceAtLeast(0)
        val nextIndex = (currentIndex + offset).mod(uris.size)
        selectBenchmark(uris[nextIndex])
    }

    private suspend fun analyze(bitmap: Bitmap) {
        _done.update { 0 }
        _failedFormats.update { emptyList() }
        _results.update { emptyList() }
        _selectedResultIndex.update { 0 }

        val successful = mutableListOf<CompressionLabResult>()
        val failed = mutableListOf<String>()
        var firstFailure: Throwable? = null

        selectedFormats.forEachIndexed { index, format ->
            runSuspendCatching {
                findCandidate(bitmap, format).toResult(bitmap, format)
            }.onSuccess { result ->
                successful += result
                registerChanges()
            }.onFailure {
                failed += format.title
                if (firstFailure == null) firstFailure = it
            }
            _results.update { successful.toList() }
            _failedFormats.update { failed.toList() }
            _done.update { index + 1 }
        }

        firstFailure?.let(AppToastHost::showFailureToast)
    }

    private suspend fun findCandidate(
        source: Bitmap,
        format: ImageFormat
    ): Candidate {
        val range = qualityRange(format)

        if (range == null) {
            val candidate = encode(
                source = source,
                format = format,
                qualityValue = null,
                withSsim = searchMode == CompressionSearchMode.TargetQuality
            )
            return when (searchMode) {
                CompressionSearchMode.Manual -> candidate
                CompressionSearchMode.TargetQuality -> candidate.copy(
                    targetSatisfied = (candidate.ssim ?: 0.0) >= targetQuality / 100.0
                )

                CompressionSearchMode.TargetSize -> candidate.copy(
                    targetSatisfied = candidate.sizeBytes <= targetSizeKb * 1024L
                )
            }
        }

        val cache = mutableMapOf<Int, Candidate>()

        suspend fun at(quality: Int, withSsim: Boolean = false): Candidate {
            val cached = cache[quality]
            if (cached != null && (!withSsim || cached.ssim != null)) return cached

            return encode(
                source = source,
                format = format,
                qualityValue = quality,
                withSsim = withSsim
            ).also { cache[quality] = it }
        }

        return when (searchMode) {
            CompressionSearchMode.Manual -> at(manualQuality.coerceIn(range))

            CompressionSearchMode.TargetQuality -> {
                var low = range.first
                var high = range.last
                var best: Candidate? = null
                val target = targetQuality / 100.0

                while (low <= high) {
                    val quality = (low + high) / 2
                    val candidate = at(quality, withSsim = true)
                    if ((candidate.ssim ?: 0.0) >= target) {
                        best = candidate
                        high = quality - 1
                    } else {
                        low = quality + 1
                    }
                }

                best ?: at(range.last, withSsim = true).copy(targetSatisfied = false)
            }

            CompressionSearchMode.TargetSize -> {
                var low = range.first
                var high = range.last
                var best: Candidate? = null
                val targetBytes = targetSizeKb * 1024L

                while (low <= high) {
                    val quality = (low + high) / 2
                    val candidate = at(quality)
                    if (candidate.sizeBytes <= targetBytes) {
                        best = candidate
                        low = quality + 1
                    } else {
                        high = quality - 1
                    }
                }

                best ?: at(range.first).copy(targetSatisfied = false)
            }
        }
    }

    private suspend fun encode(
        source: Bitmap,
        format: ImageFormat,
        qualityValue: Int?,
        withSsim: Boolean
    ): Candidate {
        val quality = qualityFor(format, qualityValue)
        val mark = TimeSource.Monotonic.markNow()
        val data = imageCompressor.compress(
            image = source,
            imageFormat = format,
            quality = quality
        )
        val encodingTimeMillis = mark.elapsedNow().inWholeMilliseconds
        val ssim = if (withSsim) {
            val bitmap = imageGetter.getImage(data = data, originalSize = true)
                ?: error("Unable to decode ${format.title} result")
            metric(source, bitmap, ComparisonType.SSIM)
        } else null

        return Candidate(
            qualityValue = qualityValue,
            quality = quality,
            uri = cacheEncoded(data, format),
            sizeBytes = data.size.toLong(),
            encodingTimeMillis = encodingTimeMillis,
            ssim = ssim
        )
    }

    private suspend fun Candidate.toResult(
        source: Bitmap,
        format: ImageFormat
    ): CompressionLabResult {
        val bitmap = imageGetter.getImage(
            uri = uri.toString(),
            originalSize = true
        )?.image ?: error("Unable to decode ${format.title} result")

        return CompressionLabResult(
            format = format,
            qualityValue = qualityValue,
            quality = quality,
            uri = uri,
            width = bitmap.width,
            height = bitmap.height,
            sizeBytes = sizeBytes,
            encodingTimeMillis = encodingTimeMillis,
            ssim = ssim ?: metric(source, bitmap, ComparisonType.SSIM),
            psnr = metric(source, bitmap, ComparisonType.PSNR),
            targetSatisfied = targetSatisfied
        )
    }

    private suspend fun encodeWithProfile(
        uri: Uri,
        profile: CompressionLabResult
    ): EncodedOutput {
        if (uri == benchmarkUri) {
            return EncodedOutput(
                uri = profile.uri,
                width = profile.width,
                height = profile.height
            )
        }

        val bitmap = imageGetter.getImage(
            uri = uri.toString(),
            originalSize = true
        )?.image ?: error("Unable to decode image")

        val width = bitmap.width
        val height = bitmap.height
        val data = imageCompressor.compress(
            image = bitmap,
            imageFormat = profile.format,
            quality = profile.quality
        )

        return EncodedOutput(
            uri = cacheEncoded(data, profile.format),
            width = width,
            height = height
        )
    }

    private fun EncodedOutput.saveTarget(
        uri: Uri,
        profile: CompressionLabResult,
        sequenceNumber: Int,
        data: ByteArray
    ) = ImageSaveTarget(
        imageInfo = ImageInfo(
            width = width,
            height = height,
            imageFormat = profile.format,
            quality = profile.quality,
            originalUri = uri.toString()
        ),
        originalUri = uri.toString(),
        sequenceNumber = sequenceNumber,
        data = data,
        imageFormat = profile.format
    )

    private suspend fun cacheEncoded(
        data: ByteArray,
        format: ImageFormat
    ): Uri = shareProvider.cacheByteArray(
        byteArray = data,
        filename = filenameCreator.constructRandomFilename(format.extension)
    )?.let(Uri::parse) ?: error("Unable to cache ${format.title} result")

    private fun metric(
        source: Bitmap,
        result: Bitmap,
        type: ComparisonType
    ): Double = ImageDiffTool.highlightDifferences(
        input = source,
        other = result,
        comparisonType = type,
        highlightColor = 0
    ).let {
        it.highlightedBitmap.recycle()
        it.score
    }

    private fun clearResults() {
        analysisJob?.cancel()
        analysisJob = null
        _isImageLoading.update { false }
        _results.update { emptyList() }
        _failedFormats.update { emptyList() }
        _selectedResultIndex.update { 0 }
        registerChangesCleared()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): CompressionLabComponent
    }

    companion object {
        val AvailableFormats: List<ImageFormat> = ImageFormat.entries

        val DefaultFormats: List<ImageFormat> = listOf(
            ImageFormat.Jpg,
            ImageFormat.Webp.Lossy,
            ImageFormat.Avif.LossyAv1,
            ImageFormat.Jxl.Lossy
        )

        private fun qualityRange(format: ImageFormat): IntRange? {
            if (!format.canChangeCompressionValue) return null

            return format.compressionTypes
                .filterIsInstance<ImageFormat.CompressionType.Quality>()
                .firstOrNull()
                ?.compressionRange
        }

        private fun qualityFor(format: ImageFormat, value: Int?): Quality {
            if (value == null) return Quality.Base().coerceIn(format)

            return when (format) {
                ImageFormat.Png.ImageQuant -> Quality.PngQuant(quality = value)
                is ImageFormat.Avif -> Quality.Avif(qualityValue = value)
                ImageFormat.Heic.VvcLossy -> Quality.Vvc(qualityValue = value)
                is ImageFormat.Heic -> Quality.Heic(qualityValue = value)
                is ImageFormat.Jxl -> Quality.Jxl(qualityValue = value)
                else -> Quality.Base(value).coerceIn(format)
            }
        }
    }

    private data class Candidate(
        val qualityValue: Int?,
        val quality: Quality,
        val uri: Uri,
        val sizeBytes: Long,
        val encodingTimeMillis: Long,
        val ssim: Double? = null,
        val targetSatisfied: Boolean = true
    )

    private data class EncodedOutput(
        val uri: Uri,
        val width: Int,
        val height: Int
    )
}

enum class CompressionSearchMode {
    Manual,
    TargetQuality,
    TargetSize
}

data class CompressionLabResult(
    val format: ImageFormat,
    val qualityValue: Int?,
    val quality: Quality,
    val uri: Uri,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val encodingTimeMillis: Long,
    val ssim: Double,
    val psnr: Double,
    val targetSatisfied: Boolean
)
