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

package com.t8rin.imagetoolbox.feature.document_scanner.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ScanResult
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.navigation.coroutineScope
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlin.random.Random

class DocumentScannerComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val pdfManager: PdfManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

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
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris.forEach { uri ->
                runSuspendCatching {
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

                updateProgress(
                    done = done,
                    total = left
                )
            }
            onComplete(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun savePdfTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            getPdfUri()?.let { pdfUri ->
                fileController.transferBytes(
                    fromUri = pdfUri.toString(),
                    toUri = uri.toString(),
                ).also(onResult).onSuccess(::registerSave)
                _isSaving.value = false
            }
        }
    }

    private suspend fun createPdfUri(): Uri? {
        _done.value = 0
        _left.value = uris.size
        return runSuspendCatching {
            pdfManager.convertImagesToPdf(
                imageUris = uris.map { it.toString() },
                onProgressChange = {
                    _done.value = it
                },
                scaleSmallImagesToLarge = false,
                preset = Preset.Original,
                tempFilename = generatePdfFilename()
            )
        }.getOrNull()?.toUri()
    }

    fun generatePdfFilename(): String {
        val timeStamp = "${timestamp()}_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "PDF_$timeStamp.pdf"
    }

    fun sharePdf(
        onComplete: () -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            getPdfUri()?.let { pdfUri ->
                _done.update { 0 }
                _left.update { 0 }

                runSuspendCatching {
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
        savingJob = trackProgress {
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
                    updateProgress(
                        done = done,
                        total = left
                    )
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

    fun shareUri(uri: Uri) {
        coroutineScope.launch {
            shareProvider.shareUri(
                uri = uri.toString(),
                onComplete = {}
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): DocumentScannerComponent
    }

}