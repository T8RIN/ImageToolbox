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

val Icons.Outlined.UploadFile: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.UploadFile",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 593f)
            verticalLineToRelative(127f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 760f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 720f)
            verticalLineToRelative(-127f)
            lineToRelative(36f, 36f)
            quadToRelative(6f, 6f, 13.5f, 9f)
            reflectiveQuadToRelative(15f, 2.5f)
            quadToRelative(7.5f, -0.5f, 14.5f, -3.5f)
            reflectiveQuadToRelative(13f, -9f)
            quadToRelative(11f, -12f, 11.5f, -28f)
            reflectiveQuadTo(612f, 572f)
            lineTo(508f, 468f)
            quadToRelative(-6f, -6f, -13f, -8.5f)
            reflectiveQuadToRelative(-15f, -2.5f)
            quadToRelative(-8f, 0f, -15f, 2.5f)
            reflectiveQuadToRelative(-13f, 8.5f)
            lineTo(348f, 572f)
            quadToRelative(-12f, 12f, -11.5f, 28f)
            reflectiveQuadToRelative(12.5f, 28f)
            quadToRelative(12f, 11f, 28f, 11.5f)
            reflectiveQuadToRelative(28f, -11.5f)
            lineToRelative(35f, -35f)
            close()
            moveTo(240f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 800f)
            verticalLineToRelative(-640f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 80f)
            horizontalLineToRelative(287f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(194f, 194f)
            quadToRelative(11f, 11f, 17f, 25.5f)
            reflectiveQuadToRelative(6f, 30.5f)
            verticalLineToRelative(447f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 880f)
            lineTo(240f, 880f)
            close()
            moveTo(520f, 320f)
            verticalLineToRelative(-160f)
            lineTo(240f, 160f)
            verticalLineToRelative(640f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-440f)
            lineTo(560f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 320f)
            close()
            moveTo(240f, 160f)
            verticalLineToRelative(200f)
            verticalLineToRelative(-200f)
            verticalLineToRelative(640f)
            verticalLineToRelative(-640f)
            close()
        }
    }.build()
}
