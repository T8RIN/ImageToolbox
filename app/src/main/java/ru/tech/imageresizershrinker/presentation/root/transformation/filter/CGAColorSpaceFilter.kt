package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCGAColorspaceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class CGAColorSpaceFilter(
    private val context: @RawValue Context,
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.cga_colorspace,
    value = Unit
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageCGAColorspaceFilter()
}