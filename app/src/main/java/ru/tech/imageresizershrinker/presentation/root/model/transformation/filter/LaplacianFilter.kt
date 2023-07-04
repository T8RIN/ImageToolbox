package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLaplacianFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class LaplacianFilter(
    private val context: @RawValue Context,
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.laplacian,
    value = Unit,
    valueRange = 0f..0f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageLaplacianFilter()
}