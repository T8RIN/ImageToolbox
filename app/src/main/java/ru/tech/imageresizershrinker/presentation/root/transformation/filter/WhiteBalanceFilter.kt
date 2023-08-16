package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


class WhiteBalanceFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 5000.0f to 0.0f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.white_balance,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.temperature, 1000f..10000f, 0),
        FilterParam(R.string.tint, -100f..100f, 2)
    )
), Filter.WhiteBalance<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageWhiteBalanceFilter(value.first, value.second)
}