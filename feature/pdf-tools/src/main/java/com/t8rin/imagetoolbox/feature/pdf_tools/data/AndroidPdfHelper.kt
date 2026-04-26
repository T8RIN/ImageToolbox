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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.awxkee.aire.ScaleColorSpace
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.observeHasChanges
import com.t8rin.imagetoolbox.core.domain.PDF
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.HocrData
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.HocrPageBox
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.HocrWord
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.PdfRenderer
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.asXObject
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPage
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.pageIndices
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.safeOpenPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.save
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfHelper
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageSize
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCheckResult
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCreationParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PrintPdfParams
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

internal class AndroidPdfHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScope: AppScope,
    private val shareProvider: ShareProvider,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : PdfHelper, DispatchersHolder by dispatchersHolder {

    companion object {
        private const val SIGNATURES_LIMIT = 20

        val HOCR_PAGE_REGEX = Regex(
            pattern = "<(?:div|span)[^>]*class=[\"'][^\"']*ocr_page[^\"']*[\"'][^>]*title=[\"'][^\"']*bbox\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)[^\"']*[\"'][^>]*>",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        )
        val HOCR_WORD_REGEX = Regex(
            pattern = "<span[^>]*class=[\"'][^\"']*ocrx_word[^\"']*[\"'][^>]*title=[\"'][^\"']*bbox\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)[^\"']*[\"'][^>]*>(.*?)</span>",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        )
        val HOCR_TAG_REGEX = Regex("<[^>]+>")
        val PDF_CONTROL_REGEX = Regex("[\\p{Cntrl}&&[^\\n\\r\\t]]")
    }

    private val pagesCache = hashMapOf<String, List<IntegerSize>>()

    private val signaturesDir: File get() = File(context.filesDir, "signatures").apply(File::mkdirs)

    private val updateFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    private var _masterPassword: String? = null
    val masterPassword: String? get() = _masterPassword

    override val savedSignatures: StateFlow<List<String>> =
        merge(
            updateFlow,
            signaturesDir.observeHasChanges().debounce(100)
        ).map {
            signaturesDir
                .listFiles()
                .orEmpty()
                .sortedByDescending { it.lastModified() }
                .map { it.toUri().toString() }
        }.stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override suspend fun saveSignature(signature: Any): Boolean {
        return runSuspendCatching {
            val currentSignatures = savedSignatures.value

            if (currentSignatures.size + 1 > SIGNATURES_LIMIT) {
                currentSignatures.last().toUri().toFile().delete()
            }

            File(signaturesDir, "signature_${System.currentTimeMillis()}.png")
                .outputStream()
                .use { out ->
                    imageGetter.getImage(signature)?.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        out
                    )
                }
            updateFlow.emit(Unit)
        }.onFailure {
            it.makeLog("saveSignature")
        }.isSuccess
    }

    override fun setMasterPassword(password: String?) {
        _masterPassword = password
    }

    override fun createTempName(key: String, uri: String?): String = tempName(
        key = key,
        uri = uri
    ).removePrefix(PDF)

    override fun clearPdfCache(uri: String?) {
        appScope.launch {
            runCatching {
                if (uri.isNullOrBlank()) {
                    File(context.cacheDir, "pdf").deleteRecursively()
                } else {
                    context.contentResolver.delete(uri.toUri(), null, null)
                }
            }.onFailure {
                "failed to delete $uri".makeLog("delete")
            }
        }
    }

    override suspend fun checkPdf(uri: String): PdfCheckResult = withContext(defaultDispatcher) {
        try {
            usePdf(uri) { document ->
                if (masterPassword.isNullOrBlank()) {
                    PdfCheckResult.Open
                } else {
                    PdfCheckResult.Protected.Unlocked(
                        document.save(
                            filename = uri.toUri().filename()?.let { "$PDF$it" } ?: tempName(
                                key = "unlocked",
                                uri = uri
                            )
                        )
                    )
                }
            }
        } catch (_: InvalidPasswordException) {
            PdfCheckResult.Protected.NeedsPassword
        } catch (t: Throwable) {
            ensureActive()
            PdfCheckResult.Failure(t)
        }.makeLog("checkPdf")
    }

    override suspend fun getPdfPages(
        uri: String
    ): List<Int> = withContext(decodingDispatcher) {
        try {
            usePdf(
                uri = uri,
                password = masterPassword,
                action = PDDocument::pageIndices
            )
        } catch (_: Throwable) {
            emptyList()
        }
    }

    override suspend fun getPdfPageSizes(
        uri: String
    ): List<IntegerSize> = withContext(decodingDispatcher) {
        pagesCache[uri]?.takeIf { it.isNotEmpty() }?.let { return@withContext it }

        try {
            PdfRenderer(
                uri = uri,
                password = masterPassword
            )?.use { renderer ->
                List(renderer.pageCount) {
                    renderer.openPage(it).run {
                        IntegerSize(width, height)
                    }
                }
            }.orEmpty()
        } catch (_: Throwable) {
            emptyList()
        }.also { pagesCache[uri] = it }
    }

    internal fun tempName(
        key: String,
        uri: String? = null
    ): String {
        val keyFixed = if (key.isBlank()) "_" else "_${key}_"

        return PDF + (uri?.toUri()?.filename()?.substringBeforeLast('.')?.let {
            "${it}${keyFixed.removeSuffix("_")}.pdf"
        } ?: "PDF$keyFixed${timestamp()}_${
            Random(Random.nextInt()).hashCode().toString().take(4)
        }.pdf")
    }

    internal inline fun <T> usePdf(
        uri: String,
        password: String? = masterPassword,
        action: (PDDocument) -> T
    ): T = openPdf(
        uri = uri,
        password = password
    ).use(action)

    internal fun openPdf(
        uri: String,
        password: String? = masterPassword
    ): PDDocument = safeOpenPdf(
        uri = uri,
        password = password
    )

    internal inline fun <T> useRenderer(
        uri: String,
        password: String? = masterPassword,
        action: (PdfRenderer) -> T
    ) = PdfRenderer(
        uri = uri,
        password = password,
        onFailure = { throw it }
    )?.use(action)

    internal suspend fun PDDocument.save(
        filename: String,
        password: String? = null,
        metadata: PdfMetadata? = PdfMetadata.Empty
    ): String {
        if (metadata != PdfMetadata.Empty) {
            setMetadata(metadata)
        }

        return shareProvider.cacheDataOrThrow(
            filename = filename,
            writeData = {
                save(
                    writeable = it,
                    password = password
                )
            }
        )
    }

    internal fun PrintPdfParams.calculatePageSize(
        firstPageOnSheet: PDPage
    ) = pageSizeFinal
        ?: copy(
            pageSize = firstPageOnSheet.cropBox.run {
                PageSize(
                    width = width.roundToInt(),
                    height = height.roundToInt(),
                    name = "Auto"
                )
            }
        ).pageSizeFinal

    internal fun parseHocrData(hocr: String): HocrData {
        val pageBox = HOCR_PAGE_REGEX.find(hocr)?.let { match ->
            val left = match.groupValues.getOrNull(1)?.toFloatOrNull() ?: return@let null
            val top = match.groupValues.getOrNull(2)?.toFloatOrNull() ?: return@let null
            val right = match.groupValues.getOrNull(3)?.toFloatOrNull() ?: return@let null
            val bottom = match.groupValues.getOrNull(4)?.toFloatOrNull() ?: return@let null
            HocrPageBox(
                width = (right - left).coerceAtLeast(1f),
                height = (bottom - top).coerceAtLeast(1f)
            )
        }

        val words = HOCR_WORD_REGEX
            .findAll(hocr)
            .mapNotNull { match ->
                val left = match.groupValues.getOrNull(1)?.toFloatOrNull() ?: return@mapNotNull null
                val top = match.groupValues.getOrNull(2)?.toFloatOrNull() ?: return@mapNotNull null
                val right =
                    match.groupValues.getOrNull(3)?.toFloatOrNull() ?: return@mapNotNull null
                val bottom =
                    match.groupValues.getOrNull(4)?.toFloatOrNull() ?: return@mapNotNull null
                val rawText = match.groupValues.getOrNull(5).orEmpty()
                val text = HtmlCompat
                    .fromHtml(rawText.replace(HOCR_TAG_REGEX, ""), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .toString()
                    .trim()
                    .takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null

                HocrWord(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    text = text
                )
            }
            .toList()

        return HocrData(
            pageBox = pageBox,
            words = words
        )
    }

    internal fun String.cleanPdfText(): String = replace(PDF_CONTROL_REGEX, "").take(2000)

    internal suspend fun prepareImagesForPdf(
        imageUris: List<String>,
        params: PdfCreationParams
    ): List<Bitmap> {
        val scale = params.preset.value / 100f
        return imageUris.mapNotNull { uri ->
            imageGetter.getImage(data = uri)?.let {
                imageScaler.scaleImage(
                    image = it,
                    width = (it.width * scale).roundToInt(),
                    height = (it.height * scale).roundToInt(),
                    resizeType = ResizeType.Flexible
                )
            }
        }
    }

    internal suspend fun createPdfFromPreparedImages(
        images: List<Bitmap>,
        quality: Float,
        scaleSmallImagesToLarge: Boolean,
        addTextLayer: (PDPageContentStream.(pageIndex: Int, pageWidth: Float, pageHeight: Float, document: PDDocument) -> Unit)?
    ): String {
        if (images.isEmpty()) error("No PDF created")

        return createPdf { newDoc ->
            var h = 0
            var maxWidth = 0

            images.forEach {
                maxWidth = max(maxWidth, it.width)
            }

            for (image in images) {
                h += if (scaleSmallImagesToLarge && image.width != maxWidth) {
                    (maxWidth / image.aspectRatio).toInt().coerceAtLeast(1)
                } else {
                    image.height.coerceAtLeast(1)
                }
            }

            val size = IntegerSize(maxWidth, h)

            images.forEachIndexed { index, image ->
                val bitmap = if (scaleSmallImagesToLarge && image.width != size.width) {
                    Aire.scale(
                        bitmap = image,
                        dstWidth = size.width,
                        dstHeight = (size.width / image.aspectRatio).toInt(),
                        scaleMode = ResizeFunction.Bicubic,
                        colorSpace = ScaleColorSpace.SRGB
                    )
                } else image

                val pageHeight = bitmap.height.toFloat()
                val pageWidth = bitmap.width.toFloat()
                newDoc.createPage(
                    PDPage(
                        PDRectangle(
                            pageWidth,
                            pageHeight
                        )
                    )
                ) {
                    drawImage(
                        bitmap.asXObject(newDoc, quality),
                        0f,
                        0f,
                        pageWidth,
                        pageHeight
                    )
                    addTextLayer?.invoke(this, index, pageWidth, pageHeight, newDoc)
                }
            }

            shareProvider.cacheData(
                writeData = newDoc::save,
                filename = createTempName(key = "")
            ) ?: error("No PDF created")
        }
    }

}