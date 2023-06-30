package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class SepiaFilter(
    private val context: @RawValue Context,
    override val value: Float = 0f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.sepia,
    value = value,
    valueRange = 0f..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSepiaToneFilter(value)
}