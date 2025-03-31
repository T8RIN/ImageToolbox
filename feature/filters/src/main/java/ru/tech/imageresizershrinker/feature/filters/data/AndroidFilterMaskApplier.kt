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

package ru.tech.imageresizershrinker.feature.filters.data

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import ru.tech.imageresizershrinker.core.data.utils.safeConfig
import ru.tech.imageresizershrinker.core.data.utils.toSoftware
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.draw.domain.PathPaint
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMask
import ru.tech.imageresizershrinker.feature.filters.domain.FilterMaskApplier
import javax.inject.Inject

internal class AndroidFilterMaskApplier @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
) : FilterMaskApplier<Bitmap, Path, Color> {

    override suspend fun filterByMask(
        filterMask: FilterMask<Path, Color>,
        imageUri: String,
    ): Bitmap? = imageGetter.getImage(uri = imageUri)?.let {
        filterByMask(filterMask = filterMask, image = it.image)
    }

    override suspend fun filterByMask(
        filterMask: FilterMask<Path, Color>,
        image: Bitmap,
    ): Bitmap? {
        if (filterMask.filters.isEmpty()) return image

        val filteredBitmap = imageTransformer.transform(
            image = image,
            transformations = filterMask.filters.map {
                filterProvider.filterToTransformation(it)
            }
        )?.clipBitmap(
            pathPaints = filterMask.maskPaints,
            inverse = filterMask.isInverseFillType
        )
        return filteredBitmap?.let {
            image.let { bitmap ->
                if (filterMask.filters.any { it is Filter.RemoveColor }) {
                    bitmap.clipBitmap(
                        pathPaints = filterMask.maskPaints,
                        inverse = !filterMask.isInverseFillType
                    )
                } else bitmap
            }.overlay(filteredBitmap)
        }
    }

    private fun Bitmap.clipBitmap(
        pathPaints: List<PathPaint<Path, Color>>,
        inverse: Boolean,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width, this.height, this.safeConfig)
            .apply { setHasAlpha(true) }
        val canvasSize = bitmap.run { IntegerSize(width, height) }
        Canvas(bitmap).apply {
            pathPaints.forEach { pathPaint ->
                val path = pathPaint.path.scaleToFitCanvas(
                    currentSize = canvasSize,
                    oldSize = pathPaint.canvasSize
                )
                val drawPathMode = pathPaint.drawPathMode
                val isSharpEdge = drawPathMode.isSharpEdge
                val isFilled = drawPathMode.isFilled

                drawPath(
                    path,
                    Paint().apply {
                        if (pathPaint.isErasing) {
                            style = PaintingStyle.Stroke
                            this.strokeWidth = pathPaint.strokeWidth.toPx(canvasSize)
                            strokeCap = StrokeCap.Round
                            strokeJoin = StrokeJoin.Round
                        } else {
                            if (isFilled) {
                                style = PaintingStyle.Fill
                            } else {
                                style = PaintingStyle.Stroke
                                if (isSharpEdge) {
                                    strokeCap = StrokeCap.Square
                                } else {
                                    strokeCap = StrokeCap.Round
                                    strokeJoin = StrokeJoin.Round
                                }
                                this.strokeWidth = pathPaint.strokeWidth.toPx(canvasSize)
                            }
                        }
                        color = pathPaint.drawColor
                        if (pathPaint.isErasing) {
                            blendMode = BlendMode.Clear
                        }
                    }.asFrameworkPaint().apply {
                        if (pathPaint.brushSoftness.value > 0f) {
                            maskFilter = BlurMaskFilter(
                                pathPaint.brushSoftness.toPx(canvasSize),
                                BlurMaskFilter.Blur.NORMAL
                            )
                        }
                    }
                )
            }
            drawBitmap(
                this@clipBitmap,
                0f,
                0f,
                android.graphics.Paint()
                    .apply {
                        xfermode = if (!inverse) {
                            PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                        } else {
                            PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
                        }
                    }
            )
        }
        return bitmap
    }

    private fun Bitmap.overlay(overlay: Bitmap): Bitmap {
        val image = this
        val finalBitmap =
            Bitmap.createBitmap(image.width, image.height, image.safeConfig.toSoftware())
        val canvas = Canvas(finalBitmap)
        canvas.drawBitmap(image, Matrix(), null)
        canvas.drawBitmap(overlay.toSoftware(), 0f, 0f, null)
        return finalBitmap
    }

    private fun Path.scaleToFitCanvas(
        currentSize: IntegerSize,
        oldSize: IntegerSize,
        onGetScale: (Float, Float) -> Unit = { _, _ -> },
    ): android.graphics.Path {
        val sx = currentSize.width.toFloat() / oldSize.width
        val sy = currentSize.height.toFloat() / oldSize.height
        onGetScale(sx, sy)
        return android.graphics.Path(this.asAndroidPath()).apply {
            transform(
                Matrix().apply {
                    setScale(sx, sy)
                }
            )
        }
    }

    override suspend fun filterByMasks(
        filterMasks: List<FilterMask<Path, Color>>,
        imageUri: String,
    ): Bitmap? = imageGetter.getImage(uri = imageUri)?.let {
        filterByMasks(filterMasks, it.image)
    }

    override suspend fun filterByMasks(
        filterMasks: List<FilterMask<Path, Color>>,
        image: Bitmap,
    ): Bitmap? = filterMasks.fold<FilterMask<Path, Color>, Bitmap?>(
        initial = image,
        operation = { bmp, mask ->
            bmp?.let {
                filterByMask(
                    filterMask = mask, image = bmp
                )
            }
        }
    )
}