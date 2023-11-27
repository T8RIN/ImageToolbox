package ru.tech.imageresizershrinker.presentation.root.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FloatingActionButton: ImageVector by lazy {
    Builder(
        name = "Floating Action Button", defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.3f, 3.0f)
            horizontalLineTo(7.7f)
            curveTo(5.1f, 3.0f, 3.0f, 5.1f, 3.0f, 7.7f)
            verticalLineToRelative(8.7f)
            curveTo(3.0f, 18.9f, 5.1f, 21.0f, 7.7f, 21.0f)
            horizontalLineToRelative(8.7f)
            curveToRelative(2.6f, 0.0f, 4.7f, -2.1f, 4.7f, -4.7f)
            verticalLineTo(7.7f)
            curveTo(21.0f, 5.1f, 18.9f, 3.0f, 16.3f, 3.0f)
            close()
            moveTo(11.6f, 14.1f)
            lineTo(10.7f, 16.0f)
            lineToRelative(-0.9f, -1.9f)
            lineTo(8.0f, 13.3f)
            lineToRelative(1.9f, -0.9f)
            lineToRelative(0.9f, -1.9f)
            lineToRelative(0.9f, 1.9f)
            lineToRelative(1.9f, 0.9f)
            lineTo(11.6f, 14.1f)
            close()
            moveTo(14.8f, 10.5f)
            lineToRelative(-0.6f, 1.4f)
            lineToRelative(-0.6f, -1.4f)
            lineToRelative(-1.4f, -0.6f)
            lineToRelative(1.4f, -0.6f)
            lineToRelative(0.6f, -1.4f)
            lineToRelative(0.6f, 1.4f)
            lineToRelative(1.4f, 0.6f)
            lineTo(14.8f, 10.5f)
            close()
        }
    }.build()
}