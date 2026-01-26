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

package com.t8rin.imagetoolbox.feature.recognize.text.domain

import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress

interface ImageTextReader {

    suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        ocrEngineMode: OcrEngineMode,
        parameters: TessParams,
        model: Any?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult

    suspend fun downloadTrainingData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (DownloadProgress) -> Unit
    )

    fun isLanguageDataExists(
        type: RecognitionType,
        languageCode: String
    ): Boolean

    suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage>

    fun getLanguageForCode(
        code: String
    ): OCRLanguage

    suspend fun deleteLanguage(
        language: OCRLanguage,
        types: List<RecognitionType>
    )

    suspend fun exportLanguagesToZip(): String?

    suspend fun importLanguagesFromUri(
        zipUri: String
    ): Result<Any>

}