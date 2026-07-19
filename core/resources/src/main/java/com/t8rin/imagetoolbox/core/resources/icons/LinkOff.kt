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

val Icons.Rounded.LinkOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.LinkOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveToRelative(625f, 511f)
            lineToRelative(-71f, -71f)
            horizontalLineToRelative(46f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(640f, 480f)
            quadToRelative(0f, 10f, -4f, 18f)
            reflectiveQuadToRelative(-11f, 13f)
            close()
            moveTo(820f, 876f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(84f, 196f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(680f, 680f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            close()
            moveTo(280f, 680f)
            quadToRelative(-83f, 0f, -141.5f, -58.5f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -69f, 42f, -123f)
            reflectiveQuadToRelative(108f, -71f)
            lineToRelative(74f, 74f)
            horizontalLineToRelative(-24f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(440f, 640f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 680f)
            lineTo(280f, 680f)
            close()
            moveTo(360f, 520f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(320f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(360f, 440f)
            horizontalLineToRelative(25f)
            lineToRelative(79f, 80f)
            lineTo(360f, 520f)
            close()
            moveTo(740f, 632f)
            quadToRelative(-9f, -14f, -6.5f, -30f)
            reflectiveQuadToRelative(16.5f, -25f)
            quadToRelative(23f, -17f, 36.5f, -42f)
            reflectiveQuadToRelative(13.5f, -55f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            lineTo(560f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 280f)
            horizontalLineToRelative(120f)
            quadToRelative(83f, 0f, 141.5f, 58.5f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 49f, -22.5f, 91.5f)
            reflectiveQuadTo(795f, 642f)
            quadToRelative(-14f, 9f, -30f, 6.5f)
            reflectiveQuadTo(740f, 632f)
            close()
        }
    }.build()
}