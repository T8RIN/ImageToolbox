package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Latitude: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Latitude",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            curveTo(6.5f, 2f, 2f, 6.5f, 2f, 12f)
            reflectiveCurveTo(6.5f, 22f, 12f, 22f)
            reflectiveCurveTo(22f, 17.5f, 22f, 12f)
            reflectiveCurveTo(17.5f, 2f, 12f, 2f)
            moveTo(12f, 4f)
            curveTo(15f, 4f, 17.5f, 5.6f, 18.9f, 8f)
            horizontalLineTo(5.1f)
            curveTo(6.5f, 5.6f, 9f, 4f, 12f, 4f)
            moveTo(12f, 20f)
            curveTo(9f, 20f, 6.5f, 18.4f, 5.1f, 16f)
            horizontalLineTo(18.9f)
            curveTo(17.5f, 18.4f, 15f, 20f, 12f, 20f)
            moveTo(4.3f, 14f)
            curveTo(4.1f, 13.4f, 4f, 12.7f, 4f, 12f)
            reflectiveCurveTo(4.1f, 10.6f, 4.3f, 10f)
            horizontalLineTo(19.8f)
            curveTo(20f, 10.6f, 20.1f, 11.3f, 20.1f, 12f)
            reflectiveCurveTo(20f, 13.4f, 19.8f, 14f)
            horizontalLineTo(4.3f)
            close()
        }
    }.build()
}
