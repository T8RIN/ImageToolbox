package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R


@Parcelize
class SobelEdgeDetectionFilter(
    private val context: @RawValue Context,
    override val value: Float = 1f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.sobel_edge,
    value = value,
    paramsInfo = listOf(
        FilterParam(title = R.string.line_width, valueRange = 1f..25f, roundTo = 0)
    )
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSobelEdgeDetectionFilter().apply {
        setLineSize(value)
    }
}