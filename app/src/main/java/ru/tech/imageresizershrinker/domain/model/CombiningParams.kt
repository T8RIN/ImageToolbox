package ru.tech.imageresizershrinker.domain.model

data class CombiningParams(
    val isHorizontal: Boolean = true,
    val spacing: Int = 0,
    val scaleSmallImagesToLarge: Boolean = true,
    val backgroundColor: Int = 0x00000000,
    val fadingEdgesMode: Int? = 0
)
