package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiColorFilter(
    override val value: Color = Color.Transparent,
) : UiFilter<Color>(
    title = R.string.color_filter,
    value = Color.Transparent
), Filter.Color<Bitmap, Color>