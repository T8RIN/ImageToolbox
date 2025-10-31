package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.MobileCast: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MobileCast",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 920f)
            lineTo(200f, 840f)
            quadTo(233f, 840f, 256.5f, 863.5f)
            quadTo(280f, 887f, 280f, 920f)
            lineTo(200f, 920f)
            close()
            moveTo(360f, 920f)
            quadTo(360f, 854f, 313f, 807f)
            quadTo(266f, 760f, 200f, 760f)
            lineTo(200f, 680f)
            quadTo(300f, 680f, 370f, 750f)
            quadTo(440f, 820f, 440f, 920f)
            lineTo(360f, 920f)
            close()
            moveTo(520f, 920f)
            quadTo(520f, 786f, 427f, 693f)
            quadTo(334f, 600f, 200f, 600f)
            lineTo(200f, 520f)
            quadTo(283f, 520f, 356f, 551.5f)
            quadTo(429f, 583f, 483f, 637f)
            quadTo(537f, 691f, 568.5f, 764f)
            quadTo(600f, 837f, 600f, 920f)
            lineTo(520f, 920f)
            close()
            moveTo(480f, 240f)
            quadTo(497f, 240f, 508.5f, 228.5f)
            quadTo(520f, 217f, 520f, 200f)
            quadTo(520f, 183f, 508.5f, 171.5f)
            quadTo(497f, 160f, 480f, 160f)
            quadTo(463f, 160f, 451.5f, 171.5f)
            quadTo(440f, 183f, 440f, 200f)
            quadTo(440f, 217f, 451.5f, 228.5f)
            quadTo(463f, 240f, 480f, 240f)
            close()
            moveTo(680f, 920f)
            quadTo(680f, 820f, 642f, 732.5f)
            quadTo(604f, 645f, 538.5f, 580f)
            quadTo(473f, 515f, 386f, 477.5f)
            quadTo(299f, 440f, 200f, 440f)
            lineTo(200f, 120f)
            quadTo(200f, 87f, 223.5f, 63.5f)
            quadTo(247f, 40f, 280f, 40f)
            lineTo(680f, 40f)
            quadTo(713f, 40f, 736.5f, 63.5f)
            quadTo(760f, 87f, 760f, 120f)
            lineTo(760f, 244f)
            quadTo(778f, 251f, 789f, 266f)
            quadTo(800f, 281f, 800f, 300f)
            lineTo(800f, 380f)
            quadTo(800f, 399f, 789f, 414f)
            quadTo(778f, 429f, 760f, 436f)
            lineTo(760f, 840f)
            quadTo(760f, 873f, 736.5f, 896.5f)
            quadTo(713f, 920f, 680f, 920f)
            close()
        }
    }.build()
}
