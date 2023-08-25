package com.smarttoolfactory.colorpicker.util

// Regex for checking if this string is a 6 char hex or 8 char hex
val hexWithAlphaRegex = "^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})\$".toRegex()
val hexRegex = "^#?([0-9a-fA-F]{6})\$".toRegex()

// Check only on char if it's in range of 0-9, a-f, A-F
val hexRegexSingleChar = "[a-fA-F0-9]".toRegex()