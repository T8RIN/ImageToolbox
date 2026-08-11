package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.ViewQuilt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ViewQuilt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 200f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 760f)
            lineTo(200f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 680f)
            close()
            moveTo(413f, 440f)
            horizontalLineToRelative(347f)
            verticalLineToRelative(-160f)
            lineTo(413f, 280f)
            verticalLineToRelative(160f)
            close()
            moveTo(627f, 680f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-160f)
            lineTo(627f, 520f)
            verticalLineToRelative(160f)
            close()
            moveTo(413f, 680f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-160f)
            lineTo(413f, 520f)
            verticalLineToRelative(160f)
            close()
            moveTo(200f, 680f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-400f)
            lineTo(200f, 280f)
            verticalLineToRelative(400f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ViewQuilt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ViewQuilt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 5.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(7f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(8.325f, 17f)
            horizontalLineToRelative(-3.325f)
            verticalLineTo(7f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(10f)
            close()
            moveTo(13.675f, 17f)
            horizontalLineToRelative(-3.35f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(3.35f)
            verticalLineToRelative(4f)
            close()
            moveTo(19f, 17f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(4f)
            close()
            moveTo(19f, 11f)
            horizontalLineToRelative(-8.675f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(8.675f)
            verticalLineToRelative(4f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(10.325f, 7f)
            horizontalLineToRelative(8.675f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-8.675f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(15.675f, 13f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-3.325f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(10.325f, 13f)
            horizontalLineToRelative(3.35f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-3.35f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 7f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(-3.325f)
            close()
        }
    }.build()
}
