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

package com.t8rin.imagetoolbox.feature.palette_pdf.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withClip
import androidx.core.net.toUri
import com.t8rin.colors.extractImageColorPalette
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.PalettePdfExporter
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.model.PalettePdfColor
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.model.PalettePdfParams
import com.t8rin.imagetoolbox.feature.palette_pdf.domain.model.PalettePdfSourceType
import com.t8rin.palette.Palette
import com.t8rin.palette.PaletteFormat
import com.t8rin.palette.decode
import com.t8rin.palette.getCoder
import com.t8rin.palette.use
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.min

internal class AndroidPalettePdfExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : PalettePdfExporter, DispatchersHolder by dispatchersHolder {

    override suspend fun detectSourceType(
        sourceUri: String
    ): PalettePdfSourceType? = withContext(defaultDispatcher) {
        val uri = sourceUri.toUri()
        val filename = uri.filename(context) ?: sourceUri
        val hasPaletteExtension = PaletteFormat.matchingFilename(filename)
            .any { it != PaletteFormat.IMAGE }

        if (hasPaletteExtension && decodePalette(sourceUri, null) != null) {
            return@withContext PalettePdfSourceType.PaletteFile
        }

        val image = loadSourceBitmap(sourceUri)
        if (image != null) {
            return@withContext PalettePdfSourceType.Image
        }

        decodePalette(sourceUri, null)?.let { PalettePdfSourceType.PaletteFile }
    }

    override suspend fun export(
        sourceUri: String,
        sourceType: PalettePdfSourceType,
        sourcePaletteFormat: String?,
        params: PalettePdfParams,
        writeable: Writeable
    ): Unit = withContext(defaultDispatcher) {
        val source = prepareSource(
            sourceUri = sourceUri,
            sourceType = sourceType,
            sourcePaletteFormat = sourcePaletteFormat,
            maximumColorCount = params.maximumColorCount
        )
        require(source.colors.isNotEmpty()) { "Palette must contain at least one color" }

        val document = PdfDocument()
        try {
            PalettePdfRenderer(
                document = document,
                image = source.image,
                colors = source.colors,
                params = params.copy(
                    includeSourceImage = params.includeSourceImage && source.image != null
                ),
                sourceFilename = source.filename,
                paletteName = source.paletteName
            ).render()
            document.writeTo(writeable.outputStream())
        } finally {
            document.close()
        }
    }

    private suspend fun prepareSource(
        sourceUri: String,
        sourceType: PalettePdfSourceType,
        sourcePaletteFormat: String?,
        maximumColorCount: Int
    ): PalettePdfSource = when (sourceType) {
        PalettePdfSourceType.Image -> prepareImageSource(
            sourceUri = sourceUri,
            maximumColorCount = maximumColorCount
        )

        PalettePdfSourceType.PaletteFile -> preparePaletteSource(
            sourceUri = sourceUri,
            sourcePaletteFormat = sourcePaletteFormat
        )
    }

    private suspend fun prepareImageSource(
        sourceUri: String,
        maximumColorCount: Int
    ): PalettePdfSource {
        val image = imageScaler.scaleUntilCanShow(
            loadSourceBitmap(sourceUri)
        ) ?: error("Unable to load palette source image")

        val colors = extractImageColorPalette(
            image = image,
            maximumColorCount = maximumColorCount
        ).map { paletteData ->
            val argb = paletteData.colorData.color.toArgb()
            PalettePdfColor(
                argb = argb,
                name = paletteData.colorData.name,
                hex = argb.toHex()
            )
        }

        return PalettePdfSource(
            image = image,
            colors = colors,
            filename = sourceUri.toUri().filename(context)
                ?: sourceUri.toUri().lastPathSegment.orEmpty(),
            paletteName = null
        )
    }

    private suspend fun loadSourceBitmap(sourceUri: String): Bitmap? = runCatching {
        imageGetter.getImage(
            data = sourceUri,
            originalSize = false
        ) ?: context.contentResolver.openInputStream(sourceUri.toUri())?.use {
            BitmapFactory.decodeStream(it)
        }
    }.getOrNull()

    private fun preparePaletteSource(
        sourceUri: String,
        sourcePaletteFormat: String?
    ): PalettePdfSource {
        val palette = decodePalette(
            sourceUri = sourceUri,
            sourcePaletteFormat = sourcePaletteFormat
        )
            ?: error("Unable to decode palette source")
        val colors = palette.allColors()
            .filter { it.alpha > 0.0 }
            .map { paletteColor ->
                val argb = paletteColor.toArgb()
                PalettePdfColor(
                    argb = argb,
                    name = paletteColor.name,
                    hex = argb.toHex()
                )
            }
            .distinctBy { it.argb to it.name }

        return PalettePdfSource(
            image = null,
            colors = colors,
            filename = sourceUri.toUri().filename(context)
                ?: sourceUri.toUri().lastPathSegment.orEmpty(),
            paletteName = palette.name
        )
    }

    private fun decodePalette(
        sourceUri: String,
        sourcePaletteFormat: String?
    ): Palette? {
        val uri = sourceUri.toUri()
        val filename = uri.filename(context) ?: sourceUri
        val knownFormat = sourcePaletteFormat?.let { name ->
            PaletteFormat.entries.firstOrNull { it.name == name }
        }
        val formats = (
                listOfNotNull(knownFormat) +
                        PaletteFormat.matchingFilename(filename) +
                        PALETTE_FORMATS
                ).distinct()

        return formats.firstNotNullOfOrNull { format ->
            format.getCoder().use {
                decode(
                    uri = uri,
                    context = context
                )
            }.getOrNull()?.takeIf { it.totalColorCount > 0 }
        }
    }

    private fun Int.toHex(): String = if (ushr(24) == 0xFF) {
        "#%06X".format(this and 0xFFFFFF)
    } else {
        "#%08X".format(this)
    }

    private companion object {
        val PALETTE_FORMATS = PaletteFormat.entries.toSet().minus(
            setOf(
                PaletteFormat.CSV,
                PaletteFormat.HEX_RGBA
            )
        ).plus(
            setOf(
                PaletteFormat.HEX_RGBA,
                PaletteFormat.CSV
            )
        ).toList()
    }
}

private data class PalettePdfSource(
    val image: Bitmap?,
    val colors: List<PalettePdfColor>,
    val filename: String,
    val paletteName: String?
)

private class PalettePdfRenderer(
    private val document: PdfDocument,
    private val image: Bitmap?,
    private val colors: List<PalettePdfColor>,
    params: PalettePdfParams,
    private val sourceFilename: String,
    private val paletteName: String?
) {
    private val columns = params.columns.coerceIn(1, 6)
    private val margin = params.margin.coerceIn(0f, PAGE_WIDTH / 3f)
    private val spacing = params.spacing.coerceIn(0f, 36f)
    private val includeSourceImage = params.includeSourceImage
    private val showSourceFilename = params.showSourceFilename
    private val showPaletteName = params.showPaletteName
    private val showColorNames = params.showColorNames
    private val showHexValues = params.showHexValues

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(247, 247, 249)
    }
    private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(28, 0, 0, 0)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 34)
        textSize = 22f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    private val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(95, 95, 102)
        textSize = 12f
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
    private val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(35, 35, 39)
        textSize = 10f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
    }
    private val hexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(95, 95, 102)
        textSize = 9f
        typeface = Typeface.create("monospace", Typeface.NORMAL)
    }
    private val pageNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(115, 115, 122)
        textSize = 8f
        textAlign = Paint.Align.RIGHT
    }

    fun render() {
        val cellWidth = (PAGE_WIDTH - margin * 2f - spacing * (columns - 1)) / columns
        require(cellWidth > 0f) { "Invalid palette PDF layout" }

        val captionHeight = when {
            showColorNames && showHexValues -> 38f
            showColorNames || showHexValues -> 26f
            else -> 0f
        }
        val cellHeight = (cellWidth * 0.62f + captionHeight).coerceIn(64f, 160f)

        var colorIndex = 0
        var pageNumber = 1
        do {
            val page = document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            val canvas = page.canvas
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), backgroundPaint)

            val contentTop = drawHeader(canvas)
            val gridTop = if (includeSourceImage && image != null && pageNumber == 1) {
                drawSourceImage(canvas, image, contentTop) + spacing
            } else {
                contentTop
            }
            val gridBottom = PAGE_HEIGHT - margin - FOOTER_HEIGHT
            val rows = floor((gridBottom - gridTop + spacing) / (cellHeight + spacing))
                .toInt()
                .coerceAtLeast(1)
            val pageCapacity = rows * columns

            colors
                .drop(colorIndex)
                .take(pageCapacity)
                .forEachIndexed { index, color ->
                    val column = index % columns
                    val row = index / columns
                    val left = margin + column * (cellWidth + spacing)
                    val top = gridTop + row * (cellHeight + spacing)
                    drawColorCard(
                        canvas = canvas,
                        color = color,
                        bounds = RectF(left, top, left + cellWidth, top + cellHeight),
                        captionHeight = captionHeight
                    )
                }

            colorIndex += pageCapacity
            canvas.drawText(
                pageNumber.toString(),
                PAGE_WIDTH - margin,
                PAGE_HEIGHT - margin / 2f,
                pageNumberPaint
            )
            document.finishPage(page)
            pageNumber++
        } while (colorIndex < colors.size)
    }

    private fun drawHeader(canvas: Canvas): Float {
        val lines = buildList {
            if (showSourceFilename && sourceFilename.isNotBlank()) {
                add(sourceFilename)
            }
            if (showPaletteName && !paletteName.isNullOrBlank()) {
                add(paletteName)
            }
        }
        if (lines.isEmpty()) return margin

        canvas.drawText(
            lines.first().ellipsize(titlePaint, PAGE_WIDTH - margin * 2f),
            margin,
            margin + titlePaint.textSize,
            titlePaint
        )

        if (lines.size > 1) {
            canvas.drawText(
                lines.drop(1).joinToString(" · ")
                    .ellipsize(subtitlePaint, PAGE_WIDTH - margin * 2f),
                margin,
                margin + titlePaint.textSize + subtitlePaint.textSize + 6f,
                subtitlePaint
            )
        }

        return margin + titlePaint.textSize + if (lines.size > 1) 30f else 18f
    }

    private fun drawSourceImage(
        canvas: Canvas,
        image: Bitmap,
        top: Float
    ): Float {
        val width = PAGE_WIDTH - margin * 2f
        val maxHeight = 220f
        val scale = min(width / image.width, maxHeight / image.height)
        val drawWidth = image.width * scale
        val drawHeight = image.height * scale
        val left = margin + (width - drawWidth) / 2f
        val bounds = RectF(left, top, left + drawWidth, top + drawHeight)

        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, cardPaint)
        canvas.withClip(bounds.roundedPath()) {
            drawBitmap(image, null, bounds, null)
        }
        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, borderPaint)
        return bounds.bottom
    }

    private fun drawColorCard(
        canvas: Canvas,
        color: PalettePdfColor,
        bounds: RectF,
        captionHeight: Float
    ) {
        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, cardPaint)

        val swatchBounds = RectF(bounds).apply {
            bottom -= captionHeight
        }
        val swatchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.argb
        }
        canvas.withClip(bounds.roundedPath()) {
            drawRect(swatchBounds, swatchPaint)
        }

        if (captionHeight > 0f) {
            val textLeft = bounds.left + 10f
            val maxTextWidth = bounds.width() - 20f
            when {
                showColorNames && showHexValues -> {
                    canvas.drawText(
                        color.name.ellipsize(namePaint, maxTextWidth),
                        textLeft,
                        swatchBounds.bottom + 15f,
                        namePaint
                    )
                    canvas.drawText(
                        color.hex.uppercase().ellipsize(hexPaint, maxTextWidth),
                        textLeft,
                        swatchBounds.bottom + 29f,
                        hexPaint
                    )
                }

                showColorNames -> canvas.drawText(
                    color.name.ellipsize(namePaint, maxTextWidth),
                    textLeft,
                    swatchBounds.bottom + 17f,
                    namePaint
                )

                showHexValues -> canvas.drawText(
                    color.hex.uppercase().ellipsize(hexPaint, maxTextWidth),
                    textLeft,
                    swatchBounds.bottom + 17f,
                    hexPaint
                )
            }
        }

        canvas.drawRoundRect(bounds, CORNER_RADIUS, CORNER_RADIUS, borderPaint)
    }

    private fun String.ellipsize(paint: Paint, maxWidth: Float): String {
        if (paint.measureText(this) <= maxWidth) return this

        val ellipsis = "..."
        var end = length
        while (end > 0 && paint.measureText(substring(0, end) + ellipsis) > maxWidth) {
            end--
        }
        return substring(0, end) + ellipsis
    }

    private fun RectF.roundedPath(): Path = Path().apply {
        addRoundRect(this@roundedPath, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW)
    }

    private companion object {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val FOOTER_HEIGHT = 12f
        const val CORNER_RADIUS = 8f
    }
}
