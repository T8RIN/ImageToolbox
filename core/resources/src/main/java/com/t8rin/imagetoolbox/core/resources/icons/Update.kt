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

val Icons.Rounded.Update: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Update",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 840f)
            quadToRelative(-75f, 0f, -140.5f, -28.5f)
            reflectiveQuadToRelative(-114f, -77f)
            quadToRelative(-48.5f, -48.5f, -77f, -114f)
            reflectiveQuadTo(120f, 480f)
            quadToRelative(0f, -75f, 28.5f, -140.5f)
            reflectiveQuadToRelative(77f, -114f)
            quadToRelative(48.5f, -48.5f, 114f, -77f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(82f, 0f, 155.5f, 35f)
            reflectiveQuadTo(760f, 254f)
            verticalLineToRelative(-54f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(800f, 160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 400f)
            lineTo(640f, 400f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 360f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 320f)
            horizontalLineToRelative(70f)
            quadToRelative(-41f, -56f, -101f, -88f)
            reflectiveQuadToRelative(-129f, -32f)
            quadToRelative(-117f, 0f, -198.5f, 81.5f)
            reflectiveQuadTo(200f, 480f)
            quadToRelative(0f, 117f, 81.5f, 198.5f)
            reflectiveQuadTo(480f, 760f)
            quadToRelative(95f, 0f, 170f, -57f)
            reflectiveQuadToRelative(99f, -147f)
            quadToRelative(5f, -16f, 18f, -24f)
            reflectiveQuadToRelative(29f, -6f)
            quadToRelative(17f, 2f, 27f, 14.5f)
            reflectiveQuadToRelative(6f, 27.5f)
            quadToRelative(-29f, 119f, -126f, 195.5f)
            reflectiveQuadTo(480f, 840f)
            close()
            moveTo(520f, 464f)
            lineTo(620f, 564f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(452f, 508f)
            quadToRelative(-6f, -6f, -9f, -13.5f)
            reflectiveQuadToRelative(-3f, -15.5f)
            verticalLineToRelative(-159f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 320f)
            verticalLineToRelative(144f)
            close()
        }
    }.build()
}
