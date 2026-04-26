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

val Icons.Rounded.Android: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Android",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(40f, 720f)
            quadToRelative(9f, -107f, 65.5f, -197f)
            reflectiveQuadTo(256f, 380f)
            lineToRelative(-74f, -128f)
            quadToRelative(-6f, -9f, -3f, -19f)
            reflectiveQuadToRelative(13f, -15f)
            quadToRelative(8f, -5f, 18f, -2f)
            reflectiveQuadToRelative(16f, 12f)
            lineToRelative(74f, 128f)
            quadToRelative(86f, -36f, 180f, -36f)
            reflectiveQuadToRelative(180f, 36f)
            lineToRelative(74f, -128f)
            quadToRelative(6f, -9f, 16f, -12f)
            reflectiveQuadToRelative(18f, 2f)
            quadToRelative(10f, 5f, 13f, 15f)
            reflectiveQuadToRelative(-3f, 19f)
            lineToRelative(-74f, 128f)
            quadToRelative(94f, 53f, 150.5f, 143f)
            reflectiveQuadTo(920f, 720f)
            lineTo(40f, 720f)
            close()
            moveTo(280f, 610f)
            quadToRelative(21f, 0f, 35.5f, -14.5f)
            reflectiveQuadTo(330f, 560f)
            quadToRelative(0f, -21f, -14.5f, -35.5f)
            reflectiveQuadTo(280f, 510f)
            quadToRelative(-21f, 0f, -35.5f, 14.5f)
            reflectiveQuadTo(230f, 560f)
            quadToRelative(0f, 21f, 14.5f, 35.5f)
            reflectiveQuadTo(280f, 610f)
            close()
            moveTo(680f, 610f)
            quadToRelative(21f, 0f, 35.5f, -14.5f)
            reflectiveQuadTo(730f, 560f)
            quadToRelative(0f, -21f, -14.5f, -35.5f)
            reflectiveQuadTo(680f, 510f)
            quadToRelative(-21f, 0f, -35.5f, 14.5f)
            reflectiveQuadTo(630f, 560f)
            quadToRelative(0f, 21f, 14.5f, 35.5f)
            reflectiveQuadTo(680f, 610f)
            close()
        }
    }.build()
}
