package ru.tech.imageresizershrinker.feature.recognize.text.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.text.HtmlCompat
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.feature.recognize.text.domain.Constants
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
import javax.inject.Inject

internal class AndroidImageTextReader @Inject constructor(
    private val imageManager: ImageManager<Bitmap, *>,
    @ApplicationContext private val context: Context
) : ImageTextReader<Bitmap> {

    init {
        RecognitionType.entries.forEach {
            File(context.filesDir, "${it.name}/tessdata").mkdirs()
        }
    }

    override suspend fun getTextFromImage(
        type: RecognitionType,
        language: String,
        segmentationMode: SegmentationMode,
        imageUri: String,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult = getTextFromImage(
        type = type,
        language = language,
        segmentationMode = segmentationMode,
        image = imageManager.getImage(imageUri)?.image,
        onProgress = onProgress
    )

    override suspend fun getTextFromImage(
        type: RecognitionType,
        language: String,
        segmentationMode: SegmentationMode,
        image: Bitmap?,
        onProgress: (Int) -> Unit
    ): TextRecognitionResult = withContext(Dispatchers.IO) {

        if (!isLanguageDataExists(type, language)) {
            return@withContext TextRecognitionResult.NoData(type, language).also {
                downloadTrainingData(it.type, it.language) { _, _ -> }
            }
        }

        val path = getPathFromMode(type)

        val api = TessBaseAPI {
            onProgress(it.percent)
        }.apply {
            init(path, language)
            pageSegMode = segmentationMode.ordinal
            setImage(image)
        }
        return@withContext try {

            val text = HtmlCompat.fromHtml(
                api.getHOCRText(1),
                HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            ).toString().trim()

            val accuracy = api.meanConfidence()

            TextRecognitionResult.Success(RecognitionData(text, accuracy))
        } catch (t: Throwable) {
            File(
                path,
                format(Constants.LANGUAGE_CODE, language)
            ).delete()

            TextRecognitionResult.Error(t)
        }
    }

    override fun isLanguageDataExists(type: RecognitionType, language: String): Boolean {
        return File(
            "${getPathFromMode(type)}/tessdata",
            format(Constants.LANGUAGE_CODE, language)
        ).exists()
    }

    override suspend fun getLanguages(
        type: RecognitionType
    ): List<OCRLanguage> = withContext(Dispatchers.IO) {
        val names = context.resources.getStringArray(R.array.ocr_engine_language)
        val codes = context.resources.getStringArray(R.array.key_ocr_engine_language_value)

        return@withContext names.mapIndexed { index, name ->
            OCRLanguage(
                name = name,
                code = codes[index],
                downloaded = isLanguageDataExists(type, codes[index])
            )
        }.toList()
    }

    override suspend fun downloadTrainingData(
        type: RecognitionType,
        language: String,
        onProgress: (Float, Long) -> Unit
    ): Boolean {
        return if (!isLanguageDataExists(type, language)) {
            downloadTrainingDataImpl(type, language, onProgress)
        } else false
    }

    private suspend fun downloadTrainingDataImpl(
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
    ): String = File(context.filesDir, type.name).absolutePath

}