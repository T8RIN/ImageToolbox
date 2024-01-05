package ru.tech.imageresizershrinker.core.domain.image.filters


data class FilterParam(
    val title: Int? = null,
    val valueRange: ClosedFloatingPointRange<Float>,
    val roundTo: Int = 2
)