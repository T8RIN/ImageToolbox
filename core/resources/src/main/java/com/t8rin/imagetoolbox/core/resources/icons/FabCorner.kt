package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.FabCorner: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FabCorner",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(2f, 20f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(18f)
            verticalLineTo(4f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(2f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 8f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(-8f)
            close()
        }
    }.build()
}
