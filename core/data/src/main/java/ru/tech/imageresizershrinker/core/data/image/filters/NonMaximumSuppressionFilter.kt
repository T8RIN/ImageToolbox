package ru.tech.imageresizershrinker.core.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageNonMaximumSuppressionFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class NonMaximumSuppressionFilter(
    private val context: Context,
    override val value: Unit = Unit
) : GPUFilterTransformation(context), Filter.NonMaximumSuppression<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageNonMaximumSuppressionFilter()
}