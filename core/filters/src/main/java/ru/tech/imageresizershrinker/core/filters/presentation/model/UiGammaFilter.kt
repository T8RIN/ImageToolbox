package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiGammaFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.gamma,
    value = value,
    valueRange = 0f..3f
), Filter.Gamma<Bitmap>