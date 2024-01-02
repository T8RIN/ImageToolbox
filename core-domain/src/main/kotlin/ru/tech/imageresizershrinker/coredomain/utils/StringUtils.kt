package ru.tech.imageresizershrinker.coredomain.utils

fun String.trimTrailingZero(): String {
    val value = this
    return if (value.isNotEmpty()) {
        if (value.indexOf(".") < 0) {
            value
        } else {
            value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        }
    } else {
        value
    }
}