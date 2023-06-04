package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
data class SmoothToonFilter(
    private val context: @RawValue Context,
    override val value: Unit = Unit
) : FilterTransformation<Unit>(
    context = context,
    title = R.string.snooth_toon,
    value = value,
    valueRange = 0f..0f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSmoothToonFilter()
}