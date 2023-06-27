package ru.tech.imageresizershrinker.presentation.utils.coil.filters

import android.content.Context
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class RGBFilter(
    private val context: @RawValue Context,
    override val value: @RawValue Color = Color.White,
) : FilterTransformation<Color>(
    context = context,
    title = R.string.rgb_filter,
    value = value,
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageRGBFilter(
        value.red, value.green, value.blue
    )
}