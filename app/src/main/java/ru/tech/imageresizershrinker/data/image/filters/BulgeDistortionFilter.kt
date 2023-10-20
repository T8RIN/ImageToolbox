package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class BulgeDistortionFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.25f to 0.5f,
) : GPUFilterTransformation(context), Filter.BulgeDistortion<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBulgeDistortionFilter(
        /* radius = */ value.first,
        /* scale = */value.second,
        /* center = */PointF(0.5f, 0.5f)
    )
}