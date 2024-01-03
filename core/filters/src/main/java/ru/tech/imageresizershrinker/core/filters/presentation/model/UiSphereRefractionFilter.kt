package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSphereRefractionFilter(
    override val value: Pair<Float, Float> = 0.25f to 0.71f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.sphere_refraction,
    value = value,
    paramsInfo = listOf(
        R.string.radius paramTo 0f..1f,
        R.string.refractive_index paramTo 0f..1f
    )
), Filter.SphereRefraction<Bitmap>