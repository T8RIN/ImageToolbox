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

package com.t8rin.imagetoolbox.feature.markup_layers.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withSave
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.toPaint
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

internal class LayersRenderer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    suspend fun render(
        backgroundImage: Bitmap,
        layers: List<MarkupLayer>
    ): Bitmap = withContext(defaultDispatcher) {
        val resultBitmap = backgroundImage.copy(Bitmap.Config.ARGB_8888, true)

        val visibleLayers = layers.filter { it.position.isVisible }
        if (visibleLayers.isEmpty()) return@withContext resultBitmap

        val targetWidth = resultBitmap.width.toFloat()
        val targetHeight = resultBitmap.height.toFloat()
        val authorSize = visibleLayers
            .asSequence()
            .map { it.position.currentCanvasSize }
            .firstOrNull { it.width > 0 && it.height > 0 }

        val authorWidth = (authorSize?.width ?: resultBitmap.width).toFloat().coerceAtLeast(1f)
        val authorHeight = (authorSize?.height ?: resultBitmap.height).toFloat().coerceAtLeast(1f)
        val ratio = min(targetWidth / authorWidth, targetHeight / authorHeight)
        val canvasOffsetX = (targetWidth - authorWidth * ratio) / 2f
        val canvasOffsetY = (targetHeight - authorHeight * ratio) / 2f
        val textFullSize = min(authorWidth, authorHeight).roundToInt().coerceAtLeast(1)

        val pictureCache = mutableMapOf<Any, Bitmap?>()
        val textCache = mutableMapOf<Triple<LayerType.Text, Int, Int?>, TextLayerRenderData>()

        resultBitmap.applyCanvas {
            withSave {
                translate(canvasOffsetX, canvasOffsetY)
                scale(ratio, ratio)

                visibleLayers.forEach { layer ->
                    val centerX = authorWidth / 2f + layer.position.offsetX
                    val centerY = authorHeight / 2f + layer.position.offsetY

                    when (val type = layer.type) {
                        is LayerType.Picture -> {
                            val contentBitmap = pictureCache.getOrPut(type.imageData) {
                                loadPictureBitmap(
                                    imageData = type.imageData,
                                    maxWidth = authorWidth / 2f,
                                    maxHeight = authorHeight / 2f
                                )
                            } ?: return@forEach

                            withSave {
                                translate(centerX, centerY)
                                rotate(layer.position.rotation)
                                scale(
                                    layer.position.scale * if (layer.position.isFlippedHorizontally) -1f else 1f,
                                    layer.position.scale * if (layer.position.isFlippedVertically) -1f else 1f
                                )

                                val destination = RectF(
                                    -contentBitmap.width / 2f,
                                    -contentBitmap.height / 2f,
                                    contentBitmap.width / 2f,
                                    contentBitmap.height / 2f
                                )
                                clipToRoundedBounds(
                                    bounds = destination,
                                    cornerRadiusPx = cornerRadiusPx(
                                        cornerRadiusPercent = layer.cornerRadiusPercent,
                                        width = destination.width(),
                                        height = destination.height()
                                    )
                                )

                                drawBitmap(
                                    contentBitmap,
                                    null,
                                    destination,
                                    layer.blendingMode.toPaint().apply {
                                        alpha = (layer.position.alpha * 255).roundToInt()
                                            .coerceIn(0, 255)
                                        isFilterBitmap = true
                                    }
                                )
                            }
                        }

                        is LayerType.Text -> {
                            val textData = textCache.getOrPut(
                                Triple(type, textFullSize, layer.visibleLineCount)
                            ) {
                                buildTextLayerRenderData(
                                    type = type,
                                    textFullSize = textFullSize,
                                    maxTextBoxWidth = authorWidth,
                                    maxLines = layer.visibleLineCount
                                )
                            }
                            drawTextLayer(
                                data = textData,
                                centerX = centerX,
                                centerY = centerY,
                                rotation = layer.position.rotation,
                                scale = layer.position.scale,
                                isFlippedHorizontally = layer.position.isFlippedHorizontally,
                                isFlippedVertically = layer.position.isFlippedVertically,
                                cornerRadiusPercent = layer.cornerRadiusPercent,
                                blendingMode = layer.blendingMode,
                                alpha = (layer.position.alpha * 255).roundToInt().coerceIn(0, 255)
                            )
                        }
                    }
                }
            }
        }

        resultBitmap
    }

    private suspend fun loadPictureBitmap(
        imageData: Any,
        maxWidth: Float,
        maxHeight: Float
    ): Bitmap? {
        val source = imageLoader.execute(
            ImageRequest.Builder(appContext)
                .data(imageData)
                .allowHardware(false)
                .size(2000)
                .build()
        ).image?.toBitmap() ?: return null

        val width = source.width.takeIf { it > 0 } ?: return null
        val height = source.height.takeIf { it > 0 } ?: return null
        val fitScale = min(
            1f,
            min(maxWidth / width, maxHeight / height)
        )

        if (fitScale >= 0.999f) return source

        return source.scale(
            (width * fitScale).roundToInt().coerceAtLeast(1),
            (height * fitScale).roundToInt().coerceAtLeast(1)
        )
    }

    private fun buildTextLayerRenderData(
        type: LayerType.Text,
        textFullSize: Int,
        maxTextBoxWidth: Float,
        maxLines: Int?
    ): TextLayerRenderData {
        val textMetrics = context.calculateTextLayerMetrics(
            type = type,
            textFullSize = textFullSize
        )
        val outlineWidth = type.outline?.width ?: 0f
        val layoutText = type.text.ifEmpty { " " }
        val availableLayoutWidth = (
                maxTextBoxWidth - (textMetrics.horizontalPaddingPx + outlineWidth) * 2f
                ).roundToInt().coerceAtLeast(1)

        val fillPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = type.color
            textSize = textMetrics.fontSizePx
            hinting = Paint.HINTING_ON
            isLinearText = true
            isUnderlineText = type.decorations.any { it == LayerType.Text.Decoration.Underline }
            isStrikeThruText = type.decorations.any { it == LayerType.Text.Decoration.LineThrough }
            typeface = textMetrics.typeface
        }

        val desiredLayoutWidth = maxLineWidth(
            text = layoutText,
            paint = fillPaint
        ).coerceAtLeast(1)
        val layoutWidth = min(desiredLayoutWidth, availableLayoutWidth)
        val alignment = when (type.alignment) {
            LayerType.Text.Alignment.Start -> Layout.Alignment.ALIGN_NORMAL
            LayerType.Text.Alignment.Center -> Layout.Alignment.ALIGN_CENTER
            LayerType.Text.Alignment.End -> Layout.Alignment.ALIGN_OPPOSITE
        }

        val fillLayout = createStaticLayout(
            text = layoutText,
            paint = fillPaint,
            width = layoutWidth,
            alignment = alignment,
            lineHeightPx = textMetrics.lineHeightPx,
            maxLines = maxLines
        )

        val bitmapWidth = ceil(
            layoutWidth + textMetrics.horizontalPaddingPx * 2f + outlineWidth * 2f
        ).toInt().coerceAtLeast(1)
        val bitmapHeight = ceil(
            fillLayout.height + textMetrics.verticalPaddingPx * 2f + outlineWidth * 2f
        ).toInt().coerceAtLeast(1)

        val outlineLayout = type.outline?.takeIf { it.width > 0f }?.let { outline ->
            val outlinePaint = TextPaint(fillPaint).apply {
                color = outline.color
                style = Paint.Style.STROKE
                strokeWidth = outline.width
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            createStaticLayout(
                text = layoutText,
                paint = outlinePaint,
                width = layoutWidth,
                alignment = alignment,
                lineHeightPx = textMetrics.lineHeightPx,
                maxLines = maxLines
            )
        }

        return TextLayerRenderData(
            width = bitmapWidth.toFloat(),
            height = bitmapHeight.toFloat(),
            bitmap = createBitmap(bitmapWidth, bitmapHeight).applyCanvas {
                val textLeft = outlineWidth + textMetrics.horizontalPaddingPx
                val textTop = outlineWidth + textMetrics.verticalPaddingPx

                type.backgroundColor.takeIf { it != 0 }?.let { backgroundColor ->
                    drawRect(
                        0f,
                        0f,
                        bitmapWidth.toFloat(),
                        bitmapHeight.toFloat(),
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = backgroundColor
                        }
                    )
                }

                outlineLayout?.let {
                    withSave {
                        translate(textLeft, textTop)
                        it.draw(this@applyCanvas)
                    }
                }

                withSave {
                    translate(textLeft, textTop)
                    fillLayout.draw(this@applyCanvas)
                }
            }
        )
    }

    private fun Canvas.drawTextLayer(
        data: TextLayerRenderData,
        centerX: Float,
        centerY: Float,
        rotation: Float,
        scale: Float,
        isFlippedHorizontally: Boolean,
        isFlippedVertically: Boolean,
        cornerRadiusPercent: Int,
        blendingMode: BlendingMode,
        alpha: Int
    ) {
        withSave {
            translate(centerX, centerY)
            rotate(rotation)
            scale(
                scale * if (isFlippedHorizontally) -1f else 1f,
                scale * if (isFlippedVertically) -1f else 1f
            )
            val destination = RectF(
                -data.width / 2f,
                -data.height / 2f,
                data.width / 2f,
                data.height / 2f
            )
            clipToRoundedBounds(
                bounds = destination,
                cornerRadiusPx = cornerRadiusPx(
                    cornerRadiusPercent = cornerRadiusPercent,
                    width = data.width,
                    height = data.height
                )
            )
            drawBitmap(
                data.bitmap,
                null,
                destination,
                blendingMode.toPaint().apply {
                    this.alpha = alpha
                    isFilterBitmap = true
                }
            )
        }
    }

    private fun maxLineWidth(
        text: String,
        paint: TextPaint
    ): Int = text
        .split('\n')
        .maxOfOrNull { line ->
            ceil(Layout.getDesiredWidth(line.ifEmpty { " " }, paint).toDouble()).toInt()
        }
        ?.coerceAtLeast(1)
        ?: 1

    private fun createStaticLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        alignment: Layout.Alignment,
        lineHeightPx: Float,
        maxLines: Int?
    ): StaticLayout {
        val boundedText = maxLines
            ?.takeIf { it > 0 }
            ?.let { linesLimit ->
                val fullLayout = StaticLayout.Builder
                    .obtain(text, 0, text.length, paint, width)
                    .setAlignment(alignment)
                    .setIncludePad(false)
                    .build()

                if (fullLayout.lineCount <= linesLimit) {
                    text
                } else {
                    text.substring(0, fullLayout.getLineEnd(linesLimit - 1))
                }
            }
            ?: text

        val naturalLineHeightPx = ceil(
            (paint.fontMetrics.descent - paint.fontMetrics.ascent).toDouble()
        ).toFloat()
        return StaticLayout.Builder
            .obtain(boundedText, 0, boundedText.length, paint, width)
            .setAlignment(alignment)
            .setIncludePad(false)
            .setLineSpacing((lineHeightPx - naturalLineHeightPx).coerceAtLeast(0f), 1f)
            .build()
    }

    private fun cornerRadiusPx(
        cornerRadiusPercent: Int,
        width: Float,
        height: Float
    ): Float {
        val normalizedPercent = cornerRadiusPercent.coerceIn(0, 50)
        if (normalizedPercent == 0) return 0f

        return min(width, height) * (normalizedPercent / 100f)
    }

    private fun Canvas.clipToRoundedBounds(
        bounds: RectF,
        cornerRadiusPx: Float
    ) {
        if (cornerRadiusPx <= 0f) return

        clipPath(
            Path().apply {
                addRoundRect(
                    bounds,
                    cornerRadiusPx,
                    cornerRadiusPx,
                    Path.Direction.CW
                )
            }
        )
    }
}

private data class TextLayerRenderData(
    val width: Float,
    val height: Float,
    val bitmap: Bitmap
)