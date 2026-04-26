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

val Icons.Rounded.GridOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.GridOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            horizontalLineToRelative(107f)
            verticalLineToRelative(-187f)
            lineTo(120f, 653f)
            verticalLineToRelative(107f)
            quadToRelative(0f, 33f, 23.5f, 56.5f)
            reflectiveQuadTo(200f, 840f)
            close()
            moveTo(387f, 840f)
            horizontalLineToRelative(186f)
            verticalLineToRelative(-187f)
            lineTo(387f, 653f)
            verticalLineToRelative(187f)
            close()
            moveTo(653f, 840f)
            horizontalLineToRelative(107f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(840f, 760f)
            verticalLineToRelative(-107f)
            lineTo(653f, 653f)
            verticalLineToRelative(187f)
            close()
            moveTo(120f, 573f)
            horizontalLineToRelative(187f)
            verticalLineToRelative(-186f)
            lineTo(120f, 387f)
            verticalLineToRelative(186f)
            close()
            moveTo(387f, 573f)
            horizontalLineToRelative(186f)
            verticalLineToRelative(-186f)
            lineTo(387f, 387f)
            verticalLineToRelative(186f)
            close()
            moveTo(653f, 573f)
            horizontalLineToRelative(187f)
            verticalLineToRelative(-186f)
            lineTo(653f, 387f)
            verticalLineToRelative(186f)
            close()
            moveTo(120f, 307f)
            horizontalLineToRelative(187f)
            verticalLineToRelative(-187f)
            lineTo(200f, 120f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(120f, 200f)
            verticalLineToRelative(107f)
            close()
            moveTo(387f, 307f)
            horizontalLineToRelative(186f)
            verticalLineToRelative(-187f)
            lineTo(387f, 120f)
            verticalLineToRelative(187f)
            close()
            moveTo(653f, 307f)
            horizontalLineToRelative(187f)
            verticalLineToRelative(-107f)
            quadToRelative(0f, -33f, -23.5f, -56.5f)
            reflectiveQuadTo(760f, 120f)
            lineTo(653f, 120f)
            verticalLineToRelative(187f)
            close()
        }
    }.build()
}
