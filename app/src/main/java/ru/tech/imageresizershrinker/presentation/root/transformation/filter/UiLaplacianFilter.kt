package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiLaplacianFilter : UiFilter<Unit>(
    title = R.string.laplacian,
    value = Unit,
    valueRange = 0f..0f
), Filter.Laplacian<Bitmap>