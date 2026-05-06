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

val Icons.Rounded.MobileVibrate: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MobileVibrate",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(240f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(320f, 120f)
            horizontalLineToRelative(320f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(720f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(640f, 840f)
            lineTo(320f, 840f)
            close()
            moveTo(508.5f, 308.5f)
            quadTo(520f, 297f, 520f, 280f)
            reflectiveQuadToRelative(-11.5f, -28.5f)
            quadTo(497f, 240f, 480f, 240f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(440f, 263f, 440f, 280f)
            reflectiveQuadToRelative(11.5f, 28.5f)
            quadTo(463f, 320f, 480f, 320f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            close()
            moveTo(0f, 560f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(40f, 360f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(80f, 400f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(40f, 600f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(0f, 560f)
            close()
            moveTo(120f, 640f)
            verticalLineToRelative(-320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(200f, 320f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(160f, 680f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 640f)
            close()
            moveTo(880f, 560f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(920f, 360f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(960f, 400f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(920f, 600f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(880f, 560f)
            close()
            moveTo(760f, 640f)
            verticalLineToRelative(-320f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(800f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 320f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 680f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(760f, 640f)
            close()
        }
    }.build()
}
