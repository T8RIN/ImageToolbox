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

val Icons.Outlined.TextSearch: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TextSearch",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 720f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 680f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(480f, 720f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(440f, 760f)
            lineTo(120f, 760f)
            close()
            moveTo(120f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 480f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 520f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 560f)
            lineTo(120f, 560f)
            close()
            moveTo(120f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 280f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 360f)
            lineTo(120f, 360f)
            close()
            moveTo(560f, 640f)
            quadToRelative(-83f, 0f, -141.5f, -58.5f)
            reflectiveQuadTo(360f, 440f)
            quadToRelative(0f, -83f, 58.5f, -141.5f)
            reflectiveQuadTo(560f, 240f)
            quadToRelative(83f, 0f, 141.5f, 58.5f)
            reflectiveQuadTo(760f, 440f)
            quadToRelative(0f, 29f, -8.5f, 57.5f)
            reflectiveQuadTo(726f, 550f)
            lineToRelative(126f, 126f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(670f, 606f)
            quadToRelative(-24f, 17f, -52.5f, 25.5f)
            reflectiveQuadTo(560f, 640f)
            close()
            moveTo(560f, 560f)
            quadToRelative(50f, 0f, 85f, -35f)
            reflectiveQuadToRelative(35f, -85f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
        }
    }.build()
}
