package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class PixelationFilter(
    private val context: Context,
    override val value: Float = 10f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.pixelation,
    value = value,
    valueRange = 1f..50f
), Filter.Pixelation<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImagePixelationFilter().apply {
        setPixel(value)
    }
}