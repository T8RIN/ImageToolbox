package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.Pixelate
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.PixelateLayer

class EnhancedDiamondPixelationFilter(
    private val context: Context,
    override val value: Float = 48f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.enhanced_diamond_pixelation,
    value = value,
    valueRange = 20f..100f
), Filter.EnhancedDiamondPixelation<Bitmap> {
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
                    .setResolution(value)
                    .setOffset(value / 4)
                    .setAlpha(0.5f)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Diamond)
                    .setResolution(value)
                    .setOffset(value)
                    .setAlpha(0.5f)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value / 3)
                    .setSize(value / 6)
                    .setOffset(value / 12)
                    .build()
            )
        )
    }
}