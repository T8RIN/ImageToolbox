package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiSketchFilter(
    override val value: Unit = Unit,
) : UiFilter<Unit>(
    title = R.string.sketch,
    value = value,
    valueRange = 0f..0f
), Filter.Sketch<Bitmap>