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

package ru.tech.imageresizershrinker.feature.document_scanner.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.onSuccess
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.ScanResult
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.pdf_tools.domain.PdfManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class DocumentScannerViewModel @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val shareProvider: ShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val pdfManager: PdfManager<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder, componentContext) {

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _imageFormat = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _quality = mutableStateOf<Quality>(Quality.Base())
    val quality by _quality

    private val _pdfUris = mutableStateOf<List<Uri>>(emptyList())

    private suspend fun getPdfUri(): Uri? =
        if (_pdfUris.value.size > 1 || _pdfUris.value.isEmpty()) {
            createPdfUri()
        } else _pdfUris.value.firstOrNull()

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun parseScanResult(scanResult: ScanResult) {
        if (scanResult.imageUris.isNotEmpty()) {
            _uris.update { scanResult.imageUris }
        }
        if (scanResult.pdfUri != null) {
            _pdfUris.update { listOfNotNull(scanResult.pdfUri) }
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onComplete: (List<SaveResult>) -> Unit
    ) {
        savingJob = viewModelScope.launch(defaultDispatcher) {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris.forEach { uri ->
                runCatching {
                    imageGetter.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    val imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = imageFormat,
                        quality = quality,
                        originalUri = uri.toString()
                    )
                    results.add(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = imageInfo,
                                metadata = null,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageCompressor.compressAndTransform(
                                    image = bitmap,
                                    imageInfo = imageInfo
                                )
                            ),
                            keepOriginalMetadata = true,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
            }
            onComplete(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun savePdfTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = viewModelScope.launch(ioDispatcher) {
            _isSaving.value = true
            getPdfUri()?.let { pdfUri ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(fileController.readBytes(pdfUri.toString())) }
                ).also(onResult).onSuccess(::registerSave)
                _isSaving.value = false
            }
        }
    }

    private suspend fun createPdfUri(): Uri? {
        _done.value = 0
        _left.value = uris.size
        val byteArray = pdfManager.convertImagesToPdf(
            imageUris = uris.map { it.toString() },
            onProgressChange = {
                _done.value = it
            },
            scaleSmallImagesToLarge = false,
            preset = Preset.Original
        )
        return shareProvider.cacheByteArray(
            byteArray = byteArray,
            filename = generatePdfFilename()
        )?.toUri()
    }

    fun generatePdfFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "PDF_$timeStamp.pdf"
    }

    fun sharePdf(
        onComplete: () -> Unit
    ) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            getPdfUri()?.let { pdfUri ->
                _done.update { 0 }
                _left.update { 0 }

                runCatching {
                    shareProvider.shareUri(
                        uri = pdfUri.toString(),
                        onComplete = onComplete
                    )
                }.onFailure {
                    val bytes = fileController.readBytes(pdfUri.toString())
                    shareProvider.shareByteArray(
                        byteArray = bytes,
                        filename = generatePdfFilename(),
                        onComplete = onComplete
                    )
                }
            }
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun removeImageUri(uri: Uri) {
        _uris.update { it - uri }
        _pdfUris.update { emptyList() }
    }

    fun addScanResult(scanResult: ScanResult) {
        _uris.update { (it + scanResult.imageUris).distinct() }
        _pdfUris.update { (it + scanResult.pdfUri).distinct().filterNotNull() }
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris.map { it.toString() },
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.image?.let { bmp ->
                        bmp to ImageInfo(
                            width = bmp.width,
                            height = bmp.height,
                            imageFormat = imageFormat,
                            quality = quality,
                            originalUri = uri
                        )
                    }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
                        _isSaving.value = false
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                }
            )
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): DocumentScannerViewModel
    }

}