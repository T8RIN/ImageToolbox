/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

val Icons.TwoTone.SplitAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.SplitAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(6.2f, 8.175f)
            reflectiveCurveToRelative(0.251f, 1.095f, 0.375f, 1.45f)
            reflectiveCurveToRelative(1.925f, -0.475f, 1.925f, -0.475f)
            curveToRelative(0f, 0f, -0.284f, -0.895f, -0.35f, -1.45f)
            reflectiveCurveToRelative(-1.95f, 0.475f, -1.95f, 0.475f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(8.35f, 12.575f)
            lineToRelative(1.1f, 1.025f)
            lineToRelative(1.425f, -1.425f)
            lineToRelative(-1.1f, -1.025f)
            lineToRelative(-1.425f, 1.425f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(11f, 22f)
            verticalLineToRelative(-5f)
            curveToRelative(0f, -0.933f, -0.142f, -1.625f, -0.425f, -2.075f)
            reflectiveCurveToRelative(-0.658f, -0.892f, -1.125f, -1.325f)
            lineToRelative(1.425f, -1.425f)
            curveToRelative(0.2f, 0.183f, 0.392f, 0.379f, 0.575f, 0.587f)
            curveToRelative(0.183f, 0.208f, 0.367f, 0.429f, 0.55f, 0.663f)
            curveToRelative(0.233f, -0.317f, 0.471f, -0.596f, 0.712f, -0.837f)
            curveToRelative(0.242f, -0.242f, 0.488f, -0.479f, 0.738f, -0.712f)
            curveToRelative(0.633f, -0.583f, 1.208f, -1.258f, 1.725f, -2.025f)
            reflectiveCurveToRelative(0.792f, -2.108f, 0.825f, -4.025f)
            lineToRelative(-1.575f, 1.575f)
            lineToRelative(-1.425f, -1.4f)
            lineToRelative(4f, -4f)
            lineToRelative(4f, 4f)
            lineToRelative(-1.4f, 1.4f)
            lineToRelative(-1.6f, -1.575f)
            curveToRelative(-0.033f, 2.383f, -0.4f, 4.079f, -1.1f, 5.088f)
            reflectiveCurveToRelative(-1.4f, 1.829f, -2.1f, 2.463f)
            curveToRelative(-0.533f, 0.483f, -0.967f, 0.954f, -1.3f, 1.413f)
            reflectiveCurveToRelative(-0.5f, 1.196f, -0.5f, 2.213f)
            verticalLineToRelative(5f)
            horizontalLineToRelative(-2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.2f, 8.175f)
            curveToRelative(-0.067f, -0.333f, -0.113f, -0.7f, -0.138f, -1.1f)
            reflectiveCurveToRelative(-0.046f, -0.817f, -0.063f, -1.25f)
            lineToRelative(-1.6f, 1.575f)
            lineToRelative(-1.4f, -1.4f)
            lineTo(7f, 2f)
            lineToRelative(4f, 4f)
            lineToRelative(-1.425f, 1.4f)
            lineToRelative(-1.575f, -1.55f)
            curveToRelative(0f, 0.35f, 0.017f, 0.679f, 0.05f, 0.988f)
            curveToRelative(0.033f, 0.308f, 0.067f, 0.596f, 0.1f, 0.862f)
            lineToRelative(-1.95f, 0.475f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.35f, 12.575f)
            curveToRelative(-0.333f, -0.35f, -0.654f, -0.758f, -0.963f, -1.225f)
            curveToRelative(-0.308f, -0.467f, -0.579f, -1.042f, -0.813f, -1.725f)
            lineToRelative(1.925f, -0.475f)
            curveToRelative(0.167f, 0.45f, 0.358f, 0.833f, 0.575f, 1.15f)
            curveToRelative(0.217f, 0.317f, 0.45f, 0.6f, 0.7f, 0.85f)
            lineToRelative(-1.425f, 1.425f)
            close()
        }
    }.build()
}