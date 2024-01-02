package ru.tech.imageresizershrinker.data.image.filters

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.data.image.filters.pixelation.Pixelate
import ru.tech.imageresizershrinker.data.image.filters.pixelation.PixelateLayer
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class PixelationFilter(
    override val value: Float = 25f,
) : Filter.Pixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Square)
                    .setResolution(value - 4f)
                    .setSize(value)
                    .build()
            )
        )
    }
}