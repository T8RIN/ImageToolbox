package ru.tech.imageresizershrinker.resize_screen.components

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.Locale

@Stable
@Parcelize
data class BitmapInfo(
    val width: String = "",
    val height: String = "",
    val quality: Float = 100f,
    val mimeTypeInt: Int = 0,
    val resizeType: Int = 0,
    val rotationDegrees: Float = 0f,
    val isFlipped: Boolean = false,
    val sizeInBytes: Int = 0
) : Parcelable

val Int.extension: String get() = if (this == 0) "jpg" else if (this == 1) "webp" else if (this == 2) "jpeg" else "png"

val String.compressFormat: Bitmap.CompressFormat get() = if (this == "jpg" || this == "jpeg") Bitmap.CompressFormat.JPEG else if (this == "webp") Bitmap.CompressFormat.WEBP else Bitmap.CompressFormat.PNG

val Bitmap.CompressFormat.extension: String get() = if (this == Bitmap.CompressFormat.JPEG) "jpg" else if (this == Bitmap.CompressFormat.WEBP) "webp" else "png"

val String.mimeTypeInt: Int get() = if ("jpg" in this) 0 else if ("webp" in this) 1 else if ("jpeg" in this) 2 else 3

fun byteCount(bytes: Long): String {
    var tempBytes = bytes
    if (-1000 < tempBytes && tempBytes < 1000) {
        return "$tempBytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (tempBytes <= -999950 || tempBytes >= 999950) {
        tempBytes /= 1000
        ci.next()
    }
    return java.lang.String.format(
        Locale.getDefault(),
        "%.1f %cB",
        tempBytes / 1000.0,
        ci.current()
    )
}