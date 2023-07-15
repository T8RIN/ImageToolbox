package ru.tech.imageresizershrinker.domain.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.domain.Domain

data class ImageInfo(
    val width: Int = 0,
    val height: Int = 0,
    val quality: Float = 100f,
    val imageFormat: ImageFormat = ImageFormat.Default(),
    val resizeType: ResizeType = ResizeType.Explicit,
    val rotationDegrees: Float = 0f,
    val isFlipped: Boolean = false,
    val sizeInBytes: Int = 0
) : Domain {
    fun haveChanges(original: Bitmap?): Boolean {
        if (original == null) return false
        return quality != 100f || rotationDegrees != 0f || isFlipped || width != original.width || height != original.height
    }
}