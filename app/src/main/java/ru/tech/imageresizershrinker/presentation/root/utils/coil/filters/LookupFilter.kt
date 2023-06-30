package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
class LookupFilter(
    private val context: @RawValue Context,
    override val value: Float = 0f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.lookup,
    value = value,
    valueRange = -10f..10f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLookupFilter(value)
}