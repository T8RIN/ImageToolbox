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

val Icons.Rounded.Line: ImageVector by lazy {
    Builder(
        name = "Line", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(212.0f, 748.0f)
            quadToRelative(-11.0f, -11.0f, -11.0f, -28.0f)
            reflectiveQuadToRelative(11.0f, -28.0f)
            lineToRelative(480.0f, -480.0f)
            quadToRelative(11.0f, -12.0f, 27.5f, -12.0f)
            reflectiveQuadToRelative(28.5f, 12.0f)
            quadToRelative(11.0f, 11.0f, 11.0f, 28.0f)
            reflectiveQuadToRelative(-11.0f, 28.0f)
            lineTo(268.0f, 748.0f)
            quadToRelative(-11.0f, 11.0f, -28.0f, 11.0f)
            reflectiveQuadToRelative(-28.0f, -11.0f)
            close()
        }
    }
        .build()
}