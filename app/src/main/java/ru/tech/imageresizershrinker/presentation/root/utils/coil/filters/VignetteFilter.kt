package ru.tech.imageresizershrinker.presentation.root.utils.coil.filters

import android.content.Context
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class VignetteFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.3f to 0.75f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.vignette,
    value = value,
    paramsInfo = listOf(
        R.string.start paramTo -2f..2f,
        R.string.end paramTo -2f..2f
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageVignetteFilter(
        PointF(0.5f, 0.5f),
        floatArrayOf(0.0f, 0.0f, 0.0f),
        value.first,
        value.second
    )
}