package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageKuwaharaFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


class KuwaharaFilter(
    private val context: Context,
    override val value: Float = 3f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.kuwahara,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 0f..10f, 0)
    )
), Filter.Kuwahara<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageKuwaharaFilter(value.toInt())
}