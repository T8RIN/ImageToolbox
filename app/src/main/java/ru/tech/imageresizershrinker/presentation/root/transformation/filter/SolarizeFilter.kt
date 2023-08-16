package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class SolarizeFilter(
    private val context: Context,
    override val value: Float = 0.5f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.solarize,
    value = value,
    valueRange = 0f..1f
), Filter.Solarize<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSolarizeFilter(value)
}