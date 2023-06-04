package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
data class SwirlDistortionEffect(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.5f to 1f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.swirl,
    value = value,
    valueRange = 0f..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSwirlFilter(
        /* radius = */ value.first,
        /* angle = */value.second,
        /* center = */PointF(0.5f, 0.5f)
    )
}