package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiRGBFilter(
    override val value: Color = Color.White,
) : UiFilter<Color>(
    title = R.string.rgb_filter,
    value = value,
), Filter.RGB<Bitmap, Color>