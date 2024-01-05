package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSketchFilter(
    override val value: Unit = Unit,
) : UiFilter<Unit>(
    title = R.string.sketch,
    value = value,
    valueRange = 0f..0f
), Filter.Sketch<Bitmap>