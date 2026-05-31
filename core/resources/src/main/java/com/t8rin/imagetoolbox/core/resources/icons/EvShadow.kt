/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Rounded.EvShadow: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.EvShadow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(389f, 613f)
            lineToRelative(337f, -337f)
            quadToRelative(-11f, -12f, -22f, -23.5f)
            reflectiveQuadTo(680f, 231f)
            lineTo(367f, 545f)
            quadToRelative(4f, 18f, 9f, 34.5f)
            reflectiveQuadToRelative(13f, 33.5f)
            close()
            moveTo(774f, 607f)
            quadToRelative(18f, -41f, 23f, -86f)
            lineTo(547f, 771f)
            quadToRelative(8f, 3f, 16.5f, 7f)
            reflectiveQuadToRelative(16.5f, 6f)
            quadToRelative(44f, -14f, 81f, -40f)
            reflectiveQuadToRelative(66f, -61f)
            quadToRelative(29f, -35f, 47f, -76f)
            close()
            moveTo(160f, 480f)
            quadToRelative(0f, 122f, 79f, 211.5f)
            reflectiveQuadTo(436f, 797f)
            quadToRelative(-72f, -55f, -114f, -137.5f)
            reflectiveQuadTo(280f, 480f)
            quadToRelative(0f, -97f, 42f, -179.5f)
            reflectiveQuadTo(436f, 163f)
            quadToRelative(-118f, 16f, -197f, 105.5f)
            reflectiveQuadTo(160f, 480f)
            close()
            moveTo(477f, 727f)
            lineTo(792f, 413f)
            quadToRelative(-4f, -18f, -9f, -35f)
            reflectiveQuadToRelative(-13f, -33f)
            lineTo(432f, 682f)
            quadToRelative(11f, 13f, 21f, 23.5f)
            reflectiveQuadToRelative(24f, 21.5f)
            close()
            moveTo(324f, 848.5f)
            quadTo(251f, 817f, 197f, 763f)
            reflectiveQuadToRelative(-85.5f, -127f)
            quadTo(80f, 563f, 80f, 480f)
            reflectiveQuadToRelative(31.5f, -156f)
            quadTo(143f, 251f, 197f, 197f)
            reflectiveQuadToRelative(127f, -85.5f)
            quadTo(397f, 80f, 480f, 80f)
            reflectiveQuadToRelative(156f, 31.5f)
            quadTo(709f, 143f, 763f, 197f)
            reflectiveQuadToRelative(85.5f, 127f)
            quadTo(880f, 397f, 880f, 480f)
            reflectiveQuadToRelative(-31.5f, 156f)
            quadTo(817f, 709f, 763f, 763f)
            reflectiveQuadToRelative(-127f, 85.5f)
            quadTo(563f, 880f, 480f, 880f)
            reflectiveQuadToRelative(-156f, -31.5f)
            close()
            moveTo(363f, 435f)
            lineToRelative(247f, -247f)
            quadToRelative(-8f, -3f, -14.5f, -6.5f)
            reflectiveQuadTo(581f, 176f)
            quadToRelative(-86f, 28f, -145.5f, 97.5f)
            reflectiveQuadTo(363f, 435f)
            close()
            moveTo(580f, 480f)
            close()
        }
    }.build()
}

val Icons.TwoTone.EvShadow: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.EvShadow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.213f, 8.1f)
            curveToRelative(-0.525f, -1.217f, -1.238f, -2.275f, -2.138f, -3.175f)
            curveToRelative(-0.9f, -0.9f, -1.958f, -1.612f, -3.175f, -2.138f)
            curveToRelative(-1.217f, -0.525f, -2.517f, -0.787f, -3.9f, -0.787f)
            reflectiveCurveToRelative(-2.683f, 0.263f, -3.9f, 0.787f)
            curveToRelative(-1.217f, 0.525f, -2.275f, 1.238f, -3.175f, 2.138f)
            curveToRelative(-0.9f, 0.9f, -1.612f, 1.958f, -2.138f, 3.175f)
            curveToRelative(-0.525f, 1.217f, -0.787f, 2.517f, -0.787f, 3.9f)
            reflectiveCurveToRelative(0.263f, 2.683f, 0.787f, 3.9f)
            curveToRelative(0.525f, 1.217f, 1.238f, 2.275f, 2.138f, 3.175f)
            curveToRelative(0.9f, 0.9f, 1.958f, 1.612f, 3.175f, 2.138f)
            curveToRelative(1.217f, 0.525f, 2.517f, 0.787f, 3.9f, 0.787f)
            reflectiveCurveToRelative(2.683f, -0.263f, 3.9f, -0.787f)
            curveToRelative(1.217f, -0.525f, 2.275f, -1.238f, 3.175f, -2.138f)
            curveToRelative(0.9f, -0.9f, 1.612f, -1.958f, 2.138f, -3.175f)
            curveToRelative(0.525f, -1.217f, 0.787f, -2.517f, 0.787f, -3.9f)
            reflectiveCurveToRelative(-0.263f, -2.683f, -0.787f, -3.9f)
            close()
            moveTo(17.037f, 5.775f)
            curveToRelative(0.217f, 0.167f, 0.417f, 0.346f, 0.6f, 0.537f)
            reflectiveCurveToRelative(0.367f, 0.388f, 0.55f, 0.588f)
            lineToRelative(-8.425f, 8.425f)
            curveToRelative(-0.133f, -0.283f, -0.242f, -0.563f, -0.325f, -0.838f)
            curveToRelative(-0.083f, -0.275f, -0.158f, -0.563f, -0.225f, -0.862f)
            lineToRelative(7.825f, -7.85f)
            close()
            moveTo(14.563f, 4.4f)
            curveToRelative(0.133f, 0.033f, 0.254f, 0.079f, 0.362f, 0.137f)
            curveToRelative(0.108f, 0.058f, 0.229f, 0.113f, 0.362f, 0.163f)
            lineToRelative(-6.175f, 6.175f)
            curveToRelative(0.217f, -1.533f, 0.821f, -2.879f, 1.813f, -4.037f)
            curveToRelative(0.992f, -1.158f, 2.204f, -1.971f, 3.638f, -2.438f)
            close()
            moveTo(6.013f, 17.287f)
            curveToRelative(-1.317f, -1.492f, -1.975f, -3.254f, -1.975f, -5.287f)
            reflectiveCurveToRelative(0.658f, -3.796f, 1.975f, -5.287f)
            curveToRelative(1.317f, -1.492f, 2.958f, -2.371f, 4.925f, -2.638f)
            curveToRelative(-1.2f, 0.917f, -2.15f, 2.063f, -2.85f, 3.438f)
            curveToRelative(-0.7f, 1.375f, -1.05f, 2.871f, -1.05f, 4.487f)
            reflectiveCurveToRelative(0.35f, 3.112f, 1.05f, 4.487f)
            curveToRelative(0.7f, 1.375f, 1.65f, 2.521f, 2.85f, 3.438f)
            curveToRelative(-1.967f, -0.267f, -3.608f, -1.146f, -4.925f, -2.638f)
            close()
            moveTo(11.963f, 18.175f)
            curveToRelative(-0.233f, -0.183f, -0.433f, -0.362f, -0.6f, -0.537f)
            curveToRelative(-0.167f, -0.175f, -0.342f, -0.371f, -0.525f, -0.588f)
            lineToRelative(8.45f, -8.425f)
            curveToRelative(0.133f, 0.267f, 0.242f, 0.542f, 0.325f, 0.825f)
            curveToRelative(0.083f, 0.283f, 0.158f, 0.575f, 0.225f, 0.875f)
            lineToRelative(-7.875f, 7.85f)
            close()
            moveTo(19.388f, 15.175f)
            curveToRelative(-0.3f, 0.683f, -0.692f, 1.317f, -1.175f, 1.9f)
            curveToRelative(-0.483f, 0.583f, -1.033f, 1.092f, -1.65f, 1.525f)
            curveToRelative(-0.617f, 0.433f, -1.292f, 0.767f, -2.025f, 1f)
            curveToRelative(-0.133f, -0.033f, -0.271f, -0.083f, -0.412f, -0.15f)
            reflectiveCurveToRelative(-0.279f, -0.125f, -0.412f, -0.175f)
            lineToRelative(6.25f, -6.25f)
            curveToRelative(-0.083f, 0.75f, -0.275f, 1.467f, -0.575f, 2.15f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(9.762f, 15.325f)
            lineToRelative(8.425f, -8.425f)
            curveToRelative(-0.183f, -0.2f, -0.367f, -0.396f, -0.55f, -0.587f)
            reflectiveCurveToRelative(-0.383f, -0.371f, -0.6f, -0.538f)
            lineToRelative(-7.825f, 7.85f)
            curveToRelative(0.067f, 0.3f, 0.142f, 0.587f, 0.225f, 0.863f)
            reflectiveCurveToRelative(0.192f, 0.554f, 0.325f, 0.837f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(19.388f, 15.175f)
            curveToRelative(0.3f, -0.683f, 0.492f, -1.4f, 0.575f, -2.15f)
            lineToRelative(-6.25f, 6.25f)
            curveToRelative(0.133f, 0.05f, 0.271f, 0.108f, 0.412f, 0.175f)
            reflectiveCurveToRelative(0.279f, 0.117f, 0.412f, 0.15f)
            curveToRelative(0.733f, -0.233f, 1.408f, -0.567f, 2.025f, -1f)
            reflectiveCurveToRelative(1.167f, -0.942f, 1.65f, -1.525f)
            curveToRelative(0.483f, -0.583f, 0.875f, -1.217f, 1.175f, -1.9f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(11.962f, 18.175f)
            lineToRelative(7.875f, -7.85f)
            curveToRelative(-0.067f, -0.3f, -0.142f, -0.592f, -0.225f, -0.875f)
            reflectiveCurveToRelative(-0.192f, -0.558f, -0.325f, -0.825f)
            lineToRelative(-8.45f, 8.425f)
            curveToRelative(0.183f, 0.217f, 0.358f, 0.412f, 0.525f, 0.587f)
            reflectiveCurveToRelative(0.367f, 0.354f, 0.6f, 0.538f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(9.113f, 10.875f)
            lineToRelative(6.175f, -6.175f)
            curveToRelative(-0.133f, -0.05f, -0.254f, -0.104f, -0.363f, -0.162f)
            reflectiveCurveToRelative(-0.229f, -0.104f, -0.363f, -0.138f)
            curveToRelative(-1.433f, 0.467f, -2.646f, 1.279f, -3.638f, 2.438f)
            reflectiveCurveToRelative(-1.596f, 2.504f, -1.813f, 4.037f)
            close()
        }
    }.build()
}
