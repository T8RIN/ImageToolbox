package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiNonMaximumSuppressionFilter : UiFilter<Unit>(
    title = R.string.non_maximum_suppression,
    value = Unit
), Filter.NonMaximumSuppression<Bitmap>