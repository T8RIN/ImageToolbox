package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBlackAndWhiteFilter : UiFilter<Unit>(
    title = R.string.black_and_white,
    value = Unit
), Filter.BlackAndWhite<Bitmap>