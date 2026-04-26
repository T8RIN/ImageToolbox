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

val Icons.Rounded.PinEnd: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.PinEnd",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            verticalLineToRelative(200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 480f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(800f, 440f)
            verticalLineToRelative(-200f)
            lineTo(160f, 240f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(360f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(560f, 760f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(520f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(496f, 400f)
            lineTo(586f, 490f)
            quadToRelative(11f, 11f, 11f, 27.5f)
            reflectiveQuadTo(586f, 546f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(529f, 546f)
            lineToRelative(-89f, -89f)
            verticalLineToRelative(49f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 546f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(360f, 506f)
            verticalLineToRelative(-146f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(400f, 320f)
            horizontalLineToRelative(146f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(586f, 360f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(546f, 400f)
            horizontalLineToRelative(-50f)
            close()
            moveTo(760f, 800f)
            quadToRelative(-50f, 0f, -85f, -35f)
            reflectiveQuadToRelative(-35f, -85f)
            quadToRelative(0f, -50f, 35f, -85f)
            reflectiveQuadToRelative(85f, -35f)
            quadToRelative(50f, 0f, 85f, 35f)
            reflectiveQuadToRelative(35f, 85f)
            quadToRelative(0f, 50f, -35f, 85f)
            reflectiveQuadToRelative(-85f, 35f)
            close()
        }
    }.build()
}
