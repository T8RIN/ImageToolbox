package ru.tech.imageresizershrinker.core.filters.presentation.model

import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import ru.tech.imageresizershrinker.core.filters.domain.model.ToneCurvesParams
import ru.tech.imageresizershrinker.core.resources.R

class UiToneCurvesFilter(
    override val value: ToneCurvesParams = ToneCurvesParams.Default
) : UiFilter<ToneCurvesParams>(
    title = R.string.tone_curves,
    paramsInfo = listOf(
        FilterParam(R.string.values, 0f..0f)
    ),
    value = value
), Filter.ToneCurves