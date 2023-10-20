package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiCGAColorSpaceFilter : UiFilter<Unit>(
    title = R.string.cga_colorspace,
    value = Unit
), Filter.CGAColorSpace<Bitmap>