package ru.tech.imageresizershrinker.core.ui.icons.material

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

val Icons.Outlined.ImageEdit: ImageVector by lazy {
    Builder(
        name = "Image Edit", defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.7f, 14.3f)
            lineTo(21.7f, 15.3f)
            lineTo(19.7f, 13.3f)
            lineTo(20.7f, 12.3f)
            curveTo(20.8f, 12.2f, 20.9f, 12.1f, 21.1f, 12.1f)
            curveTo(21.2f, 12.1f, 21.4f, 12.2f, 21.5f, 12.3f)
            lineTo(22.8f, 13.6f)
            curveTo(22.9f, 13.8f, 22.9f, 14.1f, 22.7f, 14.3f)
            moveTo(13.0f, 19.9f)
            verticalLineTo(22.0f)
            horizontalLineTo(15.1f)
            lineTo(21.2f, 15.9f)
            lineTo(19.2f, 13.9f)
            lineTo(13.0f, 19.9f)
            moveTo(11.21f, 15.83f)
            lineTo(9.25f, 13.47f)
            lineTo(6.5f, 17.0f)
            horizontalLineTo(13.12f)
            lineTo(15.66f, 14.55f)
            lineTo(13.96f, 12.29f)
            lineTo(11.21f, 15.83f)
            moveTo(11.0f, 19.9f)
            verticalLineTo(19.05f)
            lineTo(11.05f, 19.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(11.31f)
            lineTo(21.0f, 9.38f)
            verticalLineTo(5.0f)
            curveTo(21.0f, 3.9f, 20.11f, 3.0f, 19.0f, 3.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
            verticalLineTo(19.0f)
            curveTo(3.0f, 20.11f, 3.9f, 21.0f, 5.0f, 21.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(19.9f)
            close()
        }
    }
        .build()
}