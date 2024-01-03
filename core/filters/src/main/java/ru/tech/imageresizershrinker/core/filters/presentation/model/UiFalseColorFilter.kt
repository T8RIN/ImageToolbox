package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiFalseColorFilter(
    override val value: Pair<Color, Color> = Color(
        red = 0f,
        green = 0f,
        blue = 0.5f
    ) to Color(
        red = 1f,
        green = 0f,
        blue = 0f
    ),
) : UiFilter<Pair<Color, Color>>(
    title = R.string.false_color,
    value = value,
), Filter.FalseColor<Bitmap, Color>