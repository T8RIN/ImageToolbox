package ru.tech.imageresizershrinker.core.domain.model

sealed class SwitchType(val ordinal: Int) {

    data object MaterialYou : SwitchType(0)
    data object Compose : SwitchType(1)
    data object Pixel : SwitchType(2)
    data object Fluent : SwitchType(3)

    companion object {
        fun fromInt(ordinal: Int) = when (ordinal) {
            1 -> Compose
            2 -> Pixel
            3 -> Fluent
            else -> MaterialYou
        }

        val entries by lazy {
            listOf(
                MaterialYou, Compose, Pixel, Fluent
            )
        }
    }

}