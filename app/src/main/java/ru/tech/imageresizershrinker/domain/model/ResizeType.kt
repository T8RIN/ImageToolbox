package ru.tech.imageresizershrinker.domain.model

sealed class ResizeType {
    object Explicit: ResizeType()
    object Flexible: ResizeType()
    object Ratio: ResizeType()
}