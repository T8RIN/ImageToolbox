package com.smarttoolfactory.cropper.model

import com.smarttoolfactory.cropper.util.createRectShape

/**
 * Aspect ratio list with pre-defined aspect ratios
 */
val aspectRatios = listOf(
    CropAspectRatio(
        title = "9:16",
        shape = createRectShape(AspectRatio(9 / 16f)),
        aspectRatio = AspectRatio(9 / 16f)
    ),
    CropAspectRatio(
        title = "2:3",
        shape = createRectShape(AspectRatio(2 / 3f)),
        aspectRatio = AspectRatio(2 / 3f)
    ),
    CropAspectRatio(
        title = "Original",
        shape = createRectShape(AspectRatio.Original),
        aspectRatio = AspectRatio.Original
    ),
    CropAspectRatio(
        title = "1:1",
        shape = createRectShape(AspectRatio(1 / 1f)),
        aspectRatio = AspectRatio(1 / 1f)
    ),
    CropAspectRatio(
        title = "16:9",
        shape = createRectShape(AspectRatio(16 / 9f)),
        aspectRatio = AspectRatio(16 / 9f)
    ),
    CropAspectRatio(
        title = "1.91:1",
        shape = createRectShape(AspectRatio(1.91f / 1f)),
        aspectRatio = AspectRatio(1.91f / 1f)
    ),
    CropAspectRatio(
        title = "3:2",
        shape = createRectShape(AspectRatio(3 / 2f)),
        aspectRatio = AspectRatio(3 / 2f)
    ),
    CropAspectRatio(
        title = "3:4",
        shape = createRectShape(AspectRatio(3 / 4f)),
        aspectRatio = AspectRatio(3 / 4f)
    ),
    CropAspectRatio(
        title = "3:5",
        shape = createRectShape(AspectRatio(3 / 5f)),
        aspectRatio = AspectRatio(3 / 5f)
    )
)