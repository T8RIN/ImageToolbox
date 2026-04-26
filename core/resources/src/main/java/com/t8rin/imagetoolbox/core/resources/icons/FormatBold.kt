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

val Icons.Rounded.FormatBold: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FormatBold",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(352f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(272f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(352f, 200f)
            horizontalLineToRelative(141f)
            quadToRelative(65f, 0f, 120f, 40f)
            reflectiveQuadToRelative(55f, 111f)
            quadToRelative(0f, 51f, -23f, 78.5f)
            reflectiveQuadTo(602f, 469f)
            quadToRelative(25f, 11f, 55.5f, 41f)
            reflectiveQuadToRelative(30.5f, 90f)
            quadToRelative(0f, 89f, -65f, 124.5f)
            reflectiveQuadTo(501f, 760f)
            lineTo(352f, 760f)
            close()
            moveTo(393f, 648f)
            horizontalLineToRelative(104f)
            quadToRelative(48f, 0f, 58.5f, -24.5f)
            reflectiveQuadTo(566f, 588f)
            quadToRelative(0f, -11f, -10.5f, -35.5f)
            reflectiveQuadTo(494f, 528f)
            lineTo(393f, 528f)
            verticalLineToRelative(120f)
            close()
            moveTo(393f, 420f)
            horizontalLineToRelative(93f)
            quadToRelative(33f, 0f, 48f, -17f)
            reflectiveQuadToRelative(15f, -38f)
            quadToRelative(0f, -24f, -17f, -39f)
            reflectiveQuadToRelative(-44f, -15f)
            horizontalLineToRelative(-95f)
            verticalLineToRelative(109f)
            close()
        }
    }.build()
}
