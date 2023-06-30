package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class CrosshatchFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.01f to 0.003f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.crosshatch,
    value = value,
    paramsInfo = listOf(
        FilterParam(title = R.string.spacing, valueRange = 0.001f..0.05f, roundTo = 4),
        FilterParam(title = R.string.line_width, valueRange = 0.001f..0.02f, roundTo = 4)
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageCrosshatchFilter(value.first, value.second)
}