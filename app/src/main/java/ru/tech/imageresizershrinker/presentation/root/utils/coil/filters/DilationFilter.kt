package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDilationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class DilationFilter(
    private val context: @RawValue Context,
    override val value: Float = 1f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.dilation,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 0f..4f, 0)
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageDilationFilter(value.toInt())
}