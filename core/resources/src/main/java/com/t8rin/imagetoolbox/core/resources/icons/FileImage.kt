package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FileImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FileImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 9f)
            horizontalLineTo(18.5f)
            lineTo(13f, 3.5f)
            verticalLineTo(9f)
            moveTo(6f, 2f)
            horizontalLineTo(14f)
            lineTo(20f, 8f)
            verticalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 22f)
            horizontalLineTo(6f)
            curveTo(4.89f, 22f, 4f, 21.1f, 4f, 20f)
            verticalLineTo(4f)
            curveTo(4f, 2.89f, 4.89f, 2f, 6f, 2f)
            moveTo(6f, 20f)
            horizontalLineTo(15f)
            lineTo(18f, 20f)
            verticalLineTo(12f)
            lineTo(14f, 16f)
            lineTo(12f, 14f)
            lineTo(6f, 20f)
            moveTo(8f, 9f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 11f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 13f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10f, 11f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 9f)
            close()
        }
    }.build()
}

val Icons.Outlined.FileImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FileImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14f, 2f)
            lineTo(20f, 8f)
            verticalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 22f)
            horizontalLineTo(6f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 20f)
            verticalLineTo(4f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 2f)
            horizontalLineTo(14f)
            moveTo(18f, 20f)
            verticalLineTo(9f)
            horizontalLineTo(13f)
            verticalLineTo(4f)
            horizontalLineTo(6f)
            verticalLineTo(20f)
            horizontalLineTo(18f)
            moveTo(17f, 13f)
            verticalLineTo(19f)
            horizontalLineTo(7f)
            lineTo(12f, 14f)
            lineTo(14f, 16f)
            moveTo(10f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.5f, 12f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.5f, 9f)
            arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 10.5f)
            close()
        }
    }.build()
}