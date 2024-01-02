package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageZoomBlurFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class ZoomBlurFilter(
    private val context: Context,
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.5f, 5f),
) : GPUFilterTransformation(context), Filter.ZoomBlur<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()


    override fun createFilter(): GPUImageFilter =
        GPUImageZoomBlurFilter(PointF(value.first, value.second), value.third)
}