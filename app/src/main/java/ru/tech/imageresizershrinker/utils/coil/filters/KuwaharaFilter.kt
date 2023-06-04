package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageKuwaharaFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
data class KuwaharaFilter(
    private val context: @RawValue Context,
    override val value: Float = 3f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.kuwahara,
    value = value,
    valueRange = 0f..10f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageKuwaharaFilter(value.toInt())
}