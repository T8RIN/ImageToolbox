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

val Icons.Filled.LetterO: ImageVector by lazy {
    Builder(
        name = "Letter O", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth =
        24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.0f, 7.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 9.0f, 9.0f)
            verticalLineTo(15.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 11.0f, 17.0f)
            horizontalLineTo(13.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 15.0f, 15.0f)
            verticalLineTo(9.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 13.0f, 7.0f)
            horizontalLineTo(11.0f)
            moveTo(11.0f, 9.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(9.0f)
            close()
        }
    }.build()
}
