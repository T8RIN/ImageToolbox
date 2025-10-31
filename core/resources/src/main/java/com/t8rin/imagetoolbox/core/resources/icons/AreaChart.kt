package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.AreaChart: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.AreaChart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(840f, 640f)
            lineTo(464f, 346f)
            lineTo(305f, 565f)
            lineTo(120f, 420f)
            lineTo(120f, 280f)
            lineTo(280f, 400f)
            lineTo(480f, 120f)
            lineTo(680f, 280f)
            lineTo(840f, 280f)
            lineTo(840f, 640f)
            close()
            moveTo(120f, 800f)
            lineTo(120f, 520f)
            lineTo(320f, 680f)
            lineTo(480f, 460f)
            lineTo(840f, 741f)
            lineTo(840f, 800f)
            lineTo(120f, 800f)
            close()
        }
    }.build()
}
