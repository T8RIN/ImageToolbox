package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageOverlay: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.ImageOverlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(3.5f, 1.5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(11f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            curveToRelative(3.67f, 0f, 7.33f, 0f, 11f, 0f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            curveToRelative(0f, -3.67f, 0f, -7.33f, 0f, -11f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineTo(3.5f)
            moveTo(3.5f, 3.5f)
            horizontalLineToRelative(11f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            verticalLineTo(3.5f)
            moveTo(18.5f, 7.5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(11f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            verticalLineToRelative(-11f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineTo(18.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.633f, 9.242f)
            lineToRelative(-2.292f, 2.95f)
            lineToRelative(-1.633f, -1.967f)
            lineToRelative(-2.292f, 2.942f)
            lineToRelative(9.167f, 0f)
            close()
        }
    }.build()
}
