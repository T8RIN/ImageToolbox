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
import androidx.core.net.toUri
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.awxkee.aire.ScaleColorSpace
import com.t8rin.imagetoolbox.core.data.saving.io.ByteArrayReadable
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.saving.io.shielded
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.PDF
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
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.PdfRenderer
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.asXObject
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPage
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.defaultFont
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getAllImages
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getPageSafe
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.metadata
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.orAll
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.pageIndices
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.save
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setAlpha
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setColor
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.writePage
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfHelper
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.ImagesToPdfParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PageNumbersParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfToImagesAction
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfWatermarkParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PrintPdfParams
import com.t8rin.logger.makeLog
import com.t8rin.trickle.Trickle
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val helper: AndroidPdfHelper,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PdfManager, PdfHelper by helper {

    override fun convertPdfToImages(
        uri: String,
        pages: List<Int>?,
        preset: Preset.Percentage
    ): Flow<PdfToImagesAction> = channelFlow {
        val scale = preset.value / 100f

        helper.useRenderer(uri) { renderer ->
            send(PdfToImagesAction.PagesCount(pages?.size ?: renderer.pageCount))

            pages.orAll(renderer.pDocument).forEach { pageIndex ->
                send(
                    PdfToImagesAction.Progress(
                        index = pageIndex,
                        image = renderer.safeRenderDpi(pageIndex, scale).whiteBg()
                    )
                )
            }
        }
        close()
    }

    override suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        params: ImagesToPdfParams
    ): String = catchPdf {
        createPdf { newDoc ->
            val scale = params.preset.value / 100f
            val quality = params.quality / 100f

            var h = 0
            var maxHeight = 0
            var maxWidth = 0
            val images = imageUris.mapNotNull { uri ->
                imageGetter.getImage(data = uri)?.let {
                    imageScaler.scaleImage(
                        image = it,
                        width = (it.width * scale).roundToInt(),
                        height = (it.height * scale).roundToInt(),
                        resizeType = ResizeType.Flexible
                    )
                }?.apply {
                    maxWidth = max(maxWidth, width)
                    maxHeight = max(maxHeight, height)
                }
            }

            for (image in images) {
                h += if (params.scaleSmallImagesToLarge && image.width != maxWidth) {
                    (maxWidth / image.aspectRatio).toInt().coerceAtLeast(1)
                } else {
                    image.height.coerceAtLeast(1)
                }
            }

            val size = IntegerSize(maxWidth, h)

            images.forEachIndexed { index, image ->
                val bitmap = if (params.scaleSmallImagesToLarge && image.width != size.width) {
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
                        bitmap.asXObject(newDoc, quality),
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
                filename = tempName("")
            ) ?: error("No PDF created")
        }
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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
            createPdf { newDoc ->
                document.pageIndices.forEach { index ->
                    if (index !in pages) newDoc.addPage(document.getPage(index))
                }

                if (newDoc.numberOfPages <= 0) {
                    error(getString(R.string.cant_remove_all))
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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
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
        params: PageNumbersParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val font = document.defaultFont
            val totalPages = document.numberOfPages
            val label = params.labelFormat
                .replace("{total}", totalPages.toString())

            document.pages.forEachIndexed { idx, page ->
                val text = label.replace("{n}", (idx + 1).toString())

                val cropBox = page.cropBox
                val pageWidth = cropBox.width
                val pageHeight = cropBox.height
                val originX = cropBox.lowerLeftX
                val originY = cropBox.lowerLeftY

                val textWidth = font.getStringWidth(text) / 1000f * 12f

                val baseX = when (params.position) {
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

                val baseY = when (params.position) {
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

                val adjustedX = when (params.position) {
                    Position.TopCenter,
                    Position.Center,
                    Position.BottomCenter -> baseX - textWidth / 2f

                    Position.TopRight,
                    Position.CenterRight,
                    Position.BottomRight -> baseX - textWidth

                    else -> baseX
                }

                val adjustedY = when (params.position) {
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
                    setColor(params.color)
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

        usePdf(uri) { document ->
            val font = document.defaultFont

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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
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
        usePdf(
            uri = uri,
            password = password,
            action = { document ->
                document.save(
                    filename = tempName(
                        key = "unlocked",
                        uri = uri
                    )
                )
            }
        )
    }

    override suspend fun extractPagesFromPdf(uri: String): List<String> = catchPdf {
        helper.useRenderer(uri) { renderer ->
            renderer.pageIndices.mapNotNull { pageIndex ->
                val bitmap = renderer.renderImage(pageIndex, 1f).whiteBg()

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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
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
        usePdf(uri) { document ->
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
        usePdf(
            uri = uri,
            action = PDDocument::metadata
        )
    }

    override suspend fun stripText(uri: String): List<String> = catchPdf {
        usePdf(uri) { document ->
            PDFTextStripper().run {
                document.pageIndices.map { pageIndex ->
                    startPage = pageIndex + 1
                    endPage = pageIndex + 1
                    getText(document).trim()
                }
            }
        }
    }

    override suspend fun cropPdf(
        uri: String,
        pages: List<Int>?,
        rect: RectModel
    ): String = catchPdf {
        usePdf(uri) { document ->
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
                        originX + rect.top * width,
                        originY + rect.left * height,
                        (rect.bottom - rect.top) * width,
                        (rect.right - rect.left) * height
                    )

                    180 -> PDRectangle(
                        originX + (1f - rect.right) * width,
                        originY + rect.top * height,
                        (rect.right - rect.left) * width,
                        (rect.bottom - rect.top) * height
                    )

                    270 -> PDRectangle(
                        originX + (1f - rect.bottom) * width,
                        originY + (1f - rect.right) * height,
                        (rect.bottom - rect.top) * width,
                        (rect.right - rect.left) * height
                    )

                    else -> PDRectangle(
                        originX + rect.left * width,
                        originY + (1f - rect.bottom) * height,
                        (rect.right - rect.left) * width,
                        (rect.bottom - rect.top) * height
                    )
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

        usePdf(uri) { document ->
            val renderer = PdfRenderer(document)

            createPdf { newDoc ->
                document.pages.forEachIndexed { index, page ->
                    val cropBox = page.cropBox

                    val pdImage = renderer
                        .safeRenderDpi(index, dpi)
                        .whiteBg()
                        .asXObject(newDoc, quality)

                    newDoc.createPage(PDPage(cropBox)) {
                        drawImage(
                            pdImage,
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
        usePdf(uri) { document ->
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

    override suspend fun extractImagesFromPdf(
        uri: String
    ): String? = catchPdf {
        var hasImages = false

        val prefix = uri.toUri().filename()?.substringBeforeLast('.') ?: timestamp()
        val filename = "$PDF${prefix}_extracted.zip"

        val zipPath = usePdf(uri) { document ->
            shareProvider.cacheDataOrThrow(
                filename = filename
            ) { output ->
                val seen = mutableSetOf<Any>()
                var index = 0

                output.outputStream().createZip { zip ->
                    for (xObject in document.getAllImages()) {
                        if (!seen.add(xObject.cosObject)) continue

                        val suffix = xObject.suffix?.lowercase() ?: "png"
                        val stream = if (suffix == "jpg" || suffix == "jp2" || suffix == "tiff") {
                            xObject.stream.createInputStream()
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

                            data.inputStream()
                        }

                        zip.putEntry(
                            name = "extracted_${index++}.$suffix",
                            input = stream
                        )
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

    override suspend fun convertToZip(
        uri: String,
        interval: Int
    ): String = catchPdf {
        val prefix = uri.toUri().filename()?.substringBeforeLast('.') ?: timestamp()
        val filename = "$PDF${prefix}.zip"

        usePdf(uri) { document ->
            shareProvider.cacheDataOrThrow(
                filename = filename
            ) { output ->
                var index = 0

                output.outputStream().createZip { zip ->
                    document.pageIndices
                        .chunked(interval.coerceAtLeast(1))
                        .forEach { pages ->
                            createPdf { newDoc ->
                                pages.forEach { pageIndex ->
                                    newDoc.addPage(document.getPageSafe(pageIndex))
                                }

                                zip.putEntry(
                                    name = "${prefix}_${index++}.pdf",
                                    write = {
                                        newDoc.save(StreamWriteable(it).shielded())
                                    }
                                )
                            }
                        }
                }
            }
        }
    }

    override suspend fun printPdf(
        uri: String,
        quality: Float,
        params: PrintPdfParams
    ): String = catchPdf {
        val dpi = 72f + (228f * quality)

        usePdf(uri) { document ->
            val renderer = PdfRenderer(document)

            createPdf { newDoc ->
                val pagesPerSheet = params.pagesPerSheet.coerceIn(PrintPdfParams.pageRange)

                val gridSize = params.gridSize

                val totalPages = document.numberOfPages
                val sheetsNeeded = (totalPages + pagesPerSheet - 1) / pagesPerSheet

                for (sheetIndex in 0 until sheetsNeeded) {
                    val startPageIndex = sheetIndex * pagesPerSheet
                    val firstPageOnSheet = document.getPage(startPageIndex)

                    val cropBox = params.calculatePageSize(firstPageOnSheet)?.let { size ->
                        PDRectangle(size.width.toFloat(), size.height.toFloat())
                    } ?: firstPageOnSheet.cropBox

                    newDoc.createPage(PDPage(cropBox)) {
                        val pageWidth = cropBox.width
                        val pageHeight = cropBox.height

                        val rows = gridSize.first
                        val cols = gridSize.second

                        val cellWidth = pageWidth / cols
                        val cellHeight = pageHeight / rows

                        val margin = if (params.marginPercent > 0) {
                            (minOf(
                                pageWidth,
                                pageHeight
                            ) * params.marginPercent / 100f).coerceAtLeast(0f)
                        } else 0f

                        val availableContentWidth = if (margin > 0) {
                            (pageWidth - (cols + 1) * margin) / cols
                        } else cellWidth

                        val availableContentHeight = if (margin > 0) {
                            (pageHeight - (rows + 1) * margin) / rows
                        } else cellHeight

                        for (i in 0 until pagesPerSheet) {
                            val pageIndex = startPageIndex + i
                            if (pageIndex >= totalPages) break

                            val sourcePage = document.getPage(pageIndex)
                            val sourceWidth = sourcePage.cropBox.width
                            val sourceHeight = sourcePage.cropBox.height

                            val scale = minOf(
                                availableContentWidth / sourceWidth,
                                availableContentHeight / sourceHeight
                            ).coerceAtMost(1f)

                            val scaledWidth = sourceWidth * scale
                            val scaledHeight = sourceHeight * scale

                            val col = i % cols
                            val row = i / cols

                            val cellLeft = col * cellWidth
                            val cellBottom = pageHeight - (row + 1) * cellHeight

                            val x: Float
                            val y: Float

                            if (margin > 0) {
                                val contentLeft = cellLeft + margin
                                val contentBottom = cellBottom + margin
                                val contentCenterX = contentLeft + availableContentWidth / 2
                                val contentCenterY = contentBottom + availableContentHeight / 2
                                x = contentCenterX - scaledWidth / 2
                                y = contentCenterY - scaledHeight / 2
                            } else {
                                x = cellLeft + (cellWidth - scaledWidth) / 2
                                y = cellBottom + (cellHeight - scaledHeight) / 2
                            }

                            val pdImage = Trickle
                                .drawColorBehind(
                                    input = renderer.safeRenderDpi(pageIndex, dpi),
                                    color = Color.White.toArgb()
                                )
                                .asXObject(newDoc, quality)

                            drawImage(pdImage, x, y, scaledWidth, scaledHeight)
                        }
                    }
                }

                newDoc.save(
                    filename = createTempName(
                        key = "printed",
                        uri = uri
                    )
                )
            }
        }
    }

    private suspend inline fun <T> catchPdf(
        crossinline action: suspend AndroidPdfHelper.() -> T
    ): T = withContext(defaultDispatcher) {
        try {
            helper.action()
        } catch (k: InvalidPasswordException) {
            throw SecurityException(k.message)
        } catch (e: Throwable) {
            e.makeLog("catchPdf")
            throw e
        }
    }

    private fun Bitmap.whiteBg(): Bitmap = Trickle.drawColorBehind(
        input = this,
        color = Color.White.toArgb()
    )

}