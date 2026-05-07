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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
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
import com.t8rin.imagetoolbox.core.settings.presentation.model.asUi
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.drawscope.Stroke as ComposeStroke
import androidx.compose.ui.text.style.TextGeometricTransform as ComposeTextGeometricTransform

internal class LayersRenderer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    suspend fun render(
        backgroundImage: Bitmap,
        layers: List<MarkupLayer>,
        fontScale: Float? = null
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
        val shapeContentInsetPx = context.resources.displayMetrics.density * 4f
        val textDensity = Density(
            density = context.resources.displayMetrics.density,
            fontScale = fontScale
                ?.takeIf { it > 0f }
                ?: context.resources.configuration.fontScale.takeIf { it > 0f }
                ?: 1f
        )
        val textMeasurer = TextMeasurer(
            defaultFontFamilyResolver = createFontFamilyResolver(context),
            defaultDensity = textDensity,
            defaultLayoutDirection = LayoutDirection.Ltr,
            cacheSize = 0
        )

        val pictureCache = mutableMapOf<Any, Bitmap?>()
        val textCache = mutableMapOf<TextLayerCacheKey, TextLayerRenderData>()
        val shapeCache = mutableMapOf<ShapeLayerCacheKey, ShapeLayerCacheValue>()

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
                                cornerRadiusPercent = layer.cornerRadiusPercent,
                                rasterScale = resolveLayerShadowRasterScale(
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
                            val shadowRasterScale = resolveLayerShadowRasterScale(
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
                                    shadowRasterScale = shadowRasterScale,
                                    density = textDensity,
                                    textMeasurer = textMeasurer
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

                        is LayerType.Shape -> {
                            val shadowRasterScale = resolveLayerShadowRasterScale(
                                layerScale = layer.position.scale
                            )
                            val shapeValue = shapeCache.getOrPut(
                                ShapeLayerCacheKey(
                                    type = type,
                                    referenceSize = textFullSize,
                                    contentSize = layer.contentSize,
                                    shadowRasterScaleKey = (shadowRasterScale * 100f).roundToInt(),
                                    contentInsetKey = shapeContentInsetPx.roundToInt()
                                )
                            ) {
                                val data = resolveShapeLayerRenderData(
                                    type = type,
                                    referenceSize = textFullSize.toFloat(),
                                    contentSize = layer.contentSize,
                                    maxWidth = authorWidth / 2f,
                                    maxHeight = authorHeight / 2f,
                                    contentInsetPx = shapeContentInsetPx
                                )
                                ShapeLayerCacheValue(
                                    data = data,
                                    shadowRenderData = buildShapeShadowRenderData(
                                        type = type,
                                        data = data,
                                        rasterScale = shadowRasterScale
                                    )
                                )
                            }
                            drawShapeLayerItem(
                                type = type,
                                data = shapeValue.data,
                                shadowRenderData = shapeValue.shadowRenderData,
                                centerX = centerX,
                                centerY = centerY,
                                rotation = layer.position.rotation,
                                scale = layer.position.scale,
                                isFlippedHorizontally = layer.position.isFlippedHorizontally,
                                isFlippedVertically = layer.position.isFlippedVertically,
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
        shadowRasterScale: Float,
        density: Density,
        textMeasurer: TextMeasurer
    ): TextLayerRenderData {
        val textMetrics = context.calculateTextLayerMetrics(
            type = type,
            textFullSize = textFullSize,
            fontScale = density.fontScale
        )
        val layoutText = type.text.ifEmpty { " " }
        val resolvedContentSize = contentSize.takeIf { it.width > 0 && it.height > 0 }
        val maxLayoutWidth = (
                (resolvedContentSize?.width?.toFloat() ?: fallbackMaxTextBoxWidth) -
                        textMetrics.padding.horizontalPx
                ).roundToInt().coerceAtLeast(1)
        val fillStyle = type.composeTextStyle(
            textMetrics = textMetrics,
            density = density
        )
        val fillLayout = textMeasurer.measure(
            text = layoutText,
            style = fillStyle,
            overflow = TextOverflow.Clip,
            maxLines = maxLines ?: Int.MAX_VALUE,
            constraints = Constraints(
                maxWidth = maxLayoutWidth
            ),
            density = density,
            layoutDirection = LayoutDirection.Ltr
        )

        val outlineLayout = type.outline?.takeIf { it.width > 0f }?.let { outline ->
            textMeasurer.measure(
                text = layoutText,
                style = fillStyle.copy(
                    color = ComposeColor(outline.color),
                    textDecoration = null,
                    drawStyle = ComposeStroke(
                        width = outline.width,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                ),
                overflow = TextOverflow.Clip,
                maxLines = maxLines ?: Int.MAX_VALUE,
                constraints = Constraints(
                    maxWidth = maxLayoutWidth
                ),
                density = density,
                layoutDirection = LayoutDirection.Ltr
            )
        }

        val shadowRenderData = buildTextShadowRenderData(
            type = type,
            textMetrics = textMetrics,
            textLayoutResult = fillLayout,
            rasterScale = shadowRasterScale
        )

        val requiredBitmapWidth = ceil(
            fillLayout.size.width +
                    textMetrics.padding.horizontalPx
        ).toInt().coerceAtLeast(1)
        val requiredBitmapHeight = ceil(
            fillLayout.size.height +
                    textMetrics.padding.verticalPx
        ).toInt().coerceAtLeast(1)
        val bitmapWidth = resolvedContentSize?.width ?: requiredBitmapWidth
        val bitmapHeight = resolvedContentSize?.height ?: requiredBitmapHeight

        return TextLayerRenderData(
            width = bitmapWidth.toFloat(),
            height = bitmapHeight.toFloat(),
            backgroundPaint = type.backgroundColor.takeIf { it != 0 }?.let { backgroundColor ->
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = backgroundColor
                }
            },
            textLeft = textMetrics.padding.leftPx,
            textTop = textMetrics.padding.topPx,
            shadowRenderData = shadowRenderData,
            outlineLayout = outlineLayout,
            density = density,
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
                drawBitmap(
                    renderTextLayerBitmap(
                        data = data,
                        cornerRadiusPercent = cornerRadiusPercent
                    ),
                    null,
                    destination,
                    blendingMode.toPaint().apply {
                        this.alpha = alpha
                        isFilterBitmap = true
                    }
                )
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

            CanvasDrawScope().draw(
                density = data.density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = ComposeCanvas(this),
                size = Size(data.width, data.height)
            ) {
                withTransform({
                    translate(data.textLeft, data.textTop)
                }) {
                    data.outlineLayout?.let(::drawText)
                    drawText(data.fillLayout)
                }
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
                drawBitmap(
                    renderPictureLayerBitmap(
                        bitmap = bitmap,
                        data = data,
                        shadowRenderData = shadowRenderData,
                        cornerRadiusPercent = cornerRadiusPercent
                    ),
                    null,
                    destination,
                    blendingMode.toPaint().apply {
                        this.alpha = alpha
                        isFilterBitmap = true
                    }
                )
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

    private fun Canvas.drawShapeLayerItem(
        type: LayerType.Shape,
        data: ShapeLayerRenderData,
        shadowRenderData: PictureShadowRenderData?,
        centerX: Float,
        centerY: Float,
        rotation: Float,
        scale: Float,
        isFlippedHorizontally: Boolean,
        isFlippedVertically: Boolean,
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
                drawShapeLayerContent(
                    type = type,
                    data = data,
                    bounds = destination,
                    shadowRenderData = shadowRenderData
                )
            } else {
                drawBitmap(
                    renderShapeLayerBitmap(
                        type = type,
                        data = data,
                        shadowRenderData = shadowRenderData
                    ),
                    null,
                    destination,
                    blendingMode.toPaint().apply {
                        this.alpha = alpha
                        isFilterBitmap = true
                    }
                )
            }
        }
    }

    private fun Canvas.drawShapeLayerContent(
        type: LayerType.Shape,
        data: ShapeLayerRenderData,
        bounds: RectF,
        shadowRenderData: PictureShadowRenderData?
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

            withSave {
                translate(data.contentLeft, data.contentTop)
                drawShapeLayer(
                    type = type,
                    data = data.copy(
                        width = data.contentWidth,
                        height = data.contentHeight,
                        contentLeft = 0f,
                        contentTop = 0f
                    )
                )
            }
        }
    }

    private fun renderTextLayerBitmap(
        data: TextLayerRenderData,
        cornerRadiusPercent: Int
    ): Bitmap = createLayerBitmap(
        width = data.width,
        height = data.height
    ) { canvas ->
        val bounds = RectF(
            0f,
            0f,
            data.width,
            data.height
        )
        canvas.withSave {
            clipToRoundedBounds(
                bounds = bounds,
                cornerRadiusPx = cornerRadiusPx(
                    cornerRadiusPercent = cornerRadiusPercent,
                    width = data.width,
                    height = data.height
                )
            )
            drawTextLayerContent(
                data = data,
                bounds = bounds
            )
        }
    }

    private fun renderPictureLayerBitmap(
        bitmap: Bitmap,
        data: PictureLayerRenderData,
        shadowRenderData: PictureShadowRenderData?,
        cornerRadiusPercent: Int
    ): Bitmap = createLayerBitmap(
        width = data.width,
        height = data.height
    ) { canvas ->
        canvas.drawPictureLayerContent(
            bitmap = bitmap,
            data = data,
            bounds = RectF(
                0f,
                0f,
                data.width,
                data.height
            ),
            shadowRenderData = shadowRenderData,
            cornerRadiusPercent = cornerRadiusPercent
        )
    }

    private fun renderShapeLayerBitmap(
        type: LayerType.Shape,
        data: ShapeLayerRenderData,
        shadowRenderData: PictureShadowRenderData?
    ): Bitmap = createLayerBitmap(
        width = data.width,
        height = data.height
    ) { canvas ->
        canvas.drawShapeLayerContent(
            type = type,
            data = data,
            bounds = RectF(
                0f,
                0f,
                data.width,
                data.height
            ),
            shadowRenderData = shadowRenderData
        )
    }

    private inline fun createLayerBitmap(
        width: Float,
        height: Float,
        draw: (Canvas) -> Unit
    ): Bitmap = createBitmap(
        ceil(width.toDouble()).toInt().coerceAtLeast(1),
        ceil(height.toDouble()).toInt().coerceAtLeast(1)
    ).apply {
        draw(Canvas(this))
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

private fun LayerType.Text.composeTextStyle(
    textMetrics: TextLayerMetrics,
    density: Density
): TextStyle = TextStyle(
    color = ComposeColor(color),
    fontSize = with(density) { textMetrics.fontSizePx.toSp() },
    lineHeight = with(density) { textMetrics.lineHeightPx.toSp() },
    fontFamily = font.asUi().fontFamily,
    letterSpacing = 0.5.sp,
    fontSynthesis = FontSynthesis.All,
    textDecoration = TextDecoration.combine(
        decorations.mapNotNull {
            when (it) {
                LayerType.Text.Decoration.LineThrough -> TextDecoration.LineThrough
                LayerType.Text.Decoration.Underline -> TextDecoration.Underline
                else -> null
            }
        }
    ),
    fontWeight = if (decorations.any { it == LayerType.Text.Decoration.Bold }) {
        FontWeight.Bold
    } else {
        null
    },
    fontStyle = if (decorations.any { it == LayerType.Text.Decoration.Italic }) {
        FontStyle.Italic
    } else {
        FontStyle.Normal
    },
    textGeometricTransform = geometricTransform?.let {
        ComposeTextGeometricTransform(
            scaleX = it.scaleX,
            skewX = it.skewX
        )
    },
    textAlign = when (alignment) {
        LayerType.Text.Alignment.Start -> TextAlign.Start
        LayerType.Text.Alignment.Center -> TextAlign.Center
        LayerType.Text.Alignment.End -> TextAlign.End
    }
)

private data class TextLayerRenderData(
    val width: Float,
    val height: Float,
    val backgroundPaint: Paint?,
    val textLeft: Float,
    val textTop: Float,
    val shadowRenderData: TextShadowRenderData?,
    val outlineLayout: TextLayoutResult?,
    val density: Density,
    val fillLayout: TextLayoutResult
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

private data class ShapeLayerCacheKey(
    val type: LayerType.Shape,
    val referenceSize: Int,
    val contentSize: IntegerSize,
    val shadowRasterScaleKey: Int,
    val contentInsetKey: Int
)

private data class ShapeLayerCacheValue(
    val data: ShapeLayerRenderData,
    val shadowRenderData: PictureShadowRenderData?
)
