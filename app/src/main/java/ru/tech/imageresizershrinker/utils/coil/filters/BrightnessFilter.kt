package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R


class BrightnessFilter(
    private val context: Context,
    override val value: Float = 0f,
) : FilterTransformation(
    context = context,
    title = R.string.brightness,
    value = value,
    valueRange = (-1f)..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBrightnessFilter(value)
}