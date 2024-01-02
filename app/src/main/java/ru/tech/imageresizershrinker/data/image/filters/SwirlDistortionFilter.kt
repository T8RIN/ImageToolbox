package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SwirlDistortionFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.5f to 1f,
) : GPUFilterTransformation(context), Filter.SwirlDistortion<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSwirlFilter(
        /* radius = */ value.first,
        /* angle = */value.second,
        /* center = */PointF(0.5f, 0.5f)
    )
}