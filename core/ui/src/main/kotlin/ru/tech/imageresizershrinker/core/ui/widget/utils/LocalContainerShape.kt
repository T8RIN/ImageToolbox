package ru.tech.imageresizershrinker.core.ui.widget.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape

val LocalContainerShape = compositionLocalOf<Shape?> { null }

@Composable
fun ProvideContainerShape(
    shape: Shape?,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    value = LocalContainerShape provides shape,
    content = content
)