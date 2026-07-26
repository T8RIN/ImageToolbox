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

val Icons.Outlined.DataObject: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DataObject",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(600f, 800f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(560f, 760f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(600f, 720f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(720f, 680f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -38f, 22f, -69f)
            reflectiveQuadToRelative(58f, -44f)
            verticalLineToRelative(-14f)
            quadToRelative(-36f, -13f, -58f, -44f)
            reflectiveQuadToRelative(-22f, -69f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(680f, 240f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(560f, 200f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(600f, 160f)
            horizontalLineToRelative(80f)
            quadToRelative(50f, 0f, 85f, 35f)
            reflectiveQuadToRelative(35f, 85f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(840f, 400f)
            reflectiveQuadToRelative(28.5f, 11.5f)
            quadTo(880f, 423f, 880f, 440f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 560f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(800f, 583f, 800f, 600f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 50f, -35f, 85f)
            reflectiveQuadToRelative(-85f, 35f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(280f, 800f)
            quadToRelative(-50f, 0f, -85f, -35f)
            reflectiveQuadToRelative(-35f, -85f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(120f, 560f)
            reflectiveQuadToRelative(-28.5f, -11.5f)
            quadTo(80f, 537f, 80f, 520f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 400f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            quadTo(160f, 377f, 160f, 360f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -50f, 35f, -85f)
            reflectiveQuadToRelative(85f, -35f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(360f, 240f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(240f, 280f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 38f, -22f, 69f)
            reflectiveQuadToRelative(-58f, 44f)
            verticalLineToRelative(14f)
            quadToRelative(36f, 13f, 58f, 44f)
            reflectiveQuadToRelative(22f, 69f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(280f, 720f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 760f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(360f, 800f)
            horizontalLineToRelative(-80f)
            close()
        }
    }.build()
}
