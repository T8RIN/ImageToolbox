package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSwirlDistortionFilter(
    override val value: Pair<Float, Float> = 0.5f to 1f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.swirl,
    value = value,
    paramsInfo = listOf(
        R.string.radius paramTo 0f..1f,
        R.string.angle paramTo -1f..1f
    )
), Filter.SwirlDistortion<Bitmap>