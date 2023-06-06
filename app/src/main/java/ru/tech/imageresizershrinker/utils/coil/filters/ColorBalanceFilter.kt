package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBalanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class ColorBalanceFilter(
    private val context: @RawValue Context,
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : FilterTransformation<FloatArray>(
    context = context,
    title = R.string.color_balance,
    value = value,
    valueRange = 3f..3f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageColorBalanceFilter().apply {
        setHighlights(value.take(3).toFloatArray())
        setMidtones(floatArrayOf(value[3], value[4], value[6]))
        setShowdows(value.takeLast(3).toFloatArray())
    }
}