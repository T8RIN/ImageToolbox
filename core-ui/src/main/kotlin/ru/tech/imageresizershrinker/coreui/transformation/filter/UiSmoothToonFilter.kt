package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSmoothToonFilter(
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.2f, 10f)
) : UiFilter<Triple<Float, Float, Float>>(
    title = R.string.snooth_toon,
    value = value,
    paramsInfo = listOf(
        R.string.blur_size paramTo 0f..100f,
        R.string.threshold paramTo 0f..5f,
        R.string.quantizationLevels paramTo 0f..100f
    )
), Filter.SmoothToon<Bitmap>