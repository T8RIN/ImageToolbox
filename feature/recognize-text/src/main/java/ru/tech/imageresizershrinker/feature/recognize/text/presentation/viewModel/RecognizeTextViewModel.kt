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

package ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tech.imageresizershrinker.core.data.utils.asDomain
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiContrastFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiSharpenFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiThresholdFilter
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.recognize.text.domain.DownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.ImageTextReader
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OcrEngineMode
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.domain.SegmentationMode
import ru.tech.imageresizershrinker.feature.recognize.text.domain.TextRecognitionResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import coil.transform.Transformation as CoilTransformation


@HiltViewModel
class RecognizeTextViewModel @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageTextReader: ImageTextReader<Bitmap>,
    private val settingsManager: SettingsManager,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

    private val _segmentationMode: MutableState<SegmentationMode> =
        mutableStateOf(SegmentationMode.PSM_AUTO_OSD)
    val segmentationMode by _segmentationMode

    private val _ocrEngineMode: MutableState<OcrEngineMode> = mutableStateOf(OcrEngineMode.DEFAULT)
    val ocrEngineMode by _ocrEngineMode

    private val _selectedLanguages = mutableStateOf(listOf(OCRLanguage.Default))
    val selectedLanguages by _selectedLanguages

    private val _recognitionType = mutableStateOf(RecognitionType.Standard)
    val recognitionType by _recognitionType

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _recognitionData = mutableStateOf<RecognitionData?>(null)
    val recognitionData by _recognitionData

    private val _textLoadingProgress: MutableState<Int> = mutableIntStateOf(-1)
    val textLoadingProgress by _textLoadingProgress

    private val _languages: MutableState<List<OCRLanguage>> = mutableStateOf(emptyList())
    val languages by _languages

    private val contrastFilterInstance = UiContrastFilter()

    private val sharpenFilterInstance = UiSharpenFilter()

    private val thresholdFilterInstance = UiThresholdFilter()

    private val filtersOrder = listOf(
        contrastFilterInstance,
        sharpenFilterInstance,
        thresholdFilterInstance
    )

    private val _filtersAdded: MutableState<List<Filter<*>>> = mutableStateOf(emptyList())
    val filtersAdded by _filtersAdded

    private val internalBitmap: MutableState<Bitmap?> = mutableStateOf(null)

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _rotation: MutableState<Float> = mutableFloatStateOf(0f)

    private val _isFlipped: MutableState<Boolean> = mutableStateOf(false)

    private val _selectedAspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Free)
    val selectedAspectRatio by _selectedAspectRatio

    private val _cropProperties = mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                OutlineType.Rect,
                RectCropShape(
                    id = 0,
                    title = OutlineType.Rect.name
                )
            ),
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _isExporting: MutableState<Boolean> = mutableStateOf(false)
    val isExporting by _isExporting

    private var languagesJob: Job? by smartJob {
        _isExporting.update { false }
    }

    val isTextLoading: Boolean
        get() = textLoadingProgress in 0..100

    private var loadingJob: Job? by smartJob()

    private fun loadLanguages(
        onComplete: suspend () -> Unit = {}
    ) {
        loadingJob = viewModelScope.launch {
            delay(200L)
            val data = imageTextReader.getLanguages(recognitionType)
            _selectedLanguages.update { ocrLanguages ->
                val list = ocrLanguages.toMutableList()
                data.forEach { ocrLanguage ->
                    ocrLanguages.indexOfFirst {
                        it.code == ocrLanguage.code
                    }.takeIf { it != -1 }?.let { index ->
                        list[index] = ocrLanguage
                    }
                }
                list
            }
            _languages.update { data }
            onComplete()
        }
    }

    init {
        loadLanguages()
        viewModelScope.launch {
            val languageCodes = settingsManager.getInitialOCRLanguageCodes().map {
                imageTextReader.getLanguageForCode(it)
            }
            _selectedLanguages.update { languageCodes }
        }
    }

    fun getTransformations(): List<CoilTransformation> = filtersOrder.filter {
        it in filtersAdded
    }.map {
        filterProvider.filterToTransformation(it).toCoil()
    }

    fun updateUri(
        uri: Uri?,
        onImageSet: () -> Unit
    ) {
        _uri.update { uri }
        uri?.let {
            viewModelScope.launch {
                _isImageLoading.value = true
                imageGetter.getImage(data = uri, false)?.let {
                    updateBitmap(it, onImageSet)
                }
                _isImageLoading.value = false
            }
        }
    }

    fun updateBitmap(
        bitmap: Bitmap,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            internalBitmap.update { previewBitmap }
            _isImageLoading.value = false
            onComplete()
        }
    }

    private var recognitionJob: Job? by smartJob {
        _textLoadingProgress.update { -1 }
    }

    fun startRecognition(
        onError: (Throwable) -> Unit,
        onRequestDownload: (List<DownloadData>) -> Unit
    ) {
        recognitionJob = viewModelScope.launch {
            if (uri == null) return@launch
            delay(400L)
            _textLoadingProgress.update { 0 }
            imageTextReader.getTextFromImage(
                type = recognitionType,
                languageCode = selectedLanguages.joinToString("+") { it.code },
                segmentationMode = segmentationMode,
                image = previewBitmap?.let { bitmap ->
                    imageTransformer.transform(
                        transformations = getTransformations().map {
                            it.asDomain()
                        },
                        image = bitmap
                    )
                },
                ocrEngineMode = ocrEngineMode,
                onProgress = { progress ->
                    _textLoadingProgress.update { progress }
                }
            ).also { result ->
                when (result) {
                    is TextRecognitionResult.Error -> {
                        onError(result.throwable)
                    }

                    is TextRecognitionResult.NoData -> {
                        onRequestDownload(result.data)
                    }

                    is TextRecognitionResult.Success -> {
                        _recognitionData.update { result.data }
                    }
                }
            }
            _textLoadingProgress.update { -1 }
        }
    }

    fun setRecognitionType(recognitionType: RecognitionType) {
        _recognitionType.update { recognitionType }
        loadLanguages()
    }

    private val downloadMutex = Mutex()
    fun downloadTrainData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (Float, Long) -> Unit,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            downloadMutex.withLock {
                imageTextReader.downloadTrainingData(
                    type = type,
                    languageCode = languageCode,
                    onProgress = onProgress
                )
                loadLanguages {
                    settingsManager.setInitialOCRLanguageCodes(
                        selectedLanguages.filter {
                            it.downloaded.isNotEmpty()
                        }.map { it.code }
                    )
                }
                onComplete()
            }
        }
    }

    fun onLanguagesSelected(ocrLanguages: List<OCRLanguage>) {
        if (ocrLanguages.isNotEmpty()) {
            viewModelScope.launch {
                settingsManager.setInitialOCRLanguageCodes(
                    ocrLanguages.filter {
                        it.downloaded.isNotEmpty()
                    }.map { it.code }
                )
            }
            _selectedLanguages.update { ocrLanguages }
            _recognitionData.update { null }
            recognitionJob?.cancel()
            _textLoadingProgress.update { -1 }
        }
    }

    fun setSegmentationMode(segmentationMode: SegmentationMode) {
        _segmentationMode.update { segmentationMode }
    }

    fun deleteLanguage(
        language: OCRLanguage,
        types: List<RecognitionType>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            imageTextReader.deleteLanguage(language, types)
            onLanguagesSelected(selectedLanguages - language)
            val availableTypes = language.downloaded - types.toSet()
            availableTypes.firstOrNull()?.let(::setRecognitionType) ?: loadLanguages()
            onSuccess()
        }
    }

    fun rotateBitmapLeft() {
        _rotation.update { it - 90f }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun rotateBitmapRight() {
        _rotation.update { it + 90f }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun flipImage() {
        _isFlipped.update { !it }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    private suspend fun checkBitmapAndUpdate() {
        _previewBitmap.value = internalBitmap.value?.let {
            imageTransformer.flip(
                image = imageTransformer.rotate(
                    image = it,
                    degrees = _rotation.value
                ),
                isFlipped = _isFlipped.value
            )
        }
    }

    fun setCropAspectRatio(
        domainAspectRatio: DomainAspectRatio,
        aspectRatio: AspectRatio
    ) {
        _cropProperties.value = _cropProperties.value.copy(
            aspectRatio = aspectRatio.takeIf {
                domainAspectRatio != DomainAspectRatio.Original
            } ?: _previewBitmap.value?.let {
                AspectRatio(it.safeAspectRatio)
            } ?: aspectRatio,
            fixedAspectRatio = domainAspectRatio != DomainAspectRatio.Free
        )
        _selectedAspectRatio.update { domainAspectRatio }
    }

    fun setCropMask(cropOutlineProperty: CropOutlineProperty) {
        _cropProperties.value =
            _cropProperties.value.copy(cropOutlineProperty = cropOutlineProperty)
    }

    suspend fun loadImage(uri: Uri): Bitmap? = imageGetter.getImage(data = uri)

    fun toggleContrastFilter() {
        _filtersAdded.update {
            if (contrastFilterInstance in it) it - contrastFilterInstance
            else it + contrastFilterInstance
        }
    }

    fun toggleThresholdFilter() {
        _filtersAdded.update {
            if (thresholdFilterInstance in it) it - thresholdFilterInstance
            else it + thresholdFilterInstance
        }
    }

    fun toggleSharpnessFilter() {
        _filtersAdded.update {
            if (sharpenFilterInstance in it) it - sharpenFilterInstance
            else it + sharpenFilterInstance
        }
    }

    fun setOcrEngineMode(mode: OcrEngineMode) {
        _ocrEngineMode.update { mode }
    }

    fun shareRecognizedText(onComplete: () -> Unit) {
        recognitionData?.text?.let {
            shareProvider.shareText(
                value = it,
                onComplete = onComplete
            )
        }
    }

    fun exportLanguagesTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        languagesJob = viewModelScope.launch(ioDispatcher) {
            _isExporting.value = true
            imageTextReader.exportLanguagesToZip()?.let { zipUri ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(fileController.readBytes(zipUri)) }
                ).also(onResult).onSuccess(::registerSave)
                _isExporting.value = false
            }
        }
    }

    fun generateExportFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        return "image_toolbox_ocr_languages_$timeStamp.zip"
    }

    fun importLanguagesFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        languagesJob = viewModelScope.launch(ioDispatcher) {
            _isExporting.value = true
            imageTextReader.importLanguagesFromUri(uri.toString())
                .onSuccess {
                    loadLanguages {
                        onSuccess()
                    }
                }
                .onFailure(onFailure)
            _isExporting.value = false
        }
    }

}