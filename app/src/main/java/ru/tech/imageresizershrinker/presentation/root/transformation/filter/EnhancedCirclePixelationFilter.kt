package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.Pixelate
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.PixelateLayer


class EnhancedCirclePixelationFilter(
    private val context: Context,
    override val value: Float = 32f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.enhanced_circle_pixelation,
    value = value,
    valueRange = 15f..100f
), Filter.EnhancedCirclePixelation<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = throw NotImplementedError()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Square)
                    .setResolution(value)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setOffset(value / 2)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 1.2f)
                    .setOffset(value / 2.5f)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 1.8f)
                    .setOffset(value / 3)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 2.7f)
                    .setOffset(value / 4)
                    .build()
            )
        )
    }
}