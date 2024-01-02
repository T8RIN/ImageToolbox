package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiLaplacianFilter : UiFilter<Unit>(
    title = R.string.laplacian,
    value = Unit,
    valueRange = 0f..0f
), Filter.Laplacian<Bitmap>