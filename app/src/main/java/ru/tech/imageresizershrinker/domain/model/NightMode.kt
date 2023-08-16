package ru.tech.imageresizershrinker.domain.model

sealed class NightMode(val ordinal: Int) {
    data object Light : NightMode(0)
    data object Dark : NightMode(1)
    data object System : NightMode(2)

    companion object {
        fun fromOrdinal(int: Int?): NightMode = when (int) {
            0 -> Light
            1 -> Dark
            else -> System
        }
    }
}