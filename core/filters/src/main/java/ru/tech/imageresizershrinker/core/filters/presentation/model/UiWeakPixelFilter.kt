package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiWeakPixelFilter : UiFilter<Unit>(
    title = R.string.weak_pixel_inclusion,
    value = Unit
), Filter.WeakPixel<Bitmap>