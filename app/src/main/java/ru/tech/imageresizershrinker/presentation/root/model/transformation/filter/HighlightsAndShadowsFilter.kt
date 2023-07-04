package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class HighlightsAndShadowsFilter(
    private val context: @RawValue Context,
    override val value: Pair<Float, Float> = 0f to 1f
) : FilterTransformation<Pair<Float, Float>>(
    context = context,
    title = R.string.highlights_shadows,
    value = value,
    paramsInfo = listOf(
        R.string.highlights paramTo 0f..1f,
        R.string.shadows paramTo 0f..1f
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter =
        GPUImageHighlightShadowFilter(value.first, value.second)
}