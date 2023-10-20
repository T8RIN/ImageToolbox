package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class WhiteBalanceFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 5000.0f to 0.0f,
) : GPUFilterTransformation(context), Filter.WhiteBalance<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageWhiteBalanceFilter(value.first, value.second)
}