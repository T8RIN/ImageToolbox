package ru.tech.imageresizershrinker.domain.model

data class CombiningParams(
    val isHorizontal: Boolean,
    val spacing: Int,
    val scaleSmallImagesToLarge: Boolean,
    val backgroundColor: Int
)
