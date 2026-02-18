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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.SwapVerticalCircle: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "SwapVerticalCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9f, 8.85f)
            verticalLineToRelative(3.15f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.287f, -0.429f, 0.287f, -0.712f)
            verticalLineToRelative(-3.15f)
            lineToRelative(0.9f, 0.875f)
            curveToRelative(0.183f, 0.183f, 0.412f, 0.275f, 0.688f, 0.275f)
            reflectiveCurveToRelative(0.512f, -0.1f, 0.712f, -0.3f)
            curveToRelative(0.183f, -0.183f, 0.275f, -0.417f, 0.275f, -0.7f)
            reflectiveCurveToRelative(-0.092f, -0.517f, -0.275f, -0.7f)
            lineToRelative(-2.575f, -2.575f)
            curveToRelative(-0.2f, -0.2f, -0.438f, -0.3f, -0.712f, -0.3f)
            reflectiveCurveToRelative(-0.512f, 0.1f, -0.712f, 0.3f)
            lineToRelative(-2.6f, 2.575f)
            curveToRelative(-0.183f, 0.183f, -0.279f, 0.412f, -0.287f, 0.688f)
            reflectiveCurveToRelative(0.087f, 0.512f, 0.287f, 0.712f)
            curveToRelative(0.183f, 0.183f, 0.412f, 0.279f, 0.688f, 0.287f)
            reflectiveCurveToRelative(0.512f, -0.079f, 0.712f, -0.262f)
            lineToRelative(0.9f, -0.875f)
            close()
            moveTo(13f, 15.15f)
            lineToRelative(-0.9f, -0.875f)
            curveToRelative(-0.183f, -0.183f, -0.412f, -0.275f, -0.688f, -0.275f)
            reflectiveCurveToRelative(-0.512f, 0.1f, -0.712f, 0.3f)
            curveToRelative(-0.183f, 0.183f, -0.275f, 0.417f, -0.275f, 0.7f)
            reflectiveCurveToRelative(0.092f, 0.517f, 0.275f, 0.7f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.2f, 0.2f, 0.438f, 0.3f, 0.712f, 0.3f)
            reflectiveCurveToRelative(0.512f, -0.1f, 0.712f, -0.3f)
            lineToRelative(2.575f, -2.6f)
            curveToRelative(0.183f, -0.183f, 0.279f, -0.412f, 0.287f, -0.688f)
            reflectiveCurveToRelative(-0.087f, -0.512f, -0.287f, -0.712f)
            curveToRelative(-0.183f, -0.183f, -0.412f, -0.279f, -0.688f, -0.287f)
            reflectiveCurveToRelative(-0.512f, 0.079f, -0.712f, 0.262f)
            lineToRelative(-0.9f, 0.875f)
            verticalLineToRelative(-3.15f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(3.15f)
            close()
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
            curveToRelative(2.233f, 0f, 4.125f, -0.775f, 5.675f, -2.325f)
            reflectiveCurveToRelative(2.325f, -3.442f, 2.325f, -5.675f)
            reflectiveCurveToRelative(-0.775f, -4.125f, -2.325f, -5.675f)
            reflectiveCurveToRelative(-3.442f, -2.325f, -5.675f, -2.325f)
            reflectiveCurveToRelative(-4.125f, 0.775f, -5.675f, 2.325f)
            reflectiveCurveToRelative(-2.325f, 3.442f, -2.325f, 5.675f)
            reflectiveCurveToRelative(0.775f, 4.125f, 2.325f, 5.675f)
            reflectiveCurveToRelative(3.442f, 2.325f, 5.675f, 2.325f)
            close()
        }
    }.build()
}

val Icons.TwoTone.SwapVerticalCircle: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneSwapVerticalCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9f, 8.85f)
            verticalLineToRelative(3.15f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.287f, -0.429f, 0.287f, -0.712f)
            verticalLineToRelative(-3.15f)
            lineToRelative(0.9f, 0.875f)
            curveToRelative(0.183f, 0.183f, 0.412f, 0.275f, 0.688f, 0.275f)
            reflectiveCurveToRelative(0.512f, -0.1f, 0.712f, -0.3f)
            curveToRelative(0.183f, -0.183f, 0.275f, -0.417f, 0.275f, -0.7f)
            reflectiveCurveToRelative(-0.092f, -0.517f, -0.275f, -0.7f)
            lineToRelative(-2.575f, -2.575f)
            curveToRelative(-0.2f, -0.2f, -0.438f, -0.3f, -0.712f, -0.3f)
            reflectiveCurveToRelative(-0.512f, 0.1f, -0.712f, 0.3f)
            lineToRelative(-2.6f, 2.575f)
            curveToRelative(-0.183f, 0.183f, -0.279f, 0.412f, -0.287f, 0.688f)
            reflectiveCurveToRelative(0.087f, 0.512f, 0.287f, 0.712f)
            curveToRelative(0.183f, 0.183f, 0.412f, 0.279f, 0.688f, 0.287f)
            reflectiveCurveToRelative(0.512f, -0.079f, 0.712f, -0.262f)
            lineToRelative(0.9f, -0.875f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 15.15f)
            lineToRelative(-0.9f, -0.875f)
            curveToRelative(-0.183f, -0.183f, -0.412f, -0.275f, -0.688f, -0.275f)
            reflectiveCurveToRelative(-0.512f, 0.1f, -0.712f, 0.3f)
            curveToRelative(-0.183f, 0.183f, -0.275f, 0.417f, -0.275f, 0.7f)
            reflectiveCurveToRelative(0.092f, 0.517f, 0.275f, 0.7f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.2f, 0.2f, 0.438f, 0.3f, 0.712f, 0.3f)
            reflectiveCurveToRelative(0.512f, -0.1f, 0.712f, -0.3f)
            lineToRelative(2.575f, -2.6f)
            curveToRelative(0.183f, -0.183f, 0.279f, -0.412f, 0.287f, -0.688f)
            reflectiveCurveToRelative(-0.087f, -0.512f, -0.287f, -0.712f)
            curveToRelative(-0.183f, -0.183f, -0.412f, -0.279f, -0.688f, -0.287f)
            reflectiveCurveToRelative(-0.512f, 0.079f, -0.712f, 0.262f)
            lineToRelative(-0.9f, 0.875f)
            verticalLineToRelative(-3.15f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(3.15f)
            close()
        }
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
            moveTo(17.675f, 17.675f)
            curveToRelative(-1.55f, 1.55f, -3.442f, 2.325f, -5.675f, 2.325f)
            reflectiveCurveToRelative(-4.125f, -0.775f, -5.675f, -2.325f)
            reflectiveCurveToRelative(-2.325f, -3.442f, -2.325f, -5.675f)
            reflectiveCurveToRelative(0.775f, -4.125f, 2.325f, -5.675f)
            reflectiveCurveToRelative(3.442f, -2.325f, 5.675f, -2.325f)
            reflectiveCurveToRelative(4.125f, 0.775f, 5.675f, 2.325f)
            reflectiveCurveToRelative(2.325f, 3.442f, 2.325f, 5.675f)
            reflectiveCurveToRelative(-0.775f, 4.125f, -2.325f, 5.675f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 20f)
            curveToRelative(2.233f, 0f, 4.125f, -0.775f, 5.675f, -2.325f)
            reflectiveCurveToRelative(2.325f, -3.442f, 2.325f, -5.675f)
            reflectiveCurveToRelative(-0.775f, -4.125f, -2.325f, -5.675f)
            reflectiveCurveToRelative(-3.442f, -2.325f, -5.675f, -2.325f)
            reflectiveCurveToRelative(-4.125f, 0.775f, -5.675f, 2.325f)
            reflectiveCurveToRelative(-2.325f, 3.442f, -2.325f, 5.675f)
            reflectiveCurveToRelative(0.775f, 4.125f, 2.325f, 5.675f)
            reflectiveCurveToRelative(3.442f, 2.325f, 5.675f, 2.325f)
            close()
        }
    }.build()
}
