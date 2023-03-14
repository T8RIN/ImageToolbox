package ru.tech.imageresizershrinker.utils

inline operator fun <T> T.invoke(apply: T.() -> Unit) = this.apply { apply(this) }
