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

val Icons.Outlined.Scanner: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Scanner",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(704f, 480f)
            lineTo(140f, 276f)
            lineToRelative(28f, -76f)
            lineToRelative(624f, 228f)
            quadToRelative(20f, 8f, 34f, 28f)
            reflectiveQuadToRelative(14f, 44f)
            verticalLineToRelative(220f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 800f)
            lineTo(200f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 720f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 480f)
            horizontalLineToRelative(504f)
            close()
            moveTo(760f, 720f)
            verticalLineToRelative(-160f)
            lineTo(200f, 560f)
            verticalLineToRelative(160f)
            horizontalLineToRelative(560f)
            close()
            moveTo(400f, 680f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(-80f)
            lineTo(400f, 600f)
            verticalLineToRelative(80f)
            close()
            moveTo(308.5f, 668.5f)
            quadTo(320f, 657f, 320f, 640f)
            reflectiveQuadToRelative(-11.5f, -28.5f)
            quadTo(297f, 600f, 280f, 600f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(240f, 623f, 240f, 640f)
            reflectiveQuadToRelative(11.5f, 28.5f)
            quadTo(263f, 680f, 280f, 680f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            close()
            moveTo(200f, 720f)
            verticalLineToRelative(-160f)
            verticalLineToRelative(160f)
            close()
        }
    }.build()
}
