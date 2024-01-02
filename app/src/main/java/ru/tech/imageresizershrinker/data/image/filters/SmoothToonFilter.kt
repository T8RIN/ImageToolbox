package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SmoothToonFilter(
    private val context: Context,
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.2f, 10f)
) : GPUFilterTransformation(context), Filter.SmoothToon<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSmoothToonFilter().apply {
        setBlurSize(value.first)
        setThreshold(value.second)
        setQuantizationLevels(value.third)
    }
}