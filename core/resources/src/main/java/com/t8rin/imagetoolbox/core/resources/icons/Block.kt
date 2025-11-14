package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.TwoTone.Block: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Block",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 2f)
            lineTo(12f, 2f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 12f)
            lineTo(22f, 12f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 22f)
            lineTo(12f, 22f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 12f)
            lineTo(2f, 12f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 2f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 22f)
            curveToRelative(-1.383f, 0f, -2.683f, -0.262f, -3.9f, -0.788f)
            reflectiveCurveToRelative(-2.275f, -1.237f, -3.175f, -2.138f)
            reflectiveCurveToRelative(-1.612f, -1.958f, -2.138f, -3.175f)
            reflectiveCurveToRelative(-0.788f, -2.517f, -0.788f, -3.9f)
            curveToRelative(0f, -1.383f, 0.262f, -2.683f, 0.788f, -3.9f)
            reflectiveCurveToRelative(1.237f, -2.275f, 2.138f, -3.175f)
            reflectiveCurveToRelative(1.958f, -1.612f, 3.175f, -2.138f)
            reflectiveCurveToRelative(2.517f, -0.788f, 3.9f, -0.788f)
            curveToRelative(1.383f, 0f, 2.683f, 0.262f, 3.9f, 0.788f)
            reflectiveCurveToRelative(2.275f, 1.237f, 3.175f, 2.138f)
            reflectiveCurveToRelative(1.612f, 1.958f, 2.138f, 3.175f)
            reflectiveCurveToRelative(0.788f, 2.517f, 0.788f, 3.9f)
            curveToRelative(0f, 1.383f, -0.262f, 2.683f, -0.788f, 3.9f)
            reflectiveCurveToRelative(-1.237f, 2.275f, -2.138f, 3.175f)
            reflectiveCurveToRelative(-1.958f, 1.612f, -3.175f, 2.138f)
            reflectiveCurveToRelative(-2.517f, 0.788f, -3.9f, 0.788f)
            close()
            moveTo(12f, 20f)
            curveToRelative(0.9f, 0f, 1.767f, -0.146f, 2.6f, -0.438f)
            reflectiveCurveToRelative(1.6f, -0.712f, 2.3f, -1.263f)
            lineTo(5.7f, 7.1f)
            curveToRelative(-0.55f, 0.7f, -0.971f, 1.467f, -1.263f, 2.3f)
            reflectiveCurveToRelative(-0.438f, 1.7f, -0.438f, 2.6f)
            curveToRelative(0f, 2.233f, 0.775f, 4.125f, 2.325f, 5.675f)
            reflectiveCurveToRelative(3.442f, 2.325f, 5.675f, 2.325f)
            close()
            moveTo(18.3f, 16.9f)
            curveToRelative(0.55f, -0.7f, 0.971f, -1.467f, 1.263f, -2.3f)
            reflectiveCurveToRelative(0.438f, -1.7f, 0.438f, -2.6f)
            curveToRelative(0f, -2.233f, -0.775f, -4.125f, -2.325f, -5.675f)
            reflectiveCurveToRelative(-3.442f, -2.325f, -5.675f, -2.325f)
            curveToRelative(-0.9f, 0f, -1.767f, 0.146f, -2.6f, 0.438f)
            reflectiveCurveToRelative(-1.6f, 0.712f, -2.3f, 1.263f)
            lineToRelative(11.2f, 11.2f)
            close()
        }
    }.build()
}
