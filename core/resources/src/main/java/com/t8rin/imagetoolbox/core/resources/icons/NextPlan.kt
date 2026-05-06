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

val Icons.Outlined.NextPlan: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.NextPlan",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(584f, 480f)
            horizontalLineToRelative(-24f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(520f, 520f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(560f, 560f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(720f, 520f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(680f, 360f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(640f, 400f)
            verticalLineToRelative(22f)
            quadToRelative(-32f, -38f, -76.5f, -60f)
            reflectiveQuadTo(466f, 340f)
            quadToRelative(-83f, 0f, -143.5f, 49.5f)
            reflectiveQuadTo(245f, 514f)
            quadToRelative(-4f, 18f, 7f, 32f)
            reflectiveQuadToRelative(28f, 14f)
            quadToRelative(17f, 0f, 29.5f, -12.5f)
            reflectiveQuadTo(327f, 518f)
            quadToRelative(14f, -43f, 52f, -70.5f)
            reflectiveQuadToRelative(87f, -27.5f)
            quadToRelative(36f, 0f, 67f, 16.5f)
            reflectiveQuadToRelative(51f, 43.5f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 800f)
            quadToRelative(134f, 0f, 227f, -93f)
            reflectiveQuadToRelative(93f, -227f)
            quadToRelative(0f, -134f, -93f, -227f)
            reflectiveQuadToRelative(-227f, -93f)
            quadToRelative(-134f, 0f, -227f, 93f)
            reflectiveQuadToRelative(-93f, 227f)
            quadToRelative(0f, 134f, 93f, 227f)
            reflectiveQuadToRelative(227f, 93f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
