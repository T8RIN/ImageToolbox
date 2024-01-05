package ru.tech.imageresizershrinker.core.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SketchFilter(
    private val context: Context,
    override val value: Unit = Unit,
) : GPUFilterTransformation(context), Filter.Sketch<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSketchFilter()
}