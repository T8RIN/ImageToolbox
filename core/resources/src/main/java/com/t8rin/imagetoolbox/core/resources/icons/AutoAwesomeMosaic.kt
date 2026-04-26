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

val Icons.Outlined.AutoAwesomeMosaic: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.AutoAwesomeMosaic",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 840f)
            lineTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(720f)
            close()
            moveTo(360f, 760f)
            verticalLineToRelative(-560f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(160f)
            close()
            moveTo(520f, 440f)
            verticalLineToRelative(-320f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(240f)
            lineTo(520f, 440f)
            close()
            moveTo(600f, 360f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-160f)
            lineTo(600f, 200f)
            verticalLineToRelative(160f)
            close()
            moveTo(520f, 840f)
            verticalLineToRelative(-320f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(520f, 840f)
            close()
            moveTo(600f, 760f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-160f)
            lineTo(600f, 600f)
            verticalLineToRelative(160f)
            close()
            moveTo(360f, 480f)
            close()
            moveTo(600f, 360f)
            close()
            moveTo(600f, 600f)
            close()
        }
    }.build()
}
