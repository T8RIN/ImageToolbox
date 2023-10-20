package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class VignetteFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.3f to 0.75f,
) : GPUFilterTransformation(context), Filter.Vignette<Bitmap> {

    //TODO: Add Color param
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageVignetteFilter(
        PointF(0.5f, 0.5f),
        floatArrayOf(0.0f, 0.0f, 0.0f),
        value.first,
        value.second
    )
}