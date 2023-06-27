package ru.tech.imageresizershrinker.presentation.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class SharpenFilter(
    private val context: @RawValue Context,
    override val value: Float = 0f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.sharpen,
    value = value,
    valueRange = -4f..4f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSharpenFilter(value)
}