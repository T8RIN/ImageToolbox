package ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.recognize.text.domain.DownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.ImageTextReader
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.domain.SegmentationMode
import ru.tech.imageresizershrinker.feature.recognize.text.domain.TextRecognitionResult
import javax.inject.Inject


@HiltViewModel
class RecognizeTextViewModel @Inject constructor(
    private val imageTextReader: ImageTextReader<Bitmap>,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _segmentationMode: MutableState<SegmentationMode> =
        mutableStateOf(SegmentationMode.PSM_AUTO_OSD)
    val segmentationMode by _segmentationMode

    private val _selectedLanguages = mutableStateOf(listOf(OCRLanguage.Default))
    val selectedLanguages by _selectedLanguages

    private val _recognitionType = mutableStateOf<RecognitionType>(RecognitionType.Standard)
    val recognitionType by _recognitionType

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _recognitionData = mutableStateOf<RecognitionData?>(null)
    val recognitionData by _recognitionData

    private val _textLoadingProgress: MutableState<Int> = mutableIntStateOf(-1)
    val textLoadingProgress by _textLoadingProgress

    private val _languages: MutableState<List<OCRLanguage>> = mutableStateOf(emptyList())
    val languages by _languages

    val isTextLoading: Boolean
        get() = textLoadingProgress in 0..100

    private var longsJob: Job? = null
    private fun loadLanguages() {
        longsJob?.cancel()
        longsJob = viewModelScope.launch {
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
        }
    }

    init {
        loadLanguages()
        viewModelScope.launch {
            val languageCodes = settingsRepository.getInitialOCRLanguageCodes().map {
                imageTextReader.getLanguageForCode(it)
            }
            _selectedLanguages.update { languageCodes }
        }
    }

    fun updateUri(uri: Uri?) {
        _uri.update { uri }
    }

    private var job: Job? = null

    fun startRecognition(
        onError: (Throwable) -> Unit,
        onRequestDownload: (List<DownloadData>) -> Unit
    ) {
        _textLoadingProgress.update { -1 }
        job?.cancel()
        job = viewModelScope.launch {
            if (uri == null) return@launch
            delay(400L)
            _textLoadingProgress.update { 0 }
            imageTextReader.getTextFromImage(
                type = recognitionType,
                languageCode = selectedLanguages.joinToString("+") { it.code },
                segmentationMode = segmentationMode,
                imageUri = uri.toString(),
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
                loadLanguages()
                onComplete()
            }
        }
    }

    fun onLanguagesSelected(ocrLanguages: List<OCRLanguage>) {
        if (ocrLanguages.isNotEmpty()) {
            viewModelScope.launch {
                settingsRepository.setInitialOCRLanguageCodes(
                    ocrLanguages.map { it.code }
                )
            }
            _selectedLanguages.update { ocrLanguages }
            _recognitionData.update { null }
            job?.cancel()
            _textLoadingProgress.update { -1 }
        }
    }

    fun setSegmentationMode(segmentationMode: SegmentationMode) {
        _segmentationMode.update { segmentationMode }
    }

}