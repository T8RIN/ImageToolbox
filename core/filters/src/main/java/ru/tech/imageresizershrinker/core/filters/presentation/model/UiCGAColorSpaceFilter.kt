package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiCGAColorSpaceFilter : UiFilter<Unit>(
    title = R.string.cga_colorspace,
    value = Unit
), Filter.CGAColorSpace<Bitmap>