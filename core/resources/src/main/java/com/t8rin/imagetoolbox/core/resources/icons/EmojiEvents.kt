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

val Icons.Outlined.EmojiEvents: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EmojiEvents",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(440f, 760f)
            verticalLineToRelative(-124f)
            quadToRelative(-49f, -11f, -87.5f, -41.5f)
            reflectiveQuadTo(296f, 518f)
            quadToRelative(-75f, -9f, -125.5f, -65.5f)
            reflectiveQuadTo(120f, 320f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 200f)
            horizontalLineToRelative(80f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(360f, 120f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(680f, 200f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 280f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 76f, -50.5f, 132.5f)
            reflectiveQuadTo(664f, 518f)
            quadToRelative(-18f, 46f, -56.5f, 76.5f)
            reflectiveQuadTo(520f, 636f)
            verticalLineToRelative(124f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 800f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 840f)
            lineTo(320f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 800f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 760f)
            horizontalLineToRelative(120f)
            close()
            moveTo(280f, 432f)
            verticalLineToRelative(-152f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 38f, 22f, 68.5f)
            reflectiveQuadToRelative(58f, 43.5f)
            close()
            moveTo(480f, 560f)
            quadToRelative(50f, 0f, 85f, -35f)
            reflectiveQuadToRelative(35f, -85f)
            verticalLineToRelative(-240f)
            lineTo(360f, 200f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
            moveTo(680f, 432f)
            quadToRelative(36f, -13f, 58f, -43.5f)
            reflectiveQuadToRelative(22f, -68.5f)
            verticalLineToRelative(-40f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(152f)
            close()
            moveTo(480f, 380f)
            close()
        }
    }.build()
}
