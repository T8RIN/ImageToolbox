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

val Icons.Outlined.FontDownload: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FontDownload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(384f, 598f)
            lineTo(576f, 598f)
            lineTo(611f, 695f)
            quadTo(615f, 706f, 625f, 713f)
            quadTo(635f, 720f, 647f, 720f)
            quadTo(667f, 720f, 679.5f, 703.5f)
            quadTo(692f, 687f, 684f, 667f)
            lineTo(532f, 265f)
            quadTo(527f, 254f, 517f, 247f)
            quadTo(507f, 240f, 495f, 240f)
            lineTo(465f, 240f)
            quadTo(453f, 240f, 443f, 247f)
            quadTo(433f, 254f, 428f, 265f)
            lineTo(276f, 667f)
            quadTo(268f, 686f, 280f, 703f)
            quadTo(292f, 720f, 312f, 720f)
            quadTo(325f, 720f, 334.5f, 713f)
            quadTo(344f, 706f, 349f, 694f)
            lineTo(384f, 598f)
            close()
            moveTo(408f, 528f)
            lineTo(478f, 330f)
            lineTo(482f, 330f)
            lineTo(552f, 528f)
            lineTo(408f, 528f)
            close()
            moveTo(160f, 880f)
            quadTo(127f, 880f, 103.5f, 856.5f)
            quadTo(80f, 833f, 80f, 800f)
            lineTo(80f, 160f)
            quadTo(80f, 127f, 103.5f, 103.5f)
            quadTo(127f, 80f, 160f, 80f)
            lineTo(800f, 80f)
            quadTo(833f, 80f, 856.5f, 103.5f)
            quadTo(880f, 127f, 880f, 160f)
            lineTo(880f, 800f)
            quadTo(880f, 833f, 856.5f, 856.5f)
            quadTo(833f, 880f, 800f, 880f)
            lineTo(160f, 880f)
            close()
            moveTo(160f, 800f)
            lineTo(800f, 800f)
            lineTo(800f, 160f)
            lineTo(160f, 160f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 160f)
            lineTo(160f, 800f)
            lineTo(160f, 160f)
            close()
        }
    }.build()
}
