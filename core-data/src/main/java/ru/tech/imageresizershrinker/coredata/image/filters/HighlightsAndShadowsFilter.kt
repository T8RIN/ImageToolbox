package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class HighlightsAndShadowsFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0f to 1f
) : GPUFilterTransformation(context), Filter.HighlightsAndShadows<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageHighlightShadowFilter(value.first, value.second)
}