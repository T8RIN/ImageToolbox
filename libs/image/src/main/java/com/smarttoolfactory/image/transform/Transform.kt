package com.smarttoolfactory.image.transform

import androidx.compose.runtime.Immutable

@Immutable
data class Transform(
    val translationX: Float = 0f,
    val translationY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f
)