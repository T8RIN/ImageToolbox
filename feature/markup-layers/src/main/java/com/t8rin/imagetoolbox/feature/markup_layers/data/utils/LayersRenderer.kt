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
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withSave
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.toTypeface
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
        val textCache = mutableMapOf<Pair<LayerType.Text, Int>, Bitmap?>()

        resultBitmap.applyCanvas {
            withSave {
                translate(canvasOffsetX, canvasOffsetY)
                scale(ratio, ratio)

                visibleLayers.forEach { layer ->
                    val contentBitmap = when (val type = layer.type) {
                        is LayerType.Picture -> pictureCache.getOrPut(type.imageData) {
                            loadPictureBitmap(
                                imageData = type.imageData,
                                maxWidth = authorWidth / 2f,
                                maxHeight = authorHeight / 2f
                            )
                        }

                        is LayerType.Text -> textCache.getOrPut(type to textFullSize) {
                            renderTextBitmap(
                                type = type,
                                textFullSize = textFullSize
                            )
                        }
                    } ?: return@forEach

                    val centerX = authorWidth / 2f + layer.position.offsetX
                    val centerY = authorHeight / 2f + layer.position.offsetY

                    withSave {
                        translate(centerX, centerY)
                        rotate(layer.position.rotation)
                        scale(layer.position.scale, layer.position.scale)

                        drawBitmap(
                            contentBitmap,
                            null,
                            RectF(
                                -contentBitmap.width / 2f,
                                -contentBitmap.height / 2f,
                                contentBitmap.width / 2f,
                                contentBitmap.height / 2f
                            ),
                            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                                alpha = (layer.position.alpha * 255).roundToInt().coerceIn(0, 255)
                                isFilterBitmap = true
                            }
                        )

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

    private fun renderTextBitmap(
        type: LayerType.Text,
        textFullSize: Int
    ): Bitmap {
        val displayMetrics = context.resources.displayMetrics
        val fontSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textFullSize * type.size / 5f,
            displayMetrics
        )
        val horizontalPaddingPx = (textFullSize * type.size / 10f) * displayMetrics.density
        val verticalPaddingPx = (textFullSize * type.size / 12f) * displayMetrics.density
        val cornerRadiusPx = 4f * displayMetrics.density
        val outlineWidth = type.outline?.width ?: 0f
        val layoutText = type.text.ifEmpty { " " }

        val fillPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
            color = type.color
            textSize = fontSizePx
            isUnderlineText = type.decorations.any { it == LayerType.Text.Decoration.Underline }
            isStrikeThruText = type.decorations.any { it == LayerType.Text.Decoration.LineThrough }
            typeface = createTypeface(
                baseTypeface = type.font.toTypeface(),
                isBold = type.decorations.any { it == LayerType.Text.Decoration.Bold },
                isItalic = type.decorations.any { it == LayerType.Text.Decoration.Italic }
            )
        }

        val layoutWidth = maxLineWidth(
            text = layoutText,
            paint = fillPaint
        ).coerceAtLeast(1)
        val alignment = when (type.alignment) {
            LayerType.Text.Alignment.Start -> Layout.Alignment.ALIGN_NORMAL
            LayerType.Text.Alignment.Center -> Layout.Alignment.ALIGN_CENTER
            LayerType.Text.Alignment.End -> Layout.Alignment.ALIGN_OPPOSITE
        }

        val fillLayout = createStaticLayout(
            text = layoutText,
            paint = fillPaint,
            width = layoutWidth,
            alignment = alignment
        )

        val bitmapWidth = ceil(
            layoutWidth + horizontalPaddingPx * 2f + outlineWidth * 2f
        ).toInt().coerceAtLeast(1)
        val bitmapHeight = ceil(
            fillLayout.height + verticalPaddingPx * 2f + outlineWidth * 2f
        ).toInt().coerceAtLeast(1)

        return createBitmap(bitmapWidth, bitmapHeight).applyCanvas {
            if (type.backgroundColor != 0) {
                drawRoundRect(
                    0f,
                    0f,
                    bitmapWidth.toFloat(),
                    bitmapHeight.toFloat(),
                    cornerRadiusPx,
                    cornerRadiusPx,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = type.backgroundColor
                    }
                )
            }

            val textLeft = outlineWidth + horizontalPaddingPx
            val textTop = outlineWidth + verticalPaddingPx

            type.outline?.takeIf { it.width > 0f }?.let { outline ->
                val outlinePaint = TextPaint(fillPaint).apply {
                    color = outline.color
                    style = Paint.Style.STROKE
                    strokeWidth = outline.width
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                }
                val outlineLayout = createStaticLayout(
                    text = layoutText,
                    paint = outlinePaint,
                    width = layoutWidth,
                    alignment = alignment
                )
                withSave {
                    translate(textLeft, textTop)
                    outlineLayout.draw(this)
                }
            }

            withSave {
                translate(textLeft, textTop)
                fillLayout.draw(this)
            }
        }
    }

    private fun createTypeface(
        baseTypeface: Typeface?,
        isBold: Boolean,
        isItalic: Boolean
    ): Typeface {
        val style = when {
            isBold && isItalic -> Typeface.BOLD_ITALIC
            isBold -> Typeface.BOLD
            isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        return Typeface.create(baseTypeface ?: Typeface.DEFAULT, style)
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
        alignment: Layout.Alignment
    ): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setIncludePad(false)
            .setLineSpacing(0f, 1f)
            .build()
    }
}