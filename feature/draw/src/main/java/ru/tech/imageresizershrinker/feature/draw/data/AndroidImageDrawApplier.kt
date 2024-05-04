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

package ru.tech.imageresizershrinker.feature.draw.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
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
import androidx.core.content.res.ResourcesCompat
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.draw.domain.DrawBehavior
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import ru.tech.imageresizershrinker.feature.draw.domain.ImageDrawApplier
import ru.tech.imageresizershrinker.feature.draw.domain.PathPaint
import javax.inject.Inject

internal class AndroidImageDrawApplier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val filterProvider: FilterProvider<Bitmap>
) : ImageDrawApplier<Bitmap, Path, Color> {

    override suspend fun applyDrawToImage(
        drawBehavior: DrawBehavior,
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Bitmap? {
        val image: Bitmap? = when (drawBehavior) {
            is DrawBehavior.Image -> {
                imageGetter.getImage(data = imageUri)
            }

            is DrawBehavior.Background -> {
                Bitmap.createBitmap(
                    drawBehavior.width,
                    drawBehavior.height,
                    Bitmap.Config.ARGB_8888
                ).apply {
                    val canvas = Canvas(this)
                    val paint = android.graphics.Paint().apply {
                        color = drawBehavior.color
                    }
                    canvas.drawRect(
                        0f,
                        0f,
                        drawBehavior.width.toFloat(),
                        drawBehavior.height.toFloat(),
                        paint
                    )
                }
            }

            else -> null
        }

        val drawImage = image?.let { image.copy(image.config, true) }

        drawImage?.let { bitmap ->
            val canvas = Canvas(bitmap)
            val canvasSize = IntegerSize(
                canvas.width,
                canvas.height
            )
            canvas.apply {
                (drawBehavior as? DrawBehavior.Background)?.apply { drawColor(color) }

                pathPaints.forEach { (nonScaledPath, nonScaledStroke, radius, drawColor, isErasing, drawMode, size, pathMode) ->
                    val stroke = nonScaledStroke.toPx(canvasSize)
                    val path = nonScaledPath.scaleToFitCanvas(
                        currentSize = canvasSize,
                        oldSize = size
                    )
                    val isRect = listOf(
                        DrawPathMode.OutlinedRect,
                        DrawPathMode.OutlinedOval,
                        DrawPathMode.Rect,
                        DrawPathMode.Oval
                    ).any { pathMode::class.isInstance(it) }

                    val isFilled = listOf(
                        DrawPathMode.Rect,
                        DrawPathMode.Oval,
                        DrawPathMode.Lasso
                    ).any { pathMode::class.isInstance(it) }

                    if (drawMode is DrawMode.PathEffect && !isErasing) {
                        val shaderSource = imageTransformer.transform(
                            image = image.overlay(bitmap),
                            transformations = transformationsForMode(drawMode)
                        )?.asImageBitmap()?.clipBitmap(
                            path = path,
                            paint = Paint().apply {
                                if (isFilled) {
                                    style = PaintingStyle.Fill
                                } else {
                                    style = PaintingStyle.Stroke
                                    this.strokeWidth = stroke
                                    if (isRect) {
                                        strokeCap = StrokeCap.Square
                                    } else {
                                        strokeCap = StrokeCap.Round
                                        strokeJoin = StrokeJoin.Round
                                    }
                                }

                                color = Color.Transparent
                                blendMode = BlendMode.Clear
                            }
                        )
                        if (shaderSource != null) {
                            drawBitmap(
                                shaderSource.asAndroidBitmap(),
                                0f,
                                0f,
                                android.graphics.Paint()
                            )
                        }
                    } else {
                        val paint = Paint().apply {
                            blendMode = if (!isErasing) blendMode else BlendMode.Clear
                            if (isErasing) {
                                style = PaintingStyle.Stroke
                                this.strokeWidth = stroke
                                strokeCap = StrokeCap.Round
                                strokeJoin = StrokeJoin.Round
                            } else {
                                if (drawMode !is DrawMode.Text) {
                                    if (isFilled) {
                                        style = PaintingStyle.Fill
                                    } else {
                                        style = PaintingStyle.Stroke
                                        strokeWidth = stroke
                                        if (drawMode is DrawMode.Highlighter || isRect) {
                                            strokeCap = StrokeCap.Square
                                        } else {
                                            strokeCap = StrokeCap.Round
                                            strokeJoin = StrokeJoin.Round
                                        }
                                    }
                                }
                            }
                            color = drawColor
                            alpha = drawColor.alpha
                        }.asFrameworkPaint().apply {
                            if (drawMode is DrawMode.Neon && !isErasing) {
                                this.color = Color.White.toArgb()
                                setShadowLayer(
                                    radius.toPx(canvasSize),
                                    0f,
                                    0f,
                                    drawColor
                                        .copy(alpha = .8f)
                                        .toArgb()
                                )
                            } else if (radius.value > 0f) {
                                maskFilter =
                                    BlurMaskFilter(
                                        radius.toPx(canvasSize),
                                        BlurMaskFilter.Blur.NORMAL
                                    )
                            }
                            if (drawMode is DrawMode.Text && !isErasing) {
                                isAntiAlias = true
                                textSize = stroke
                                if (drawMode.font != 0) {
                                    typeface = ResourcesCompat.getFont(context, drawMode.font)
                                }
                            }
                        }
                        val androidPath = path.asAndroidPath()
                        if (drawMode is DrawMode.Text && !isErasing) {
                            if (drawMode.isRepeated) {
                                drawRepeatedTextOnPath(
                                    text = drawMode.text,
                                    path = androidPath,
                                    paint = paint,
                                    interval = drawMode.repeatingDashSize.toPx(canvasSize)
                                )
                            } else {
                                drawTextOnPath(drawMode.text, androidPath, 0f, 0f, paint)
                            }
                        } else {
                            drawPath(androidPath, paint)
                        }
                    }
                }
            }
        }
        return drawImage?.let { image.overlay(it) }
    }

    override suspend fun applyEraseToImage(
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Bitmap? = applyEraseToImage(
        pathPaints = pathPaints,
        image = imageGetter.getImage(data = imageUri),
        shaderSourceUri = imageUri
    )

    override suspend fun applyEraseToImage(
        pathPaints: List<PathPaint<Path, Color>>,
        image: Bitmap?,
        shaderSourceUri: String
    ): Bitmap? {
        val drawImage = image?.let { it.copy(it.config, true) }

        drawImage?.let {
            val canvas = Canvas(it)
            val canvasSize = IntegerSize(
                canvas.width,
                canvas.height
            )
            canvas.apply {
                drawBitmap(
                    it, 0f, 0f, android.graphics.Paint()
                )

                val recoveryShader = imageGetter.getImage(
                    data = shaderSourceUri
                )?.asImageBitmap()?.let { bmp -> ImageShader(bmp) }

                pathPaints.forEach { (nonScaledPath, stroke, radius, _, isRecoveryOn, _, size, mode) ->
                    val path = nonScaledPath.scaleToFitCanvas(
                        currentSize = canvasSize,
                        oldSize = size
                    )
                    this.drawPath(
                        path.asAndroidPath(),
                        Paint().apply {
                            style = if (mode is DrawPathMode.Lasso) {
                                PaintingStyle.Fill
                            } else PaintingStyle.Stroke
                            if (isRecoveryOn) {
                                shader = recoveryShader
                            } else {
                                blendMode = BlendMode.Clear
                            }
                            strokeCap = StrokeCap.Round
                            this.strokeWidth = stroke.toPx(canvasSize)
                            strokeJoin = StrokeJoin.Round

                        }.asFrameworkPaint().apply {
                            if (radius.value > 0f) {
                                maskFilter =
                                    BlurMaskFilter(
                                        radius.toPx(canvasSize),
                                        BlurMaskFilter.Blur.NORMAL
                                    )
                            }
                        }
                    )
                }

            }
        }

        return drawImage
    }

    private fun transformationsForMode(
        drawMode: DrawMode
    ): List<Transformation<Bitmap>> = when (drawMode) {
        is DrawMode.PathEffect.PrivacyBlur -> {
            listOf(
                object : Filter.StackBlur<Bitmap> {
                    override val value: Pair<Float, Int>
                        get() = when {
                            drawMode.blurRadius < 10 -> 0.8f
                            drawMode.blurRadius < 20 -> 0.5f
                            else -> 0.3f
                        } to drawMode.blurRadius
                }
            )
        }

        is DrawMode.PathEffect.Pixelation -> {
            listOf(
                object : Filter.StackBlur<Bitmap> {
                    override val value: Pair<Float, Int>
                        get() = when {
                            drawMode.pixelSize < 10 -> 0.8f
                            drawMode.pixelSize < 20 -> 0.5f
                            else -> 0.3f
                        } to 20
                },
                object : Filter.Pixelation<Bitmap> {
                    override val value: Float
                        get() = drawMode.pixelSize
                }
            )
        }

        else -> emptyList()
    }.map {
        filterProvider.filterToTransformation(it)
    }

    private fun ImageBitmap.clipBitmap(
        path: Path,
        paint: Paint,
    ): ImageBitmap {
        val bitmap = this.asAndroidBitmap()
        val newPath = android.graphics.Path(path.asAndroidPath())
        Canvas(bitmap).apply {
            drawPath(
                newPath.apply {
                    fillType = android.graphics.Path.FillType.INVERSE_WINDING
                },
                paint.asFrameworkPaint()
            )
        }
        return bitmap.asImageBitmap()
    }

    private fun Bitmap.overlay(overlay: Bitmap): Bitmap {
        val image = this
        val finalBitmap = Bitmap.createBitmap(image.width, image.height, image.config)
        val canvas = Canvas(finalBitmap)
        canvas.drawBitmap(image, Matrix(), null)
        canvas.drawBitmap(overlay, 0f, 0f, null)
        return finalBitmap
    }

    private fun Path.scaleToFitCanvas(
        currentSize: IntegerSize,
        oldSize: IntegerSize,
        onGetScale: (Float, Float) -> Unit = { _, _ -> }
    ): Path {
        val sx = currentSize.width.toFloat() / oldSize.width
        val sy = currentSize.height.toFloat() / oldSize.height
        onGetScale(sx, sy)
        return android.graphics.Path(this.asAndroidPath()).apply {
            transform(
                Matrix().apply {
                    setScale(sx, sy)
                }
            )
        }.asComposePath()
    }

}