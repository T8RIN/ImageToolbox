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
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.utils.state.update
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class PdfToolsViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController
) : ViewModel() {

    private val _pdfToImageState: MutableState<List<Uri>?> = mutableStateOf(null)
    val pdfToImageState by _pdfToImageState

    private val _imagesToPdfState: MutableState<List<Uri>?> = mutableStateOf(null)
    val imagesToPdfState by _imagesToPdfState

    private val _pdfPreviewUri: MutableState<Uri?> = mutableStateOf(null)
    val pdfPreviewUri by _pdfPreviewUri

    private val _pdfType: MutableState<Screen.PdfTools.Type?> = mutableStateOf(null)
    val pdfType: Screen.PdfTools.Type? by _pdfType

    private val _byteArray = mutableStateOf<ByteArray?>(null)
    val byteArray by _byteArray

    private val _imageInfo = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _presetSelected: MutableState<Preset> = mutableStateOf(Preset.None)
    val presetSelected by _presetSelected

    private val _scaleSmallImagesToLarge: MutableState<Boolean> = mutableStateOf(false)
    val scaleSmallImagesToLarge by _scaleSmallImagesToLarge

    private var savingJob: Job? = null

    fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun savePdfTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            kotlin.runCatching {
                outputStream?.use {
                    it.write(_byteArray.value)
                }
            }.exceptionOrNull().let(onComplete)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
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
        resetCalculatedData()
    }

    fun setPdfToImagesUri(newUri: Uri?) {
        _pdfType.update {
            if (it !is Screen.PdfTools.Type.PdfToImages) {
                Screen.PdfTools.Type.PdfToImages(newUri)
            } else it
        }
        _imagesToPdfState.update { null }
        _pdfPreviewUri.update { null }
        resetCalculatedData()
    }

    fun clearType() {
        _pdfType.update { null }
        _pdfPreviewUri.update { null }
        _imagesToPdfState.update { null }
        resetCalculatedData()
    }

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    fun savePdfToImage(
        onComplete: (path: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            _done.value = 0
            pdfToImageState?.forEach { uri ->
                runCatching {
                    imageManager.getImage(uri.toString())?.image
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
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageManager.compress(
                                    ImageData(
                                        image = bitmap,
                                        imageInfo = imageInfo,
                                        metadata = null
                                    )
                                )
                            ), false
                        )
                        if (result is SaveResult.Error.MissingPermissions) {
                            return@withContext onComplete("")
                        }
                    }
                }
                _done.value += 1
            }
            onComplete(fileController.savingPath)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun convertImagesToPdf(onComplete: () -> Unit) {
        savingJob?.cancel()
        _isSaving.value = false
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            _byteArray.value = imageManager.convertImagesToPdf(
                imageUris = imagesToPdfState?.map { it.toString() } ?: emptyList(),
                scaleSmallImagesToLarge = _scaleSmallImagesToLarge.value
            )
            onComplete()
            _isSaving.value = false
        }
    }

    fun generatePdfFilename(): String {
        return "TEST"
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
                    imageManager.convertImagesToPdf(
                        imageUris = imagesToPdfState?.map { it.toString() } ?: emptyList(),
                        scaleSmallImagesToLarge = _scaleSmallImagesToLarge.value
                    ).let {
                        imageManager.shareFile(
                            byteArray = it,
                            filename = generatePdfFilename() + ".pdf",
                            onComplete = onComplete
                        )
                    }
                }

                is Screen.PdfTools.Type.PdfToImages -> {

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
            it?.plus(uris)
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

}