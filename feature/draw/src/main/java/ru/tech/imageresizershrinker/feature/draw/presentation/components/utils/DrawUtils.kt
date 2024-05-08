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

package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import coil.request.ImageRequest
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalImageLoader
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.domain.Pt
import kotlin.math.roundToInt
import android.graphics.Path as NativePath

fun Path.copy(): Path = NativePath(this.asAndroidPath()).asComposePath()

fun ImageBitmap.clipBitmap(
    path: Path,
    paint: Paint,
): ImageBitmap {
    val bitmap = this.asAndroidBitmap()
    val newPath = NativePath(path.asAndroidPath())
    Canvas(bitmap).apply {
        drawPath(
            newPath.apply {
                fillType = NativePath.FillType.INVERSE_WINDING
            },
            paint.asFrameworkPaint()
        )
    }
    return bitmap.asImageBitmap()
}

fun ImageBitmap.overlay(overlay: ImageBitmap): ImageBitmap {
    val image = this.asAndroidBitmap()
    val finalBitmap = Bitmap.createBitmap(image.width, image.height, image.config)
    val canvas = Canvas(finalBitmap)
    canvas.drawBitmap(image, Matrix(), null)
    canvas.drawBitmap(overlay.asAndroidBitmap(), 0f, 0f, null)
    return finalBitmap.asImageBitmap()
}

@Composable
fun rememberPaint(
    strokeWidth: Pt,
    isEraserOn: Boolean,
    drawColor: Color,
    brushSoftness: Pt,
    drawMode: DrawMode,
    canvasSize: IntegerSize,
    drawPathMode: DrawPathMode,
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
        context
    ) {
        derivedStateOf {
            val isRect = listOf(
                DrawPathMode.OutlinedRect,
                DrawPathMode.OutlinedOval,
                DrawPathMode.Rect,
                DrawPathMode.Oval,
                DrawPathMode.Lasso
            ).any { drawPathMode::class.isInstance(it) }

            val isFilled = listOf(
                DrawPathMode.Rect,
                DrawPathMode.Oval,
                DrawPathMode.Lasso,
                DrawPathMode.Triangle,
                DrawPathMode.Polygon(),
                DrawPathMode.Star()
            ).any { drawPathMode::class.isInstance(it) }

            Paint().apply {
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
                            this.strokeWidth = strokeWidth.toPx(canvasSize)
                            if (drawMode is DrawMode.Highlighter || isRect) {
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
                    if (drawMode.font != 0) {
                        typeface = ResourcesCompat.getFont(context, drawMode.font)
                    }
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
    val isRect = listOf(
        DrawPathMode.OutlinedRect,
        DrawPathMode.OutlinedOval,
        DrawPathMode.Rect,
        DrawPathMode.Oval,
        DrawPathMode.Lasso
    ).any { drawPathMode::class.isInstance(it) }

    val isFilled = listOf(
        DrawPathMode.Rect,
        DrawPathMode.Oval,
        DrawPathMode.Lasso,
        DrawPathMode.Triangle,
        DrawPathMode.Polygon(),
        DrawPathMode.Star()
    ).any { drawPathMode::class.isInstance(it) }

    return Paint().apply {
        if (isFilled) {
            style = PaintingStyle.Fill
        } else {
            style = PaintingStyle.Stroke
            this.strokeWidth = strokeWidth.toPx(canvasSize)
            if (isRect) {
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

@Composable
fun NativeCanvas.drawRepeatedImageOnPath(
    drawMode: DrawMode.Image,
    strokeWidth: Pt,
    canvasSize: IntegerSize,
    path: NativePath,
    paint: NativePaint,
) {
    val context = LocalContext.current
    var pathImage by remember(strokeWidth, canvasSize) {
        mutableStateOf<Bitmap?>(null)
    }
    val imageLoader = LocalImageLoader.current
    LaunchedEffect(pathImage, drawMode.imageData, strokeWidth, canvasSize) {
        if (pathImage == null) {
            pathImage = imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(drawMode.imageData)
                    .size(strokeWidth.toPx(canvasSize).roundToInt())
                    .build()
            ).drawable?.toBitmap()
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