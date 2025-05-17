package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components.model

enum class GradientMakerType {
    Default,
    Overlay,
    Mesh,
    MeshOverlay
}

fun GradientMakerType?.isMesh() =
    this == GradientMakerType.Mesh || this == GradientMakerType.MeshOverlay

fun GradientMakerType?.canPickImage() =
    this == GradientMakerType.Overlay || this == GradientMakerType.MeshOverlay