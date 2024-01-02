package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiNonMaximumSuppressionFilter : UiFilter<Unit>(
    title = R.string.non_maximum_suppression,
    value = Unit
), Filter.NonMaximumSuppression<Bitmap>