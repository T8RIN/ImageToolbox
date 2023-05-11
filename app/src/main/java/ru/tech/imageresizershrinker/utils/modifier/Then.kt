package ru.tech.imageresizershrinker.utils.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun Modifier.thenUse(crossinline block: @Composable () -> Modifier?): Modifier = composed {
    block()?.let { this.then(it) } ?: this
}