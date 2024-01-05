package ru.tech.imageresizershrinker.core.data.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class PosterizeFilter(
    private val context: Context,
    override val value: Float = 10f,
) : GPUFilterTransformation(context), Filter.Posterize<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImagePosterizeFilter(value.toInt())
}