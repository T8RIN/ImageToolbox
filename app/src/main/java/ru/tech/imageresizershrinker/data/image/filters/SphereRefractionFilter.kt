package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SphereRefractionFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.25f to 0.71f,
) : GPUFilterTransformation(context), Filter.SphereRefraction<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSphereRefractionFilter(
        PointF(0.5f, 0.5f),
        value.first, value.second
    )
}