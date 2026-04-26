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

val Icons.Rounded.IosShare: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.IosShare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 920f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 840f)
            verticalLineToRelative(-440f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 320f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 360f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 400f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(440f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-440f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 360f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 320f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 400f)
            verticalLineToRelative(440f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 920f)
            lineTo(240f, 920f)
            close()
            moveTo(440f, 193f)
            lineTo(404f, 229f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(348f, 228f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(104f, -104f)
            quadToRelative(12f, -12f, 28f, -12f)
            reflectiveQuadToRelative(28f, 12f)
            lineToRelative(104f, 104f)
            quadToRelative(11f, 11f, 11f, 27.5f)
            reflectiveQuadTo(612f, 228f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(555f, 228f)
            lineToRelative(-35f, -35f)
            verticalLineToRelative(407f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 640f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 600f)
            verticalLineToRelative(-407f)
            close()
        }
    }.build()
}
