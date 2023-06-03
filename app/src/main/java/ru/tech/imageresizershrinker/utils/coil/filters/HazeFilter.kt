package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
data class HazeFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.2f to 0f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.haze,
    value = value,
    valueRange = -0.3f..0.3f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHazeFilter(value.first, value.second)
}