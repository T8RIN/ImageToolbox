package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

@Parcelize
class SmoothToonFilter(
    private val context: @RawValue Context,
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.2f, 10f)
) : FilterTransformation<Triple<Float, Float, Float>>(
    context = context,
    title = R.string.snooth_toon,
    value = value,
    paramsInfo = listOf(
        R.string.blur_size paramTo 0f..100f,
        R.string.threshold paramTo 0f..5f,
        R.string.quantizationLevels paramTo 0f..100f
    )
), Filter.SmoothToon<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSmoothToonFilter().apply {
        setBlurSize(value.first)
        setThreshold(value.second)
        setQuantizationLevels(value.third)
    }
}