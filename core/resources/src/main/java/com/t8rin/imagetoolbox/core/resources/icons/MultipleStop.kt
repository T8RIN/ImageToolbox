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

val Icons.Rounded.MultipleStop: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MultipleStop",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(273f, 680f)
            lineToRelative(36f, 36f)
            quadToRelative(11f, 11f, 11f, 27.5f)
            reflectiveQuadTo(308f, 772f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(148f, 668f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(104f, -104f)
            quadToRelative(11f, -11f, 27.5f, -11f)
            reflectiveQuadToRelative(28.5f, 11f)
            quadToRelative(12f, 12f, 12f, 28.5f)
            reflectiveQuadTo(308f, 565f)
            lineToRelative(-35f, 35f)
            horizontalLineToRelative(127f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(440f, 640f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 680f)
            lineTo(273f, 680f)
            close()
            moveTo(560f, 680f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 640f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 600f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 640f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(560f, 680f)
            close()
            moveTo(720f, 680f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(680f, 640f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(720f, 600f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 640f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 680f)
            close()
            moveTo(687f, 360f)
            lineTo(560f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 280f)
            horizontalLineToRelative(127f)
            lineToRelative(-36f, -36f)
            quadToRelative(-11f, -11f, -11f, -27.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(104f, 104f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(708f, 452f)
            quadToRelative(-11f, 11f, -27.5f, 11f)
            reflectiveQuadTo(652f, 452f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            lineToRelative(35f, -35f)
            close()
            moveTo(240f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 360f)
            close()
            moveTo(400f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(360f, 320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(400f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(440f, 320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 360f)
            close()
        }
    }.build()
}
