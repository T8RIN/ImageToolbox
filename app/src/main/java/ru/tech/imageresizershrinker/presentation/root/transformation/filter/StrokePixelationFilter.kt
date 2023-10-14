package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.Pixelate
import ru.tech.imageresizershrinker.presentation.root.transformation.pixelation.PixelateLayer

class StrokePixelationFilter(
    private val context: Context,
    override val value: Float = 32f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.stroke_pixelation,
    value = value,
    valueRange = 5f..75f
), Filter.StrokePixelation<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = throw NotImplementedError()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 5)
                    .setOffset(value / 4)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 4)
                    .setOffset(value / 2)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 3)
                    .setOffset(value / 1.3f)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(value)
                    .setSize(value / 4)
                    .setOffset(0f)
                    .build()
            )
        )
    }
}