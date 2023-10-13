package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.Pixelate
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.PixelateLayer

class DiamondPixelationFilter(
    private val context: Context,
    override val value: Float = 24f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.diamond_pixelation,
    value = value,
    valueRange = 10f..60f
), Filter.DiamondPixelation<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = throw NotImplementedError()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Diamond)
                    .setResolution(value)
                    .setSize(value + 1)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Diamond)
                    .setResolution(value)
                    .setOffset(value / 2)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Square)
                    .setResolution(value)
                    .setAlpha(0.6f)
                    .build()
            )
        )
    }
}