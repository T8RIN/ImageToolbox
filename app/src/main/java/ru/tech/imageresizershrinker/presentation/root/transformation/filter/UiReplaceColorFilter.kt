package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


class UiReplaceColorFilter(
    override val value: Triple<Float, Color, Color> = Triple(
        first = 0f,
        second = Color(red = 0.0f, green = 0.0f, blue = 0.0f, alpha = 1.0f),
        third = Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 1.0f)
    ),
) : UiFilter<Triple<Float, Color, Color>>(
    title = R.string.replace_color,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.tolerance,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.color_to_replace,
            valueRange = 0f..0f
        ),
        FilterParam(
            title = R.string.target_color,
            valueRange = 0f..0f
        )
    )
), Filter.ReplaceColor<Bitmap, Color>