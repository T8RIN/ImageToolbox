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

import com.t8rin.imagetoolbox.core.resources.Icons
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

val Icons.Rounded.Block: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Block",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 800f)
            quadToRelative(54f, 0f, 104f, -17.5f)
            reflectiveQuadToRelative(92f, -50.5f)
            lineTo(228f, 284f)
            quadToRelative(-33f, 42f, -50.5f, 92f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(0f, 134f, 93f, 227f)
            reflectiveQuadToRelative(227f, 93f)
            close()
            moveTo(732f, 676f)
            quadToRelative(33f, -42f, 50.5f, -92f)
            reflectiveQuadTo(800f, 480f)
            quadToRelative(0f, -134f, -93f, -227f)
            reflectiveQuadToRelative(-227f, -93f)
            quadToRelative(-54f, 0f, -104f, 17.5f)
            reflectiveQuadTo(284f, 228f)
            lineToRelative(448f, 448f)
            close()
        }
    }.build()
}