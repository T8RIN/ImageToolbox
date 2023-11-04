package ru.tech.imageresizershrinker.presentation.pdf_tools_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.pdf_tools_screen.components.PdfToImageState
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.utils.state.update
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PdfToolsViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController
) : ViewModel() {

    private val _pdfToImageState: MutableState<PdfToImageState?> = mutableStateOf(null)
    val pdfToImageState by _pdfToImageState

    private val _imagesToPdfState: MutableState<List<Uri>?> = mutableStateOf(null)
    val imagesToPdfState by _imagesToPdfState

    private val _pdfPreviewUri: MutableState<Uri?> = mutableStateOf(null)
    val pdfPreviewUri by _pdfPreviewUri

    private val _pdfType: MutableState<Screen.PdfTools.Type?> = mutableStateOf(null)
    val pdfType: Screen.PdfTools.Type? by _pdfType

    private val _byteArray = mutableStateOf<ByteArray?>(null)

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _presetSelected: MutableState<Preset> = mutableStateOf(Preset.Numeric(100))
    val presetSelected by _presetSelected

    private val _scaleSmallImagesToLarge: MutableState<Boolean> = mutableStateOf(false)
    val scaleSmallImagesToLarge by _scaleSmallImagesToLarge

    private val _showOOMWarning: MutableState<Boolean> = mutableStateOf(false)
    val showOOMWarning by _showOOMWarning

    private var savingJob: Job? = null

    private fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun savePdfTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.value = true
                kotlin.runCatching {
                    outputStream?.use {
                        it.write(_byteArray.value)
                    }
                }.exceptionOrNull().let(onComplete)
                _isSaving.value = false
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun canGoBack(): Boolean {
        return _byteArray.value == null && _imageInfo.value == ImageInfo()
    }

    fun setType(type: Screen.PdfTools.Type) {
        when (type) {
            is Screen.PdfTools.Type.ImagesToPdf -> setImagesToPdf(type.imageUris)
            is Screen.PdfTools.Type.PdfToImages -> setPdfToImagesUri(type.pdfUri)
            is Screen.PdfTools.Type.Preview -> setPdfPreview(type.pdfUri)
        }
        resetCalculatedData()
    }

    fun setPdfPreview(uri: Uri?) {
        _pdfType.update {
            if (it !is Screen.PdfTools.Type.Preview) {
                Screen.PdfTools.Type.Preview(uri)
            } else it
        }
        _pdfPreviewUri.update { uri }
        _imagesToPdfState.update { null }
        _pdfToImageState.update { null }
        resetCalculatedData()
    }

    fun setImagesToPdf(uris: List<Uri>?) {
        _pdfType.update {
            if (it !is Screen.PdfTools.Type.ImagesToPdf) {
                Screen.PdfTools.Type.ImagesToPdf(uris)
            } else it
        }
        _imagesToPdfState.update { uris }
        _pdfPreviewUri.update { null }
        _pdfToImageState.update { null }
        resetCalculatedData()
    }

    fun setPdfToImagesUri(newUri: Uri?) {
        _pdfToImageState.update { null }
        _pdfType.update {
            if (it !is Screen.PdfTools.Type.PdfToImages) {
                Screen.PdfTools.Type.PdfToImages(newUri)
            } else it
        }
        viewModelScope.launch {
            newUri?.let {
                val pages = imageManager.getPdfPages(newUri.toString())
                _pdfToImageState.update {
                    PdfToImageState(newUri, pages)
                }
                checkForOOM()
            }
        }

        _imagesToPdfState.update { null }
        _pdfPreviewUri.update { null }
        resetCalculatedData()
    }

    fun clearType() {
        _pdfType.update { null }
        _pdfPreviewUri.update { null }
        _imagesToPdfState.update { null }
        _pdfToImageState.update { null }
        _presetSelected.update { Preset.Numeric(100) }
        _showOOMWarning.value = false
        _imageInfo.value = ImageInfo()
        resetCalculatedData()
    }

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(1)
    val left by _left

    fun savePdfToImage(
        onComplete: (path: String) -> Unit
    ) {
        savingJob?.cancel()
        _done.value = 0
        _left.value = 1
        _isSaving.value = false
        savingJob = imageManager.convertPdfToImages(
            pdfUri = _pdfToImageState.value?.uri.toString(),
            pages = _pdfToImageState.value?.pages,
            onProgressChange = { _, uri ->
                runCatching {
                    imageManager.getImage(uri)?.image
                }.getOrNull()?.let { bitmap ->
                    imageInfo.let {
                        imageManager.applyPresetBy(
                            image = bitmap,
                            preset = _presetSelected.value,
                            currentInfo = it
                        )
                    }.apply {
                        val result = fileController.save(
                            ImageSaveTarget(
                                imageInfo = this,
                                metadata = null,
                                originalUri = uri,
                                sequenceNumber = _done.value + 1,
                                data = imageManager.compress(
                                    ImageData(
                                        image = bitmap,
                                        imageInfo = this,
                                        metadata = null
                                    )
                                )
                            ), false
                        )
                        if (result is SaveResult.Error.MissingPermissions) {
                            savingJob?.cancel()
                            return@convertPdfToImages onComplete("")
                        }
                    }
                }
                _done.value += 1
            },
            onGetPagesCount = { size ->
                _left.update { size }
                _isSaving.value = true
            },
            onComplete = {
                _isSaving.value = false
                onComplete(fileController.savingPath)
            }
        )
    }

    fun convertImagesToPdf(onComplete: () -> Unit) {
        savingJob?.cancel()
        _isSaving.value = false
        _done.value = 0
        _left.value = 0
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            _left.value = imagesToPdfState?.size ?: 0
            _byteArray.value = imageManager.convertImagesToPdf(
                imageUris = imagesToPdfState?.map { it.toString() } ?: emptyList(),
                onProgressChange = {
                    _done.value = it
                },
                scaleSmallImagesToLarge = _scaleSmallImagesToLarge.value
            )
            onComplete()
            _isSaving.value = false
        }
    }

    fun generatePdfFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "PDF_$timeStamp"
    }

    fun preformSharing(
        onComplete: () -> Unit
    ) {
        savingJob?.cancel()
        _isSaving.value = false
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            when (val type = _pdfType.value) {
                is Screen.PdfTools.Type.ImagesToPdf -> {
                    _isSaving.value = true
                    _left.value = imagesToPdfState?.size ?: 0
                    imageManager.convertImagesToPdf(
                        imageUris = imagesToPdfState?.map { it.toString() } ?: emptyList(),
                        onProgressChange = {
                            _done.value = it
                        },
                        scaleSmallImagesToLarge = _scaleSmallImagesToLarge.value
                    ).let {
                        imageManager.shareFile(
                            byteArray = it,
                            filename = generatePdfFilename() + ".pdf",
                            onComplete = onComplete
                        )
                    }
                    onComplete()
                }

                is Screen.PdfTools.Type.PdfToImages -> {
                    savingJob?.cancel()
                    _done.value = 0
                    _left.value = 1
                    _isSaving.value = false
                    val uris: MutableList<String?> = mutableListOf()
                    savingJob = imageManager.convertPdfToImages(
                        pdfUri = _pdfToImageState.value?.uri.toString(),
                        pages = _pdfToImageState.value?.pages,
                        onProgressChange = { _, uri ->
                            runCatching {
                                imageManager.getImage(uri)?.image
                            }.getOrNull()?.let { bitmap ->
                                imageInfo.let {
                                    imageManager.applyPresetBy(
                                        image = bitmap,
                                        preset = _presetSelected.value,
                                        currentInfo = it
                                    )
                                }.apply {
                                    uris.add(
                                        imageManager.cacheImage(
                                            imageInfo = this,
                                            image = bitmap
                                        )
                                    )
                                }
                            }
                            _done.value += 1
                        },
                        onGetPagesCount = { size ->
                            _left.update { size }
                            _isSaving.value = true
                        },
                        onComplete = {
                            _isSaving.value = false
                            imageManager.shareImageUris(uris.filterNotNull())
                            onComplete()
                        }
                    )
                }

                is Screen.PdfTools.Type.Preview -> {
                    type.pdfUri?.toString()?.let {
                        imageManager.shareUri(
                            uri = it,
                            type = null
                        )
                        onComplete()
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    fun addImagesToPdf(uris: List<Uri>) {
        _imagesToPdfState.update {
            it?.plus(uris)?.toSet()?.toList()
        }
    }

    fun removeImageToPdfAt(index: Int) {
        runCatching {
            _imagesToPdfState.update {
                it?.toMutableList()?.apply { removeAt(index) }
            }
        }
    }

    fun reorderImagesToPdf(uris: List<Uri>?) {
        _imagesToPdfState.update { uris }
    }

    fun toggleScaleSmallImagesToLarge() {
        _scaleSmallImagesToLarge.update { !it }
    }

    private var presetSelectionJob: Job? = null

    private fun checkForOOM() {
        val preset = _presetSelected.value
        if (preset is Preset.Numeric) {
            presetSelectionJob?.cancel()
            presetSelectionJob = viewModelScope.launch {
                runCatching {
                    _pdfToImageState.value?.let { (uri, pages) ->
                        val pagesSize = imageManager.getPdfPageSizes(uri.toString())
                            .filterIndexed { index, _ -> index in pages }
                        _showOOMWarning.update {
                            pagesSize.maxOf { size ->
                                size.width * (preset.value / 100f) * size.height * (preset.value / 100f) * 4
                            } >= 10_000 * 10_000 * 3
                        }
                    }
                }.getOrNull() ?: _showOOMWarning.update { false }
            }
        }
    }

    fun selectPreset(preset: Preset) {
        _presetSelected.update { preset }
        preset.value()?.takeIf { it <= 100f }?.let { quality ->
            _imageInfo.update { it.copy(quality = quality.toFloat()) }
        }
        checkForOOM()
    }

    fun updatePdfToImageSelection(ints: List<Int>) {
        _pdfToImageState.update {
            it?.copy(pages = ints)
        }
        checkForOOM()
    }

    fun updateImageFormat(imageFormat: ImageFormat) {
        _imageInfo.update {
            it.copy(imageFormat = imageFormat)
        }
    }

    fun setQuality(value: Float) {
        _imageInfo.update {
            it.copy(quality = value)
        }
    }

}