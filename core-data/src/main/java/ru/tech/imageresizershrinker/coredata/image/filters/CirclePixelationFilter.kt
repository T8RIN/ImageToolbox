package ru.tech.imageresizershrinker.coredata.image.filters

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.coredata.image.filters.pixelation.Pixelate
import ru.tech.imageresizershrinker.coredata.image.filters.pixelation.PixelateLayer
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

class CirclePixelationFilter(
    override val value: Float = 24f,
) : Filter.CirclePixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 3f)
                    .setOffset(value / 2)
                    .build()
            )
        )
    }
}