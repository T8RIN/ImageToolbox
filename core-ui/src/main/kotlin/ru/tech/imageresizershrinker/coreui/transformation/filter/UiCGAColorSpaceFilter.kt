package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiCGAColorSpaceFilter : UiFilter<Unit>(
    title = R.string.cga_colorspace,
    value = Unit
), Filter.CGAColorSpace<Bitmap>