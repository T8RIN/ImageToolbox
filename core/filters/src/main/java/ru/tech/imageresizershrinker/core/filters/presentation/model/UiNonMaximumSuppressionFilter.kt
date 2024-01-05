package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiNonMaximumSuppressionFilter : UiFilter<Unit>(
    title = R.string.non_maximum_suppression,
    value = Unit
), Filter.NonMaximumSuppression<Bitmap>