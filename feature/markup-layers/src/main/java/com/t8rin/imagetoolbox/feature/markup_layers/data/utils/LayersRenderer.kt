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
import androidx.core.graphics.withSave
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.static
import com.t8rin.imagetoolbox.core.data.image.utils.toPaint
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue
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
        val textCache = mutableMapOf<TextLayerCacheKey, TextLayerRenderData>()

        resultBitmap.applyCanvas {
            withSave {
                translate(canvasOffsetX, canvasOffsetY)
                scale(ratio, ratio)

                layers.forEach { layer ->
                    if (!layer.position.isVisible) return@forEach

                    val centerX = authorWidth / 2f + layer.position.offsetX
                    val centerY = authorHeight / 2f + layer.position.offsetY

                    when (val type = layer.type) {
                        is LayerType.Picture -> {
                            val contentBitmap = pictureCache.getOrPut(type.imageData) {
                                loadPictureBitmap(
                                    imageData = type.imageData
                                )
                            } ?: return@forEach

                            val pictureData = resolvePictureRenderData(
                                bitmap = contentBitmap,
                                shadow = type.shadow,
                                contentSize = layer.contentSize,
                                maxWidth = authorWidth / 2f,
                                maxHeight = authorHeight / 2f
                            ) ?: return@forEach
                            val shadowRenderData = buildPictureShadowRenderData(
                                sourceBitmap = contentBitmap,
                                shadow = type.shadow,
                                targetWidth = pictureData.contentWidth,
                                targetHeight = pictureData.contentHeight,
                                rasterScale = resolveShadowRasterScale(
                                    layerScale = layer.position.scale
                                )
                            )

                            drawPictureLayer(
                                bitmap = contentBitmap,
                                data = pictureData,
                                shadowRenderData = shadowRenderData,
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

                        is LayerType.Text -> {
                            val shadowRasterScale = resolveShadowRasterScale(
                                layerScale = layer.position.scale
                            )
                            val textData = textCache.getOrPut(
                                TextLayerCacheKey(
                                    type = type,
                                    textFullSize = textFullSize,
                                    contentSize = layer.contentSize,
                                    maxLines = layer.visibleLineCount,
                                    shadowRasterScaleKey = (shadowRasterScale * 100f).roundToInt()
                                )
                            ) {
                                buildTextLayerRenderData(
                                    type = type,
                                    textFullSize = textFullSize,
                                    contentSize = layer.contentSize,
                                    fallbackMaxTextBoxWidth = authorWidth,
                                    maxLines = layer.visibleLineCount,
                                    shadowRasterScale = shadowRasterScale
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
        imageData: Any
    ): Bitmap? = imageLoader.execute(
            ImageRequest.Builder(appContext)
                .data(imageData)
                .static()
                .allowHardware(false)
                .size(1600)
                .build()
    ).image?.toBitmap()

    private fun resolvePictureRenderData(
        bitmap: Bitmap,
        shadow: com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow?,
        contentSize: IntegerSize,
        maxWidth: Float,
        maxHeight: Float
    ): PictureLayerRenderData? {
        val shadowPadding = calculateShadowPadding(shadow)
        val horizontalShadowPadding = shadowPadding.leftPx + shadowPadding.rightPx
        val verticalShadowPadding = shadowPadding.topPx + shadowPadding.bottomPx

        contentSize.takeIf { it.width > 0 && it.height > 0 }?.let {
            val width = it.width.toFloat().coerceAtLeast(horizontalShadowPadding + 1f)
            val height = it.height.toFloat().coerceAtLeast(verticalShadowPadding + 1f)
            return PictureLayerRenderData(
                width = width,
                height = height,
                contentLeft = shadowPadding.leftPx,
                contentTop = shadowPadding.topPx,
                contentWidth = (width - horizontalShadowPadding).coerceAtLeast(1f),
                contentHeight = (height - verticalShadowPadding).coerceAtLeast(1f)
            )
        }

        val width = bitmap.width.takeIf { it > 0 } ?: return null
        val height = bitmap.height.takeIf { it > 0 } ?: return null
        val availableContentWidth = (maxWidth - horizontalShadowPadding).coerceAtLeast(1f)
        val availableContentHeight = (maxHeight - verticalShadowPadding).coerceAtLeast(1f)
        val fitScale = min(
            1f,
            min(availableContentWidth / width, availableContentHeight / height)
        )
        val contentWidth = (width * fitScale).roundToInt().coerceAtLeast(1).toFloat()
        val contentHeight = (height * fitScale).roundToInt().coerceAtLeast(1).toFloat()

        return PictureLayerRenderData(
            width = contentWidth + horizontalShadowPadding,
            height = contentHeight + verticalShadowPadding,
            contentLeft = shadowPadding.leftPx,
            contentTop = shadowPadding.topPx,
            contentWidth = contentWidth,
            contentHeight = contentHeight
        )
    }

    private fun buildTextLayerRenderData(
        type: LayerType.Text,
        textFullSize: Int,
        contentSize: IntegerSize,
        fallbackMaxTextBoxWidth: Float,
        maxLines: Int?,
        shadowRasterScale: Float
    ): TextLayerRenderData {
        val textMetrics = context.calculateTextLayerMetrics(
            type = type,
            textFullSize = textFullSize
        )
        val outlineWidth = type.outline?.width ?: 0f
        val layoutText = type.text.ifEmpty { " " }
        val resolvedContentSize = contentSize.takeIf { it.width > 0 && it.height > 0 }
        val availableLayoutWidth = (
                (resolvedContentSize?.width?.toFloat() ?: fallbackMaxTextBoxWidth) -
                        textMetrics.padding.leftPx -
                        textMetrics.padding.rightPx -
                        outlineWidth * 2f
                ).roundToInt().coerceAtLeast(1)

        val fillPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = type.color
            textSize = textMetrics.fontSizePx
            hinting = Paint.HINTING_ON
            isLinearText = true
            isUnderlineText = type.decorations.any { it == LayerType.Text.Decoration.Underline }
            isStrikeThruText = type.decorations.any { it == LayerType.Text.Decoration.LineThrough }
            typeface = textMetrics.typeface
            textScaleX = type.geometricTransform?.scaleX?.coerceAtLeast(0.01f) ?: 1f
            textSkewX = type.geometricTransform?.skewX ?: 0f
        }

        val desiredLayoutWidth = maxLineWidth(
            text = layoutText,
            paint = fillPaint
        ).coerceAtLeast(1)
        val layoutWidth = resolvedContentSize?.let { availableLayoutWidth }
            ?: min(desiredLayoutWidth, availableLayoutWidth)
        val alignment = when (type.alignment) {
            LayerType.Text.Alignment.Start -> Layout.Alignment.ALIGN_NORMAL
            LayerType.Text.Alignment.Center -> Layout.Alignment.ALIGN_CENTER
            LayerType.Text.Alignment.End -> Layout.Alignment.ALIGN_OPPOSITE
        }

        val fillLayout = createTextStaticLayout(
            text = layoutText,
            paint = fillPaint,
            width = layoutWidth,
            alignment = alignment,
            lineHeightPx = textMetrics.lineHeightPx,
            maxLines = maxLines
        )
        val shadowRenderData = buildTextShadowRenderData(
            type = type,
            textMetrics = textMetrics,
            layoutWidth = layoutWidth,
            maxLines = maxLines,
            rasterScale = shadowRasterScale
        )

        val bitmapWidth = resolvedContentSize?.width?.coerceAtLeast(1) ?: ceil(
            layoutWidth +
                    textMetrics.padding.leftPx +
                    textMetrics.padding.rightPx +
                    outlineWidth * 2f
        ).toInt().coerceAtLeast(1)
        val bitmapHeight = resolvedContentSize?.height?.coerceAtLeast(1) ?: ceil(
            fillLayout.height +
                    textMetrics.padding.topPx +
                    textMetrics.padding.bottomPx +
                    outlineWidth * 2f
        ).toInt().coerceAtLeast(1)

        val outlineLayout = type.outline?.takeIf { it.width > 0f }?.let { outline ->
            val outlinePaint = TextPaint(fillPaint).apply {
                color = outline.color
                style = Paint.Style.STROKE
                strokeWidth = outline.width
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                clearShadowLayer()
            }
            createTextStaticLayout(
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
            backgroundPaint = type.backgroundColor.takeIf { it != 0 }?.let { backgroundColor ->
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = backgroundColor
                }
            },
            textLeft = outlineWidth + textMetrics.padding.leftPx,
            textTop = outlineWidth + textMetrics.padding.topPx,
            shadowRenderData = shadowRenderData,
            outlineLayout = outlineLayout,
            fillLayout = fillLayout
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

            if (blendingMode == BlendingMode.SrcOver && alpha >= 255) {
                drawTextLayerContent(
                    data = data,
                    bounds = destination
                )
            } else {
                val checkpoint = saveLayer(
                    destination,
                    blendingMode.toPaint().apply {
                        this.alpha = alpha
                    }
                )
                drawTextLayerContent(
                    data = data,
                    bounds = destination
                )
                restoreToCount(checkpoint)
            }
        }
    }

    private fun Canvas.drawTextLayerContent(
        data: TextLayerRenderData,
        bounds: RectF
    ) {
        withSave {
            translate(bounds.left, bounds.top)

            data.backgroundPaint?.let {
                drawRect(
                    0f,
                    0f,
                    data.width,
                    data.height,
                    it
                )
            }

            data.shadowRenderData?.let { shadow ->
                withSave {
                    val rasterScale = shadow.rasterScale.coerceAtLeast(1f)
                    scale(1f / rasterScale, 1f / rasterScale)
                    drawBitmap(
                        shadow.bitmap,
                        data.textLeft * rasterScale + shadow.left,
                        data.textTop * rasterScale + shadow.top,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            isFilterBitmap = true
                        }
                    )
                }
            }

            withSave {
                translate(data.textLeft, data.textTop)
                data.outlineLayout?.draw(this@drawTextLayerContent)
                data.fillLayout.draw(this@drawTextLayerContent)
            }
        }
    }

    private fun Canvas.drawPictureLayer(
        bitmap: Bitmap,
        data: PictureLayerRenderData,
        shadowRenderData: PictureShadowRenderData?,
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

            if (blendingMode == BlendingMode.SrcOver && alpha >= 255) {
                drawPictureLayerContent(
                    bitmap = bitmap,
                    data = data,
                    bounds = destination,
                    shadowRenderData = shadowRenderData,
                    cornerRadiusPercent = cornerRadiusPercent
                )
            } else {
                val checkpoint = saveLayer(
                    destination,
                    blendingMode.toPaint().apply {
                        this.alpha = alpha
                    }
                )
                drawPictureLayerContent(
                    bitmap = bitmap,
                    data = data,
                    bounds = destination,
                    shadowRenderData = shadowRenderData,
                    cornerRadiusPercent = cornerRadiusPercent
                )
                restoreToCount(checkpoint)
            }
        }
    }

    private fun Canvas.drawPictureLayerContent(
        bitmap: Bitmap,
        data: PictureLayerRenderData,
        bounds: RectF,
        shadowRenderData: PictureShadowRenderData?,
        cornerRadiusPercent: Int
    ) {
        withSave {
            translate(bounds.left, bounds.top)

            shadowRenderData?.let { shadow ->
                withSave {
                    val rasterScale = shadow.rasterScale.coerceAtLeast(1f)
                    scale(1f / rasterScale, 1f / rasterScale)
                    drawBitmap(
                        shadow.bitmap,
                        data.contentLeft * rasterScale + shadow.left,
                        data.contentTop * rasterScale + shadow.top,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            isFilterBitmap = true
                        }
                    )
                }
            }

            val imageBounds = RectF(
                data.contentLeft,
                data.contentTop,
                data.contentLeft + data.contentWidth,
                data.contentTop + data.contentHeight
            )
            withSave {
                clipToRoundedBounds(
                    bounds = imageBounds,
                    cornerRadiusPx = cornerRadiusPx(
                        cornerRadiusPercent = cornerRadiusPercent,
                        width = imageBounds.width(),
                        height = imageBounds.height()
                    )
                )
                drawBitmap(
                    bitmap,
                    null,
                    imageBounds,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        isFilterBitmap = true
                    }
                )
            }
        }
    }

    private fun resolveShadowRasterScale(
        layerScale: Float
    ): Float = layerScale
        .absoluteValue
        .coerceAtLeast(1f)
        .coerceAtMost(MAX_SHADOW_RASTER_SCALE)

    private companion object {
        const val MAX_SHADOW_RASTER_SCALE = 4f
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
    val backgroundPaint: Paint?,
    val textLeft: Float,
    val textTop: Float,
    val shadowRenderData: TextShadowRenderData?,
    val outlineLayout: StaticLayout?,
    val fillLayout: StaticLayout
)

private data class TextLayerCacheKey(
    val type: LayerType.Text,
    val textFullSize: Int,
    val contentSize: IntegerSize,
    val maxLines: Int?,
    val shadowRasterScaleKey: Int
)

private data class PictureLayerRenderData(
    val width: Float,
    val height: Float,
    val contentLeft: Float,
    val contentTop: Float,
    val contentWidth: Float,
    val contentHeight: Float
)
