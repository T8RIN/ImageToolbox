package ru.tech.imageresizershrinker.main_screen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.theme.suggestContainerColorBy

fun Modifier.block(
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = Color.Unspecified,
) = composed {
    val color1 = if (color.isUnspecified) {
        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    } else color
    background(
        color = color1,
        shape = shape
    )
        .border(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant(0.1f, color1),
            shape
        )
        .padding(4.dp)
}

fun Modifier.navBarsLandscapePadding(enabled: Boolean = true) = composed {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE && enabled) Modifier.navigationBarsPadding()
    else Modifier
}

fun Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(enabled: Boolean = true) = composed {
    if (WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() == 0.dp && enabled
    ) Modifier.navigationBarsPadding()
    else Modifier
}

fun Modifier.navBarsPaddingOnlyIfTheyAtTheBottom(enabled: Boolean = true) = composed {
    if (WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() != 0.dp && enabled
    ) Modifier.navigationBarsPadding()
    else Modifier
}

fun Modifier.drawHorizontalStroke(top: Boolean = false, height: Dp = 1.dp) = composed {
    val color = MaterialTheme.colorScheme.outlineVariant(0.3f)

    val heightPx = with(LocalDensity.current) { height.toPx() }

    drawWithContent {
        drawContent()
        drawRect(
            color,
            topLeft = if (top) Offset(0f, 0f) else Offset(0f, this.size.height),
            size = Size(this.size.width, heightPx)
        )
    }.zIndex(100f)
}

fun Modifier.fabBorder(height: Dp = 1.dp) = composed {
    border(
        height,
        MaterialTheme.colorScheme.outlineVariant(
            luminance = 0.3f,
            onTopOf = MaterialTheme.colorScheme.suggestContainerColorBy(LocalContentColor.current)
        ),
        FloatingActionButtonDefaults.shape
    )
}