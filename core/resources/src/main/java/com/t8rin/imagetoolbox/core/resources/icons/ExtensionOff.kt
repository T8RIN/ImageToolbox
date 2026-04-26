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

val Icons.Outlined.ExtensionOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ExtensionOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-152f)
            quadToRelative(48f, 0f, 84f, -30.5f)
            reflectiveQuadToRelative(36f, -77.5f)
            quadToRelative(0f, -47f, -36f, -77.5f)
            reflectiveQuadTo(120f, 392f)
            verticalLineToRelative(-152f)
            quadToRelative(0f, -17f, 6f, -31.5f)
            reflectiveQuadToRelative(17f, -25.5f)
            lineToRelative(57f, 57f)
            verticalLineToRelative(88f)
            quadToRelative(54f, 20f, 87f, 67f)
            reflectiveQuadToRelative(33f, 105f)
            quadToRelative(0f, 57f, -33f, 104f)
            reflectiveQuadToRelative(-87f, 68f)
            verticalLineToRelative(88f)
            horizontalLineToRelative(88f)
            quadToRelative(21f, -54f, 68f, -87f)
            reflectiveQuadToRelative(104f, -33f)
            quadToRelative(57f, 0f, 104f, 33f)
            reflectiveQuadToRelative(68f, 87f)
            horizontalLineToRelative(88f)
            lineToRelative(57f, 57f)
            quadToRelative(-11f, 11f, -25.5f, 17f)
            reflectiveQuadToRelative(-31.5f, 6f)
            lineTo(568f, 840f)
            quadToRelative(0f, -48f, -30.5f, -84f)
            reflectiveQuadTo(460f, 720f)
            quadToRelative(-47f, 0f, -77.5f, 36f)
            reflectiveQuadTo(352f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(800f, 686f)
            lineTo(720f, 606f)
            verticalLineToRelative(-86f)
            horizontalLineToRelative(80f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-240f)
            lineTo(480f, 240f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            quadToRelative(-8f, 0f, -14f, 6f)
            reflectiveQuadToRelative(-6f, 14f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-86f)
            lineToRelative(-80f, -80f)
            horizontalLineToRelative(86f)
            quadToRelative(0f, -42f, 29f, -71f)
            reflectiveQuadToRelative(71f, -29f)
            quadToRelative(42f, 0f, 71f, 29f)
            reflectiveQuadToRelative(29f, 71f)
            horizontalLineToRelative(160f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 240f)
            verticalLineToRelative(160f)
            quadToRelative(42f, 0f, 71f, 29f)
            reflectiveQuadToRelative(29f, 71f)
            quadToRelative(0f, 42f, -29f, 71f)
            reflectiveQuadToRelative(-71f, 29f)
            verticalLineToRelative(86f)
            close()
            moveTo(763f, 876f)
            lineTo(83f, 197f)
            quadToRelative(-12f, -12f, -11.5f, -28.5f)
            reflectiveQuadTo(84f, 140f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            lineToRelative(679f, 680f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(763f, 876f)
            close()
            moveTo(537f, 423f)
            close()
            moveTo(460f, 500f)
            close()
        }
    }.build()
}
