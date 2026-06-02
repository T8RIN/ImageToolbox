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

val Icons.Rounded.HardDrive: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.HardDrive",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(680f, 640f)
            quadTo(705f, 640f, 722.5f, 623f)
            quadTo(740f, 606f, 740f, 580f)
            quadTo(740f, 555f, 722.5f, 537.5f)
            quadTo(705f, 520f, 680f, 520f)
            quadTo(654f, 520f, 637f, 537.5f)
            quadTo(620f, 555f, 620f, 580f)
            quadTo(620f, 606f, 637f, 623f)
            quadTo(654f, 640f, 680f, 640f)
            close()
            moveTo(80f, 360f)
            lineTo(216f, 224f)
            quadTo(227f, 213f, 241.5f, 206.5f)
            quadTo(256f, 200f, 273f, 200f)
            lineTo(686f, 200f)
            quadTo(703f, 200f, 717.5f, 206.5f)
            quadTo(732f, 213f, 743f, 224f)
            lineTo(880f, 360f)
            lineTo(80f, 360f)
            close()
            moveTo(160f, 760f)
            quadTo(126f, 760f, 103f, 737f)
            quadTo(80f, 714f, 80f, 680f)
            lineTo(80f, 440f)
            lineTo(880f, 440f)
            lineTo(880f, 680f)
            quadTo(880f, 714f, 856.5f, 737f)
            quadTo(833f, 760f, 800f, 760f)
            lineTo(160f, 760f)
            close()
        }
    }.build()
}

val Icons.Outlined.HardDrive: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.HardDrive",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 680f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-240f)
            lineTo(160f, 440f)
            verticalLineToRelative(240f)
            close()
            moveTo(722.5f, 602.5f)
            quadTo(740f, 585f, 740f, 560f)
            reflectiveQuadToRelative(-17.5f, -42.5f)
            quadTo(705f, 500f, 680f, 500f)
            reflectiveQuadToRelative(-42.5f, 17.5f)
            quadTo(620f, 535f, 620f, 560f)
            reflectiveQuadToRelative(17.5f, 42.5f)
            quadTo(655f, 620f, 680f, 620f)
            reflectiveQuadToRelative(42.5f, -17.5f)
            close()
            moveTo(880f, 360f)
            lineTo(767f, 360f)
            lineToRelative(-80f, -80f)
            lineTo(273f, 280f)
            lineToRelative(-80f, 80f)
            lineTo(80f, 360f)
            lineToRelative(137f, -137f)
            quadToRelative(11f, -11f, 25.5f, -17f)
            reflectiveQuadToRelative(30.5f, -6f)
            horizontalLineToRelative(414f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(137f, 137f)
            close()
            moveTo(160f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 680f)
            verticalLineToRelative(-320f)
            horizontalLineToRelative(800f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 760f)
            lineTo(160f, 760f)
            close()
        }
    }.build()
}