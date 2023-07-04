package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWeakPixelInclusionFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class WeakPixelFilter(
    private val context: @RawValue Context,
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.weak_pixel_inclusion,
    value = Unit
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageWeakPixelInclusionFilter()
}