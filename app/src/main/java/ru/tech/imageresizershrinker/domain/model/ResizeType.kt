package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.utils.Domain

sealed class ResizeType : Domain {
    object Explicit : ResizeType()
    object Flexible : ResizeType()
    object Ratio : ResizeType()

    companion object {
        val entries
            get() = listOf(
                Explicit,
                Flexible,
                Ratio
            )
    }
}