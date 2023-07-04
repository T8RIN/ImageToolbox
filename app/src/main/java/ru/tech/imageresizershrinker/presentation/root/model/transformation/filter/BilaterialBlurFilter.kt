package ru.tech.imageresizershrinker.presentation.root.model.transformation.filter

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBilateralBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class BilaterialBlurFilter(
    private val context: @RawValue Context,
    override val value: Float = -8f,
) : FilterTransformation<Float>(
    context = context,
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageBilateralBlurFilter(-value)
}