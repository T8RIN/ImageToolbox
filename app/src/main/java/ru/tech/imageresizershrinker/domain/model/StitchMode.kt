package ru.tech.imageresizershrinker.domain.model

sealed class StitchMode {
    data object Horizontal : StitchMode()
    data object Vertical : StitchMode()
    sealed class Grid : StitchMode() {
        data class Horizontal(val rows: Int = 2) : Grid()
        data class Vertical(val columns: Int = 2) : Grid()
    }

    fun gridCellsCount(): Int {
        return when (this) {
            is Grid.Horizontal -> this.rows
            is Grid.Vertical -> this.columns
            else -> 0
        }
    }

    fun isHorizontal(): Boolean = this is Horizontal || this is Grid.Horizontal
}