package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
data class BulgeDistortionEffect(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.25f to 0.5f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.bulge,
    value = value,
    valueRange = -1f..1f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBulgeDistortionFilter(
        /* radius = */ value.first,
        /* scale = */value.second,
        /* center = */PointF(0.5f, 0.5f)
    )
}