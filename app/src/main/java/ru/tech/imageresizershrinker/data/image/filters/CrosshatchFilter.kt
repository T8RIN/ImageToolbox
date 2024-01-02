package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class CrosshatchFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.01f to 0.003f,
) : GPUFilterTransformation(context), Filter.Crosshatch<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageCrosshatchFilter(value.first, value.second)
}