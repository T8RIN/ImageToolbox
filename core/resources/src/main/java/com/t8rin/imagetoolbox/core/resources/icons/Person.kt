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

val Icons.Outlined.Person: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Person",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(367f, 433f)
            quadToRelative(-47f, -47f, -47f, -113f)
            reflectiveQuadToRelative(47f, -113f)
            quadToRelative(47f, -47f, 113f, -47f)
            reflectiveQuadToRelative(113f, 47f)
            quadToRelative(47f, 47f, 47f, 113f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(-47f, 47f, -113f, 47f)
            reflectiveQuadToRelative(-113f, -47f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-32f)
            quadToRelative(0f, -34f, 17.5f, -62.5f)
            reflectiveQuadTo(224f, 582f)
            quadToRelative(62f, -31f, 126f, -46.5f)
            reflectiveQuadTo(480f, 520f)
            quadToRelative(66f, 0f, 130f, 15.5f)
            reflectiveQuadTo(736f, 582f)
            quadToRelative(29f, 15f, 46.5f, 43.5f)
            reflectiveQuadTo(800f, 688f)
            verticalLineToRelative(32f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 800f)
            lineTo(240f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 720f)
            close()
            moveTo(240f, 720f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-32f)
            quadToRelative(0f, -11f, -5.5f, -20f)
            reflectiveQuadTo(700f, 654f)
            quadToRelative(-54f, -27f, -109f, -40.5f)
            reflectiveQuadTo(480f, 600f)
            quadToRelative(-56f, 0f, -111f, 13.5f)
            reflectiveQuadTo(260f, 654f)
            quadToRelative(-9f, 5f, -14.5f, 14f)
            reflectiveQuadToRelative(-5.5f, 20f)
            verticalLineToRelative(32f)
            close()
            moveTo(536.5f, 376.5f)
            quadTo(560f, 353f, 560f, 320f)
            reflectiveQuadToRelative(-23.5f, -56.5f)
            quadTo(513f, 240f, 480f, 240f)
            reflectiveQuadToRelative(-56.5f, 23.5f)
            quadTo(400f, 287f, 400f, 320f)
            reflectiveQuadToRelative(23.5f, 56.5f)
            quadTo(447f, 400f, 480f, 400f)
            reflectiveQuadToRelative(56.5f, -23.5f)
            close()
            moveTo(480f, 320f)
            close()
            moveTo(480f, 720f)
            close()
        }
    }.build()
}

val Icons.Rounded.Person: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Person",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(367f, 433f)
            quadToRelative(-47f, -47f, -47f, -113f)
            reflectiveQuadToRelative(47f, -113f)
            quadToRelative(47f, -47f, 113f, -47f)
            reflectiveQuadToRelative(113f, 47f)
            quadToRelative(47f, 47f, 47f, 113f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(-47f, 47f, -113f, 47f)
            reflectiveQuadToRelative(-113f, -47f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-32f)
            quadToRelative(0f, -34f, 17.5f, -62.5f)
            reflectiveQuadTo(224f, 582f)
            quadToRelative(62f, -31f, 126f, -46.5f)
            reflectiveQuadTo(480f, 520f)
            quadToRelative(66f, 0f, 130f, 15.5f)
            reflectiveQuadTo(736f, 582f)
            quadToRelative(29f, 15f, 46.5f, 43.5f)
            reflectiveQuadTo(800f, 688f)
            verticalLineToRelative(32f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 800f)
            lineTo(240f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 720f)
            close()
        }
    }.build()
}