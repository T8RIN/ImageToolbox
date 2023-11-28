package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.Domain

sealed class ResizeType : Domain {
    data object Explicit : ResizeType()
    data object Flexible : ResizeType()

    data class CenterCrop(
        val canvasColor: Int? = 0,
        val blurRadius: Int = 35
    ) : ResizeType()

    sealed class Limits(val autoRotateLimitBox: Boolean) : ResizeType() {

        fun copy(autoRotateLimitBox: Boolean) = when (this) {
            is Recode -> Recode(autoRotateLimitBox)
            is Skip -> Skip(autoRotateLimitBox)
            is Zoom -> Zoom(autoRotateLimitBox)
        }

        class Skip(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)

        class Recode(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)

        class Zoom(
            autoRotateLimitBox: Boolean = false
        ) : Limits(autoRotateLimitBox)
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
                Limits.Skip(),
                Limits.Recode(),
                Limits.Zoom()
            )
        }
    }
}