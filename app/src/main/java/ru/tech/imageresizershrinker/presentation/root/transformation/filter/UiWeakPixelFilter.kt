package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiWeakPixelFilter : UiFilter<Unit>(
    title = R.string.weak_pixel_inclusion,
    value = Unit
), Filter.WeakPixel<Bitmap>