package ru.tech.imageresizershrinker.main_screen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

fun Modifier.block(shape: Shape = RoundedCornerShape(16.dp)) = composed {
    background(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = shape
    ).padding(4.dp)
}

fun Modifier.navBarsLandscapePadding(enabled: Boolean = true) = composed {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE && enabled) Modifier.navigationBarsPadding()
    else Modifier
}