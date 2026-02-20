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
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.getSuitableConfig
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.domain.model.RectModel
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfWatermarkParams
import com.tom_roush.harmony.awt.AWTColor
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random


internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PdfManager<Bitmap> {

    private val pagesCache = hashMapOf<String, List<IntegerSize>>()

    private var password: String? = null

    override fun setMasterPassword(password: String?) {
        this.password = password
    }

    override suspend fun checkIsPdfEncrypted(uri: String): String? = catchPdf {
        unlockPdf(
            uri = uri,
            password = password.orEmpty(),
            filename = uri.toUri().filename() ?: tempName(
                key = "unlocked",
                uri = uri
            )
        ).takeIf {
            !password.isNullOrBlank()
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
        PDDocument().use { pdfDocument ->
            val (size, images) = calculateCombinedImageDimensionsAndBitmaps(
                imageUris = imageUris,
                scaleSmallImagesToLarge = scaleSmallImagesToLarge,
                isHorizontal = false,
                imageSpacing = 0,
                percent = preset.value
            )

            val bitmaps = images.map { image ->
                if (scaleSmallImagesToLarge && image.shouldUpscale(false, size)) {
                    image.upscale(false, size)
                } else image
            }

            val qualityValue = (quality / 100f).coerceIn(0f, 1f)

            bitmaps.forEachIndexed { index, imageBitmap ->
                val page = PDPage(
                    PDRectangle(
                        imageBitmap.width.toFloat(),
                        imageBitmap.height.toFloat()
                    )
                )
                pdfDocument.addPage(page)

                PDPageContentStream(pdfDocument, page).use { contentStream ->
                    contentStream.drawImage(
                        JPEGFactory.createFromImage(pdfDocument, imageBitmap, qualityValue),
                        0f,
                        0f,
                        imageBitmap.width.toFloat(),
                        imageBitmap.height.toFloat()
                    )
                }

                delay(10L)
                onProgressChange(index)
            }

            shareProvider.cacheData(
                writeData = {
                    pdfDocument.save(it.outputStream())
                },
                filename = tempFilename
            ) ?: throw IllegalAccessException("No PDF created")
        }
    }

    override suspend fun convertPdfToImages(
        pdfUri: String,
        password: String?,
        onFailure: (Throwable) -> Unit,
        pages: List<Int>?,
        preset: Preset.Percentage,
        onGetPagesCount: suspend (Int) -> Unit,
        onProgressChange: suspend (Int, Bitmap) -> Unit,
        onComplete: suspend () -> Unit
    ): Unit = withContext(ioDispatcher) {
        val pdfRenderer = pdfUri.toUri().createPdfRenderer(
            password = password ?: this@AndroidPdfManager.password,
            onFailure = onFailure,
            onPasswordRequest = null
        ) ?: return@withContext onFailure(NullPointerException("File cannot be read"))

        onGetPagesCount(pages?.size ?: pdfRenderer.pageCount)

        for (pageIndex in 0 until pdfRenderer.pageCount) {
            if (pages == null || pages.contains(pageIndex)) {
                val bitmap = pdfRenderer.renderImage(
                    pageIndex,
                    preset.value / 100f
                )

                val renderedBitmap = createBitmap(
                    width = bitmap.width,
                    height = bitmap.height,
                    config = getSuitableConfig(bitmap)
                ).applyCanvas {
                    drawColor(Color.White.toArgb())
                    drawBitmap(bitmap)
                }

                onProgressChange(pageIndex, renderedBitmap)
            }
        }
        onComplete()
        pdfRenderer.close()
    }

    override suspend fun getPdfPages(
        uri: String,
        password: String?
    ): List<Int> = withContext(decodingDispatcher) {
        runCatching {
            val renderer = uri.toUri().createPdfRenderer(
                password = password ?: password,
                onFailure = {},
                onPasswordRequest = null
            )

            List(renderer?.pageCount ?: 0) { it }
        }.getOrNull() ?: emptyList()
    }

    override suspend fun getPdfPageSizes(
        uri: String,
        password: String?
    ): List<IntegerSize> = withContext(decodingDispatcher) {
        pagesCache[uri]?.takeIf { it.isNotEmpty() } ?: runCatching {
            val renderer = uri.toUri().createPdfRenderer(
                password = password ?: password,
                onFailure = {},
                onPasswordRequest = null
            ) ?: return@withContext emptyList()

            List(renderer.pageCount) {
                renderer.openPage(it).run {
                    IntegerSize(width, height)
                }
            }.also {
                pagesCache[uri] = it
            }
        }.getOrNull() ?: emptyList()
    }

    override suspend fun mergePdfs(uris: List<String>): String = catchPdf {
        shareProvider.cacheDataOrThrow(filename = tempName("merged")) { output ->
            PDFMergerUtility().apply {
                uris.forEach { uri ->
                    addSource(uri.inputStream())
                }
                destinationStream = output.outputStream()
                mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly())
            }
        }
    }

    override suspend fun splitPdf(
        uri: String,
        pages: List<Int>?
    ): String = catchPdf {
        PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
            shareProvider.cacheDataOrThrow(
                filename = tempName(
                    key = "split",
                    uri = uri
                )
            ) { output ->
                PDDocument().use { newDoc ->
                    (pages ?: List(document.numberOfPages) { it }).forEach { index ->
                        newDoc.addPage(document.getPage(index))
                    }
                    newDoc.save(output.outputStream())
                }
            }
        }
    }

    override suspend fun removePdfPages(
        uri: String,
        pages: List<Int>
    ): String = catchPdf {
        PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
            if (pages.size >= document.numberOfPages) {
                throw IllegalArgumentException(getString(R.string.cant_remove_all))
            }

            shareProvider.cacheDataOrThrow(
                filename = tempName(
                    key = "removed",
                    uri = uri
                )
            ) { output ->
                PDDocument().use { newDoc ->
                    repeat(document.numberOfPages) { index ->
                        if (index !in pages) newDoc.addPage(document.getPage(index))
                    }
                    newDoc.save(output.outputStream())
                }
            }
        }
    }

    override suspend fun rotatePdf(
        uri: String,
        rotations: List<Int>
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "rotated",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                document.pages.forEachIndexed { idx, page ->
                    val angle = rotations.getOrNull(idx) ?: 0
                    page.rotation = (page.rotation + angle) % 360
                }
                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun rearrangePdf(
        uri: String,
        newOrder: List<Int>
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "rearranged",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                PDDocument().use { newDoc ->
                    newOrder.forEach { idx -> newDoc.addPage(document.getPage(idx)) }
                    newDoc.save(output.outputStream())
                }
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

        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "numbered",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                val font = document.getBaseFont()
                val totalPages = document.pages.count()

                document.pages.forEachIndexed { idx, page ->
                    val text = labelFormat.replace("{n}", (idx + 1).toString())
                        .replace("{total}", totalPages.toString())

                    val baseBox = page.cropBox ?: page.mediaBox
                    val pageWidth = baseBox.width
                    val pageHeight = baseBox.height
                    val originX = baseBox.lowerLeftX
                    val originY = baseBox.lowerLeftY

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

                    PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true
                    ).use { stream ->
                        val gs = PDExtendedGraphicsState().apply {
                            nonStrokingAlphaConstant = color.alpha
                        }

                        stream.setGraphicsStateParameters(gs)
                        stream.beginText()
                        stream.setFont(font, 12f)
                        stream.setNonStrokingColor(
                            AWTColor(
                                (color.red * 255).roundToInt(),
                                (color.green * 255).roundToInt(),
                                (color.blue * 255).roundToInt(),
                                (color.alpha * 255).roundToInt()
                            )
                        )
                        stream.newLineAtOffset(adjustedXWithOrigin, adjustedYWithOrigin)
                        stream.showText(text)
                        stream.endText()
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun addWatermark(
        uri: String,
        watermarkText: String,
        params: PdfWatermarkParams
    ): String = catchPdf {
        val color = Color(params.color)

        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "watermarked",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                val font = document.getBaseFont()

                val pages =
                    params.pages.ifEmpty { List(document.pages.count) { it } }

                pages.forEach { idx ->
                    val page =
                        document.getPage(idx.coerceIn(0 until document.pages.count()))

                    PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true
                    ).use { stream ->

                        val gs = PDExtendedGraphicsState().apply {
                            nonStrokingAlphaConstant = params.opacity
                        }

                        stream.setGraphicsStateParameters(gs)
                        stream.beginText()
                        stream.setFont(font, params.fontSize)
                        stream.setNonStrokingColor(
                            AWTColor(
                                (color.red * 255).roundToInt(),
                                (color.green * 255).roundToInt(),
                                (color.blue * 255).roundToInt(),
                                (color.alpha * 255).roundToInt()
                            )
                        )

                        val textWidth =
                            font.getStringWidth(watermarkText) / 1000f * params.fontSize

                        val radians = Math.toRadians(-params.rotation.toDouble())
                        val baseBox = page.cropBox ?: page.mediaBox

                        val originX = baseBox.lowerLeftX
                        val originY = baseBox.lowerLeftY

                        val centerX = originX + baseBox.width / 2f
                        val centerY = originY + baseBox.height / 2f

                        val matrix = Matrix.getRotateInstance(
                            radians,
                            centerX,
                            centerY
                        )

                        stream.setTextMatrix(matrix)
                        stream.newLineAtOffset(-textWidth / 2f, 0f)
                        stream.showText(watermarkText)
                        stream.endText()
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun addSignature(
        uri: String,
        signatureImage: Bitmap,
        params: PdfSignatureParams
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "signed",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                val pagesToSign =
                    params.pages.ifEmpty { List(document.pages.count) { it } }

                val pdImage = LosslessFactory.createFromImage(document, signatureImage)

                val imageAspect =
                    signatureImage.width.toFloat() / signatureImage.height.toFloat()

                pagesToSign.forEach { idx ->
                    val page =
                        document.getPage(idx.coerceIn(0 until document.pages.count()))

                    val baseBox = page.cropBox ?: page.mediaBox

                    val pageWidth = baseBox.width
                    val pageHeight = baseBox.height

                    val originX = baseBox.lowerLeftX
                    val originY = baseBox.lowerLeftY

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

                    PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true
                    ).use { stream ->
                        val gs = PDExtendedGraphicsState().apply {
                            nonStrokingAlphaConstant = params.opacity
                        }

                        stream.setGraphicsStateParameters(gs)

                        stream.drawImage(
                            pdImage,
                            targetX,
                            targetY,
                            targetWidth,
                            targetHeight
                        )
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun protectPdf(
        uri: String,
        password: String
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "protected",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), this.password.orEmpty()).use { document ->
                val protection = StandardProtectionPolicy(password, password, AccessPermission())
                protection.encryptionKeyLength = 128
                document.protect(protection)
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun unlockPdf(
        uri: String,
        password: String
    ): String = catchPdf {
        unlockPdf(
            uri = uri,
            password = password,
            filename = tempName(
                key = "unlocked",
                uri = uri
            )
        )
    }

    override suspend fun extractPagesFromPdf(uri: String): List<String> = catchPdf {
        uri.toUri().createPdfRenderer(
            password = password,
            onFailure = { throw it },
            onPasswordRequest = null
        )?.use { pdfRenderer ->
            (0 until pdfRenderer.pageCount).mapNotNull { pageIndex ->
                val bitmap = pdfRenderer.renderImage(pageIndex, 1f)

                val renderedBitmap = createBitmap(
                    width = bitmap.width,
                    height = bitmap.height,
                    config = getSuitableConfig(bitmap)
                ).applyCanvas {
                    drawColor(Color.White.toArgb())
                    drawBitmap(bitmap)
                }

                shareProvider.cacheImage(
                    image = renderedBitmap,
                    imageInfo = ImageInfo(
                        width = renderedBitmap.width,
                        height = renderedBitmap.height,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )
            }
        } ?: throw NullPointerException("File cannot be read")
    }

    override suspend fun compressPdf(
        uri: String,
        quality: Float
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "compressed",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password).use { document ->
                document.pages.forEach { page ->
                    val resources = page.resources
                    val xObjectNames = resources.xObjectNames
                    xObjectNames.forEach { name ->
                        val xobj = resources.getXObject(name)
                        if (xobj is PDImageXObject) {
                            val orig = xobj.image

                            val jpeg = JPEGFactory.createFromImage(
                                document,
                                orig,
                                quality
                            )

                            resources.put(name, jpeg)
                        }
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun convertToGrayscale(uri: String): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "grayscale",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password).use { document ->
                document.pages.forEach { page ->
                    val resources = page.resources
                    val xObjectNames = resources.xObjectNames
                    xObjectNames.forEach { name ->
                        val xobj = resources.getXObject(name)
                        if (xobj is PDImageXObject) {
                            val bmp = xobj.image

                            val grayBmp = bmp.copy(Bitmap.Config.ARGB_8888, true)
                                .applyCanvas {
                                    val paint = Paint().apply {
                                        colorFilter = ColorMatrixColorFilter(
                                            ColorMatrix().apply {
                                                setSaturation(0f)
                                            }
                                        )
                                    }

                                    drawBitmap(bmp, 0f, 0f, paint)
                                }

                            val newImage = JPEGFactory.createFromImage(document, grayBmp, 0.8f)
                            resources.put(name, newImage)
                        }
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun repairPdf(uri: String): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "repaired",
                uri = uri
            )
        ) { output ->
            try {
                PDDocument.load(uri.inputStream(), password).use { document ->
                    document.isAllSecurityToBeRemoved = true
                    document.save(output.outputStream())
                }
            } catch (t: Throwable) {
                if (t is InvalidPasswordException) throw t

                PDDocument.load(uri.inputStream(), password, MemoryUsageSetting.setupTempFileOnly())
                    .use { document ->
                        document.isAllSecurityToBeRemoved = true
                        document.save(output.outputStream())
                    }
            }
        }
    }

    override suspend fun changePdfMetadata(
        uri: String,
        metadata: PdfMetadata?
    ): String = catchPdf {
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "metadata",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password).use { document ->
                if (metadata == null) {
                    document.documentInformation = PDDocumentInformation().apply {
                        creationDate = Calendar.getInstance()
                        modificationDate = Calendar.getInstance()
                    }
                } else {
                    document.documentInformation.apply {
                        title = metadata.title ?: title
                        author = metadata.author ?: author
                        subject = metadata.subject ?: subject
                        keywords = metadata.keywords ?: keywords
                        creator = metadata.creator ?: creator
                        producer = metadata.producer ?: producer
                    }
                }
                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun getPdfMetadata(uri: String): PdfMetadata = catchPdf {
        PDDocument.load(uri.inputStream(), password).use { document ->
            document.documentInformation.run {
                PdfMetadata(
                    title = title,
                    author = author,
                    subject = subject,
                    keywords = keywords,
                    creator = creator,
                    producer = producer
                )
            }
        }
    }

    override suspend fun stripText(uri: String): List<String> = catchPdf {
        PDDocument.load(uri.inputStream(), password).use { document ->
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
        shareProvider.cacheDataOrThrow(
            filename = tempName(
                key = "cropped",
                uri = uri
            )
        ) { output ->
            PDDocument.load(uri.inputStream(), password.orEmpty()).use { document ->
                val pagesToProcess = pages ?: List(document.numberOfPages) { it }

                with(rect) {
                    pagesToProcess.forEach { pageIndex ->
                        val page = document.getPage(pageIndex)

                        val baseBox = page.cropBox ?: page.mediaBox

                        val width = baseBox.width
                        val height = baseBox.height

                        val originX = baseBox.lowerLeftX
                        val originY = baseBox.lowerLeftY

                        val rotation = page.rotation

                        val cropBox = when (rotation) {

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

                        page.cropBox = cropBox
                    }
                }

                document.isAllSecurityToBeRemoved = true
                document.save(output.outputStream())
            }
        }
    }

    override suspend fun flattenPdf(
        uri: String,
        quality: Float
    ): String = catchPdf {
        val dpi = 72f + (228f * quality)

        shareProvider.cacheDataOrThrow(
            filename = createTempName("flattened", uri)
        ) { output ->

            PDDocument.load(uri.inputStream(), password.orEmpty()).use { source ->

                val renderer = PDFRenderer(source)

                PDDocument().use { target ->
                    for (pageIndex in 0 until source.numberOfPages) {
                        val sourcePage = source.getPage(pageIndex)
                        val cropBox = sourcePage.cropBox ?: sourcePage.mediaBox

                        val image = renderer.renderImageWithDPI(
                            pageIndex,
                            dpi,
                            ImageType.RGB
                        )

                        val newPage = PDPage(
                            PDRectangle(
                                cropBox.width,
                                cropBox.height
                            )
                        )

                        target.addPage(newPage)

                        val pdImage = JPEGFactory.createFromImage(
                            target,
                            image,
                            quality
                        )

                        PDPageContentStream(
                            target,
                            newPage,
                            PDPageContentStream.AppendMode.OVERWRITE,
                            false,
                            false
                        ).use { content ->
                            content.drawImage(
                                pdImage,
                                0f,
                                0f,
                                cropBox.width,
                                cropBox.height
                            )
                        }
                    }

                    target.isAllSecurityToBeRemoved = true

                    target.save(output.outputStream())
                }
            }
        }
    }

    override fun createTempName(key: String, uri: String?): String = tempName(
        key = key,
        uri = uri
    )

    private suspend fun unlockPdf(
        uri: String,
        password: String,
        filename: String
    ) = shareProvider.cacheDataOrThrow(filename = filename) { output ->
        PDDocument.load(uri.inputStream(), password).use { document ->
            document.isAllSecurityToBeRemoved = true
            document.save(output.outputStream())
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

    private suspend fun calculateCombinedImageDimensionsAndBitmaps(
        imageUris: List<String>,
        isHorizontal: Boolean,
        scaleSmallImagesToLarge: Boolean,
        imageSpacing: Int,
        percent: Int
    ): Pair<IntegerSize, List<Bitmap>> = withContext(defaultDispatcher) {
        var w = 0
        var h = 0
        var maxHeight = 0
        var maxWidth = 0
        val drawables = imageUris.mapNotNull { uri ->
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .size(Size.ORIGINAL)
                    .build()
            ).image?.toBitmap()?.let {
                imageScaler.scaleImage(
                    image = it,
                    width = (it.width * percent / 100f).roundToInt(),
                    height = (it.height * percent / 100f).roundToInt(),
                    resizeType = ResizeType.Flexible
                )
            }?.apply {
                maxWidth = max(maxWidth, width)
                maxHeight = max(maxHeight, height)
            }
        }

        drawables.forEachIndexed { index, image ->
            val width = image.width
            val height = image.height

            val spacing = if (index != drawables.lastIndex) imageSpacing else 0

            if (scaleSmallImagesToLarge && image.shouldUpscale(
                    isHorizontal = isHorizontal,
                    size = IntegerSize(maxWidth, maxHeight)
                )
            ) {
                val targetHeight: Int
                val targetWidth: Int

                if (isHorizontal) {
                    targetHeight = maxHeight
                    targetWidth = (targetHeight * image.aspectRatio).toInt()
                } else {
                    targetWidth = maxWidth
                    targetHeight = (targetWidth / image.aspectRatio).toInt()
                }
                if (isHorizontal) {
                    w += (targetWidth + spacing).coerceAtLeast(1)
                } else {
                    h += (targetHeight + spacing).coerceAtLeast(1)
                }
            } else {
                if (isHorizontal) {
                    w += (width + spacing).coerceAtLeast(1)
                } else {
                    h += (height + spacing).coerceAtLeast(1)
                }
            }
        }

        if (isHorizontal) {
            h = maxHeight
        } else {
            w = maxWidth
        }

        IntegerSize(w, h) to drawables
    }

    private fun Bitmap.shouldUpscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Boolean {
        return if (isHorizontal) this.height != size.height
        else this.width != size.width
    }

    private suspend fun Bitmap.upscale(
        isHorizontal: Boolean,
        size: IntegerSize
    ): Bitmap = withContext(encodingDispatcher) {
        if (isHorizontal) {
            createScaledBitmap(
                width = (size.height * aspectRatio).toInt(),
                height = size.height
            )
        } else {
            createScaledBitmap(
                width = size.width,
                height = (size.width / aspectRatio).toInt()
            )
        }
    }

    private suspend fun Bitmap.createScaledBitmap(
        width: Int,
        height: Int
    ): Bitmap = imageScaler.scaleImage(
        image = this,
        width = width,
        height = height
    )

    private fun String.inputStream() = UriReadable(toUri(), context).stream

    private fun tempName(
        key: String,
        uri: String? = null
    ): String {
        val keyFixed = if (key.isBlank()) "_" else "_${key}_"

        return uri?.toUri()?.filename()?.substringBeforeLast('.')?.let {
            "${it}${keyFixed.removeSuffix("_")}.pdf"
        } ?: "PDF$keyFixed${timestamp()}_${
            Random(Random.nextInt()).hashCode().toString().take(4)
        }.pdf"
    }

    private fun PDDocument.getBaseFont() =
        PDType0Font.load(this, context.resources.openRawResource(R.raw.roboto_bold))
}