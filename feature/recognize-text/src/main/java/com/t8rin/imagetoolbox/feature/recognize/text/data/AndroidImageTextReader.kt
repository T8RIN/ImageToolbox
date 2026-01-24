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

package com.t8rin.imagetoolbox.feature.recognize.text.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import com.googlecode.tesseract.android.TessBaseAPI
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.recognize.text.domain.DownloadData
import com.t8rin.imagetoolbox.feature.recognize.text.domain.ImageTextReader
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OCRLanguage
import com.t8rin.imagetoolbox.feature.recognize.text.domain.OcrEngineMode
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionData
import com.t8rin.imagetoolbox.feature.recognize.text.domain.RecognitionType
import com.t8rin.imagetoolbox.feature.recognize.text.domain.SegmentationMode
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TessConstants
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TessParams
import com.t8rin.imagetoolbox.feature.recognize.text.domain.TextRecognitionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.String.format
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

internal class AndroidImageTextReader @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    private val shareProvider: ShareProvider,
    private val keepAliveService: KeepAliveService,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder,
    appScope: AppScope,
) : ImageTextReader,
    DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager {

    init {
        appScope.launch {
            RecognitionType.entries.forEach {
                File(context.filesDir, "${it.displayName}/tessdata").mkdirs()
            }
        }
    }

    override suspend fun getTextFromImage(
        type: RecognitionType,
        languageCode: String,
        segmentationMode: SegmentationMode,
        ocrEngineMode: OcrEngineMode,
        parameters: TessParams,
        model: Any?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult = withContext(defaultDispatcher) {
        val empty = TextRecognitionResult.Success(RecognitionData("", 0))

        if (model == null) return@withContext empty

        val image = model as? Bitmap ?: (imageGetter.getImage(model) ?: return@withContext empty)

        val needToDownload = getNeedToDownloadLanguages(type, languageCode)

        if (needToDownload.isNotEmpty()) {
            return@withContext TextRecognitionResult.NoData(needToDownload)
        }

        return@withContext runCatching {
            val api = TessBaseAPI {
                if (isActive) onProgress(it.percent)
                else return@TessBaseAPI
            }.apply {
                val success = init(
                    getPathFromMode(type),
                    languageCode,
                    ocrEngineMode.ordinal
                )
                if (!success) {
                    return@withContext TextRecognitionResult.NoData(
                        getNeedToDownloadLanguages(
                            type = type,
                            languageCode = languageCode
                        )
                    ).also {
                        it.data.forEach { data ->
                            getModelFile(
                                type = type,
                                languageCode = data.languageCode
                            ).delete()
                        }
                    }
                }
                pageSegMode = segmentationMode.ordinal

                parameters.tessParamList.forEach { param ->
                    setVariable(param.key, param.stringValue)
                }
                runCatching {
                    parameters.tessCustomParams.trim().removePrefix("--").split("--").forEach { s ->
                        val (key, value) = s.trim().split(" ").filter { it.isNotEmpty() }
                        setVariable(key, value)
                    }
                }

                setImage(image.copy(Bitmap.Config.ARGB_8888, false))
            }

            api.getHOCRText(0)

            val text = api.utF8Text

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
                    getModelFile(
                        type = type,
                        languageCode = code
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
                        name = getDisplayName(code, false),
                        localizedName = getDisplayName(code, true)
                    )
                )
            }
        }
        return needToDownload
    }

    override fun isLanguageDataExists(
        type: RecognitionType,
        languageCode: String
    ): Boolean = getModelFile(
        type = type,
        languageCode = languageCode
    ).exists()

    override suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage> = withContext(ioDispatcher) {
        val codes = context.resources.getStringArray(R.array.key_ocr_engine_language_value)

        return@withContext codes.mapNotNull { code ->
            val name = getDisplayName(code, false)
            val localizedName = getDisplayName(code, true)
            if (name.isBlank() || localizedName.isBlank()) return@mapNotNull null

            OCRLanguage(
                name = name,
                code = code,
                downloaded = RecognitionType.entries.filter {
                    isLanguageDataExists(
                        type = it,
                        languageCode = code
                    )
                },
                localizedName = localizedName
            )
        }.toList()
    }

    override fun getLanguageForCode(
        code: String
    ): OCRLanguage = OCRLanguage(
        name = getDisplayName(code, false),
        code = code,
        downloaded = RecognitionType.entries.filter {
            isLanguageDataExists(it, code)
        },
        localizedName = getDisplayName(code, true)
    )

    override suspend fun deleteLanguage(
        language: OCRLanguage,
        types: List<RecognitionType>
    ) = withContext(ioDispatcher) {
        types.forEach { type ->
            getModelFile(
                type = type,
                languageCode = language.code
            ).delete()
        }
    }

    override suspend fun downloadTrainingData(
        type: RecognitionType,
        languageCode: String,
        onProgress: (Float, Long) -> Unit
    ) {
        val needToDownloadLanguages = getNeedToDownloadLanguages(type, languageCode)

        if (needToDownloadLanguages.isNotEmpty()) {
            downloadTrainingDataImpl(
                type = type,
                needToDownloadLanguages = needToDownloadLanguages,
                onProgress = onProgress
            )
        }
    }

    private suspend fun downloadTrainingDataImpl(
        type: RecognitionType,
        needToDownloadLanguages: List<DownloadData>,
        onProgress: (Float, Long) -> Unit
    ) = needToDownloadLanguages.map {
        downloadTrainingDataForCode(
            type = type,
            lang = it.languageCode,
            onProgress = onProgress
        )
    }

    private suspend fun downloadTrainingDataForCode(
        type: RecognitionType,
        lang: String,
        onProgress: (Float, Long) -> Unit
    ) = withContext(defaultDispatcher) {
        keepAliveService.track(
            initial = {
                updateOrStart(
                    title = getString(R.string.downloading)
                )
            }
        ) {
            val url = when (type) {
                RecognitionType.Best -> TessConstants.TESSERACT_DATA_DOWNLOAD_URL_BEST
                RecognitionType.Standard -> TessConstants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD
                RecognitionType.Fast -> TessConstants.TESSERACT_DATA_DOWNLOAD_URL_FAST
            }.format(lang)

            channelFlow {
                client.prepareGet(url).execute { response ->
                    val total = response.contentLength() ?: -1L

                    trySend(0f to total)

                    ensureActive()

                    val modelFile = getModelFile(
                        type = type,
                        languageCode = lang
                    ).apply(File::createNewFile)

                    val tmp = File(modelFile.parentFile, modelFile.name + ".tmp")

                    val channel = response.bodyAsChannel()
                    var downloaded = 0L

                    FileOutputStream(tmp).use { fos ->
                        try {
                            while (!channel.isClosedForRead) {
                                ensureActive()
                                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                                while (!packet.exhausted()) {
                                    ensureActive()
                                    val bytes = packet.readByteArray()
                                    downloaded += bytes.size
                                    fos.write(bytes)
                                    trySend((if (total > 0) downloaded.toFloat() / total else 0f) to total)
                                }
                            }

                            tmp.renameTo(modelFile)

                            close()
                        } catch (e: Throwable) {
                            tmp.delete()
                            close(e)
                        }
                    }
                }
            }.onEach { onProgress(it.first, it.second) }
                .throttleLatest(50)
                .collect {
                    updateProgress(
                        title = getString(R.string.downloading),
                        done = (it.first * 100).roundToInt(),
                        total = 100
                    )
                }
        }
    }

    private fun getPathFromMode(
        type: RecognitionType
    ): String = File(context.filesDir, type.displayName).absolutePath

    private fun getModelFile(
        type: RecognitionType,
        languageCode: String
    ) = File(
        "${getPathFromMode(type)}/tessdata",
        format(TessConstants.LANGUAGE_CODE, languageCode)
    )

    private fun getDisplayName(
        lang: String?,
        useDefaultLocale: Boolean
    ): String {
        if (lang.isNullOrEmpty()) {
            return ""
        }

        val locale = Locale.forLanguageTag(
            if (lang.contains("chi_sim")) "zh-CN"
            else if (lang.contains("chi_tra")) "zh-TW"
            else lang
        )
        return locale.getDisplayName(
            if (useDefaultLocale) Locale.getDefault()
            else locale
        ).replaceFirstChar { it.uppercase(locale) }
    }

    override suspend fun exportLanguagesToZip(): String? = withContext(ioDispatcher) {
        val out = ByteArrayOutputStream()

        ZipOutputStream(out).use { zipOut ->
            RecognitionType.entries.forEach { type ->
                val dir = File(context.filesDir, "${type.displayName}/tessdata")
                dir.listFiles()?.forEach { file ->
                    FileInputStream(file).use { fis ->
                        val zipEntry = ZipEntry("${type.displayName}/tessdata/${file.name}")
                        zipOut.putNextEntry(zipEntry)
                        fis.copyTo(zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
        }

        shareProvider.cacheByteArray(
            byteArray = out.toByteArray(),
            filename = "exported_languages.zip"
        )
    }

    override suspend fun importLanguagesFromUri(
        zipUri: String
    ): Result<Any> = withContext(ioDispatcher) {
        val zipInput = context.contentResolver.openInputStream(
            zipUri.toUri()
        ) ?: return@withContext Result.failure(NullPointerException())

        runCatching {
            zipInput.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry: ZipEntry?
                    while (zipIn.nextEntry.also { entry = it } != null) {
                        entry?.let { zipEntry ->
                            val outFile = File(context.filesDir, zipEntry.name)
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { fos ->
                                zipIn.copyTo(fos)
                            }
                            zipIn.closeEntry()
                        }
                    }
                }
            }
        }
    }
}