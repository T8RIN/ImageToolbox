package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class BlackAndWhiteFilter(
    private val context: Context,
    override val value: Unit = Unit
) : GPUFilterTransformation(context), Filter.BlackAndWhite<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLuminanceFilter()
}