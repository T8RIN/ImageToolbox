package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
data class VignetteFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.3f to 0.75f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.vignette,
    value = value,
    valueRange = -4f..4f
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