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

@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.t8rin.imagetoolbox.core.data.utils.asDomain
import com.t8rin.imagetoolbox.core.data.utils.toCoil
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiContrastFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiSharpenFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiThresholdFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.recognize.text.domain.DownloadData
import com.t8rin.imagetoolbox.feature.recognize.text.domain.ImageTextReader
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OcrEngineMode
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionData
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType
import com.t8rin.imagetoolbox.feature.recognize.text.domain.SegmentationMode
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TessParams
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TextRecognitionResult
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.components.UiDownloadData
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.components.toUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import coil3.transform.Transformation as CoilTransformation

class RecognizeTextComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.RecognizeText.Type?,
    @Assisted onGoBack: () -> Unit,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageTextReader: ImageTextReader,
    private val settingsManager: SettingsManager,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider,
    private val fileController: FileController,
    private val filenameCreator: FilenameCreator,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext), ResourceManager by resourceManager {

    private val _segmentationMode: MutableState<SegmentationMode> =
        mutableStateOf(SegmentationMode.PSM_AUTO_OSD)
    val segmentationMode by _segmentationMode

    private val _ocrEngineMode: MutableState<OcrEngineMode> = mutableStateOf(OcrEngineMode.DEFAULT)
    val ocrEngineMode by _ocrEngineMode

    private val _params: MutableState<TessParams> = mutableStateOf(TessParams.Default)
    val params by _params

    private val _selectedLanguages = mutableStateOf(listOf(OCRLanguage.Default))
    val selectedLanguages by _selectedLanguages

    private var isRecognitionTypeSet = false
    private val _recognitionType = mutableStateOf(RecognitionType.Standard)
    val recognitionType by _recognitionType

    private val _type = mutableStateOf<Screen.RecognizeText.Type?>(null)
    val type by _type

    val uris: List<Uri>
        get() = when (val target = type) {
            is Screen.RecognizeText.Type.WriteToFile -> target.uris ?: emptyList()
            is Screen.RecognizeText.Type.WriteToMetadata -> target.uris ?: emptyList()
            else -> emptyList()
        }

    val onGoBack: () -> Unit = {
        if (type == null) onGoBack()
        else {
            _recognitionData.update { null }
            _type.update { null }
        }
    }

    private val _recognitionData = mutableStateOf<RecognitionData?>(null)
    val recognitionData by _recognitionData

    val text: String? get() = recognitionData?.text?.takeIf { it.isNotEmpty() }

    private val _editedText = mutableStateOf(text)
    val editedText by _editedText

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

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private var languagesJob: Job? by smartJob {
        _isExporting.update { false }
    }

    val isTextLoading: Boolean
        get() = textLoadingProgress in 0..100

    private var loadingJob: Job? by smartJob()

    private val _selectionSheetData = mutableStateOf(emptyList<Uri>())
    val selectionSheetData by _selectionSheetData

    private val _downloadDialogData = mutableStateOf<List<UiDownloadData>>(emptyList())
    val downloadDialogData by _downloadDialogData

    fun clearDownloadDialogData() {
        _downloadDialogData.update { emptyList() }
    }

    fun showSelectionTypeSheet(uris: List<Uri>) {
        _selectionSheetData.update { uris }
    }

    fun hideSelectionTypeSheet() {
        _selectionSheetData.update { emptyList() }
    }

    private fun loadLanguages(
        onComplete: suspend () -> Unit = {}
    ) {
        loadingJob = componentScope.launch {
            delay(200L)
            if (!isRecognitionTypeSet) {
                _recognitionType.update {
                    RecognitionType.entries[settingsManager.getSettingsState().initialOcrMode]
                }
                isRecognitionTypeSet = true
            }
            val data = imageTextReader.getLanguages(recognitionType)
            _selectedLanguages.update { ocrLanguages ->
                ocrLanguages.toMutableList().also { oldList ->
                    data.forEach { ocrLanguage ->
                        ocrLanguages.indexOfFirst {
                            it.code == ocrLanguage.code
                        }.takeIf { it != -1 }?.let { index ->
                            oldList[index] = ocrLanguage
                        }
                    }
                }.ifEmpty {
                    listOf(OCRLanguage.Default)
                }
            }
            _languages.update { data }
            onComplete()
        }
    }

    init {
        loadLanguages()
        componentScope.launch {
            val languageCodes = settingsManager
                .getSettingsState()
                .initialOcrCodes
                .filter { it.isNotBlank() }
                .map(imageTextReader::getLanguageForCode)
            _selectedLanguages.update {
                languageCodes.ifEmpty { listOf(OCRLanguage.Default) }
            }
        }
    }

    fun getTransformations(): List<CoilTransformation> = filtersOrder.filter {
        it in filtersAdded
    }.map {
        filterProvider.filterToTransformation(it).toCoil()
    }

    fun updateType(
        type: Screen.RecognizeText.Type?,
        onImageSet: () -> Unit
    ) {
        type?.let {
            componentScope.launch {
                _isImageLoading.value = true
                _type.update { type }
                if (type is Screen.RecognizeText.Type.Extraction) {
                    imageGetter.getImage(
                        data = type.uri ?: "",
                        originalSize = false
                    )?.let {
                        updateBitmap(it, onImageSet)
                    }
                }
                _isImageLoading.value = false
            }
        }
    }

    fun save(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit,
    ) {
        recognitionJob = componentScope.launch {
            delay(400)
            _isSaving.update { true }
            when (type) {
                is Screen.RecognizeText.Type.WriteToFile -> {
                    val txtString = StringBuilder()

                    _left.update { uris.size }

                    uris.forEach { uri ->
                        uri.readText().appendToStringBuilder(
                            builder = txtString,
                            uri = uri,
                            onRequestDownload = { data ->
                                _downloadDialogData.update { data.map(DownloadData::toUi) }
                                return@launch
                            }
                        )
                        _done.update { it + 1 }
                    }

                    onResult(
                        listOf(
                            fileController.save(
                                saveTarget = TxtSaveTarget(
                                    txtBytes = txtString.toString().toByteArray()
                                ),
                                keepOriginalMetadata = true,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            ).onSuccess(::registerSave)
                        )
                    )
                }

                is Screen.RecognizeText.Type.WriteToMetadata -> {
                    val results = mutableListOf<SaveResult>()

                    _left.update { uris.size }

                    uris.forEach { uri ->
                        runSuspendCatching {
                            imageGetter.getImage(uri.toString())
                        }.getOrNull()?.let { data ->
                            val txtString = when (val result = data.image.readText()) {
                                is TextRecognitionResult.Error -> {
                                    result.throwable.message ?: ""
                                }

                                is TextRecognitionResult.NoData -> {
                                    _downloadDialogData.update { result.data.map(DownloadData::toUi) }
                                    return@launch
                                }

                                is TextRecognitionResult.Success -> {
                                    result.data.text.ifEmpty { getString(R.string.picture_has_no_text) }
                                }
                            }

                            results.add(
                                fileController.save(
                                    ImageSaveTarget(
                                        imageInfo = data.imageInfo,
                                        originalUri = uri.toString(),
                                        sequenceNumber = null,
                                        metadata = data.metadata?.apply {
                                            setAttribute(
                                                MetadataTag.UserComment,
                                                txtString.takeIf { it.isNotEmpty() }
                                            )
                                        },
                                        data = ByteArray(0),
                                        readFromUriInsteadOfData = true
                                    ),
                                    keepOriginalMetadata = false,
                                    oneTimeSaveLocationUri = oneTimeSaveLocationUri
                                )
                            )
                            _done.update { it + 1 }
                        }
                    }

                    onResult(results.onSuccess(::registerSave))
                }

                else -> return@launch
            }
        }.apply {
            invokeOnCompletion {
                _isSaving.update { false }
            }
        }
    }

    private fun TxtSaveTarget(
        txtBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = "",
        filename = filenameCreator.constructImageFilename(
            ImageSaveTarget(
                imageInfo = ImageInfo(),
                originalUri = "",
                sequenceNumber = null,
                metadata = null,
                data = ByteArray(0),
                extension = "txt"
            ),
            oneTimePrefix = "OCR_images(${uris.size})",
            forceNotAddSizeInFilename = true
        ),
        data = txtBytes,
        mimeType = MimeType.Txt,
        extension = "txt"
    )

    fun removeUri(uri: Uri) {
        when (type) {
            is Screen.RecognizeText.Type.WriteToFile -> {
                updateType(
                    type = Screen.RecognizeText.Type.WriteToFile(uris - uri),
                    onImageSet = {}
                )
            }

            is Screen.RecognizeText.Type.WriteToMetadata -> {
                updateType(
                    type = Screen.RecognizeText.Type.WriteToMetadata(uris - uri),
                    onImageSet = {}
                )
            }

            else -> Unit
        }
    }

    fun updateBitmap(
        bitmap: Bitmap,
        onComplete: () -> Unit = {}
    ) {
        componentScope.launch {
            _isImageLoading.value = true
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            internalBitmap.update { previewBitmap }
            _isImageLoading.value = false
            onComplete()
        }
    }

    private var recognitionJob: Job? by smartJob {
        _textLoadingProgress.update { -1 }
        _isSaving.update { false }
    }

    fun startRecognition(
        onFailure: (Throwable) -> Unit
    ) {
        recognitionJob = componentScope.launch {
            val type = _type.value
            if (type !is Screen.RecognizeText.Type.Extraction) return@launch
            delay(400L)
            _textLoadingProgress.update { 0 }
            (previewBitmap ?: type.uri)?.readText()?.also { result ->
                when (result) {
                    is TextRecognitionResult.Error -> {
                        onFailure(result.throwable)
                    }

                    is TextRecognitionResult.NoData -> {
                        _downloadDialogData.update { result.data.map(DownloadData::toUi) }
                    }

                    is TextRecognitionResult.Success -> {
                        _recognitionData.update { result.data }
                        _editedText.update { text }
                    }
                }
            }
            _textLoadingProgress.update { -1 }
        }
    }

    fun setRecognitionType(recognitionType: RecognitionType) {
        _recognitionType.update { recognitionType }
        componentScope.launch {
            settingsManager.setInitialOcrMode(recognitionType.ordinal)
        }
        loadLanguages()
    }

    private val downloadMutex = Mutex()
    fun downloadTrainData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (DownloadProgress) -> Unit,
        onComplete: () -> Unit
    ) {
        componentScope.launch {
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
                        }.map { it.code }.ifEmpty {
                            listOf(OCRLanguage.Default.code)
                        }
                    )
                }
                onComplete()
            }
        }
    }

    fun onLanguagesSelected(ocrLanguages: List<OCRLanguage>) {
        componentScope.launch {
            settingsManager.setInitialOCRLanguageCodes(
                ocrLanguages.filter {
                    it.downloaded.isNotEmpty() && it.code.isNotBlank()
                }.map { it.code }.ifEmpty {
                    listOf(OCRLanguage.Default.code)
                }
            )
        }
        _selectedLanguages.update {
            ocrLanguages.ifEmpty {
                listOf(OCRLanguage.Default)
            }
        }
        _recognitionData.update { null }
        _editedText.update { null }
        recognitionJob?.cancel()
        _textLoadingProgress.update { -1 }
    }

    fun setSegmentationMode(segmentationMode: SegmentationMode) {
        _segmentationMode.update { segmentationMode }
    }

    fun deleteLanguage(
        language: OCRLanguage,
        types: List<RecognitionType>,
        onSuccess: () -> Unit
    ) {
        componentScope.launch {
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
        _cropProperties.update { properties ->
            properties.copy(
                aspectRatio = aspectRatio.takeIf {
                    domainAspectRatio != DomainAspectRatio.Original
                } ?: _previewBitmap.value?.let {
                    AspectRatio(it.safeAspectRatio)
                } ?: aspectRatio,
                fixedAspectRatio = domainAspectRatio != DomainAspectRatio.Free
            )
        }
        _selectedAspectRatio.update { domainAspectRatio }
    }

    fun setCropMask(cropOutlineProperty: CropOutlineProperty) {
        _cropProperties.update { it.copy(cropOutlineProperty = cropOutlineProperty) }
    }

    suspend fun loadImage(uri: Uri): Bitmap? = imageGetter.getImage(data = uri)

    fun toggleContrastFilter() {
        _filtersAdded.update {
            it.toggle(contrastFilterInstance)
        }
    }

    fun toggleThresholdFilter() {
        _filtersAdded.update {
            it.toggle(thresholdFilterInstance)
        }
    }

    fun toggleSharpnessFilter() {
        _filtersAdded.update {
            it.toggle(sharpenFilterInstance)
        }
    }

    fun setOcrEngineMode(mode: OcrEngineMode) {
        _ocrEngineMode.update { mode }
    }

    fun shareEditedText(
        onComplete: () -> Unit
    ) {
        editedText?.let {
            shareProvider.shareText(
                value = it,
                onComplete = onComplete
            )
        }
    }

    fun updateEditedText(text: String) {
        _editedText.update { text }
    }

    fun shareData(
        onComplete: () -> Unit
    ) {
        recognitionJob = componentScope.launch {
            delay(400)
            _isSaving.update { true }
            when (type) {
                is Screen.RecognizeText.Type.WriteToFile -> {
                    val txtString = StringBuilder()

                    _left.update { uris.size }

                    uris.forEach { uri ->
                        uri.readText().also { result ->
                            result.appendToStringBuilder(
                                builder = txtString,
                                uri = uri,
                                onRequestDownload = { data ->
                                    _downloadDialogData.update { data.map(DownloadData::toUi) }
                                    return@launch
                                }
                            )
                            _done.update { it + 1 }
                        }
                    }

                    val saveTarget = TxtSaveTarget(
                        txtBytes = txtString.toString().toByteArray()
                    )

                    shareProvider.shareByteArray(
                        byteArray = saveTarget.data,
                        filename = saveTarget.filename ?: "",
                        onComplete = onComplete
                    )
                }

                is Screen.RecognizeText.Type.WriteToMetadata -> {
                    val cachedUris = mutableListOf<String>()

                    _left.update { uris.size }

                    uris.forEach { uri ->
                        runSuspendCatching {
                            imageGetter.getImage(uri.toString())
                        }.getOrNull()?.let { data ->
                            data.image.readText().also { result ->
                                val txtString = when (result) {
                                    is TextRecognitionResult.Error -> {
                                        result.throwable.message ?: ""
                                    }

                                    is TextRecognitionResult.NoData -> {
                                        _downloadDialogData.update { result.data.map(DownloadData::toUi) }
                                        return@launch
                                    }

                                    is TextRecognitionResult.Success -> {
                                        result.data.text.ifEmpty { getString(R.string.picture_has_no_text) }
                                    }
                                }

                                val exif = data.metadata?.apply {
                                    setAttribute(
                                        MetadataTag.UserComment,
                                        txtString.takeIf { it.isNotEmpty() }
                                    )
                                }

                                shareProvider.cacheData(
                                    writeData = { w ->
                                        w.writeBytes(
                                            fileController.readBytes(uri.toString())
                                        )
                                    },
                                    filename = filenameCreator.constructImageFilename(
                                        saveTarget = ImageSaveTarget(
                                            imageInfo = data.imageInfo.copy(originalUri = uri.toString()),
                                            originalUri = uri.toString(),
                                            metadata = exif,
                                            sequenceNumber = null,
                                            data = ByteArray(0)
                                        )
                                    )
                                )?.let { uri ->
                                    fileController.writeMetadata(
                                        imageUri = uri,
                                        metadata = exif
                                    )

                                    cachedUris.add(uri)
                                }

                                _done.update { it + 1 }
                            }
                        }
                    }

                    shareProvider.shareUris(cachedUris)
                }

                else -> return@launch
            }
        }.apply {
            invokeOnCompletion {
                _isSaving.update { false }
            }
        }
    }

    private inline fun TextRecognitionResult.appendToStringBuilder(
        builder: StringBuilder,
        uri: Uri,
        onRequestDownload: (List<DownloadData>) -> Unit
    ) {
        when (this) {
            is TextRecognitionResult.Error -> {
                builder.apply {
                    append("${done + 1} - ")
                    append("[${filenameCreator.getFilename(uri.toString())}]")
                    append("\n\n")
                    append(throwable.message)
                    append("\n\n")
                }
            }

            is TextRecognitionResult.NoData -> onRequestDownload(data)

            is TextRecognitionResult.Success -> {
                builder.apply {
                    append("${done + 1} - ")
                    append("[${filenameCreator.getFilename(uri.toString())}]")
                    append(" ")
                    append(getString(R.string.accuracy, data.accuracy))
                    append("\n\n")
                    append(data.text.ifEmpty { getString(R.string.picture_has_no_text) })
                    append("\n\n")
                }
            }
        }
    }

    fun exportLanguagesTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        languagesJob = componentScope.launch {
            _isExporting.value = true
            imageTextReader.exportLanguagesToZip()?.let { zipUri ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(fileController.readBytes(zipUri)) }
                ).also(onResult).onSuccess(::registerSave)
            }
            _isExporting.value = false
        }
    }

    fun generateExportFilename(): String = "image_toolbox_ocr_languages_${timestamp()}.zip"

    fun generateTextFilename(): String = "OCR_${timestamp()}.txt"

    fun importLanguagesFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        languagesJob = componentScope.launch {
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

    fun saveContentToTxt(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        recognitionData?.text?.takeIf { it.isNotEmpty() }?.let { data ->
            componentScope.launch {
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = {
                        it.writeBytes(data.encodeToByteArray())
                    }
                ).also(onResult).onSuccess(::registerSave)
            }
        }
    }

    fun updateParams(newParams: TessParams) {
        _params.update { newParams }
    }

    fun cancelSaving() {
        recognitionJob?.cancel()
        recognitionJob = null
        _isSaving.update { false }
    }

    private suspend fun Any.readText(): TextRecognitionResult {
        return imageTextReader.getTextFromImage(
            type = recognitionType,
            languageCode = selectedLanguages.joinToString("+") { it.code },
            segmentationMode = segmentationMode,
            model = imageGetter.getImage(this)?.let { bitmap ->
                imageTransformer.transform(
                    transformations = getTransformations().map(CoilTransformation::asDomain),
                    image = bitmap
                )
            },
            parameters = params,
            ocrEngineMode = ocrEngineMode,
            onProgress = { progress ->
                _textLoadingProgress.update { progress }
            }
        ).also {
            _textLoadingProgress.update { -1 }
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.RecognizeText.Type?,
            onGoBack: () -> Unit,
        ): RecognizeTextComponent
    }

}