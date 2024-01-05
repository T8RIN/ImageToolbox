package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiColorFilter(
    override val value: Color = Color.Transparent,
) : UiFilter<Color>(
    title = R.string.color_filter,
    value = Color.Transparent
), Filter.Color<Bitmap, Color>