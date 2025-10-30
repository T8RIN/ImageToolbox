package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.SyncArrowDown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SyncArrowDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(80f, 780f)
            lineTo(80f, 700f)
            lineTo(137f, 700f)
            quadTo(90f, 658f, 65f, 600.5f)
            quadTo(40f, 543f, 40f, 480f)
            quadTo(40f, 373f, 107f, 290.5f)
            quadTo(174f, 208f, 280f, 186f)
            lineTo(280f, 268f)
            quadTo(208f, 288f, 164f, 346.5f)
            quadTo(120f, 405f, 120f, 479f)
            quadTo(120f, 529f, 141f, 572.5f)
            quadTo(162f, 616f, 200f, 648f)
            lineTo(200f, 580f)
            lineTo(280f, 580f)
            lineTo(280f, 780f)
            lineTo(80f, 780f)
            close()
            moveTo(400f, 774f)
            lineTo(400f, 692f)
            quadTo(472f, 672f, 516f, 613.5f)
            quadTo(560f, 555f, 560f, 481f)
            quadTo(560f, 431f, 539f, 387.5f)
            quadTo(518f, 344f, 480f, 312f)
            lineTo(480f, 380f)
            lineTo(400f, 380f)
            lineTo(400f, 180f)
            lineTo(600f, 180f)
            lineTo(600f, 260f)
            lineTo(543f, 260f)
            quadTo(590f, 302f, 615f, 359.5f)
            quadTo(640f, 417f, 640f, 480f)
            quadTo(640f, 587f, 573f, 669.5f)
            quadTo(506f, 752f, 400f, 774f)
            close()
            moveTo(780f, 800f)
            lineTo(640f, 660f)
            lineTo(697f, 604f)
            lineTo(740f, 647f)
            lineTo(740f, 160f)
            lineTo(820f, 160f)
            lineTo(820f, 648f)
            lineTo(864f, 604f)
            lineTo(920f, 660f)
            lineTo(780f, 800f)
            close()
        }
    }.build()
}
