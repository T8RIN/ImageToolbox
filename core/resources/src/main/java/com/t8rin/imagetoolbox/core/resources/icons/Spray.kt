package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Spray: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Spray",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 4f)
            horizontalLineTo(12f)
            verticalLineTo(6f)
            horizontalLineTo(10f)
            verticalLineTo(4f)
            moveTo(7f, 3f)
            horizontalLineTo(9f)
            verticalLineTo(5f)
            horizontalLineTo(7f)
            verticalLineTo(3f)
            moveTo(7f, 6f)
            horizontalLineTo(9f)
            verticalLineTo(8f)
            horizontalLineTo(7f)
            verticalLineTo(6f)
            moveTo(6f, 8f)
            verticalLineTo(10f)
            horizontalLineTo(4f)
            verticalLineTo(8f)
            horizontalLineTo(6f)
            moveTo(6f, 5f)
            verticalLineTo(7f)
            horizontalLineTo(4f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            moveTo(6f, 2f)
            verticalLineTo(4f)
            horizontalLineTo(4f)
            verticalLineTo(2f)
            horizontalLineTo(6f)
            moveTo(13f, 22f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11f, 20f)
            verticalLineTo(10f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13f, 8f)
            verticalLineTo(7f)
            horizontalLineTo(14f)
            verticalLineTo(4f)
            horizontalLineTo(17f)
            verticalLineTo(7f)
            horizontalLineTo(18f)
            verticalLineTo(8f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 10f)
            verticalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 22f)
            horizontalLineTo(13f)
            moveTo(13f, 10f)
            verticalLineTo(20f)
            horizontalLineTo(18f)
            verticalLineTo(10f)
            horizontalLineTo(13f)
            close()
        }
    }.build()
}
