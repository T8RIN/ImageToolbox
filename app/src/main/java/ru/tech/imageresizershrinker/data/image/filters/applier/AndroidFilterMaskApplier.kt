package ru.tech.imageresizershrinker.data.image.filters.applier

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
import androidx.exifinterface.media.ExifInterface
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.draw.PathPaint
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterMaskApplier
import ru.tech.imageresizershrinker.domain.model.FilterMask
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import javax.inject.Inject

class AndroidFilterMaskApplier @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : FilterMaskApplier<Bitmap, Path, Color> {

    override suspend fun filterByMask(
        filterMask: FilterMask<Bitmap, Path, Color>,
        imageUri: String
    ): Bitmap? = imageManager.getImage(uri = imageUri)?.let {
        filterByMask(filterMask = filterMask, image = it.image)
    }

    override suspend fun filterByMask(
        filterMask: FilterMask<Bitmap, Path, Color>,
        image: Bitmap
    ): Bitmap? {
        if (filterMask.filters.isEmpty()) return image

        //TODO: Looks weird
        val filteredBitmap = imageManager.filter(
            image = image,
            filters = filterMask.filters
        )?.clipBitmap(
            pathPaints = filterMask.maskPaints,
            inverse = filterMask.isInverseFillType
        )
        return filteredBitmap?.let {
            image.let { bitmap ->
                if (filterMask.filters.any { it is Filter.RemoveColor<*, *> }) {
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
        inverse: Boolean
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width, this.height, this.config)
            .apply { setHasAlpha(true) }
        val canvasSize = bitmap.run { IntegerSize(width, height) }
        Canvas(bitmap).apply {
            pathPaints.forEach {
                val path = it.path.scaleToFitCanvas(
                    currentSize = canvasSize,
                    oldSize = it.canvasSize
                )
                drawPath(
                    path.apply {
                        if (inverse) fillType = android.graphics.Path.FillType.INVERSE_WINDING
                    },
                    Paint().apply {
                        style = PaintingStyle.Stroke
                        strokeCap = StrokeCap.Round
                        this.strokeWidth = it.strokeWidth.toPx(canvasSize)
                        strokeJoin = StrokeJoin.Round
                        isAntiAlias = true
                        color = it.drawColor
                        if (it.isErasing) {
                            blendMode = BlendMode.Clear
                        }
                    }.asFrameworkPaint().apply {
                        if (it.brushSoftness.value > 0f) {
                            maskFilter =
                                BlurMaskFilter(
                                    it.brushSoftness.toPx(canvasSize),
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
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    }
            )
        }
        return bitmap
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
        filterMasks: List<FilterMask<Bitmap, Path, Color>>,
        imageUri: String
    ): Bitmap? = imageManager.getImage(uri = imageUri)?.let {
        filterByMasks(filterMasks, it.image)
    }

    override suspend fun filterByMasks(
        filterMasks: List<FilterMask<Bitmap, Path, Color>>,
        image: Bitmap
    ): Bitmap? = filterMasks.fold<FilterMask<Bitmap, Path, Color>, Bitmap?>(
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