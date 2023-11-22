package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ResizeType : Domain {
    data object Explicit : ResizeType()
    data object Flexible : ResizeType()

    data class CenterCrop(
        val canvasColor: Int? = 0,
        val blurRadius: Int = 35
    ) : ResizeType()

    sealed class Limits : ResizeType() {
        data object Skip : Limits()
        data object Recode : Limits()
        data object Zoom : Limits()
    }

    companion object {
        val entries by lazy {
            listOf(
                Explicit,
                Flexible,
                CenterCrop()
            )
        }
        val limitsEntries by lazy {
            listOf(
                Limits.Skip,
                Limits.Recode,
                Limits.Zoom
            )
        }
    }
}