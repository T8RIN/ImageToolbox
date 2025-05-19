package ru.tech.imageresizershrinker.core.filters.presentation.model

import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.filters.domain.model.MirrorSide
import ru.tech.imageresizershrinker.core.resources.R

class UiMirrorFilter(
    override val value: Pair<Float, MirrorSide> = 0.5f to MirrorSide.LeftToRight,
) : UiFilter<Pair<Float, MirrorSide>>(
    title = R.string.tile_mode_mirror,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.center,
            valueRange = 0f..1f
        ),
        FilterParam(
            title = R.string.side,
            valueRange = 0f..0f
        )
    )
), Filter.Mirror