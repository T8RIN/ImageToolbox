package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiNegativeFilter : UiFilter<Unit>(
    title = R.string.negative,
    value = Unit
), Filter.Negative<Bitmap>