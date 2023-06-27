package ru.tech.imageresizershrinker.presentation.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class BrightnessFilter(
    private val context: @RawValue Context,
    override val value: Float = 0f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.brightness,
    value = value,
    valueRange = (-1f)..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBrightnessFilter(value)
}