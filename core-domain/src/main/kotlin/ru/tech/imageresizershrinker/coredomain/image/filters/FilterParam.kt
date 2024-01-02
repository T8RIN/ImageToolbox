package ru.tech.imageresizershrinker.coredomain.image.filters


data class FilterParam(
    val title: Int? = null,
    val valueRange: ClosedFloatingPointRange<Float>,
    val roundTo: Int = 2
)