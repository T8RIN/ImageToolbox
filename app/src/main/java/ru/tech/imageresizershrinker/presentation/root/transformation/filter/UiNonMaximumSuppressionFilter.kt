package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiNonMaximumSuppressionFilter : UiFilter<Unit>(
    title = R.string.non_maximum_suppression,
    value = Unit
), Filter.NonMaximumSuppression<Bitmap>