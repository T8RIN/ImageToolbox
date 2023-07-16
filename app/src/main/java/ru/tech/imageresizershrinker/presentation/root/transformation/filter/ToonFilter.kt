package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

@Parcelize
class ToonFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0.2f to 10f,
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.toon,
    value = value,
    paramsInfo = listOf(
        R.string.threshold paramTo 0f..5f,
        R.string.quantizationLevels paramTo 0f..100f
    )
), Filter.Toon<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageToonFilter(
            /* threshold = */ value.first,
            /* quantizationLevels = */value.second
        )
}