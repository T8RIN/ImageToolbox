package ru.tech.imageresizershrinker.core.ui.widget.utils


inline fun <reified T> T?.notNullAnd(
    predicate: (T) -> Boolean
): Boolean = if (this != null) predicate(this)
else false