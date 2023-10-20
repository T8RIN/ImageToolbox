package ru.tech.imageresizershrinker.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class ToonFilter(
    private val context: Context,
    override val value: Pair<Float, Float> = 0.2f to 10f,
) : GPUFilterTransformation(context), Filter.Toon<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageToonFilter(
            /* threshold = */ value.first,
            /* quantizationLevels = */value.second
        )
}