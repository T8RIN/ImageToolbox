package ru.tech.imageresizershrinker.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBalanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class ColorBalanceFilter(
    private val context: Context,
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : GPUFilterTransformation(context), Filter.ColorBalance<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageColorBalanceFilter().apply {
        setHighlights(value.take(3).toFloatArray())
        setMidtones(floatArrayOf(value[3], value[4], value[6]))
        setShowdows(value.takeLast(3).toFloatArray())
    }
}