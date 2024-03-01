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

package ru.tech.imageresizershrinker.feature.recognize.text.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.util.fastAll
import androidx.core.text.HtmlCompat
import androidx.exifinterface.media.ExifInterface
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.feature.recognize.text.domain.Constants
import ru.tech.imageresizershrinker.feature.recognize.text.domain.DownloadData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.ImageTextReader
import ru.tech.imageresizershrinker.feature.recognize.text.domain.OCRLanguage
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionData
import ru.tech.imageresizershrinker.feature.recognize.text.domain.RecognitionType
import ru.tech.imageresizershrinker.feature.recognize.text.domain.SegmentationMode
import ru.tech.imageresizershrinker.feature.recognize.text.domain.TextRecognitionResult
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.String.format
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import javax.inject.Inject

internal class AndroidImageTextReader @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    @ApplicationContext private val context: Context
) : ImageTextReader<Bitmap> {

    init {
        RecognitionType.entries.forEach {
            File(context.filesDir, "${it.displayName}/tessdata").mkdirs()
        }
    }

    override suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        imageUri: String,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult = getTextFromImage(
        type = type,
        languageCode = languageCode,
        segmentationMode = segmentationMode,
        image = imageGetter.getImage(imageUri)?.image,
        onProgress = onProgress
    )

    override suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        image: Bitmap?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult = withContext(Dispatchers.IO) {

        if (image == null) return@withContext TextRecognitionResult.Success(RecognitionData("", 0))

        val needToDownload = getNeedToDownloadLanguages(type, languageCode)

        if (needToDownload.isNotEmpty()) {
            return@withContext TextRecognitionResult.NoData(needToDownload)
        }

        val path = getPathFromMode(type)

        val api = TessBaseAPI {
            if (isActive) onProgress(it.percent)
        }.apply {
            val success = init(path, languageCode)
            if (!success) {
                return@withContext TextRecognitionResult.NoData(
                    getNeedToDownloadLanguages(
                        type = type,
                        languageCode = languageCode
                    )
                ).also {
                    it.data.forEach { data ->
                        File(
                            "${getPathFromMode(type)}/tessdata",
                            format(Constants.LANGUAGE_CODE, data.languageCode)
                        ).delete()
                    }
                }
            }
            pageSegMode = segmentationMode.ordinal
            setImage(image)
        }
        return@withContext runCatching {
            val text = HtmlCompat.fromHtml(
                api.getHOCRText(1),
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            ).toString().trim()

            val accuracy = api.meanConfidence()

            TextRecognitionResult.Success(
                RecognitionData(
                    text = text,
                    accuracy = if (text.isEmpty()) 0 else accuracy
                )
            )
        }.let {
            if (it.isSuccess) {
                it.getOrNull()!!
            } else {
                languageCode.split("+").forEach { code ->
                    File(
                        path,
                        format(Constants.LANGUAGE_CODE, code)
                    ).delete()
                }

                TextRecognitionResult.Error(it.exceptionOrNull()!!)
            }
        }
    }

    private fun getNeedToDownloadLanguages(
        type: RecognitionType,
        languageCode: String
    ): List<DownloadData> {
        val needToDownload = mutableListOf<DownloadData>()
        languageCode.split("+").forEach { code ->
            if (!isLanguageDataExists(type, code)) {
                needToDownload.add(
                    DownloadData(
                        type = type,
                        languageCode = code,
                        name = getDisplayName(code)
                    )
                )
            }
        }
        return needToDownload
    }

    override fun isLanguageDataExists(
        type: RecognitionType,
        languageCode: String
    ): Boolean {
        return File(
            "${getPathFromMode(type)}/tessdata",
            format(Constants.LANGUAGE_CODE, languageCode)
        ).exists()
    }

    override suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage> = withContext(Dispatchers.IO) {

        val codes = context.resources.getStringArray(R.array.key_ocr_engine_language_value)

        return@withContext codes.mapNotNull { code ->
            val name = getDisplayName(code)
            if (name.isEmpty()) return@mapNotNull null

            OCRLanguage(
                name = name,
                code = code,
                downloaded = RecognitionType.entries.filter {
                    isLanguageDataExists(it, code)
                }
            )
        }.toList()
    }

    override fun getLanguageForCode(
        code: String
    ): OCRLanguage = OCRLanguage(
        name = getDisplayName(code),
        code = code,
        downloaded = RecognitionType.entries.filter {
            isLanguageDataExists(it, code)
        }
    )

    override suspend fun deleteLanguage(
        language: OCRLanguage,
        types: List<RecognitionType>
    ) {
        types.forEach { type ->
            File(
                "${getPathFromMode(type)}/tessdata",
                format(Constants.LANGUAGE_CODE, language.code)
            ).delete()
        }
    }

    override suspend fun downloadTrainingData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (Float, Long) -> Unit
    ): Boolean {
        val needToDownloadLanguages = getNeedToDownloadLanguages(type, languageCode)

        return if (needToDownloadLanguages.isNotEmpty()) {
            downloadTrainingDataImpl(type, needToDownloadLanguages, onProgress)
        } else false
    }

    private suspend fun downloadTrainingDataImpl(
        type: RecognitionType,
        needToDownloadLanguages: List<DownloadData>,
        onProgress: (Float, Long) -> Unit
    ): Boolean = needToDownloadLanguages.map {
        downloadTrainingDataForCode(type, it.languageCode, onProgress)
    }.fastAll { it }

    private suspend fun downloadTrainingDataForCode(
        type: RecognitionType,
        lang: String,
        onProgress: (Float, Long) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        var location: String
        var downloadURL = when (type) {
            RecognitionType.Best -> format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang)
            RecognitionType.Standard -> format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang)
            RecognitionType.Fast -> format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang)
        }
        var url: URL
        var base: URL
        var next: URL
        var conn: HttpURLConnection
        return@withContext runCatching {
            while (true) {
                url = URL(downloadURL)
                conn = url.openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = false

                when (conn.responseCode) {
                    HttpURLConnection.HTTP_MOVED_PERM,
                    HttpURLConnection.HTTP_MOVED_TEMP -> {
                        location = conn.getHeaderField("Location")
                        base = URL(downloadURL)
                        next = URL(base, location)
                        downloadURL = next.toExternalForm()
                        continue
                    }
                }
                break
            }
            conn.connect()

            val totalContentSize = conn.contentLength.toLong()
            onProgress(0f, totalContentSize)

            val input: InputStream = BufferedInputStream(url.openStream())
            val output: OutputStream = FileOutputStream(
                File(
                    "${getPathFromMode(type)}/tessdata",
                    format(Constants.LANGUAGE_CODE, lang)
                ).apply {
                    createNewFile()
                }
            )
            val data = ByteArray(1024 * 8)
            var count: Int
            var downloaded = 0
            while (input.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
                downloaded += count
                val percentage = downloaded * 100f / totalContentSize
                onProgress(percentage, totalContentSize)
            }

            output.flush()
            output.close()
            input.close()
        }.isSuccess
    }

    private fun getPathFromMode(
        type: RecognitionType
    ): String = File(context.filesDir, type.displayName).absolutePath

    private fun getDisplayName(lang: String?): String {
        if (lang.isNullOrEmpty()) {
            return ""
        }

        val locale = Locale.forLanguageTag(
            if (lang.contains("chi_sim")) "zh-CN"
            else if (lang.contains("chi_tra")) "zh-TW"
            else lang
        )
        return locale.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }
}