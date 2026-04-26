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

val Icons.Outlined.Brightness4: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Brightness4",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(492f, 680f)
            quadToRelative(83f, 0f, 141.5f, -58.5f)
            reflectiveQuadTo(692f, 480f)
            quadToRelative(0f, -83f, -58.5f, -141.5f)
            reflectiveQuadTo(492f, 280f)
            horizontalLineToRelative(-16.5f)
            quadToRelative(-8.5f, 0f, -16.5f, 2f)
            quadToRelative(-15f, 3f, -19f, 17.5f)
            reflectiveQuadToRelative(7f, 22.5f)
            quadToRelative(38f, 28f, 58f, 69.5f)
            reflectiveQuadToRelative(20f, 88.5f)
            quadToRelative(0f, 48f, -19f, 90.5f)
            reflectiveQuadTo(447f, 638f)
            quadToRelative(-12f, 8f, -8.5f, 22.5f)
            reflectiveQuadTo(459f, 677f)
            quadToRelative(8f, 2f, 16.5f, 2.5f)
            reflectiveQuadToRelative(16.5f, 0.5f)
            close()
            moveTo(346f, 800f)
            lineTo(240f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 720f)
            verticalLineToRelative(-106f)
            lineToRelative(-77f, -78f)
            quadToRelative(-11f, -12f, -17f, -26.5f)
            reflectiveQuadTo(60f, 480f)
            quadToRelative(0f, -15f, 6f, -29.5f)
            reflectiveQuadTo(83f, 424f)
            lineToRelative(77f, -78f)
            verticalLineToRelative(-106f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 160f)
            horizontalLineToRelative(106f)
            lineToRelative(78f, -77f)
            quadToRelative(12f, -11f, 26.5f, -17f)
            reflectiveQuadToRelative(29.5f, -6f)
            quadToRelative(15f, 0f, 29.5f, 6f)
            reflectiveQuadToRelative(26.5f, 17f)
            lineToRelative(78f, 77f)
            horizontalLineToRelative(106f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 240f)
            verticalLineToRelative(106f)
            lineToRelative(77f, 78f)
            quadToRelative(11f, 12f, 17f, 26.5f)
            reflectiveQuadToRelative(6f, 29.5f)
            quadToRelative(0f, 15f, -6f, 29.5f)
            reflectiveQuadTo(877f, 536f)
            lineToRelative(-77f, 78f)
            verticalLineToRelative(106f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 800f)
            lineTo(614f, 800f)
            lineToRelative(-78f, 77f)
            quadToRelative(-12f, 11f, -26.5f, 17f)
            reflectiveQuadTo(480f, 900f)
            quadToRelative(-15f, 0f, -29.5f, -6f)
            reflectiveQuadTo(424f, 877f)
            lineToRelative(-78f, -77f)
            close()
            moveTo(380f, 720f)
            lineTo(480f, 820f)
            lineTo(580f, 720f)
            horizontalLineToRelative(140f)
            verticalLineToRelative(-140f)
            lineToRelative(100f, -100f)
            lineToRelative(-100f, -100f)
            verticalLineToRelative(-140f)
            lineTo(580f, 240f)
            lineTo(480f, 140f)
            lineTo(380f, 240f)
            lineTo(240f, 240f)
            verticalLineToRelative(140f)
            lineTo(140f, 480f)
            lineToRelative(100f, 100f)
            verticalLineToRelative(140f)
            horizontalLineToRelative(140f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
