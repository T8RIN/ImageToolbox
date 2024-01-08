package ru.tech.imageresizershrinker.core.domain.model

sealed class CopyToClipboardMode(
    open val value: Int
) {

    data object Disabled : CopyToClipboardMode(0)

    sealed class Enabled(
        override val value: Int
    ) : CopyToClipboardMode(value) {
        data object WithoutSaving : Enabled(1)
        data object WithSaving : Enabled(2)
    }

    companion object {
        fun fromInt(
            value: Int
        ): CopyToClipboardMode = when (value) {
            1 -> Enabled.WithSaving
            2 -> Enabled.WithSaving
            else -> Disabled
        }
    }

}