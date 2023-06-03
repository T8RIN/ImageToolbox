package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import androidx.annotation.StringRes
import coil.transform.Transformation
import com.commit451.coiltransformations.gpu.GPUFilterTransformation

sealed class FilterTransformation(
    private val context: Context,
    @StringRes val title: Int,
    val valueRange: ClosedFloatingPointRange<Float>,
    open val value: Float,
) : GPUFilterTransformation(context), Transformation {
    fun copy(value: Float): FilterTransformation {
        return when (this) {
            is BrightnessFilter -> BrightnessFilter(context, value)
            is ContrastFilter -> ContrastFilter(context, value)
            is HueFilter -> HueFilter(context, value)
            is SaturationFilter -> SaturationFilter(context, value)
            is ColorFilter -> ColorFilter(context, value)
        }
    }
}