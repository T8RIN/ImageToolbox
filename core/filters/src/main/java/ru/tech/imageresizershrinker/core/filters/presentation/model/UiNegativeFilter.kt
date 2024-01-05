package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiNegativeFilter : UiFilter<Unit>(
    title = R.string.negative,
    value = Unit
), Filter.Negative<Bitmap>