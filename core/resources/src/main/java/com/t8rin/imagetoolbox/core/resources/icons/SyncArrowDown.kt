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

val Icons.Outlined.SyncArrowDown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SyncArrowDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 780f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 740f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 700f)
            horizontalLineToRelative(17f)
            quadToRelative(-47f, -42f, -72f, -99.5f)
            reflectiveQuadTo(40f, 480f)
            quadToRelative(0f, -91f, 49.5f, -165.5f)
            reflectiveQuadTo(220f, 205f)
            quadToRelative(15f, -7f, 29.5f, 1f)
            reflectiveQuadToRelative(19.5f, 24f)
            quadToRelative(5f, 16f, -3.5f, 30f)
            reflectiveQuadTo(242f, 282f)
            quadToRelative(-56f, 27f, -89f, 80f)
            reflectiveQuadToRelative(-33f, 117f)
            quadToRelative(0f, 50f, 21f, 93.5f)
            reflectiveQuadToRelative(59f, 75.5f)
            verticalLineToRelative(-28f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 580f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 620f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 780f)
            lineTo(120f, 780f)
            close()
            moveTo(452f, 758f)
            quadToRelative(-14f, 5f, -25f, -4f)
            reflectiveQuadToRelative(-16f, -24f)
            quadToRelative(-5f, -15f, 0f, -29f)
            reflectiveQuadToRelative(19f, -20f)
            quadToRelative(59f, -26f, 94.5f, -80f)
            reflectiveQuadTo(560f, 481f)
            quadToRelative(0f, -50f, -21f, -93.5f)
            reflectiveQuadTo(480f, 312f)
            verticalLineToRelative(28f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(440f, 380f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(400f, 340f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(440f, 180f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 220f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(560f, 260f)
            horizontalLineToRelative(-17f)
            quadToRelative(47f, 42f, 72f, 99.5f)
            reflectiveQuadTo(640f, 480f)
            quadToRelative(0f, 94f, -51.5f, 169f)
            reflectiveQuadTo(452f, 758f)
            close()
            moveTo(752f, 772f)
            lineTo(660f, 680f)
            quadToRelative(-11f, -12f, -11f, -28.5f)
            reflectiveQuadToRelative(12f, -27.5f)
            quadToRelative(12f, -11f, 28.5f, -11.5f)
            reflectiveQuadTo(717f, 624f)
            lineToRelative(23f, 23f)
            verticalLineToRelative(-447f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(780f, 160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(820f, 200f)
            verticalLineToRelative(448f)
            lineToRelative(24f, -24f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineToRelative(-92f, 92f)
            quadToRelative(-12f, 12f, -28f, 12f)
            reflectiveQuadToRelative(-28f, -12f)
            close()
        }
    }.build()
}
