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

val Icons.Outlined.SkipNext: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SkipNext",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(660f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(700f, 240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(740f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(700f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(660f, 680f)
            close()
            moveTo(220f, 645f)
            verticalLineToRelative(-330f)
            quadToRelative(0f, -18f, 12f, -29f)
            reflectiveQuadToRelative(28f, -11f)
            quadToRelative(5f, 0f, 11f, 1f)
            reflectiveQuadToRelative(11f, 5f)
            lineToRelative(248f, 166f)
            quadToRelative(9f, 6f, 13.5f, 14.5f)
            reflectiveQuadTo(548f, 480f)
            quadToRelative(0f, 10f, -4.5f, 18.5f)
            reflectiveQuadTo(530f, 513f)
            lineTo(282f, 679f)
            quadToRelative(-5f, 4f, -11f, 5f)
            reflectiveQuadToRelative(-11f, 1f)
            quadToRelative(-16f, 0f, -28f, -11f)
            reflectiveQuadToRelative(-12f, -29f)
            close()
            moveTo(300f, 480f)
            close()
            moveTo(300f, 570f)
            lineTo(436f, 480f)
            lineTo(300f, 390f)
            verticalLineToRelative(180f)
            close()
        }
    }.build()
}
