package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FileImport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Rounded.FileImport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6f, 2f)
            curveTo(4.89f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 22f)
            horizontalLineTo(18f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            moveTo(13f, 3.5f)
            lineTo(18.5f, 9f)
            horizontalLineTo(13f)
            moveTo(10.05f, 11.22f)
            lineTo(12.88f, 14.05f)
            lineTo(15f, 11.93f)
            verticalLineTo(19f)
            horizontalLineTo(7.93f)
            lineTo(10.05f, 16.88f)
            lineTo(7.22f, 14.05f)
        }
    }.build()
}

val Icons.Outlined.FileImport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.FileImport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14f, 2f)
            horizontalLineTo(6f)
            curveTo(4.89f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            curveTo(4f, 21.11f, 4.89f, 22f, 6f, 22f)
            horizontalLineTo(18f)
            curveTo(19.11f, 22f, 20f, 21.11f, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            moveTo(18f, 20f)
            horizontalLineTo(6f)
            verticalLineTo(4f)
            horizontalLineTo(13f)
            verticalLineTo(9f)
            horizontalLineTo(18f)
            verticalLineTo(20f)
            moveTo(15f, 11.93f)
            verticalLineTo(19f)
            horizontalLineTo(7.93f)
            lineTo(10.05f, 16.88f)
            lineTo(7.22f, 14.05f)
            lineTo(10.05f, 11.22f)
            lineTo(12.88f, 14.05f)
            lineTo(15f, 11.93f)
            close()
        }
    }.build()
}
