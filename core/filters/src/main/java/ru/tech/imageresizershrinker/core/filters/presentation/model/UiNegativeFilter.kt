package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiNegativeFilter : UiFilter<Unit>(
    title = R.string.negative,
    value = Unit
), Filter.Negative<Bitmap>