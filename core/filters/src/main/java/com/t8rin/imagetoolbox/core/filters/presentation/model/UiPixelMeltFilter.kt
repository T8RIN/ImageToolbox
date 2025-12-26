package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.resources.R

class UiPixelMeltFilter(
    override val value: Pair<Float, Float> = 20f to 0.5f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.pixel_melt,
    value = value,
    paramsInfo = listOf(
        FilterParam(
            title = R.string.max_drop,
            valueRange = 0f..250f,
            roundTo = 0
        ),
        FilterParam(
            title = R.string.strength,
            valueRange = 0f..1f,
            roundTo = 3
        ),
    )
), Filter.PixelMelt