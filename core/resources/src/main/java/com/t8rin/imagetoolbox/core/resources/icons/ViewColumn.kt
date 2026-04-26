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

val Icons.Outlined.ViewColumn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ViewColumn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(121f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(201f, 200f)
            horizontalLineToRelative(559f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 760f)
            lineTo(201f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(121f, 680f)
            close()
            moveTo(200f, 680f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-400f)
            lineTo(200f, 280f)
            verticalLineToRelative(400f)
            close()
            moveTo(413f, 680f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-400f)
            lineTo(413f, 280f)
            verticalLineToRelative(400f)
            close()
            moveTo(626f, 680f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-400f)
            lineTo(626f, 280f)
            verticalLineToRelative(400f)
            close()
        }
    }.build()
}

val Icons.Rounded.ViewColumn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ViewColumn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(200f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 200f)
            horizontalLineToRelative(53f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(333f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(253f, 760f)
            horizontalLineToRelative(-53f)
            close()
            moveTo(453f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(373f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(453f, 200f)
            horizontalLineToRelative(53f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(586f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(506f, 760f)
            horizontalLineToRelative(-53f)
            close()
            moveTo(706f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(626f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(706f, 200f)
            horizontalLineToRelative(53f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(839f, 280f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(759f, 760f)
            horizontalLineToRelative(-53f)
            close()
        }
    }.build()
}
