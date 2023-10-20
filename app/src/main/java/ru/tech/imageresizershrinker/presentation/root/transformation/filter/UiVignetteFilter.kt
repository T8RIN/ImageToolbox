package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiVignetteFilter(
    override val value: Pair<Float, Float> = 0.3f to 0.75f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.vignette,
    value = value,
    paramsInfo = listOf(
        R.string.start paramTo -2f..2f,
        R.string.end paramTo -2f..2f
    )
), Filter.Vignette<Bitmap>