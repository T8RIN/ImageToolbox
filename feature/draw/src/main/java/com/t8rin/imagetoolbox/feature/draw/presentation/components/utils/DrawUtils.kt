/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.max
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiNativeStackBlurFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPixelationFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.density
import com.t8rin.imagetoolbox.core.ui.widget.modifier.Line
import com.t8rin.imagetoolbox.core.utils.toTypeface
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import kotlin.math.roundToInt
import kotlin.math.sqrt
import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath

/**
 *  Needed to trigger recomposition
 **/
fun ImageBitmap.copy(): ImageBitmap = asAndroidBitmap().asImageBitmap()

internal fun Path.copy(): Path = copyAsAndroidPath().asComposePath()

internal fun Path.copyAsAndroidPath(): NativePath = NativePath(this.asAndroidPath())

internal fun NativePath.mirror(
    x: Float,
    y: Float,
    x1: Float,
    y1: Float
): NativePath {
    val dx = x1 - x
    val dy = y1 - y
    val lengthSq = dx * dx + dy * dy

    val matrix = Matrix().apply {
        setValues(
            floatArrayOf(
                (dx * dx - dy * dy) / lengthSq,
                (2 * dx * dy) / lengthSq,
                (2 * x * dy * dy - 2 * y * dx * dy) / lengthSq,
                (2 * dx * dy) / lengthSq,
                (dy * dy - dx * dx) / lengthSq,
                (2 * y * dx * dx - 2 * x * dx * dy) / lengthSq,
                0f,
                0f,
                1f
            )
        )
    }

    val mirroredPath = NativePath()
    this.transform(matrix, mirroredPath)
    return mirroredPath
}

@Suppress("unused")
internal fun Path.mirrorIfNeeded(
    canvasSize: IntegerSize,
    mirroringLines: List<Line>
): Path = asAndroidPath().mirrorIfNeeded(
    canvasSize = canvasSize,
    mirroringLines = mirroringLines
).asComposePath()

internal fun NativePath.mirrorIfNeeded(
    canvasSize: IntegerSize,
    mirroringLines: List<Line>
): NativePath = if (mirroringLines.isNotEmpty()) {
    NativePath(this).apply {
        mirroringLines.forEach { mirroringLine ->
            addPath(
                mirror(
                    x = mirroringLine.startX * canvasSize.width,
                    y = mirroringLine.startY * canvasSize.height,
                    x1 = mirroringLine.endX * canvasSize.width,
                    y1 = mirroringLine.endY * canvasSize.height
                )
            )
        }
    }
} else {
    this
}

@Suppress("unused")
internal fun Path.mirror(
    x: Float,
    y: Float,
    x1: Float,
    y1: Float
): Path = asAndroidPath().mirror(
    x = x,
    y = y,
    x1 = x1,
    y1 = y1
).asComposePath()

@Suppress("unused")
fun Canvas.drawInfiniteLine(
    line: Line,
    paint: NativePaint = NativePaint().apply {
        color = Color.Red.toArgb()
        style = NativePaint.Style.STROKE
        strokeWidth = 5f
    }
) {
    val width = width.toFloat()
    val height = height.toFloat()

    val startX = line.startX * width
    val startY = line.startY * height
    val endX = line.endX * width
    val endY = line.endY * height

    val dx = endX - startX
    val dy = endY - startY

    if (dx == 0f) {
        drawLine(startX, 0f, startX, height, paint)
        return
    }

    if (dy == 0f) {
        drawLine(0f, startY, width, startY, paint)
        return
    }

    val directionX = dx / sqrt(dx * dx + dy * dy)
    val directionY = dy / sqrt(dx * dx + dy * dy)

    val scale = maxOf(width, height) * 2
    val extendedStartX = startX - directionX * scale
    val extendedStartY = startY - directionY * scale
    val extendedEndX = endX + directionX * scale
    val extendedEndY = endY + directionY * scale

    drawLine(extendedStartX, extendedStartY, extendedEndX, extendedEndY, paint)
}

internal fun ImageBitmap.clipBitmap(
    path: Path,
    paint: Paint,
): ImageBitmap = asAndroidBitmap()
    .let { it.copy(it.safeConfig, true) }
    .applyCanvas {
        drawPath(
            NativePath(path.asAndroidPath()).apply {
                fillType = NativePath.FillType.INVERSE_WINDING
            },
            paint.asFrameworkPaint()
        )
    }.asImageBitmap()

internal fun ImageBitmap.overlay(overlay: ImageBitmap): ImageBitmap {
    val image = this.asAndroidBitmap()
    return createBitmap(
        width = image.width,
        height = image.height,
        config = image.safeConfig
    ).applyCanvas {
        drawBitmap(image)
        drawBitmap(overlay.asAndroidBitmap())
    }.asImageBitmap()
}

@Composable
internal fun rememberPaint(
    strokeWidth: Pt,
    isEraserOn: Boolean,
    drawColor: Color,
    brushSoftness: Pt,
    drawMode: DrawMode,
    canvasSize: IntegerSize,
    drawPathMode: DrawPathMode,
    drawLineStyle: DrawLineStyle
): State<NativePaint> {
    val context = LocalContext.current

    return remember(
        strokeWidth,
        isEraserOn,
        drawColor,
        brushSoftness,
        drawMode,
        canvasSize,
        drawPathMode,
        context,
        drawLineStyle
    ) {
        derivedStateOf {
            val isSharpEdge = drawPathMode.isSharpEdge
            val isFilled = drawPathMode.isFilled

            Paint().apply {
                if (drawMode !is DrawMode.Text && drawMode !is DrawMode.Image) {
                    pathEffect = drawLineStyle.asPathEffect(
                        canvasSize = canvasSize,
                        strokeWidth = strokeWidth.toPx(canvasSize),
                        context = context
                    )
                }
                blendMode = if (!isEraserOn) blendMode else BlendMode.Clear
                if (isEraserOn) {
                    style = PaintingStyle.Stroke
                    this.strokeWidth = strokeWidth.toPx(canvasSize)
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                } else {
                    if (drawMode !is DrawMode.Text) {
                        if (isFilled) {
                            style = PaintingStyle.Fill
                        } else {
                            style = PaintingStyle.Stroke
                            this.strokeWidth = drawPathMode.convertStrokeWidth(
                                strokeWidth = strokeWidth,
                                canvasSize = canvasSize
                            )
                            if (drawMode is DrawMode.Highlighter || isSharpEdge) {
                                strokeCap = StrokeCap.Square
                            } else {
                                strokeCap = StrokeCap.Round
                                strokeJoin = StrokeJoin.Round
                            }
                        }
                    }
                }
                color = if (drawMode is DrawMode.PathEffect) {
                    Color.Transparent
                } else drawColor
                alpha = drawColor.alpha
            }.asFrameworkPaint().apply {
                if (drawMode is DrawMode.Neon && !isEraserOn) {
                    this.color = Color.White.toArgb()
                    setShadowLayer(
                        brushSoftness.toPx(canvasSize),
                        0f,
                        0f,
                        drawColor
                            .copy(alpha = .8f)
                            .toArgb()
                    )
                } else if (brushSoftness.value > 0f) {
                    maskFilter = BlurMaskFilter(
                        brushSoftness.toPx(canvasSize),
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
                if (drawMode is DrawMode.Text && !isEraserOn) {
                    isAntiAlias = true
                    textSize = strokeWidth.toPx(canvasSize)
                    typeface = drawMode.font.toTypeface()
                }
            }
        }
    }
}

fun pathEffectPaint(
    strokeWidth: Pt,
    drawPathMode: DrawPathMode,
    canvasSize: IntegerSize,
): NativePaint {
    val isSharpEdge = drawPathMode.isSharpEdge
    val isFilled = drawPathMode.isFilled

    return Paint().apply {
        if (isFilled) {
            style = PaintingStyle.Fill
        } else {
            style = PaintingStyle.Stroke
            this.strokeWidth = strokeWidth.toPx(canvasSize)
            if (isSharpEdge) {
                strokeCap = StrokeCap.Square
            } else {
                strokeCap = StrokeCap.Round
                strokeJoin = StrokeJoin.Round
            }
        }

        color = Color.Transparent
        blendMode = BlendMode.Clear
    }.asFrameworkPaint()
}

@Composable
fun rememberPathEffectPaint(
    strokeWidth: Pt,
    drawPathMode: DrawPathMode,
    canvasSize: IntegerSize,
): State<NativePaint> = remember(
    strokeWidth,
    drawPathMode,
    canvasSize
) {
    derivedStateOf {
        pathEffectPaint(
            strokeWidth = strokeWidth,
            drawPathMode = drawPathMode,
            canvasSize = canvasSize
        )
    }
}

internal fun DrawLineStyle.asPathEffect(
    canvasSize: IntegerSize,
    strokeWidth: Float,
    context: Context
): PathEffect? = when (this) {
    is DrawLineStyle.Dashed -> {
        PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                size.toPx(canvasSize),
                gap.toPx(canvasSize) + strokeWidth
            ),
            phase = 0f
        )
    }

    DrawLineStyle.DotDashed -> {
        val dashOnInterval1 = strokeWidth * 4
        val dashOffInterval1 = strokeWidth * 2
        val dashOnInterval2 = strokeWidth / 4
        val dashOffInterval2 = strokeWidth * 2

        PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                dashOnInterval1,
                dashOffInterval1,
                dashOnInterval2,
                dashOffInterval2
            ),
            phase = 0f
        )
    }

    is DrawLineStyle.Stamped<*> -> {
        fun Shape.toPath(): Path = Path().apply {
            addOutline(
                createOutline(
                    size = Size(strokeWidth, strokeWidth),
                    layoutDirection = LayoutDirection.Ltr,
                    density = context.density
                )
            )
        }

        val path: Path? = when (shape) {
            is Shape -> shape.toPath()
            is NativePath -> shape.asComposePath()
            is Path -> shape
            null -> MaterialStarShape.toPath()
            else -> null
        }

        path?.let {
            PathEffect.stampedPathEffect(
                shape = it,
                advance = spacing.toPx(canvasSize) + strokeWidth,
                phase = 0f,
                style = StampedPathEffectStyle.Morph
            )
        }
    }

    is DrawLineStyle.ZigZag -> {
        val zigZagPath = Path().apply {
            val zigZagLineWidth = strokeWidth / heightRatio
            val shapeVerticalOffset = (strokeWidth / 2) / 2
            val shapeHorizontalOffset = (strokeWidth / 2) / 2
            moveTo(0f, 0f)
            lineTo(strokeWidth / 2, strokeWidth / 2)
            lineTo(strokeWidth, 0f)
            lineTo(strokeWidth, 0f + zigZagLineWidth)
            lineTo(strokeWidth / 2, strokeWidth / 2 + zigZagLineWidth)
            lineTo(0f, 0f + zigZagLineWidth)
            translate(Offset(-shapeHorizontalOffset, -shapeVerticalOffset))
        }

        PathEffect.stampedPathEffect(
            shape = zigZagPath,
            advance = strokeWidth,
            phase = 0f,
            style = StampedPathEffectStyle.Morph
        )
    }

    DrawLineStyle.None -> null
}

@SuppressLint("ComposableNaming")
@Composable
internal fun NativeCanvas.drawRepeatedImageOnPath(
    drawMode: DrawMode.Image,
    strokeWidth: Pt,
    canvasSize: IntegerSize,
    path: NativePath,
    paint: NativePaint,
    invalidations: Int
) {
    val context = LocalContext.current
    var pathImage by remember(strokeWidth, canvasSize) {
        mutableStateOf<Bitmap?>(null)
    }
    LaunchedEffect(pathImage, drawMode.imageData, strokeWidth, canvasSize, invalidations) {
        if (pathImage == null) {
            pathImage = context.imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(drawMode.imageData)
                    .size(strokeWidth.toPx(canvasSize).roundToInt())
                    .build()
            ).image?.toBitmap()
        }
    }
    pathImage?.let { bitmap ->
        drawRepeatedBitmapOnPath(
            bitmap = bitmap,
            path = path,
            paint = paint,
            interval = drawMode.repeatingInterval.toPx(canvasSize)
        )
    }
}

internal fun transformationsForMode(
    drawMode: DrawMode,
    canvasSize: IntegerSize
): List<Filter<*>> = when (drawMode) {
    is DrawMode.PathEffect.PrivacyBlur -> {
        listOf(
            UiNativeStackBlurFilter(
                value = drawMode.blurRadius.toFloat() / 1000 * max(canvasSize)
            )
        )
    }

    is DrawMode.PathEffect.Pixelation -> {
        listOf(
            UiNativeStackBlurFilter(
                value = 20f / 1000 * max(canvasSize)
            ),
            UiPixelationFilter(
                value = drawMode.pixelSize / 1000 * max(canvasSize)
            )
        )
    }

    is DrawMode.PathEffect.Custom -> {
        drawMode.filter?.let {
            listOf(it.toUiFilter())
        } ?: emptyList()
    }

    else -> emptyList()
}