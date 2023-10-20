package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageKuwaharaFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class KuwaharaFilter(
    private val context: Context,
    override val value: Float = 3f,
) : GPUFilterTransformation(context), Filter.Kuwahara<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageKuwaharaFilter(value.toInt())
}