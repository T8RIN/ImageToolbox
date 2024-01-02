package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter
import ru.tech.imageresizershrinker.coredomain.image.filters.FilterParam

class UiRemoveColorFilter(
    override val value: Pair<Float, Color> = 0f to Color(0xFF000000),
) : UiFilter<Pair<Float, Color>>(
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
), Filter.RemoveColor<Bitmap, Color>