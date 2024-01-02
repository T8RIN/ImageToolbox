package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiNegativeFilter : UiFilter<Unit>(
    title = R.string.negative,
    value = Unit
), Filter.Negative<Bitmap>