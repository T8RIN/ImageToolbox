package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import ru.tech.imageresizershrinker.R

data class HueFilter(
    private val context: Context,
    override val value: Float = 90f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.hue,
    value = value,
    valueRange = 0f..255f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHueFilter(value)
}