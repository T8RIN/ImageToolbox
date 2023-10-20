package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiHighlightsAndShadowsFilter(
    override val value: Pair<Float, Float> = 0f to 1f
) : UiFilter<Pair<Float, Float>>(
    title = R.string.highlights_shadows,
    value = value,
    paramsInfo = listOf(
        R.string.highlights paramTo 0f..1f,
        R.string.shadows paramTo 0f..1f
    )
), Filter.HighlightsAndShadows<Bitmap>