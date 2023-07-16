package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageZoomBlurFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


@Parcelize
class ZoomBlurFilter(
    private val context: @RawValue Context,
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.5f, 5f),
) : FilterTransformation<Triple<Float, Float, Float>>(
    context = context,
    title = R.string.zoom_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.blur_center_x, 0f..1f, 2),
        FilterParam(R.string.blur_center_y, 0f..1f, 2),
        FilterParam(R.string.blur_size, 0f..10f, 2)
    )
), Filter.ZoomBlur<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()


    override fun createFilter(): GPUImageFilter =
        GPUImageZoomBlurFilter(PointF(value.first, value.second), value.third)
}