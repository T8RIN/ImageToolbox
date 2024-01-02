package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class MonochromeFilter(
    private val context: Context,
    override val value: Float = 1f,
) : GPUFilterTransformation(context), Filter.Monochrome<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageMonochromeFilter(
        /* intensity = */ value,
        /* color = */ floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
    )
}