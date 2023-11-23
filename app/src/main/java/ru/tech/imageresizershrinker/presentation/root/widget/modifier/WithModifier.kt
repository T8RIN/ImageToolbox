package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun (@Composable () -> Unit).withModifier(
    modifier: Modifier,
    contentAlignment: Alignment = Alignment.Center
) = Box(
    modifier = modifier,
    contentAlignment = contentAlignment
) { this@withModifier() }