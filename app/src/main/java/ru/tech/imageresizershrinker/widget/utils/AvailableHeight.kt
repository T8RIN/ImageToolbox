package ru.tech.imageresizershrinker.widget.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun availableHeight(expanded: Boolean, collapsed: Boolean = false): Dp {
    var mid by remember { mutableFloatStateOf(0.5f) }
    val sizeClass = LocalWindowSizeClass.current
    if (sizeClass.heightSizeClass == WindowHeightSizeClass.Expanded) mid = 0.4f
    return animateDpAsState(
        (LocalConfiguration.current.screenHeightDp.dp - 80.dp - 64.dp - WindowInsets
            .systemBars
            .asPaddingValues()
            .run { calculateBottomPadding() + calculateTopPadding() }) * (if (expanded) 1f else if (collapsed) 0.2f else mid)
    ).value
}