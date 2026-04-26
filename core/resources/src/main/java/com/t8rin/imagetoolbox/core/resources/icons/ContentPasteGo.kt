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

val Icons.Rounded.ContentPasteGo: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ContentPasteGo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(727f, 720f)
            lineTo(520f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 640f)
            horizontalLineToRelative(207f)
            lineToRelative(-36f, -36f)
            quadToRelative(-11f, -11f, -11f, -27.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(104f, 104f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            lineTo(748f, 812f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(692f, 811f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(35f, -35f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(167f)
            quadToRelative(11f, -35f, 43f, -57.5f)
            reflectiveQuadToRelative(70f, -22.5f)
            quadToRelative(40f, 0f, 71.5f, 22.5f)
            reflectiveQuadTo(594f, 120f)
            horizontalLineToRelative(166f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(760f, 400f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 320f)
            lineTo(320f, 320f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 280f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 800f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(360f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(480f, 200f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 160f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 160f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 200f)
            close()
        }
    }.build()
}
