package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import coil.transform.Transformation
import com.commit451.coiltransformations.gpu.GPUFilterTransformation

sealed class FilterTransformation<T>(
    private val context: Context,
    @StringRes val title: Int,
    val valueRange: ClosedFloatingPointRange<Float>,
    open val value: T,
) : GPUFilterTransformation(context), Transformation {
    fun <T : Any> copy(value: T): FilterTransformation<*> {
        return when (this) {
            is BrightnessFilter -> BrightnessFilter(context, value as Float)
            is ContrastFilter -> ContrastFilter(context, value as Float)
            is HueFilter -> HueFilter(context, value as Float)
            is SaturationFilter -> SaturationFilter(context, value as Float)
            is ColorFilter -> ColorFilter(context, value as Color)
        }
    }
}