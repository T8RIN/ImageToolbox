package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R

class ContrastFilter(
    private val context: Context,
    override val value: Float = 0f,
) : FilterTransformation(
    context = context,
    title = R.string.contrast,
    value = value,
    valueRange = 0f..4f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageContrastFilter(value)
}