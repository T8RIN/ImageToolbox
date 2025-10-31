package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.TextFields: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TextFields",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 800f)
            lineTo(280f, 280f)
            lineTo(80f, 280f)
            lineTo(80f, 160f)
            lineTo(600f, 160f)
            lineTo(600f, 280f)
            lineTo(400f, 280f)
            lineTo(400f, 800f)
            lineTo(280f, 800f)
            close()
            moveTo(640f, 800f)
            lineTo(640f, 480f)
            lineTo(520f, 480f)
            lineTo(520f, 360f)
            lineTo(880f, 360f)
            lineTo(880f, 480f)
            lineTo(760f, 480f)
            lineTo(760f, 800f)
            lineTo(640f, 800f)
            close()
        }
    }.build()
}
