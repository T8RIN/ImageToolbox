package ru.tech.imageresizershrinker.presentation.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class PosterizeFilter(
    private val context: @RawValue Context,
    override val value: Float = 10f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.posterize,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 1f..256f, 0)
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImagePosterizeFilter(value.toInt())
}