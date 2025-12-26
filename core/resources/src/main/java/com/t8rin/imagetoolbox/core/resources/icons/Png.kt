package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Png: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Png",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(260f, 460f)
            verticalLineToRelative(-40f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(-40f)
            close()
            moveTo(660f, 600f)
            horizontalLineToRelative(40f)
            quadToRelative(25f, 0f, 42.5f, -17.5f)
            reflectiveQuadTo(760f, 540f)
            verticalLineToRelative(-60f)
            horizontalLineToRelative(-60f)
            verticalLineToRelative(60f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(100f)
            quadToRelative(0f, -25f, -17.5f, -42.5f)
            reflectiveQuadTo(700f, 360f)
            horizontalLineToRelative(-40f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(600f, 420f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 25f, 17.5f, 42.5f)
            reflectiveQuadTo(660f, 600f)
            close()
            moveTo(200f, 600f)
            horizontalLineToRelative(60f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(60f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(360f, 480f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(320f, 360f)
            lineTo(200f, 360f)
            verticalLineToRelative(240f)
            close()
            moveTo(400f, 600f)
            horizontalLineToRelative(60f)
            verticalLineToRelative(-96f)
            lineToRelative(40f, 96f)
            horizontalLineToRelative(60f)
            verticalLineToRelative(-240f)
            horizontalLineToRelative(-60f)
            verticalLineToRelative(94f)
            lineToRelative(-40f, -94f)
            horizontalLineToRelative(-60f)
            verticalLineToRelative(240f)
            close()
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 720f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-480f)
            lineTo(160f, 240f)
            verticalLineToRelative(480f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
        }
    }.build()
}
