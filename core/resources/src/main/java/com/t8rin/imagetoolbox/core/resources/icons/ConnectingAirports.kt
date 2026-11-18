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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.ConnectingAirports: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ConnectingAirports",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(776f, -320f)
                lineToRelative(18f, 60f)
                quadToRelative(2f, 8f, -2.5f, 14f)
                reflectiveQuadToRelative(-12.5f, 6f)
                horizontalLineToRelative(-11f)
                quadToRelative(-4f, 0f, -7.5f, -1.5f)
                reflectiveQuadTo(755f, -246f)
                lineToRelative(-25f, -34f)
                horizontalLineTo(616f)
                lineToRelative(45f, 152f)
                quadToRelative(3f, 9f, -2.5f, 16.5f)
                reflectiveQuadTo(643f, -104f)
                horizontalLineToRelative(-8f)
                quadToRelative(-5f, 0f, -9.5f, -2f)
                reflectiveQuadToRelative(-6.5f, -7f)
                lineToRelative(-99f, -167f)
                horizontalLineTo(400f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(360f, -320f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(400f, -360f)
                horizontalLineToRelative(120f)
                lineToRelative(98f, -166f)
                quadToRelative(3f, -5f, 8f, -7.5f)
                reflectiveQuadToRelative(10f, -2.5f)
                horizontalLineToRelative(4f)
                quadToRelative(11f, 0f, 17f, 8f)
                reflectiveQuadToRelative(3f, 19f)
                lineToRelative(-44f, 149f)
                horizontalLineToRelative(114f)
                lineToRelative(24f, -32f)
                quadToRelative(3f, -4f, 7f, -6f)
                reflectiveQuadToRelative(9f, -2f)
                horizontalLineToRelative(4f)
                quadToRelative(10f, 0f, 16f, 8f)
                reflectiveQuadToRelative(3f, 17f)
                lineToRelative(-17f, 55f)
                close()
                moveTo(184f, -640f)
                lineToRelative(-18f, -60f)
                quadToRelative(-2f, -8f, 2.5f, -14f)
                reflectiveQuadToRelative(12.5f, -6f)
                horizontalLineToRelative(11f)
                quadToRelative(4f, 0f, 7.5f, 1.5f)
                reflectiveQuadToRelative(5.5f, 4.5f)
                lineToRelative(25f, 34f)
                horizontalLineToRelative(114f)
                lineToRelative(-45f, -152f)
                quadToRelative(-3f, -9f, 2.5f, -16.5f)
                reflectiveQuadTo(317f, -856f)
                horizontalLineToRelative(8f)
                quadToRelative(5f, 0f, 9.5f, 2f)
                reflectiveQuadToRelative(6.5f, 7f)
                lineToRelative(99f, 167f)
                horizontalLineToRelative(120f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(600f, -640f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(560f, -600f)
                horizontalLineTo(440f)
                lineToRelative(-98f, 166f)
                quadToRelative(-3f, 5f, -7.5f, 7.5f)
                reflectiveQuadTo(325f, -424f)
                horizontalLineToRelative(-8f)
                quadToRelative(-8f, 0f, -14f, -8f)
                reflectiveQuadToRelative(-3f, -19f)
                lineToRelative(44f, -149f)
                horizontalLineTo(230f)
                lineToRelative(-24f, 32f)
                quadToRelative(-3f, 4f, -7f, 6f)
                reflectiveQuadToRelative(-9f, 2f)
                horizontalLineToRelative(-4f)
                quadToRelative(-10f, 0f, -16f, -8f)
                reflectiveQuadToRelative(-3f, -17f)
                lineToRelative(17f, -55f)
                close()
            }
        }
    }.build()
}