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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.awxkee.aire.ScaleColorSpace
import com.t8rin.imagetoolbox.core.data.saving.io.ByteArrayReadable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.data.utils.observeHasChanges
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.PDF
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.domain.model.RectModel
import com.t8rin.imagetoolbox.core.domain.utils.applyUse
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.asXObject
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.baseFont
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPage
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPdfRenderer
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getAllImages
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getPageSafe
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.metadata
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.safeOpenPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.save
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setAlpha
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setColor
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.writePage
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfToImagesAction
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfWatermarkParams
import com.t8rin.logger.makeLog
import com.t8rin.trickle.Trickle
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val appScope: AppScope,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PdfManager {

    private val signaturesDir: File get() = File(context.filesDir, "signatures").apply(File::mkdirs)

    private val updateFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    private val pagesCache = hashMapOf<String, List<IntegerSize>>()

    private var masterPassword: String? = null

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

    override fun setMasterPassword(password: String?) {
        masterPassword = password
    }

    override suspend fun checkIsPdfEncrypted(uri: String): String? = catchPdf {
        openPdf(uri).use { document ->
            if (masterPassword.isNullOrBlank()) {
                null
            } else {
                document.save(
                    filename = uri.toUri().filename()?.let { "$PDF$it" } ?: tempName(
                        key = "unlocked",
                        uri = uri
                    )
                )
            }
        }
    }

    override suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        scaleSmallImagesToLarge: Boolean,
        preset: Preset.Percentage,
        tempFilename: String,
        quality: Int
    ): String = withContext(encodingDispatcher) {
        createPdf { newDoc ->
            var h = 0
            var maxHeight = 0
            var maxWidth = 0
            val images = imageUris.mapNotNull { uri ->
                imageGetter.getImage(data = uri)?.let {
                    imageScaler.scaleImage(
                        image = it,
                        width = (it.width * preset.value / 100f).roundToInt(),
                        height = (it.height * preset.value / 100f).roundToInt(),
                        resizeType = ResizeType.Flexible
                    )
                }?.apply {
                    maxWidth = max(maxWidth, width)
                    maxHeight = max(maxHeight, height)
                }
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

                newDoc.createPage(
                    PDPage(
                        PDRectangle(
                            bitmap.width.toFloat(),
                            bitmap.height.toFloat()
                        )
                    )
                ) {
                    drawImage(
                        bitmap.asXObject(newDoc, quality / 100f),
                        0f,
                        0f,
                        bitmap.width.toFloat(),
                        bitmap.height.toFloat()
                    )
                }

                onProgressChange(index)
            }

            shareProvider.cacheData(
                writeData = newDoc::save,
                filename = tempFilename
            ) ?: error("No PDF created")
        }
    }

    override fun convertPdfToImages(
        pdfUri: String,
        password: String?,
        pages: List<Int>?,
        preset: Preset.Percentage
    ): Flow<PdfToImagesAction> = callbackFlow {
        pdfUri.toUri().createPdfRenderer(
            password = password ?: masterPassword,
            onFailure = { throw it }
        )?.use { renderer ->
            send(PdfToImagesAction.PagesCount(pages?.size ?: renderer.pageCount))

            for (pageIndex in 0 until renderer.pageCount) {
                if (pages == null || pages.contains(pageIndex)) {
                    val bitmap = Trickle.drawColorBehind(
                        input = renderer.renderImage(
                            pageIndex,
                            preset.value / 100f
                        ),
                        color = Color.White.toArgb()
                    )

                    send(PdfToImagesAction.Progress(pageIndex, bitmap))
                }
            }
        }
        close()
    }

    override suspend fun getPdfPages(
        uri: String,
        password: String?
    ): List<Int> = withContext(decodingDispatcher) {
        runCatching {
            openPdf(
                uri = uri,
                password = password ?: masterPassword
            ).applyUse {
                List(numberOfPages) { it }
            }
        }.getOrNull() ?: emptyList()
    }

    override suspend fun getPdfPageSizes(
        uri: String,
        password: String?
    ): List<IntegerSize> = withContext(decodingDispatcher) {
        pagesCache[uri]?.takeIf { it.isNotEmpty() } ?: runCatching {
            uri.toUri().createPdfRenderer(
                password = password ?: masterPassword
            )?.use { renderer ->
                List(renderer.pageCount) {
                    renderer.openPage(it).run {
                        IntegerSize(width, height)
                    }
                }.also {
                    pagesCache[uri] = it
                }
            }
        }.getOrNull() ?: emptyList()
    }

    override suspend fun mergePdfs(uris: List<String>): String = catchPdf {
        PDFMergerUtility().run {
            uris.forEach { uri ->
                addSource(UriReadable(uri.toUri(), context).stream)
            }
            shareProvider.cacheDataOrThrow(filename = tempName("merged")) { output ->
                destinationStream = output.outputStream()
                mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly())
            }
        }
    }

    override suspend fun splitPdf(
        uri: String,
        pages: List<Int>?
    ): String = catchPdf {
        openPdf(uri).use { document ->
            createPdf { newDoc ->
                pages.orAll(document).forEach { index ->
                    newDoc.addPage(document.getPageSafe(index))
                }

                newDoc.save(
                    filename = tempName(
                        key = "split",
                        uri = uri
                    )
                )
            }
        }
    }

    override suspend fun removePdfPages(
        uri: String,
        pages: List<Int>
    ): String = catchPdf {
        openPdf(uri).use { document ->
            val totalPages = document.numberOfPages

            require(pages.size < totalPages) {
                getString(R.string.cant_remove_all)
            }

            createPdf { newDoc ->
                repeat(totalPages) { index ->
                    if (index !in pages) newDoc.addPage(document.getPage(index))
                }

                newDoc.save(
                    filename = tempName(
                        key = "removed",
                        uri = uri
                    )
                )
            }
        }
    }

    override suspend fun rotatePdf(
        uri: String,
        rotations: List<Int>
    ): String = catchPdf {
        openPdf(uri).use { document ->
            document.pages.forEachIndexed { idx, page ->
                val angle = rotations.getOrNull(idx) ?: 0
                page.rotation = (page.rotation + angle) % 360
            }

            document.save(
                filename = tempName(
                    key = "rotated",
                    uri = uri
                )
            )
        }
    }

    override suspend fun rearrangePdf(
        uri: String,
        newOrder: List<Int>
    ): String = catchPdf {
        openPdf(uri).use { document ->
            createPdf { newDoc ->
                newOrder.forEach { pageIndex ->
                    newDoc.addPage(document.getPageSafe(pageIndex))
                }

                newDoc.save(
                    filename = tempName(
                        key = "rearranged",
                        uri = uri
                    )
                )
            }
        }
    }

    override suspend fun addPageNumbers(
        uri: String,
        labelFormat: String,
        position: Position,
        color: Int
    ): String = catchPdf {
        val color = Color(color)

        openPdf(uri).use { document ->
            val font = document.baseFont
            val totalPages = document.numberOfPages

            document.pages.forEachIndexed { idx, page ->
                val text = labelFormat.replace("{n}", (idx + 1).toString())
                    .replace("{total}", totalPages.toString())

                val cropBox = page.cropBox
                val pageWidth = cropBox.width
                val pageHeight = cropBox.height
                val originX = cropBox.lowerLeftX
                val originY = cropBox.lowerLeftY

                val textWidth = font.getStringWidth(text) / 1000f * 12f

                val baseX = when (position) {
                    Position.TopLeft,
                    Position.CenterLeft,
                    Position.BottomLeft -> 10f

                    Position.TopCenter,
                    Position.Center,
                    Position.BottomCenter -> pageWidth / 2f

                    Position.TopRight,
                    Position.CenterRight,
                    Position.BottomRight -> pageWidth - 10f
                }

                val baseY = when (position) {
                    Position.TopLeft,
                    Position.TopCenter,
                    Position.TopRight -> pageHeight - 20f

                    Position.CenterLeft,
                    Position.Center,
                    Position.CenterRight -> pageHeight / 2f

                    Position.BottomLeft,
                    Position.BottomCenter,
                    Position.BottomRight -> 20f
                }

                val adjustedX = when (position) {
                    Position.TopCenter,
                    Position.Center,
                    Position.BottomCenter -> baseX - textWidth / 2f

                    Position.TopRight,
                    Position.CenterRight,
                    Position.BottomRight -> baseX - textWidth

                    else -> baseX
                }

                val adjustedY = when (position) {
                    Position.CenterLeft,
                    Position.Center,
                    Position.CenterRight -> baseY - 6f

                    else -> baseY
                }

                val adjustedXWithOrigin = adjustedX + originX
                val adjustedYWithOrigin = adjustedY + originY

                document.writePage(page) {
                    beginText()
                    setFont(font, 12f)
                    setColor(color)
                    newLineAtOffset(adjustedXWithOrigin, adjustedYWithOrigin)
                    showText(text)
                    endText()
                }
            }

            document.save(
                filename = tempName(
                    key = "numbered",
                    uri = uri
                )
            )
        }
    }

    override suspend fun addWatermark(
        uri: String,
        watermarkText: String,
        params: PdfWatermarkParams
    ): String = catchPdf {
        val color = Color(params.color)

        openPdf(uri).use { document ->
            val font = document.baseFont

            params.pages.orAll(document).forEach { pageIndex ->
                val page = document.getPageSafe(pageIndex)

                val textWidth =
                    font.getStringWidth(watermarkText) / 1000f * params.fontSize

                val radians = Math.toRadians(-params.rotation.toDouble())
                val cropBox = page.cropBox

                val originX = cropBox.lowerLeftX
                val originY = cropBox.lowerLeftY

                val centerX = originX + cropBox.width / 2f
                val centerY = originY + cropBox.height / 2f

                val matrix = Matrix.getRotateInstance(
                    radians,
                    centerX,
                    centerY
                )

                document.writePage(page) {
                    beginText()
                    setFont(font, params.fontSize)
                    setColor(color.copy(params.opacity))
                    setTextMatrix(matrix)
                    newLineAtOffset(-textWidth / 2f, 0f)
                    showText(watermarkText)
                    endText()
                }
            }

            document.save(
                filename = tempName(
                    key = "watermarked",
                    uri = uri
                )
            )
        }
    }

    override suspend fun addSignature(
        uri: String,
        signatureImage: Any,
        params: PdfSignatureParams
    ): String = catchPdf {
        openPdf(uri).use { document ->
            val pdImage = imageGetter.getImage(data = signatureImage)!!.asXObject(
                document = document,
                quality = 1f
            )

            val imageAspect =
                pdImage.width.toFloat() / pdImage.height.toFloat()

            params.pages.orAll(document).forEach { pageIndex ->
                val page = document.getPageSafe(pageIndex)

                val cropBox = page.cropBox

                val pageWidth = cropBox.width
                val pageHeight = cropBox.height
                val originX = cropBox.lowerLeftX
                val originY = cropBox.lowerLeftY

                val targetWidth = pageWidth * params.size
                val targetHeight = targetWidth / imageAspect

                val centerX = pageWidth * params.x
                val centerY = pageHeight * params.y

                var targetX = centerX - targetWidth / 2f
                var targetY = centerY - targetHeight / 2f

                targetX = targetX.coerceIn(0f, pageWidth - targetWidth)
                targetY = targetY.coerceIn(0f, pageHeight - targetHeight)

                targetX += originX
                targetY += originY

                document.writePage(page) {
                    setAlpha(params.opacity)
                    drawImage(
                        pdImage,
                        targetX,
                        targetY,
                        targetWidth,
                        targetHeight
                    )
                }
            }

            document.save(
                filename = tempName(
                    key = "signed",
                    uri = uri
                )
            )
        }
    }

    override suspend fun protectPdf(
        uri: String,
        password: String
    ): String = catchPdf {
        openPdf(uri).use { document ->
            document.save(
                filename = tempName(
                    key = "protected",
                    uri = uri
                ),
                password = password
            )
        }
    }

    override suspend fun unlockPdf(
        uri: String,
        password: String
    ): String = catchPdf {
        openPdf(
            uri = uri,
            password = password
        ).use { document ->
            document.save(
                filename = tempName(
                    key = "unlocked",
                    uri = uri
                )
            )
        }
    }

    override suspend fun extractPagesFromPdf(uri: String): List<String> = catchPdf {
        uri.toUri().createPdfRenderer(
            password = masterPassword,
            onFailure = { throw it }
        )?.use { renderer ->
            (0 until renderer.pageCount).mapNotNull { pageIndex ->
                val bitmap = Trickle.drawColorBehind(
                    input = renderer.renderImage(pageIndex, 1f),
                    color = Color.White.toArgb()
                )

                shareProvider.cacheImage(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )
            }
        } ?: emptyList()
    }

    override suspend fun compressPdf(
        uri: String,
        quality: Float
    ): String = catchPdf {
        openPdf(uri).use { document ->
            document.pages.forEach { page ->
                page.resources.apply {
                    for (name in xObjectNames) {
                        val image = getXObject(name)
                            .safeCast<PDImageXObject>()
                            ?.image?.asXObject(document, quality)
                            ?: continue

                        put(name, image)
                    }
                }
            }

            document.save(
                filename = tempName(
                    key = "compressed",
                    uri = uri
                )
            )
        }
    }

    override suspend fun convertToGrayscale(uri: String): String = catchPdf {
        openPdf(uri).use { document ->
            document.pages.forEach { page ->
                page.resources.apply {
                    for (name in xObjectNames) {
                        val image = Aire.saturation(
                            bitmap = getXObject(name).safeCast<PDImageXObject>()?.image ?: continue,
                            saturation = 0f,
                            tonemap = false
                        ).asXObject(document, 0.8f)

                        put(name, image)
                    }
                }
            }

            document.save(
                filename = tempName(
                    key = "grayscale",
                    uri = uri
                )
            )
        }
    }

    override suspend fun repairPdf(uri: String): String = catchPdf {
        openPdf(uri).use { document ->
            document.save(
                filename = tempName(
                    key = "repaired",
                    uri = uri
                )
            )
        }
    }

    override suspend fun changePdfMetadata(
        uri: String,
        metadata: PdfMetadata?
    ): String = catchPdf {
        openPdf(uri).use { document ->
            document.save(
                metadata = metadata,
                filename = tempName(
                    key = "metadata",
                    uri = uri
                )
            )
        }
    }

    override suspend fun getPdfMetadata(uri: String): PdfMetadata = catchPdf {
        openPdf(uri).use(PDDocument::metadata)
    }

    override suspend fun stripText(uri: String): List<String> = catchPdf {
        openPdf(uri).use { document ->
            PDFTextStripper().run {
                (1..document.numberOfPages).map { pageIndex ->
                    startPage = pageIndex
                    endPage = pageIndex
                    getText(document)
                }
            }
        }
    }

    override suspend fun cropPdf(
        uri: String,
        pages: List<Int>?,
        rect: RectModel
    ): String = catchPdf {
        openPdf(uri).use { document ->
            with(rect) {
                pages.orAll(document).forEach { pageIndex ->
                    val page = document.getPageSafe(pageIndex)

                    val cropBox = page.cropBox

                    val width = cropBox.width
                    val height = cropBox.height
                    val originX = cropBox.lowerLeftX
                    val originY = cropBox.lowerLeftY

                    val rotation = page.rotation

                    page.cropBox = when (rotation) {
                        90 -> PDRectangle(
                            originX + top * width,
                            originY + left * height,
                            (bottom - top) * width,
                            (right - left) * height
                        )

                        180 -> PDRectangle(
                            originX + (1f - right) * width,
                            originY + top * height,
                            (right - left) * width,
                            (bottom - top) * height
                        )

                        270 -> PDRectangle(
                            originX + (1f - bottom) * width,
                            originY + (1f - right) * height,
                            (bottom - top) * width,
                            (right - left) * height
                        )

                        else -> PDRectangle(
                            originX + left * width,
                            originY + (1f - bottom) * height,
                            (right - left) * width,
                            (bottom - top) * height
                        )
                    }
                }
            }

            document.save(
                filename = tempName(
                    key = "cropped",
                    uri = uri
                )
            )
        }
    }

    override suspend fun flattenPdf(
        uri: String,
        quality: Float
    ): String = catchPdf {
        val dpi = 72f + (228f * quality)

        openPdf(uri).use { source ->
            createPdf { newDoc ->
                val renderer = PDFRenderer(source)

                repeat(source.numberOfPages) { pageIndex ->
                    val sourcePage = source.getPage(pageIndex)
                    val cropBox = sourcePage.cropBox

                    val image = renderer.renderImageWithDPI(
                        pageIndex,
                        dpi,
                        ImageType.RGB
                    )

                    newDoc.createPage(PDPage(cropBox)) {
                        drawImage(
                            image.asXObject(newDoc, quality),
                            0f,
                            0f,
                            cropBox.width,
                            cropBox.height
                        )
                    }
                }

                newDoc.save(
                    filename = createTempName(
                        key = "flattened",
                        uri = uri
                    )
                )
            }
        }
    }

    override suspend fun detectPdfAutoRotations(
        uri: String
    ): List<Int> = catchPdf {
        openPdf(uri).use { document ->
            val rotations = document.pages.map { page ->
                ((page.rotation % 360) + 360) % 360
            }

            val majority = rotations
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: 0

            rotations.map { rotation ->
                ((majority - rotation) + 360) % 360
            }
        }
    }

    override suspend fun saveSignature(signature: Any): Boolean {
        return runCatching {
            val currentSignatures = savedSignatures.value

            if (currentSignatures.size + 1 > 20) {
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

    override suspend fun extractImagesFromPdf(
        uri: String
    ): String? = catchPdf {
        var hasImages = false

        val prefix = uri.toUri().filename()?.substringBeforeLast('.') ?: timestamp()
        val filename = "$PDF${prefix}_extracted.zip"

        val zipPath = openPdf(uri).use { document ->
            shareProvider.cacheDataOrThrow(
                filename = filename
            ) { output ->
                val seen = mutableSetOf<Any>()
                var index = 0

                ZipOutputStream(output.outputStream()).use { zip ->
                    for (xObject in document.getAllImages()) {
                        if (!seen.add(xObject.cosObject)) continue

                        val suffix = xObject.suffix?.lowercase() ?: "png"
                        if (suffix == "jpg" || suffix == "jp2" || suffix == "tiff") {
                            val entryName = "extracted_${index++}.$suffix"
                            zip.putNextEntry(ZipEntry(entryName))
                            xObject.stream.createInputStream().use { input ->
                                input.copyTo(zip)
                            }
                            zip.closeEntry()
                        } else {
                            val data = ByteArrayOutputStream().apply {
                                use {
                                    xObject.image.compress(
                                        Bitmap.CompressFormat.PNG,
                                        100,
                                        it
                                    )
                                }
                            }.toByteArray()

                            if (!seen.add(HashingType.MD5.computeFromReadable(ByteArrayReadable(data)))) continue

                            val entryName = "extracted_${index++}.$suffix"
                            zip.putNextEntry(ZipEntry(entryName))
                            data.inputStream().copyTo(zip)
                            zip.closeEntry()
                        }
                        hasImages = true
                    }
                }
            }
        }

        if (!hasImages) {
            clearPdfCache(zipPath)
            null
        } else {
            zipPath
        }
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

    private suspend inline fun <T> catchPdf(
        crossinline action: suspend () -> T
    ): T = withContext(defaultDispatcher) {
        try {
            action()
        } catch (k: InvalidPasswordException) {
            throw SecurityException(k.message)
        } catch (e: CancellationException) {
            throw e
        }
    }

    private fun tempName(
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

    private fun openPdf(
        uri: String,
        password: String? = masterPassword
    ): PDDocument = safeOpenPdf(
        uri = uri,
        password = password
    )

    private inline fun <T> createPdf(action: (PDDocument) -> T) = PDDocument().use(action)

    private suspend fun PDDocument.save(
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

    private fun List<Int>?.orAll(document: PDDocument) =
        orEmpty().ifEmpty { List(document.numberOfPages) { it } }

}