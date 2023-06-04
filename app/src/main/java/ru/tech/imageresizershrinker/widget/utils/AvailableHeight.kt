package ru.tech.imageresizershrinker.widget.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun availableHeight(full: Boolean): Dp {
    return animateDpAsState(
        (LocalConfiguration.current.screenHeightDp.dp - 80.dp - 64.dp - WindowInsets
            .systemBars
            .asPaddingValues()
            .run { calculateBottomPadding() + calculateTopPadding() }) * (if (full) 1f else 0.5f)
    ).value
}