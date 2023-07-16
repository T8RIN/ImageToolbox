package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBilateralBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

@Parcelize
class BilaterialBlurFilter(
    private val context: @RawValue Context,
    override val value: Float = -8f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
), Filter.BilaterialBlur<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBilateralBlurFilter(-value)
}