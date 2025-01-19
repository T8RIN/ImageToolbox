package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.StickerEmoji: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.StickerEmoji",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.5f, 2f)
            curveTo(3.56f, 2f, 2f, 3.56f, 2f, 5.5f)
            verticalLineTo(18.5f)
            curveTo(2f, 20.44f, 3.56f, 22f, 5.5f, 22f)
            horizontalLineTo(16f)
            lineTo(22f, 16f)
            verticalLineTo(5.5f)
            curveTo(22f, 3.56f, 20.44f, 2f, 18.5f, 2f)
            horizontalLineTo(5.5f)
            moveTo(5.75f, 4f)
            horizontalLineTo(18.25f)
            arcTo(1.75f, 1.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 5.75f)
            verticalLineTo(15f)
            horizontalLineTo(18.5f)
            curveTo(16.56f, 15f, 15f, 16.56f, 15f, 18.5f)
            verticalLineTo(20f)
            horizontalLineTo(5.75f)
            arcTo(1.75f, 1.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 18.25f)
            verticalLineTo(5.75f)
            arcTo(1.75f, 1.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.75f, 4f)
            moveTo(14.44f, 6.77f)
            curveTo(14.28f, 6.77f, 14.12f, 6.79f, 13.97f, 6.83f)
            curveTo(13.03f, 7.09f, 12.5f, 8.05f, 12.74f, 9f)
            curveTo(12.79f, 9.15f, 12.86f, 9.3f, 12.95f, 9.44f)
            lineTo(16.18f, 8.56f)
            curveTo(16.18f, 8.39f, 16.16f, 8.22f, 16.12f, 8.05f)
            curveTo(15.91f, 7.3f, 15.22f, 6.77f, 14.44f, 6.77f)
            moveTo(8.17f, 8.5f)
            curveTo(8f, 8.5f, 7.85f, 8.5f, 7.7f, 8.55f)
            curveTo(6.77f, 8.81f, 6.22f, 9.77f, 6.47f, 10.7f)
            curveTo(6.5f, 10.86f, 6.59f, 11f, 6.68f, 11.16f)
            lineTo(9.91f, 10.28f)
            curveTo(9.91f, 10.11f, 9.89f, 9.94f, 9.85f, 9.78f)
            curveTo(9.64f, 9f, 8.95f, 8.5f, 8.17f, 8.5f)
            moveTo(16.72f, 11.26f)
            lineTo(7.59f, 13.77f)
            curveTo(8.91f, 15.3f, 11f, 15.94f, 12.95f, 15.41f)
            curveTo(14.9f, 14.87f, 16.36f, 13.25f, 16.72f, 11.26f)
            close()
        }
    }.build()
}
