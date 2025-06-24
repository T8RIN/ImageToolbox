/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageWeight: ImageVector by lazy {
    Builder(
        name = "Image Weight", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(6.0f, 19.0f)
            horizontalLineToRelative(12.0f)
            lineTo(16.575f, 9.0f)
            horizontalLineTo(7.425f)
            lineTo(6.0f, 19.0f)
            close()
            moveTo(12.0f, 7.0f)
            curveToRelative(0.2833f, 0.0f, 0.5208f, -0.0958f, 0.7125f, -0.2875f)
            reflectiveCurveTo(13.0f, 6.2833f, 13.0f, 6.0f)
            reflectiveCurveToRelative(-0.0958f, -0.5208f, -0.2875f, -0.7125f)
            curveTo(12.5208f, 5.0958f, 12.2833f, 5.0f, 12.0f, 5.0f)
            reflectiveCurveToRelative(-0.5208f, 0.0958f, -0.7125f, 0.2875f)
            curveTo(11.0958f, 5.4792f, 11.0f, 5.7167f, 11.0f, 6.0f)
            reflectiveCurveToRelative(0.0958f, 0.5208f, 0.2875f, 0.7125f)
            reflectiveCurveTo(11.7167f, 7.0f, 12.0f, 7.0f)
            close()
            moveTo(14.825f, 7.0f)
            horizontalLineToRelative(1.75f)
            curveToRelative(0.5f, 0.0f, 0.9333f, 0.1667f, 1.3f, 0.5f)
            curveToRelative(0.3667f, 0.3333f, 0.5917f, 0.7417f, 0.675f, 1.225f)
            lineToRelative(1.425f, 10.0f)
            curveToRelative(0.0833f, 0.6f, -0.0708f, 1.1292f, -0.4625f, 1.5875f)
            curveTo(19.1208f, 20.7708f, 18.6167f, 21.0f, 18.0f, 21.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-0.6167f, 0.0f, -1.1208f, -0.2292f, -1.5125f, -0.6875f)
            reflectiveCurveToRelative(-0.5458f, -0.9875f, -0.4625f, -1.5875f)
            lineToRelative(1.425f, -10.0f)
            curveTo(5.5333f, 8.2417f, 5.7583f, 7.8333f, 6.125f, 7.5f)
            curveToRelative(0.3667f, -0.3333f, 0.8f, -0.5f, 1.3f, -0.5f)
            horizontalLineToRelative(1.75f)
            curveTo(9.125f, 6.8333f, 9.0833f, 6.6708f, 9.05f, 6.5125f)
            reflectiveCurveTo(9.0f, 6.1833f, 9.0f, 6.0f)
            curveToRelative(0.0f, -0.8333f, 0.2917f, -1.5417f, 0.875f, -2.125f)
            curveToRelative(0.5833f, -0.5833f, 1.2917f, -0.875f, 2.125f, -0.875f)
            reflectiveCurveToRelative(1.5417f, 0.2917f, 2.125f, 0.875f)
            reflectiveCurveTo(15.0f, 5.1667f, 15.0f, 6.0f)
            curveToRelative(0.0f, 0.1833f, -0.0167f, 0.3542f, -0.05f, 0.5125f)
            reflectiveCurveTo(14.875f, 6.8333f, 14.825f, 7.0f)
            close()
            moveTo(6.0f, 19.0f)
            horizontalLineToRelative(12.0f)
            horizontalLineTo(6.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.2442f, 15.1263f)
            curveToRelative(-0.1654f, -0.2197f, -0.4357f, -0.5795f, -0.6007f, -0.7995f)
            lineToRelative(-0.9704f, -1.2938f)
            curveToRelative(-0.165f, -0.22f, -0.435f, -0.22f, -0.6f, 0.0f)
            lineToRelative(-1.275f, 1.7f)
            curveToRelative(-0.165f, 0.22f, -0.4354f, 0.2203f, -0.6009f, 7.0E-4f)
            lineToRelative(-0.7733f, -1.0263f)
            curveToRelative(-0.1655f, -0.2196f, -0.4361f, -0.2195f, -0.6014f, 2.0E-4f)
            lineToRelative(-1.5239f, 2.0259f)
            curveToRelative(-0.1653f, 0.2198f, -0.0756f, 0.3996f, 0.1994f, 0.3996f)
            horizontalLineToRelative(0.9267f)
            curveToRelative(0.275f, 0.0f, 0.725f, 0.0f, 1.0f, 0.0f)
            horizontalLineToRelative(3.1464f)
            curveToRelative(0.275f, 0.0f, 0.725f, 0.0f, 1.0f, 0.0f)
            horizontalLineToRelative(0.9308f)
            curveToRelative(0.275f, 0.0f, 0.3647f, -0.1798f, 0.1993f, -0.3995f)
            lineTo(15.2442f, 15.1263f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageWeight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.ImageWeight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19.975f, 18.725f)
            lineToRelative(-1.425f, -10f)
            curveToRelative(-0.083f, -0.483f, -0.308f, -0.892f, -0.675f, -1.225f)
            reflectiveCurveToRelative(-0.8f, -0.5f, -1.3f, -0.5f)
            horizontalLineToRelative(-1.75f)
            curveToRelative(0.05f, -0.167f, 0.092f, -0.329f, 0.125f, -0.487f)
            curveToRelative(0.033f, -0.158f, 0.05f, -0.329f, 0.05f, -0.513f)
            curveToRelative(0f, -0.833f, -0.292f, -1.542f, -0.875f, -2.125f)
            reflectiveCurveToRelative(-1.292f, -0.875f, -2.125f, -0.875f)
            reflectiveCurveToRelative(-1.542f, 0.292f, -2.125f, 0.875f)
            reflectiveCurveToRelative(-0.875f, 1.292f, -0.875f, 2.125f)
            curveToRelative(0f, 0.183f, 0.017f, 0.354f, 0.05f, 0.513f)
            curveToRelative(0.033f, 0.158f, 0.075f, 0.321f, 0.125f, 0.487f)
            horizontalLineToRelative(-1.75f)
            curveToRelative(-0.5f, 0f, -0.933f, 0.167f, -1.3f, 0.5f)
            reflectiveCurveToRelative(-0.592f, 0.742f, -0.675f, 1.225f)
            lineToRelative(-1.425f, 10f)
            curveToRelative(-0.083f, 0.6f, 0.071f, 1.129f, 0.462f, 1.588f)
            curveToRelative(0.392f, 0.458f, 0.896f, 0.688f, 1.513f, 0.688f)
            horizontalLineToRelative(12f)
            curveToRelative(0.617f, 0f, 1.121f, -0.229f, 1.513f, -0.688f)
            curveToRelative(0.392f, -0.458f, 0.546f, -0.987f, 0.462f, -1.588f)
            close()
            moveTo(11.287f, 5.287f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.713f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.713f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.713f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.288f, -0.713f, 0.288f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.713f, -0.288f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.713f)
            close()
            moveTo(6f, 19f)
            lineToRelative(1.425f, -10f)
            horizontalLineToRelative(9.15f)
            lineToRelative(1.425f, 10f)
            horizontalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.244f, 15.126f)
            curveToRelative(-0.165f, -0.22f, -0.436f, -0.58f, -0.601f, -0.8f)
            lineToRelative(-0.97f, -1.294f)
            curveToRelative(-0.165f, -0.22f, -0.435f, -0.22f, -0.6f, 0f)
            lineToRelative(-1.275f, 1.7f)
            curveToRelative(-0.165f, 0.22f, -0.435f, 0.22f, -0.601f, 0.001f)
            lineToRelative(-0.773f, -1.026f)
            curveToRelative(-0.165f, -0.22f, -0.436f, -0.219f, -0.601f, 0f)
            lineToRelative(-1.524f, 2.026f)
            curveToRelative(-0.165f, 0.22f, -0.076f, 0.4f, 0.199f, 0.4f)
            horizontalLineToRelative(7.004f)
            curveToRelative(0.275f, 0f, 0.365f, -0.18f, 0.199f, -0.399f)
            lineToRelative(-0.457f, -0.607f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(6f, 19f)
            lineToRelative(12f, 0f)
            lineToRelative(-1.425f, -10f)
            lineToRelative(-9.15f, 0f)
            lineToRelative(-1.425f, 10f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 7f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.288f, -0.429f, 0.288f, -0.713f)
            reflectiveCurveToRelative(-0.096f, -0.521f, -0.288f, -0.713f)
            reflectiveCurveToRelative(-0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.288f, 0.429f, -0.288f, 0.713f)
            reflectiveCurveToRelative(0.096f, 0.521f, 0.288f, 0.713f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6f, 19f)
            horizontalLineToRelative(12f)
            horizontalLineTo(6f)
            close()
        }
    }.build()
}