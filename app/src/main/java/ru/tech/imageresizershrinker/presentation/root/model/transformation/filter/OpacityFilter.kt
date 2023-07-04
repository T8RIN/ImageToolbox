package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOpacityFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class OpacityFilter(
    private val context: @RawValue Context,
    override val value: Float = 1f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.opacity,
    value = value,
    valueRange = 0f..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageOpacityFilter(value)
}