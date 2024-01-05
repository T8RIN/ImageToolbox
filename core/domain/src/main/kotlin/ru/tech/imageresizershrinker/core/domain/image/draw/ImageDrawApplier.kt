package ru.tech.imageresizershrinker.core.domain.image.draw

interface ImageDrawApplier<Image, Path, Color> {

    suspend fun applyDrawToImage(
        drawBehavior: DrawBehavior,
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Image?

    suspend fun applyEraseToImage(
        pathPaints: List<PathPaint<Path, Color>>,
        imageUri: String
    ): Image?

    suspend fun applyEraseToImage(
        pathPaints: List<PathPaint<Path, Color>>,
        image: Image?,
        shaderSourceUri: String
    ): Image?

}