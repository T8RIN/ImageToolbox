package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import coil.size.Size
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam

class RemoveColorFilter(
    private val context: Context,
    override val value: Pair<Float, Color> = 0f to Color(0xFF000000),
) : FilterTransformation<Pair<Float, Color>>(
    context = context,
    title = R.string.remove_color,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.tolerance,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.color_to_remove,
            valueRange = 0f..0f
        )
    )
), Filter.RemoveColor<Bitmap, Color> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = throw NotImplementedError()

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = ReplaceColorFilter(
        context = context,
        value = Triple(
            first = value.first,
            second = value.second,
            third = Color.Transparent
        )
    ).transform(input, size)
}