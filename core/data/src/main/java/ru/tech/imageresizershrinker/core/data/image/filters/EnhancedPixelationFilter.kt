package ru.tech.imageresizershrinker.core.data.image.filters

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.core.data.image.filters.pixelation.Pixelate
import ru.tech.imageresizershrinker.core.data.image.filters.pixelation.PixelateLayer
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter

class EnhancedPixelationFilter(
    override val value: Float = 48f,
) : Filter.EnhancedPixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

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