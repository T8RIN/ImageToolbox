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
                    ocrLanguages.filter {
                        it.downloaded.isNotEmpty()
                    }.map { it.code }
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

}