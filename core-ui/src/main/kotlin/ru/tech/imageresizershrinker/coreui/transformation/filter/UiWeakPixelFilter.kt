package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiWeakPixelFilter : UiFilter<Unit>(
    title = R.string.weak_pixel_inclusion,
    value = Unit
), Filter.WeakPixel<Bitmap>