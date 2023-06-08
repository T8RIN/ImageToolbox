package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
class VibranceFilter(
    private val context: @RawValue Context,
    override val value: Float = 0f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.vibrance,
    value = value,
    valueRange = -2f..2f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageVibranceFilter(value)
}