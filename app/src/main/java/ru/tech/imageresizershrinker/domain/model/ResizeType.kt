package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ResizeType : Domain {
    data object Explicit : ResizeType()
    data object Flexible : ResizeType()

    data class CenterCrop(
        val canvasColor: Int = 0
    ) : ResizeType()

    data object Ratio : ResizeType()

    sealed class Limits : ResizeType() {
        data object Skip : Limits()
        data object Copy : Limits()
        data object Force : Limits()
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