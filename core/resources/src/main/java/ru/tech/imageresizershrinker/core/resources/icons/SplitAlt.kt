package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.SplitAlt: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.SplitAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(440f, 880f)
            lineTo(440f, 680f)
            quadTo(440f, 624f, 423f, 597f)
            quadTo(406f, 570f, 378f, 544f)
            lineTo(435f, 487f)
            quadTo(447f, 498f, 458f, 510.5f)
            quadTo(469f, 523f, 480f, 537f)
            quadTo(494f, 518f, 508.5f, 503.5f)
            quadTo(523f, 489f, 538f, 475f)
            quadTo(576f, 440f, 607f, 394f)
            quadTo(638f, 348f, 640f, 233f)
            lineTo(577f, 296f)
            lineTo(520f, 240f)
            lineTo(680f, 80f)
            lineTo(840f, 240f)
            lineTo(784f, 296f)
            lineTo(720f, 233f)
            quadTo(718f, 376f, 676f, 436.5f)
            quadTo(634f, 497f, 592f, 535f)
            quadTo(560f, 564f, 540f, 591.5f)
            quadTo(520f, 619f, 520f, 680f)
            lineTo(520f, 880f)
            lineTo(440f, 880f)
            close()
            moveTo(248f, 327f)
            quadTo(244f, 307f, 242.5f, 283f)
            quadTo(241f, 259f, 240f, 233f)
            lineTo(176f, 296f)
            lineTo(120f, 240f)
            lineTo(280f, 80f)
            lineTo(440f, 240f)
            lineTo(383f, 296f)
            lineTo(320f, 234f)
            quadTo(320f, 255f, 322f, 273.5f)
            quadTo(324f, 292f, 326f, 308f)
            lineTo(248f, 327f)
            close()
            moveTo(334f, 503f)
            quadTo(314f, 482f, 295.5f, 454f)
            quadTo(277f, 426f, 263f, 385f)
            lineTo(340f, 366f)
            quadTo(350f, 393f, 363f, 412f)
            quadTo(376f, 431f, 391f, 446f)
            lineTo(334f, 503f)
            close()
        }
    }.build()
}
