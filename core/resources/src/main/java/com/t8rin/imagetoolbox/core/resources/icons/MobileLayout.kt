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

val Icons.Rounded.MobileLayout: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MobileLayout",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(40f, 760f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(120f, 600f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(440f, 680f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(360f, 840f)
            lineTo(120f, 840f)
            close()
            moveTo(600f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(520f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(600f, 120f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(920f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(840f, 840f)
            lineTo(600f, 840f)
            close()
            moveTo(720f, 720f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(760f, 680f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(720f, 640f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(680f, 680f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(720f, 720f)
            close()
            moveTo(120f, 520f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(40f, 440f)
            verticalLineToRelative(-240f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(120f, 120f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(440f, 200f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(360f, 520f)
            lineTo(120f, 520f)
            close()
            moveTo(280f, 320f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(320f, 280f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(280f, 240f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(240f, 280f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(280f, 320f)
            close()
            moveTo(150f, 440f)
            horizontalLineToRelative(100f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineToRelative(-50f, -67f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-50f, 67f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
        }
    }.build()
}