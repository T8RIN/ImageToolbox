package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiSketchFilter(
    override val value: Unit = Unit,
) : UiFilter<Unit>(
    title = R.string.sketch,
    value = value,
    valueRange = 0f..0f
), Filter.Sketch<Bitmap>