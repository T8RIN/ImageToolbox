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

val Icons.Outlined.PushPin: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.PushPin",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(640f, 200f)
            verticalLineToRelative(280f)
            lineToRelative(68f, 68f)
            quadToRelative(6f, 6f, 9f, 13.5f)
            reflectiveQuadToRelative(3f, 15.5f)
            verticalLineToRelative(23f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(680f, 640f)
            lineTo(520f, 640f)
            verticalLineToRelative(234f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 914f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 874f)
            verticalLineToRelative(-234f)
            lineTo(280f, 640f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(240f, 600f)
            verticalLineToRelative(-23f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(9f, -13.5f)
            lineToRelative(68f, -68f)
            verticalLineToRelative(-280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 120f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 200f)
            close()
            moveTo(354f, 560f)
            horizontalLineToRelative(252f)
            lineToRelative(-46f, -46f)
            verticalLineToRelative(-314f)
            lineTo(400f, 200f)
            verticalLineToRelative(314f)
            lineToRelative(-46f, 46f)
            close()
            moveTo(480f, 560f)
            close()
        }
    }.build()
}

val Icons.Rounded.PushPin: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.PushPin",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(640f, 200f)
            verticalLineToRelative(280f)
            lineToRelative(68f, 68f)
            quadToRelative(6f, 6f, 9f, 13.5f)
            reflectiveQuadToRelative(3f, 15.5f)
            verticalLineToRelative(23f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(680f, 640f)
            lineTo(520f, 640f)
            verticalLineToRelative(234f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 914f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 874f)
            verticalLineToRelative(-234f)
            lineTo(280f, 640f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(240f, 600f)
            verticalLineToRelative(-23f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(9f, -13.5f)
            lineToRelative(68f, -68f)
            verticalLineToRelative(-280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 120f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 200f)
            close()
        }
    }.build()
}