package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.SaveConfirm: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.SaveConfirm",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14f, 12.8f)
            curveTo(13.5f, 12.31f, 12.78f, 12f, 12f, 12f)
            curveTo(10.34f, 12f, 9f, 13.34f, 9f, 15f)
            curveTo(9f, 16.31f, 9.84f, 17.41f, 11f, 17.82f)
            curveTo(11.07f, 15.67f, 12.27f, 13.8f, 14f, 12.8f)
            moveTo(11.09f, 19f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineTo(16.17f)
            lineTo(19f, 7.83f)
            verticalLineTo(12.35f)
            curveTo(19.75f, 12.61f, 20.42f, 13f, 21f, 13.54f)
            verticalLineTo(7f)
            lineTo(17f, 3f)
            horizontalLineTo(5f)
            curveTo(3.89f, 3f, 3f, 3.9f, 3f, 5f)
            verticalLineTo(19f)
            curveTo(3f, 20.1f, 3.89f, 21f, 5f, 21f)
            horizontalLineTo(11.81f)
            curveTo(11.46f, 20.39f, 11.21f, 19.72f, 11.09f, 19f)
            moveTo(6f, 10f)
            horizontalLineTo(15f)
            verticalLineTo(6f)
            horizontalLineTo(6f)
            verticalLineTo(10f)
            moveTo(15.75f, 21f)
            lineTo(13f, 18f)
            lineTo(14.16f, 16.84f)
            lineTo(15.75f, 18.43f)
            lineTo(19.34f, 14.84f)
            lineTo(20.5f, 16.25f)
            lineTo(15.75f, 21f)
        }
    }.build()
}
