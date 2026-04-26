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

val Icons.Rounded.FontDownload: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FontDownload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(384f, 598f)
            horizontalLineToRelative(192f)
            lineToRelative(35f, 97f)
            quadToRelative(4f, 11f, 14f, 18f)
            reflectiveQuadToRelative(22f, 7f)
            quadToRelative(20f, 0f, 32.5f, -16.5f)
            reflectiveQuadTo(684f, 667f)
            lineTo(532f, 265f)
            quadToRelative(-5f, -11f, -15f, -18f)
            reflectiveQuadToRelative(-22f, -7f)
            horizontalLineToRelative(-30f)
            quadToRelative(-12f, 0f, -22f, 7f)
            reflectiveQuadToRelative(-15f, 18f)
            lineTo(276f, 667f)
            quadToRelative(-8f, 19f, 4f, 36f)
            reflectiveQuadToRelative(32f, 17f)
            quadToRelative(13f, 0f, 22.5f, -7f)
            reflectiveQuadToRelative(14.5f, -19f)
            lineToRelative(35f, -96f)
            close()
            moveTo(408f, 528f)
            lineTo(478f, 330f)
            horizontalLineToRelative(4f)
            lineToRelative(70f, 198f)
            lineTo(408f, 528f)
            close()
            moveTo(160f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 800f)
            verticalLineToRelative(-640f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 80f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 160f)
            verticalLineToRelative(640f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 880f)
            lineTo(160f, 880f)
            close()
        }
    }.build()
}
