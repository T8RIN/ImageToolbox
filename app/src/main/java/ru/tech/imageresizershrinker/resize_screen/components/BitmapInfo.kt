package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.runtime.Stable
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*

@Stable
data class BitmapInfo(
    val width: String = "",
    val height: String = "",
    val quality: Float = 100f,
    val mime: Int = 0,
    val resizeType: Int = 0,
    val rotation: Float = 0f,
    val isFlipped: Boolean = false,
    val size: Int = 0
)

fun byteCount(bytes: Int): String {
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