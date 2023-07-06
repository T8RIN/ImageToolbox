package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.tech.imageresizershrinker.R

@Parcelize
class FalseColorFilter(
    private val context: @RawValue Context,
    override val value: @RawValue Pair<Color, Color> = Color(0f, 0f, 0.5f) to Color(1f, 0f, 0f),
) : FilterTransformation<Pair<Color, Color>>(
    context = context,
    title = R.string.false_color,
    value = value,
) {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageFalseColorFilter(
        value.first.red,
        value.first.green,
        value.first.blue,
        value.second.red,
        value.second.green,
        value.second.blue
    )
}