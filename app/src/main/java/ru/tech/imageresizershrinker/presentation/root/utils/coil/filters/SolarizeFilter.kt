package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
class SolarizeFilter(
    private val context: @RawValue Context,
    override val value: Float = 0.5f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.solarize,
    value = value,
    valueRange = 0f..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSolarizeFilter(value)
}