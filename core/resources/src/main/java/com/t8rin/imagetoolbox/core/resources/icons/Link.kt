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

val Icons.Rounded.Link: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Link",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(318f, 840f)
            quadToRelative(-82f, 0f, -140f, -58f)
            reflectiveQuadToRelative(-58f, -140f)
            quadToRelative(0f, -40f, 15f, -76f)
            reflectiveQuadToRelative(43f, -64f)
            lineToRelative(105f, -105f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            lineTo(234f, 559f)
            quadToRelative(-17f, 17f, -25.5f, 38.5f)
            reflectiveQuadTo(200f, 642f)
            quadToRelative(0f, 49f, 34.5f, 83.5f)
            reflectiveQuadTo(318f, 760f)
            quadToRelative(23f, 0f, 45f, -8.5f)
            reflectiveQuadToRelative(39f, -25.5f)
            lineToRelative(105f, -106f)
            quadToRelative(12f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 12f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            lineTo(458f, 782f)
            quadToRelative(-28f, 28f, -64f, 43f)
            reflectiveQuadToRelative(-76f, 15f)
            close()
            moveTo(368f, 592f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            lineToRelative(167f, -167f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            quadToRelative(12f, 12f, 12f, 28.5f)
            reflectiveQuadTo(592f, 425f)
            lineTo(425f, 592f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(368f, 592f)
            close()
            moveTo(620f, 563f)
            quadToRelative(-12f, -12f, -12f, -28f)
            reflectiveQuadToRelative(12f, -28f)
            lineToRelative(106f, -105f)
            quadToRelative(17f, -17f, 25f, -38f)
            reflectiveQuadToRelative(8f, -44f)
            quadToRelative(0f, -50f, -34f, -85f)
            reflectiveQuadToRelative(-84f, -35f)
            quadToRelative(-23f, 0f, -44.5f, 8.5f)
            reflectiveQuadTo(558f, 234f)
            lineTo(453f, 340f)
            quadToRelative(-12f, 12f, -28f, 12f)
            reflectiveQuadToRelative(-28f, -12f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            lineToRelative(105f, -105f)
            quadToRelative(28f, -28f, 64f, -43f)
            reflectiveQuadToRelative(76f, -15f)
            quadToRelative(82f, 0f, 139.5f, 58f)
            reflectiveQuadTo(839f, 319f)
            quadToRelative(0f, 39f, -14.5f, 75f)
            reflectiveQuadTo(782f, 458f)
            lineTo(677f, 563f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(620f, 563f)
            close()
        }
    }.build()
}
