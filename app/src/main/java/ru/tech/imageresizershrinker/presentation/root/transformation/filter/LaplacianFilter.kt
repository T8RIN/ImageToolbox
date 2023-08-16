package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLaplacianFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class LaplacianFilter(
    private val context: Context,
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.laplacian,
    value = Unit,
    valueRange = 0f..0f
), Filter.Laplacian<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLaplacianFilter()
}