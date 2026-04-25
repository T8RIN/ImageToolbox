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

val Icons.Rounded.FormatColorFill: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FormatColorFill",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(332f, 28f)
            lineToRelative(315f, 315f)
            quadToRelative(23f, 23f, 23f, 57f)
            reflectiveQuadToRelative(-23f, 57f)
            lineTo(457f, 647f)
            quadToRelative(-23f, 23f, -57f, 23f)
            reflectiveQuadToRelative(-57f, -23f)
            lineTo(153f, 457f)
            quadToRelative(-23f, -23f, -23f, -57f)
            reflectiveQuadToRelative(23f, -57f)
            lineToRelative(190f, -191f)
            lineToRelative(-68f, -68f)
            quadToRelative(-12f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(12.5f, -28f)
            quadToRelative(12f, -11f, 28f, -11.5f)
            reflectiveQuadToRelative(28f, 11.5f)
            close()
            moveTo(400f, 209f)
            lineTo(209f, 400f)
            horizontalLineToRelative(382f)
            lineTo(400f, 209f)
            close()
            moveTo(703.5f, 656.5f)
            quadTo(680f, 633f, 680f, 600f)
            quadToRelative(0f, -21f, 12.5f, -45f)
            reflectiveQuadToRelative(27.5f, -45f)
            quadToRelative(9f, -12f, 19f, -25f)
            reflectiveQuadToRelative(21f, -25f)
            quadToRelative(11f, 12f, 21f, 25f)
            reflectiveQuadToRelative(19f, 25f)
            quadToRelative(15f, 21f, 27.5f, 45f)
            reflectiveQuadToRelative(12.5f, 45f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 680f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            close()
            moveTo(160f, 960f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 880f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 800f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 880f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 960f)
            lineTo(160f, 960f)
            close()
        }
    }.build()
}
