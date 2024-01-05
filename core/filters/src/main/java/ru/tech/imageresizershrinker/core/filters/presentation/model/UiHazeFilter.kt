package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiHazeFilter(
    override val value: Pair<Float, Float> = 0.2f to 0f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.haze,
    value = value,
    paramsInfo = listOf(
        R.string.distance paramTo -0.3f..0.3f,
        R.string.slope paramTo -0.3f..0.3f
    )
), Filter.Haze<Bitmap>