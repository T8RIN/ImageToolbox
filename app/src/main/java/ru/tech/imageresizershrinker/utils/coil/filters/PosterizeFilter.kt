package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
data class PosterizeFilter(
    private val context: @RawValue Context,
    override val value: Int = 10,
) : FilterTransformation<Int>(
    context = context,
    title = R.string.posterize,
    value = value,
    valueRange = 1f..64f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImagePosterizeFilter(value)
}