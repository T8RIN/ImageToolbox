package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiBlackAndWhiteFilter : UiFilter<Unit>(
    title = R.string.black_and_white,
    value = Unit
), Filter.BlackAndWhite<Bitmap>