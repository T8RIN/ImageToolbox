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

val Icons.Outlined.ArtTrack: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ArtTrack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(40f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(120f, 200f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(600f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(520f, 760f)
            lineTo(120f, 760f)
            close()
            moveTo(120f, 680f)
            horizontalLineToRelative(400f)
            verticalLineToRelative(-400f)
            lineTo(120f, 280f)
            verticalLineToRelative(400f)
            close()
            moveTo(160f, 600f)
            horizontalLineToRelative(320f)
            lineTo(376f, 460f)
            lineToRelative(-76f, 100f)
            lineToRelative(-56f, -74f)
            lineToRelative(-84f, 114f)
            close()
            moveTo(680f, 760f)
            verticalLineToRelative(-560f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(840f, 760f)
            verticalLineToRelative(-560f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(120f, 680f)
            verticalLineToRelative(-400f)
            verticalLineToRelative(400f)
            close()
        }
    }.build()
}
