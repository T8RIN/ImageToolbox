package ru.tech.imageresizershrinker.presentation.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class HueFilter(
    private val context: @RawValue Context,
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