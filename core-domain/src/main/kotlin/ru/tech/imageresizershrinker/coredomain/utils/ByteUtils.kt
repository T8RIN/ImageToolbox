package ru.tech.imageresizershrinker.coredomain.utils

import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.Locale


fun readableByteCount(bytes: Long): String {
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