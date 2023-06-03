package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import ru.tech.imageresizershrinker.R

class SaturationFilter(
    private val context: Context,
    override val value: Float = 1f,
) : FilterTransformation(
    context = context,
    title = R.string.saturation,
    value = value,
    valueRange = 0f..2f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSaturationFilter(value)
}