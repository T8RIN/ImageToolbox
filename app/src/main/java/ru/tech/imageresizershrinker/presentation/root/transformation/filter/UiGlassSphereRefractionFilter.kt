package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiGlassSphereRefractionFilter(
    override val value: Pair<Float, Float> = 0.25f to 0.71f,
) : UiFilter<Pair<Float, Float>>(
    title = R.string.glass_sphere_refraction,
    value = value,
    paramsInfo = listOf(
        R.string.radius paramTo 0f..1f,
        R.string.refractive_index paramTo 0f..1f
    )
), Filter.GlassSphereRefraction<Bitmap>