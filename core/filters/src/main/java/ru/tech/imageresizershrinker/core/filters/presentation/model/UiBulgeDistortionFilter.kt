package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiBulgeDistortionFilter(
    override val value: Pair<Float, Float> = 0.25f to 0.5f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.bulge,
    value = value,
    paramsInfo = listOf(
        R.string.radius paramTo 0f..1f,
        R.string.scale paramTo -1f..1f
    )
), Filter.BulgeDistortion<Bitmap>