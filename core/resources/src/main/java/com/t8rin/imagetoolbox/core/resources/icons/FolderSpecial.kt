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

val Icons.Outlined.FolderSpecial: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderSpecial",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(207f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(57f, 57f)
            horizontalLineToRelative(320f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 320f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 720f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-400f)
            lineTo(447f, 320f)
            lineToRelative(-80f, -80f)
            lineTo(160f, 240f)
            verticalLineToRelative(480f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
            moveTo(596f, 598f)
            lineTo(664f, 649f)
            quadToRelative(6f, 5f, 11.5f, 1f)
            reflectiveQuadToRelative(3.5f, -11f)
            lineToRelative(-25f, -85f)
            lineToRelative(70f, -56f)
            quadToRelative(5f, -5f, 3f, -11.5f)
            reflectiveQuadToRelative(-9f, -6.5f)
            horizontalLineToRelative(-86f)
            lineToRelative(-26f, -82f)
            quadToRelative(-2f, -7f, -10f, -7f)
            reflectiveQuadToRelative(-10f, 7f)
            lineToRelative(-26f, 82f)
            horizontalLineToRelative(-86f)
            quadToRelative(-7f, 0f, -9f, 6.5f)
            reflectiveQuadToRelative(3f, 11.5f)
            lineToRelative(70f, 56f)
            lineToRelative(-25f, 85f)
            quadToRelative(-2f, 7f, 3.5f, 11f)
            reflectiveQuadToRelative(11.5f, -1f)
            lineToRelative(68f, -51f)
            close()
        }
    }.build()
}
