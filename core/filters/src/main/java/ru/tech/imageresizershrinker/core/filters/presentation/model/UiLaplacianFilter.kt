package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiLaplacianFilter : UiFilter<Unit>(
    title = R.string.laplacian,
    value = Unit,
    valueRange = 0f..0f
), Filter.Laplacian<Bitmap>