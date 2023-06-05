package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCGAColorspaceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class GCAColorSpaceFilter(
    private val context: @RawValue Context,
    override val value: Unit = Unit,
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.gca_colorspace,
    value = value
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageCGAColorspaceFilter()
}