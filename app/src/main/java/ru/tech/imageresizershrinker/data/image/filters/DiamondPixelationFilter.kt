package ru.tech.imageresizershrinker.data.image.filters

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.data.image.filters.pixelation.Pixelate
import ru.tech.imageresizershrinker.data.image.filters.pixelation.PixelateLayer
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class DiamondPixelationFilter(
    override val value: Float = 24f,
) : Filter.DiamondPixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

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