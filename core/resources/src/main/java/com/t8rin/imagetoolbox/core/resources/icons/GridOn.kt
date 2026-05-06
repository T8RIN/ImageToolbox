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


val Icons.Outlined.GridOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.GridOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(200f, 760f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(200f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(413f, 760f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-133f)
            lineTo(413f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(627f, 760f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(627f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(200f, 547f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-134f)
            lineTo(200f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(413f, 547f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-134f)
            lineTo(413f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(627f, 547f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-134f)
            lineTo(627f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(200f, 333f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(200f, 200f)
            verticalLineToRelative(133f)
            close()
            moveTo(413f, 333f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-133f)
            lineTo(413f, 200f)
            verticalLineToRelative(133f)
            close()
            moveTo(627f, 333f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(627f, 200f)
            verticalLineToRelative(133f)
            close()
        }
    }.build()
}