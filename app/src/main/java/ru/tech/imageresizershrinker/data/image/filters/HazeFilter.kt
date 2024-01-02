package ru.tech.imageresizershrinker.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class HazeFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.2f to 0f,
) : GPUFilterTransformation(context), Filter.Haze<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHazeFilter(value.first, value.second)
}