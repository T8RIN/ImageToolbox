package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiCGAColorSpaceFilter : UiFilter<Unit>(
    title = R.string.cga_colorspace,
    value = Unit
), Filter.CGAColorSpace<Bitmap>