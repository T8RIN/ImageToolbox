package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiRGBFilter(
    override val value: Color = Color.White,
) : UiFilter<Color>(
    title = R.string.rgb_filter,
    value = value,
), Filter.RGB<Bitmap, Color>