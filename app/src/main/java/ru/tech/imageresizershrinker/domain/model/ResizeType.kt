package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ResizeType : Domain {
    object Explicit : ResizeType()
    object Flexible : ResizeType()
    object Ratio : ResizeType()

    sealed class Limits : ResizeType() {
        object Skip : Limits()
        object Copy : Limits()
        object Force : Limits()
    }

    companion object {
        val entries
            get() = listOf(
                Explicit,
                Flexible,
                Ratio
            )
        val limitsEntries
            get() = listOf(
                Limits.Skip,
                Limits.Copy,
                Limits.Force
            )
    }
}