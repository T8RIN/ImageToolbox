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

val Icons.Rounded.WrapText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.WrapText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 500f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 460f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 420f)
            horizontalLineToRelative(490f)
            quadToRelative(63f, 0f, 106.5f, 43.5f)
            reflectiveQuadTo(840f, 570f)
            quadToRelative(0f, 63f, -43.5f, 106.5f)
            reflectiveQuadTo(690f, 720f)
            horizontalLineToRelative(-96f)
            lineToRelative(22f, 22f)
            quadToRelative(12f, 12f, 11.5f, 28f)
            reflectiveQuadTo(616f, 798f)
            quadToRelative(-12f, 12f, -28.5f, 12.5f)
            reflectiveQuadTo(559f, 799f)
            lineToRelative(-91f, -91f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(91f, -91f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            quadToRelative(11f, 12f, 11.5f, 28.5f)
            reflectiveQuadTo(616f, 618f)
            lineToRelative(-22f, 22f)
            horizontalLineToRelative(96f)
            quadToRelative(29f, 0f, 49.5f, -20.5f)
            reflectiveQuadTo(760f, 570f)
            quadToRelative(0f, -29f, -20.5f, -49.5f)
            reflectiveQuadTo(690f, 500f)
            lineTo(200f, 500f)
            close()
            moveTo(200f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 640f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 680f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 720f)
            lineTo(200f, 720f)
            close()
            moveTo(200f, 280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 200f)
            horizontalLineToRelative(560f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 280f)
            lineTo(200f, 280f)
            close()
        }
    }.build()
}
