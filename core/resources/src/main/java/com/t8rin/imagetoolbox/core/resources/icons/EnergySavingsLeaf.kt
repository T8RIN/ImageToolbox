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

val Icons.Outlined.EnergySavingsLeaf: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EnergySavingsLeaf",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 720f)
            quadToRelative(100f, 0f, 169f, -70f)
            reflectiveQuadToRelative(71f, -170f)
            verticalLineToRelative(-240f)
            lineTo(480f, 240f)
            quadToRelative(-100f, 2f, -170f, 71f)
            reflectiveQuadToRelative(-70f, 169f)
            quadToRelative(0f, 100f, 70f, 170f)
            reflectiveQuadToRelative(170f, 70f)
            close()
            moveTo(433f, 653f)
            lineTo(617f, 489f)
            quadToRelative(9f, -8f, 5f, -19f)
            reflectiveQuadToRelative(-16f, -13f)
            lineToRelative(-144f, -14f)
            lineToRelative(86f, -119f)
            quadToRelative(3f, -5f, 3.5f, -9.5f)
            reflectiveQuadTo(548f, 306f)
            quadToRelative(-4f, -5f, -10f, -4.5f)
            reflectiveQuadToRelative(-11f, 4.5f)
            lineTo(344f, 470f)
            quadToRelative(-9f, 8f, -5f, 19f)
            reflectiveQuadToRelative(16f, 13f)
            lineToRelative(144f, 14f)
            lineToRelative(-87f, 119f)
            quadToRelative(-3f, 5f, -3f, 9.5f)
            reflectiveQuadToRelative(4f, 8.5f)
            quadToRelative(4f, 4f, 9.5f, 4f)
            reflectiveQuadToRelative(10.5f, -4f)
            close()
            moveTo(480f, 800f)
            quadToRelative(-56f, 0f, -105.5f, -17.5f)
            reflectiveQuadTo(284f, 733f)
            lineToRelative(-55f, 55f)
            quadToRelative(-6f, 6f, -13.5f, 9f)
            reflectiveQuadToRelative(-15.5f, 3f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 760f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(9f, -13.5f)
            lineToRelative(55f, -55f)
            quadToRelative(-32f, -41f, -49.5f, -90.5f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(0f, -134f, 93f, -227f)
            reflectiveQuadToRelative(227f, -93f)
            horizontalLineToRelative(240f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 240f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 134f, -93f, 227f)
            reflectiveQuadToRelative(-227f, 93f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
