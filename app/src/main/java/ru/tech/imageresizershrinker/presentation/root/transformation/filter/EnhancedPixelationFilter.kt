package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.Pixelate
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.PixelateLayer

class EnhancedPixelationFilter(
    private val context: Context,
    override val value: Float = 48f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.enhanced_pixelation,
    value = value,
    valueRange = 10f..100f
), Filter.EnhancedPixelation<Bitmap> {
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
                PixelateLayer.Builder(PixelateLayer.Shape.Diamond)
                    .setResolution(value / 4)
                    .setSize(value / 6)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Diamond)
                    .setResolution(value / 4)
                    .setSize(value / 6)
                    .setOffset(value / 8)
                    .build()
            )
        )
    }
}