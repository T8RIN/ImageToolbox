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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withSave
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewParams
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private const val RenderScale = 3f
private const val BaseCardWidth = 560f
private const val MaxBitmapPixels = 65_545_216f // 8096 * 8096
private const val BitmapMemoryFraction = 0.45f
private const val BytesPerPixel = 4

internal fun renderCodePreviewBitmap(
    params: CodePreviewParams,
    highlightedCode: AnnotatedString,
    maxBitmapPixels: Float = MaxBitmapPixels
): Bitmap {
    val geometry = createGeometry(
        params = params,
        highlightedCode = highlightedCode,
        scale = RenderScale
    )
    val pixelLimit = min(maxBitmapPixels, availableBitmapPixels()).coerceAtLeast(1f)
    val pixelScale = sqrt(
        pixelLimit / (
                geometry.canvasWidth.toFloat() * geometry.canvasHeight.toFloat()
                )
    )
    val outputScale = min(1f, pixelScale).let { scale ->
        if (scale < 1f) scale * 0.99f else scale
    }

    return geometry.render(params, outputScale)
}

private fun availableBitmapPixels(): Float {
    val runtime = Runtime.getRuntime()
    val usedMemory = runtime.totalMemory() - runtime.freeMemory()
    val availableMemory = (runtime.maxMemory() - usedMemory).coerceAtLeast(0L)
    return availableMemory * BitmapMemoryFraction / BytesPerPixel
}

@Suppress("SameParameterValue")
private fun createGeometry(
    params: CodePreviewParams,
    highlightedCode: AnnotatedString,
    scale: Float
): RenderGeometry {
    val theme = params.theme.highlightTheme()
    val fontSize = params.fontSize * scale
    val innerPadding = params.innerPadding * scale
    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = theme.defaultTextColor.toArgb()
        textSize = fontSize
        typeface = Typeface.MONOSPACE
    }
    val highlightedText = highlightedCode.toSpannable(
        fallbackText = params.code,
        fallbackColor = theme.defaultTextColor
    )
    val lineCount = params.code.lineSequence().count().coerceAtLeast(1)
    val lineNumberGap = if (params.showLineNumbers) 14f * scale else 0f
    val lineNumberWidth = if (params.showLineNumbers) {
        Layout.getDesiredWidth(lineCount.toString(), textPaint)
    } else 0f
    val minimumCardWidth = BaseCardWidth * scale
    val desiredCodeWidth = if (params.wrapLongLines) {
        0f
    } else {
        Layout.getDesiredWidth(highlightedText, textPaint) + scale
    }
    val cardWidth = maxOf(
        minimumCardWidth,
        desiredCodeWidth + lineNumberWidth + lineNumberGap + innerPadding * 2f
    ).let(::ceil).toInt()
    val codeWidth = (
            cardWidth - innerPadding * 2f - lineNumberWidth - lineNumberGap
            ).toInt().coerceAtLeast(1)
    val codeLayout = staticLayout(
        text = highlightedText,
        paint = textPaint,
        width = codeWidth,
        alignment = Layout.Alignment.ALIGN_NORMAL
    )
    val lineNumbersLayout = if (params.showLineNumbers) {
        staticLayout(
            text = codeLayout.lineNumbers(params.code),
            paint = TextPaint(textPaint).apply {
                color = theme.defaultTextColor.copy(alpha = 0.32f).toArgb()
            },
            width = ceil(lineNumberWidth).toInt().coerceAtLeast(1),
            alignment = Layout.Alignment.ALIGN_OPPOSITE
        )
    } else null
    val headerHeight = if (params.showWindowControls || params.showTitle) {
        42f * scale
    } else 0f
    val dividerHeight = if (headerHeight > 0f) scale.coerceAtLeast(1f) else 0f
    val contentHeight = maxOf(codeLayout.height, lineNumbersLayout?.height ?: 0)
    val cardHeight = ceil(
        headerHeight + dividerHeight + innerPadding * 2f + contentHeight
    ).toInt()
    val shadowPadding = if (params.showCardShadow && params.cardShadowBlurRadius > 0) {
        ceil(
            (params.cardShadowBlurRadius + maxOf(
                abs(params.cardShadowOffsetX),
                abs(params.cardShadowOffsetY)
            )) * scale
        ).toInt()
    } else 0
    val layerWidth = cardWidth + shadowPadding * 2
    val layerHeight = cardHeight + shadowPadding * 2
    val rotationRadians = Math.toRadians(abs(params.rotation).toDouble())
    val rotatedWidth = ceil(
        layerWidth * cos(rotationRadians) + layerHeight * sin(rotationRadians)
    ).toInt()
    val rotatedHeight = ceil(
        layerWidth * sin(rotationRadians) + layerHeight * cos(rotationRadians)
    ).toInt()
    val outerPadding = ceil(params.outerPadding * scale).toInt()

    return RenderGeometry(
        scale = scale,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        headerHeight = headerHeight,
        dividerHeight = dividerHeight,
        innerPadding = innerPadding,
        lineNumberWidth = lineNumberWidth,
        lineNumberGap = lineNumberGap,
        codeLayout = codeLayout,
        lineNumbersLayout = lineNumbersLayout,
        shadowPadding = shadowPadding,
        canvasWidth = rotatedWidth + outerPadding * 2,
        canvasHeight = rotatedHeight + outerPadding * 2
    )
}

private fun staticLayout(
    text: CharSequence,
    paint: TextPaint,
    width: Int,
    alignment: Layout.Alignment
): StaticLayout = StaticLayout.Builder
    .obtain(text, 0, text.length, paint, width)
    .setAlignment(alignment)
    .setIncludePad(false)
    .setLineSpacing(0f, 1.55f)
    .build()

private fun AnnotatedString.toSpannable(
    fallbackText: String,
    fallbackColor: Color
): SpannableString {
    val source = takeIf { text == fallbackText } ?: AnnotatedString(fallbackText)
    return SpannableString(source.text).apply {
        source.spanStyles.forEach { range ->
            val start = range.start.coerceIn(0, length)
            val end = range.end.coerceIn(0, length)
            val color = range.item.color.takeUnless { it == Color.Unspecified } ?: fallbackColor
            if (start < end) {
                setSpan(
                    ForegroundColorSpan(color.toArgb()),
                    start,
                    end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}

private fun StaticLayout.lineNumbers(code: String): String {
    val logicalLineStarts = buildMap {
        put(0, 1)
        var line = 2
        code.forEachIndexed { index, character ->
            if (character == '\n') put(index + 1, line++)
        }
    }
    return (0 until lineCount).joinToString(separator = "\n") { line ->
        logicalLineStarts[getLineStart(line)]?.toString().orEmpty()
    }
}

private data class RenderGeometry(
    val scale: Float,
    val cardWidth: Int,
    val cardHeight: Int,
    val headerHeight: Float,
    val dividerHeight: Float,
    val innerPadding: Float,
    val lineNumberWidth: Float,
    val lineNumberGap: Float,
    val codeLayout: StaticLayout,
    val lineNumbersLayout: StaticLayout?,
    val shadowPadding: Int,
    val canvasWidth: Int,
    val canvasHeight: Int
) {
    fun render(
        params: CodePreviewParams,
        outputScale: Float
    ): Bitmap {
        val theme = params.theme.highlightTheme()
        val cornerRadius = params.cornerRadius * scale
        val result = createBitmap(
            ceil(canvasWidth * outputScale).toInt(),
            ceil(canvasHeight * outputScale).toInt(),
            Bitmap.Config.ARGB_8888
        ).apply { setHasAlpha(true) }
        Canvas(result).apply {
            if (params.showCanvasBackground) {
                drawRoundRect(
                    RectF(0f, 0f, result.width.toFloat(), result.height.toFloat()),
                    params.canvasCornerRadius * scale * outputScale,
                    params.canvasCornerRadius * scale * outputScale,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(
                            0f,
                            0f,
                            result.width.toFloat(),
                            result.height.toFloat(),
                            params.backgroundColors.map { it.toArgb() }.toIntArray(),
                            null,
                            Shader.TileMode.CLAMP
                        )
                    }
                )
            }
            withSave {
                translate(result.width / 2f, result.height / 2f)
                rotate(params.rotation)
                scale(outputScale, outputScale)
                translate(
                    -(cardWidth + shadowPadding * 2) / 2f,
                    -(cardHeight + shadowPadding * 2) / 2f
                )
                drawRoundRect(
                    RectF(
                        shadowPadding.toFloat(),
                        shadowPadding.toFloat(),
                        (shadowPadding + cardWidth).toFloat(),
                        (shadowPadding + cardHeight).toFloat()
                    ),
                    cornerRadius,
                    cornerRadius,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = theme.backgroundColor.toArgb()
                        if (params.showCardShadow && params.cardShadowBlurRadius > 0) {
                            setShadowLayer(
                                params.cardShadowBlurRadius * scale,
                                params.cardShadowOffsetX * scale,
                                params.cardShadowOffsetY * scale,
                                params.cardShadowColor.toArgb()
                            )
                        }
                    }
                )
                withSave {
                    translate(shadowPadding.toFloat(), shadowPadding.toFloat())
                    drawHeader(params)

                    val contentTop = headerHeight + dividerHeight + innerPadding
                    lineNumbersLayout?.let { layout ->
                        withSave {
                            translate(innerPadding, contentTop)
                            layout.draw(this)
                        }
                    }
                    withSave {
                        translate(
                            innerPadding + lineNumberWidth + lineNumberGap,
                            contentTop
                        )
                        codeLayout.draw(this)
                    }
                }
            }
        }
        return result
    }

    private fun Canvas.drawHeader(params: CodePreviewParams) {
        if (headerHeight == 0f) return

        val theme = params.theme.highlightTheme()
        val horizontalPadding = 14f * scale
        var titleStart = horizontalPadding
        if (params.showWindowControls) {
            val radius = 5f * scale
            val centerY = headerHeight / 2f
            listOf(0xFFFF5F57, 0xFFFEBC2E, 0xFF28C840).forEachIndexed { index, color ->
                drawCircle(
                    horizontalPadding + radius + index * 17f * scale,
                    centerY,
                    radius,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.toInt() }
                )
            }
            titleStart += 63f * scale
        }

        if (params.showTitle) {
            val languagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = theme.defaultTextColor.copy(alpha = 0.52f).toArgb()
                textSize = 10f * scale
                typeface = Typeface.MONOSPACE
            }
            val language = params.language.title
            val languageWidth = languagePaint.measureText(language)
            val languageX = cardWidth - horizontalPadding - languageWidth
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = theme.defaultTextColor.toArgb()
                textSize = 12f * scale
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            val title = params.title
            val availableTitleWidth = (
                    languageX - titleStart - 8f * scale
                    ).coerceAtLeast(0f)
            val titleBaseline = headerHeight / 2f -
                    (titlePaint.fontMetrics.ascent + titlePaint.fontMetrics.descent) / 2f
            val languageBaseline = headerHeight / 2f -
                    (languagePaint.fontMetrics.ascent + languagePaint.fontMetrics.descent) / 2f
            drawText(
                TextUtils.ellipsize(
                    title,
                    TextPaint(titlePaint),
                    availableTitleWidth,
                    TextUtils.TruncateAt.END
                ).toString(),
                titleStart,
                titleBaseline,
                titlePaint
            )
            drawText(language, languageX, languageBaseline, languagePaint)
        }

        drawRect(
            0f,
            headerHeight,
            cardWidth.toFloat(),
            headerHeight + dividerHeight,
            Paint().apply {
                color = theme.defaultTextColor.copy(alpha = 0.1f).toArgb()
            }
        )
    }
}
