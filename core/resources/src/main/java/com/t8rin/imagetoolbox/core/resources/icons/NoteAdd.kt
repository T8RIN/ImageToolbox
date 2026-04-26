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

val Icons.Rounded.NoteAdd: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.NoteAdd",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 800f)
            verticalLineToRelative(-640f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 80f)
            horizontalLineToRelative(287f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(194f, 194f)
            quadToRelative(11f, 11f, 17f, 25.5f)
            reflectiveQuadToRelative(6f, 30.5f)
            verticalLineToRelative(447f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 880f)
            lineTo(240f, 880f)
            close()
            moveTo(520f, 320f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(560f, 360f)
            horizontalLineToRelative(160f)
            lineTo(520f, 160f)
            verticalLineToRelative(160f)
            close()
            moveTo(440f, 600f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 720f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 680f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(640f, 560f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(600f, 520f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 400f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 440f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(320f, 560f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(360f, 600f)
            horizontalLineToRelative(80f)
            close()
        }
    }.build()
}
