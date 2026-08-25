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
import androidx.core.net.toUri
import com.awxkee.aire.Aire
import com.t8rin.imagetoolbox.core.data.saving.io.ByteArrayReadable
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.saving.io.shielded
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.PDF
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.BaseMemoryConfig
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.HocrWord
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.asXObject
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPage
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.createPdf
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.crop
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.defaultFont
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getAllImages
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.getPageSafe
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.importPageForCopy
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.metadata
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.orAll
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.pageIndices
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.save
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setAlpha
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.setColor
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.transformImages
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.writePage
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfHelper
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfManager
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.ExtractPagesAction
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfAnnotationType
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCompareParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfContactSheetParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCreationParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeTheme
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfExtractPagesParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfMetadata
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfPageNumbersParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfPageResizeMode
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfRemoveAnnotationParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfResizeParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSanitizeParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfSignatureParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfWatermarkParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PrintPdfParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.SearchablePdfPage
import com.tom_roush.pdfbox.cos.COSDictionary
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDDocumentInformation
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.pdmodel.graphics.blend.BlendMode
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import com.tom_roush.pdfbox.pdmodel.graphics.state.RenderingMode
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLine
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationPopup
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationText
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class AndroidPdfManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val helper: AndroidPdfHelper,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PdfManager, PdfHelper by helper {

    override fun extractPages(
        uri: String,
        params: PdfExtractPagesParams
    ): Flow<ExtractPagesAction> = channelFlow {
        val scale = params.preset.value / 100f
        val dpi = 72f * scale

        catchPdf {
            useAndroidPdfRenderer(uri) { renderer ->
                params.pages.orAll(renderer).also {
                    send(ExtractPagesAction.PagesCount(it.size))
                }.forEach { pageIndex ->
                    send(
                        ExtractPagesAction.Progress(
                            index = pageIndex,
                            image = renderer.safeRenderDpi(
                                pageIndex = pageIndex,
                                dpi = dpi
                            )
                        )
                    )
                }
            }
        }
        close()
    }.flowOn(defaultDispatcher)

    override suspend fun createPdf(
        imageUris: List<String>,
        params: PdfCreationParams
    ): String = catchPdf {
        createPdfFromPreparedImages(
            images = prepareImagesForPdf(
                imageUris = imageUris,
                params = params
            ),
            quality = params.quality / 100f,
            scaleSmallImagesToLarge = params.scaleSmallImagesToLarge,
            addTextLayer = null
        )
    }

    override suspend fun createContactSheet(
        imageUris: List<String>,
        captions: List<List<String>>,
        params: PdfContactSheetParams
    ): String = catchPdf {
        if (imageUris.isEmpty()) error("No PDF created")

        val columns = params.columns.coerceIn(1, 10)
        val rows = params.rows.coerceIn(1, 10)
        val pageWidth = PDRectangle.A4.width
        val pageHeight = PDRectangle.A4.height
        val margin = params.margin.coerceIn(0f, min(pageWidth, pageHeight) / 3f)
        val spacing = params.spacing.coerceAtLeast(0f)
        val cellWidth = (pageWidth - margin * 2f - spacing * (columns - 1)) / columns
        val cellHeight = (pageHeight - margin * 2f - spacing * (rows - 1)) / rows
        if (cellWidth <= 0f || cellHeight <= 0f) error("Invalid contact sheet layout")

        createPdf { document ->
            val maxCaptionLines = captions.maxOfOrNull(List<String>::size) ?: 0
            val font = if (maxCaptionLines > 0) document.defaultFont else null
            val fontSize = 9f
            val lineHeight = 11f
            val captionGap = 4f
            val captionHeight = if (font != null) {
                captionGap + maxCaptionLines * lineHeight
            } else 0f
            val imageHeight = (cellHeight - captionHeight).coerceAtLeast(1f)
            val imageScale = 300f / 72f
            val decodeSize = IntegerSize(
                width = (cellWidth * imageScale).roundToInt().coerceIn(64, 4096),
                height = (imageHeight * imageScale).roundToInt().coerceIn(64, 4096)
            )

            imageUris.chunked(columns * rows).forEachIndexed { pageIndex, pageUris ->
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)
                pageUris.forEachIndexed cell@{ cellIndex, imageUri ->
                    val column = cellIndex % columns
                    val row = cellIndex / columns
                    val cellX = margin + column * (cellWidth + spacing)
                    val cellTop = pageHeight - margin - row * (cellHeight + spacing)
                    val bitmap = imageGetter.getImage(
                        data = imageUri,
                        size = decodeSize
                    ) ?: return@cell
                    document.writePage(page) {
                        val scale = min(
                            cellWidth / bitmap.width,
                            imageHeight / bitmap.height
                        )
                        val drawWidth = bitmap.width * scale
                        val drawHeight = bitmap.height * scale
                        val drawX = cellX + (cellWidth - drawWidth) / 2f
                        val drawY = cellTop - imageHeight + (imageHeight - drawHeight) / 2f

                        drawImage(
                            bitmap.asXObject(document, params.quality),
                            drawX,
                            drawY,
                            drawWidth,
                            drawHeight
                        )

                        font?.let { captionFont ->
                            captions
                                .getOrNull(pageIndex * columns * rows + cellIndex)
                                .orEmpty()
                                .forEachIndexed { lineIndex, rawLine ->
                                    val caption = rawLine
                                        .replace(Regex("[\\p{Cc}\\p{Cf}]"), " ")
                                        .trim()
                                        .fitPdfWidth(captionFont, fontSize, cellWidth)
                                    if (caption.isNotEmpty()) {
                                        val textWidth = captionFont.getStringWidth(caption) /
                                                1000f * fontSize
                                        beginText()
                                        setFont(captionFont, fontSize)
                                        newLineAtOffset(
                                            cellX + (cellWidth - textWidth) / 2f,
                                            drawY - captionGap - fontSize - lineIndex * lineHeight
                                        )
                                        showText(caption)
                                        endText()
                                    }
                                }
                        }
                    }
                }
            }

            shareProvider.cacheData(
                writeData = document::save,
                filename = tempName("contact_sheet")
            ) ?: error("No PDF created")
        }
    }

    override suspend fun createSearchablePdf(
        pages: List<SearchablePdfPage>,
        params: PdfCreationParams
    ): String = catchPdf {
        createPdfFromPreparedImages(
            images = prepareImagesForPdf(
                imageUris = pages.map(SearchablePdfPage::imageUri),
                params = params
            ),
            quality = params.quality / 100f,
            scaleSmallImagesToLarge = params.scaleSmallImagesToLarge,
            addTextLayer = { pageIndex, pageWidth, pageHeight, document ->
                val page = pages.getOrNull(pageIndex) ?: return@createPdfFromPreparedImages
                val hocrData = page.hocr.let(::parseHocrData)
                val sourcePageWidth = hocrData.pageBox?.width?.takeIf { it > 0f } ?: pageWidth
                val sourcePageHeight = hocrData.pageBox?.height?.takeIf { it > 0f } ?: pageHeight
                val scaleX = (pageWidth / sourcePageWidth).coerceAtLeast(0.0001f)
                val scaleY = (pageHeight / sourcePageHeight).coerceAtLeast(0.0001f)
                val font = document.defaultFont

                val words = hocrData.words
                    .ifEmpty {
                        page.text
                            .lineSequence()
                            .map(String::trim)
                            .filter(String::isNotBlank)
                            .take(300)
                            .mapIndexed { index, line ->
                                HocrWord(
                                    left = 8f,
                                    top = (index * 14f),
                                    right = 8f + 1000f,
                                    bottom = (index * 14f) + 12f,
                                    text = line
                                )
                            }
                            .toList()
                    }

                words.forEach { word ->
                    val text = word.text.cleanPdfText(font)
                    if (text.isBlank()) return@forEach

                    val left = word.left * scaleX
                    val right = word.right * scaleX
                    val top = word.top * scaleY
                    val bottom = word.bottom * scaleY

                    val boxHeight = (bottom - top).coerceAtLeast(1f)
                    val targetWidth = (right - left).coerceAtLeast(1f)
                    val x = left.coerceIn(0f, pageWidth - 1f)

                    val glyphWidthEm = (font.getStringWidth(text) / 1000f)
                        .coerceAtLeast(0.001f)

                    val fontByHeight = (boxHeight * 0.84f).coerceAtLeast(1f)
                    val fontByWidth = (targetWidth / glyphWidthEm).coerceAtLeast(1f)
                    val fontSize = (fontByHeight * 0.72f + fontByWidth * 0.28f)
                        .coerceIn(1f, pageHeight.coerceAtLeast(1f))

                    val sourceWidth = (glyphWidthEm * fontSize).coerceAtLeast(0.1f)
                    val horizontalScale = (targetWidth / sourceWidth * 100f).coerceIn(80f, 125f)

                    val y = (pageHeight - bottom +
                            ((boxHeight - fontSize).coerceAtLeast(0f) * 0.5f) +
                            (fontSize * 0.10f)
                            ).coerceIn(0f, pageHeight - 1f)

                    beginText()
                    setRenderingMode(RenderingMode.NEITHER)
                    setFont(font, fontSize)
                    setHorizontalScaling(horizontalScale)
                    newLineAtOffset(x, y)
                    showText(text)
                    endText()
                }
            }
        )
    }

    override suspend fun mergePdfs(uris: List<String>): String = catchPdf {
        PDFMergerUtility().run {
            uris.forEach { uri ->
                addSource(UriReadable(uri.toUri(), context).stream)
            }
            shareProvider.cacheDataOrThrow(filename = tempName("merged")) { output ->
                destinationStream = output.outputStream()
                mergeDocuments(BaseMemoryConfig)
            }
        }
    }

    override suspend fun splitPdf(
        uri: String,
        pages: List<Int>?
    ): String = catchPdf {
        usePdf(uri) { document ->
            createPdf { newDoc ->
                newDoc.document.version = document.version

                pages.orAll(document).forEach { index ->
                    newDoc.importPageForCopy(document.getPageSafe(index))
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
            val indicesToRemove = pages
                .asSequence()
                .filter { it in 0 until document.numberOfPages }
                .distinct()
                .sortedDescending()
                .toList()

            if (indicesToRemove.size >= document.numberOfPages) {
                error(getString(R.string.cant_remove_all))
            }

            indicesToRemove.forEach { index ->
                document.removePage(index)
            }

            document.save(
                filename = tempName(
                    key = "removed",
                    uri = uri
                )
            )
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
                newDoc.document.version = document.version

                newOrder.forEach { pageIndex ->
                    newDoc.importPageForCopy(document.getPageSafe(pageIndex))
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
        params: PdfPageNumbersParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val font = document.defaultFont
            val fontDescriptor = font.fontDescriptor
            val totalPages = document.numberOfPages
            val label = params.labelFormat
                .replace("{total}", totalPages.toString())

            document.pages.forEachIndexed { idx, page ->
                val text = label
                    .replace("{n}", (idx + 1).toString())
                    .cleanPdfText(font)

                if (text.isBlank()) return@forEachIndexed

                val cropBox = page.cropBox
                val pageWidth = cropBox.width
                val pageHeight = cropBox.height
                val originX = cropBox.lowerLeftX
                val originY = cropBox.lowerLeftY

                val glyphWidthEm = (font.getStringWidth(text) / 1000f).coerceAtLeast(0.001f)
                val fontSize = pageWidth * params.fontSize / 100f / glyphWidthEm
                val textWidth = glyphWidthEm * fontSize
                val textAscent = fontDescriptor.ascent / 1000f * fontSize
                val textDescent = fontDescriptor.descent / 1000f * fontSize
                val textVerticalCenter =
                    (fontDescriptor.ascent + fontDescriptor.descent) / 2000f * fontSize

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
                    Position.TopLeft,
                    Position.TopCenter,
                    Position.TopRight -> baseY - textAscent

                    Position.CenterLeft,
                    Position.Center,
                    Position.CenterRight -> baseY - textVerticalCenter

                    Position.BottomLeft,
                    Position.BottomCenter,
                    Position.BottomRight -> baseY - textDescent
                }

                val adjustedXWithOrigin = adjustedX + originX
                val adjustedYWithOrigin = adjustedY + originY

                document.writePage(page) {
                    beginText()
                    setFont(font, fontSize)
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
        params: PdfWatermarkParams
    ): String = catchPdf {
        val color = Color(params.color)

        usePdf(uri) { document ->
            val font = document.defaultFont
            val fontDescriptor = font.fontDescriptor

            params.pages.orAll(document).forEach { pageIndex ->
                val page = document.getPageSafe(pageIndex)
                val text = params.text.cleanPdfText(font)

                if (text.isBlank()) return@forEach

                val radians = Math.toRadians(-params.rotation.toDouble())
                val cropBox = page.cropBox
                val glyphWidthEm = (font.getStringWidth(text) / 1000f).coerceAtLeast(0.001f)
                val fontSize = cropBox.width * params.fontSize / 100f / glyphWidthEm

                val textWidth =
                    glyphWidthEm * fontSize
                val textVerticalCenter =
                    (fontDescriptor.ascent + fontDescriptor.descent) / 2000f * fontSize

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
                    setFont(font, fontSize)
                    setColor(color.copy(params.opacity))
                    setTextMatrix(matrix)
                    newLineAtOffset(-textWidth / 2f, -textVerticalCenter)
                    showText(text)
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
        params: PdfSignatureParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val signatureImage = imageGetter.getImage(data = params.signatureImage)!!.asXObject(
                document = document,
                quality = 1f
            )

            val imageAspect = signatureImage.width.toFloat() / signatureImage.height.toFloat()

            params.pages.orAll(document).forEach { pageIndex ->
                val page = document.getPageSafe(pageIndex)

                val crop = page.cropBox

                val pageWidth = crop.width
                val pageHeight = crop.height
                val originX = crop.lowerLeftX
                val originY = crop.lowerLeftY

                val targetWidth = pageWidth * params.size
                val targetHeight = targetWidth / imageAspect

                val centerX = pageWidth * params.x
                val centerY = pageHeight * params.y

                var x = centerX - targetWidth / 2f
                var y = centerY - targetHeight / 2f

                x = x.coerceIn(0f, pageWidth - targetWidth)
                y = y.coerceIn(0f, pageHeight - targetHeight)

                x += originX
                y += originY

                document.writePage(page) {
                    saveGraphicsState()
                    setAlpha(params.opacity)
                    drawImage(signatureImage, x, y, targetWidth, targetHeight)
                    restoreGraphicsState()
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
        useAndroidPdfRenderer(uri) { renderer ->
            renderer.pageIndices.mapNotNull { pageIndex ->
                val bitmap = renderer.safeRenderDpi(
                    pageIndex = pageIndex,
                    dpi = 72f
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
        }
    }

    override suspend fun compressPdf(
        uri: String,
        quality: Float
    ): String = catchPdf {
        usePdf(uri) { document ->
            document.transformImages(
                quality = quality,
                transform = { it }
            )
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
            document.transformImages(
                quality = 0.8f,
                transform = {
                    Aire.saturation(
                        bitmap = it,
                        saturation = 0f,
                        tonemap = false
                    )
                }
            )
            document.save(
                filename = tempName(
                    key = "grayscale",
                    uri = uri
                )
            )
        }
    }

    override suspend fun convertToDarkMode(
        uri: String,
        params: PdfDarkModeParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            if (document.version < 1.4f) {
                document.version = 1.4f
            }

            document.pages.forEach { page ->
                val cropBox = page.cropBox

                PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.PREPEND,
                    true,
                    true
                ).use { stream ->
                    stream.setColor(Color.White)
                    stream.addRect(
                        cropBox.lowerLeftX,
                        cropBox.lowerLeftY,
                        cropBox.width,
                        cropBox.height
                    )
                    stream.fill()
                }

                PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
                ).use { stream ->
                    fun fillPage(
                        blendMode: BlendMode,
                        color: Color
                    ) {
                        stream.saveGraphicsState()
                        stream.setGraphicsStateParameters(
                            PDExtendedGraphicsState().apply {
                                this.blendMode = blendMode
                            }
                        )
                        stream.setColor(color)
                        stream.addRect(
                            cropBox.lowerLeftX,
                            cropBox.lowerLeftY,
                            cropBox.width,
                            cropBox.height
                        )
                        stream.fill()
                        stream.restoreGraphicsState()
                    }

                    if (params.theme != PdfDarkModeTheme.Negative) {
                        fillPage(
                            blendMode = BlendMode.SATURATION,
                            color = Color.White
                        )
                    }

                    fillPage(
                        blendMode = BlendMode.DIFFERENCE,
                        color = Color.White
                    )

                    params.overlayColor?.let { backgroundColor ->
                        fillPage(
                            blendMode = params.overlayBlendMode.toPdfBlendMode(),
                            color = Color(backgroundColor)
                        )
                    }
                }
            }

            document.save(
                filename = tempName(
                    key = "dark_mode",
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
        params: PdfCropParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            params.pages.orAll(document).forEach { pageIndex ->
                document.getPageSafe(pageIndex).let { page ->
                    page.cropBox = page.cropBox.crop(
                        rotation = page.rotation,
                        rect = params.rect
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
            useAndroidPdfRenderer(uri) { renderer ->
                createPdf { newDoc ->
                    document.pages.forEachIndexed { index, page ->
                        val cropBox = page.cropBox

                        val pdImage = renderer
                            .safeRenderDpi(index, dpi)
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
                                newDoc.document.version = document.version

                                pages.forEach { pageIndex ->
                                    newDoc.importPageForCopy(document.getPageSafe(pageIndex))
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
        params: PrintPdfParams
    ): String = catchPdf {
        val dpi = 72f + (228f * params.quality)

        usePdf(uri) { document ->
            useAndroidPdfRenderer(uri) { renderer ->
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

                                val pdImage = renderer
                                    .safeRenderDpi(
                                        pageIndex = pageIndex,
                                        dpi = (dpi * scale).coerceAtLeast(36f)
                                    )
                                    .asXObject(
                                        document = newDoc,
                                        quality = params.quality
                                    )

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
    }

    override suspend fun removeAnnotations(
        uri: String,
        params: PdfRemoveAnnotationParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val removeAll = params.types == PdfAnnotationType.setEntries

            params.pages.orAll(document).forEach { pageIndex ->
                val page = document.getPageSafe(pageIndex)

                if (removeAll) {
                    page.annotations = emptyList()
                } else {
                    page.annotations = page.annotations.filterNot { annotation ->
                        params.types.any { type ->
                            when (type) {
                                PdfAnnotationType.Link -> annotation is PDAnnotationLink
                                PdfAnnotationType.FileAttachment -> annotation is PDAnnotationFileAttachment
                                PdfAnnotationType.Line -> annotation is PDAnnotationLine
                                PdfAnnotationType.Popup -> annotation is PDAnnotationPopup
                                PdfAnnotationType.Stamp -> annotation is PDAnnotationRubberStamp
                                PdfAnnotationType.SquareCircle -> annotation is PDAnnotationSquareCircle
                                PdfAnnotationType.Text -> annotation is PDAnnotationText
                                PdfAnnotationType.TextMarkup -> annotation is PDAnnotationTextMarkup
                                PdfAnnotationType.Widget -> annotation is PDAnnotationWidget
                                PdfAnnotationType.Markup -> annotation is PDAnnotationMarkup
                                PdfAnnotationType.Unknown -> annotation is PDAnnotationUnknown
                            }
                        }
                    }
                }
            }

            document.save(
                filename = tempName(
                    key = "annotations_removed",
                    uri = uri
                )
            )
        }
    }

    override suspend fun comparePdfs(
        firstUri: String,
        secondUri: String,
        params: PdfCompareParams
    ): String = catchPdf {
        useAndroidPdfRenderer(firstUri) { firstRenderer ->
            useAndroidPdfRenderer(secondUri) { secondRenderer ->
                createPdf { document ->
                    val pageCount = max(firstRenderer.pageCount, secondRenderer.pageCount)

                    repeat(pageCount) { pageIndex ->
                        val first = pageIndex.takeIf { it < firstRenderer.pageCount }?.let {
                            firstRenderer.safeRenderDpi(it, 144f)
                        }
                        val second = pageIndex.takeIf { it < secondRenderer.pageCount }?.let {
                            secondRenderer.safeRenderDpi(it, 144f)
                        }
                        val difference = createPdfDifferenceBitmap(
                            first = first,
                            second = second,
                            highlightColor = params.highlightColor,
                            comparisonType = params.comparisonType
                        )
                        val pageSize = PDRectangle(
                            difference.width.toFloat(),
                            difference.height.toFloat()
                        )

                        document.createPage(PDPage(pageSize)) {
                            drawImage(
                                difference.asXObject(document, 0.9f),
                                0f,
                                0f,
                                pageSize.width,
                                pageSize.height
                            )
                        }
                    }

                    document.save(
                        filename = createTempName(
                            key = "compared"
                        )
                    )
                }
            }
        }
    }

    override suspend fun resizePdfPages(
        uri: String,
        params: PdfResizeParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val targetWidth = params.pageSize.width.toFloat()
            val targetHeight = params.pageSize.height.toFloat()

            document.pages.forEach { page ->
                val source = page.cropBox
                val scaleX = targetWidth / source.width
                val scaleY = targetHeight / source.height
                val (contentScaleX, contentScaleY) = when (params.mode) {
                    PdfPageResizeMode.Fit -> min(scaleX, scaleY).let { it to it }
                    PdfPageResizeMode.Fill -> max(scaleX, scaleY).let { it to it }
                    PdfPageResizeMode.Stretch -> scaleX to scaleY
                }
                val offsetX = (targetWidth - source.width * contentScaleX) / 2f -
                        source.lowerLeftX * contentScaleX
                val offsetY = (targetHeight - source.height * contentScaleY) / 2f -
                        source.lowerLeftY * contentScaleY

                PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.PREPEND,
                    true,
                    true
                ).use { stream ->
                    stream.transform(
                        Matrix(
                            contentScaleX,
                            0f,
                            0f,
                            contentScaleY,
                            offsetX,
                            offsetY
                        )
                    )
                }
                page.annotations.forEach { annotation ->
                    val rectangle = annotation.rectangle
                    val left = rectangle.lowerLeftX * contentScaleX + offsetX
                    val bottom = rectangle.lowerLeftY * contentScaleY + offsetY
                    val right = rectangle.upperRightX * contentScaleX + offsetX
                    val top = rectangle.upperRightY * contentScaleY + offsetY
                    annotation.rectangle = PDRectangle(
                        left,
                        bottom,
                        right - left,
                        top - bottom
                    )
                }
                page.mediaBox = PDRectangle(targetWidth, targetHeight)
                page.cropBox = PDRectangle(targetWidth, targetHeight)
            }

            document.save(filename = tempName(key = "resized_pages", uri = uri))
        }
    }

    override suspend fun sanitizePdf(
        uri: String,
        params: PdfSanitizeParams
    ): String = catchPdf {
        usePdf(uri) { document ->
            val catalog = document.documentCatalog.cosObject

            if (params.metadata) {
                document.documentInformation = PDDocumentInformation()
                document.document.trailer.removeItem(COSName.INFO)
                catalog.removeItem(COSName.METADATA)
            }
            if (params.scripts) {
                catalog.removeItem(COSName.OPEN_ACTION)
                catalog.removeItem(COSName.AA)
            }
            if (params.forms) {
                catalog.removeItem(COSName.ACRO_FORM)
            }
            val names = catalog.getDictionaryObject(COSName.NAMES) as? COSDictionary
            if (params.attachments) {
                names?.removeItem(COSName.getPDFName("EmbeddedFiles"))
            }
            if (params.scripts) {
                names?.removeItem(COSName.getPDFName("JavaScript"))
            }

            document.pages.forEach { page ->
                page.annotations = when {
                    params.annotations -> emptyList()
                    params.forms || params.attachments -> page.annotations.filterNot { annotation ->
                        params.forms && annotation is PDAnnotationWidget ||
                                params.attachments && annotation is PDAnnotationFileAttachment
                    }

                    else -> page.annotations
                }
                if (params.metadata) page.cosObject.removeItem(COSName.METADATA)
                if (params.scripts) page.cosObject.removeItem(COSName.AA)
            }

            document.save(
                filename = tempName(
                    key = "sanitized",
                    uri = uri
                )
            )
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

}
