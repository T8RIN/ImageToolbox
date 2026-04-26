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

val Icons.Rounded.BookmarkRemove: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.BookmarkRemove",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(640f, 280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 200f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 280f)
            lineTo(640f, 280f)
            close()
            moveTo(480f, 720f)
            lineToRelative(-168f, 72f)
            quadToRelative(-40f, 17f, -76f, -6.5f)
            reflectiveQuadTo(200f, 719f)
            verticalLineToRelative(-519f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 120f)
            horizontalLineToRelative(225f)
            quadToRelative(18f, 0f, 27f, 16f)
            reflectiveQuadToRelative(1f, 33f)
            quadToRelative(-7f, 17f, -10f, 34f)
            reflectiveQuadToRelative(-3f, 37f)
            quadToRelative(0f, 72f, 45.5f, 127f)
            reflectiveQuadTo(680f, 436f)
            quadToRelative(12f, 2f, 21.5f, 2.5f)
            reflectiveQuadToRelative(18.5f, 0.5f)
            quadToRelative(17f, 0f, 28.5f, 10.5f)
            reflectiveQuadTo(760f, 476f)
            verticalLineToRelative(243f)
            quadToRelative(0f, 43f, -36f, 66.5f)
            reflectiveQuadToRelative(-76f, 6.5f)
            lineToRelative(-168f, -72f)
            close()
        }
    }.build()
}