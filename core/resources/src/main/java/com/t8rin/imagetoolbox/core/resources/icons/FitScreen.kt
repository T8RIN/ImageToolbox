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

val Icons.Outlined.FitScreen: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FitScreen",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(800f, 320f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(680f, 200f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(720f, 160f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(800f, 320f)
            close()
            moveTo(80f, 320f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 240f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(120f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 320f)
            close()
            moveTo(800f, 800f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(680f, 760f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(720f, 720f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(840f, 600f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 640f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            close()
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 600f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(160f, 640f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(280f, 760f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 800f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(240f, 560f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(320f, 320f)
            horizontalLineToRelative(320f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(720f, 400f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(640f, 640f)
            lineTo(320f, 640f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(240f, 560f)
            close()
            moveTo(320f, 560f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(-160f)
            lineTo(320f, 400f)
            verticalLineToRelative(160f)
            close()
            moveTo(320f, 560f)
            verticalLineToRelative(-160f)
            verticalLineToRelative(160f)
            close()
        }
    }.build()
}

val Icons.Rounded.FitScreen: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FitScreen",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 8f)
            lineTo(20f, 6f)
            lineTo(18f, 6f)
            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
            quadTo(17f, 5.43f, 17f, 5f)
            reflectiveQuadToRelative(0.29f, -0.71f)
            reflectiveQuadTo(18f, 4f)
            horizontalLineToRelative(2f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(22f, 5.18f, 22f, 6f)
            lineTo(22f, 8f)
            quadToRelative(0f, 0.42f, -0.29f, 0.71f)
            reflectiveQuadTo(21f, 9f)
            reflectiveQuadTo(20.29f, 8.71f)
            reflectiveQuadTo(20f, 8f)
            close()
            moveTo(2f, 8f)
            lineTo(2f, 6f)
            quadTo(2f, 5.18f, 2.59f, 4.59f)
            reflectiveQuadTo(4f, 4f)
            lineTo(6f, 4f)
            quadTo(6.43f, 4f, 6.71f, 4.29f)
            reflectiveQuadTo(7f, 5f)
            reflectiveQuadTo(6.71f, 5.71f)
            reflectiveQuadTo(6f, 6f)
            lineTo(4f, 6f)
            lineTo(4f, 8f)
            quadTo(4f, 8.42f, 3.71f, 8.71f)
            reflectiveQuadTo(3f, 9f)
            reflectiveQuadTo(2.29f, 8.71f)
            reflectiveQuadTo(2f, 8f)
            close()
            moveTo(20f, 20f)
            lineTo(18f, 20f)
            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
            quadTo(17f, 19.43f, 17f, 19f)
            reflectiveQuadToRelative(0.29f, -0.71f)
            reflectiveQuadTo(18f, 18f)
            horizontalLineToRelative(2f)
            lineTo(20f, 16f)
            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
            reflectiveQuadTo(21f, 15f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(22f, 16f)
            verticalLineToRelative(2f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 20f)
            close()
            moveTo(4f, 20f)
            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
            reflectiveQuadTo(2f, 18f)
            lineTo(2f, 16f)
            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
            reflectiveQuadTo(3f, 15f)
            reflectiveQuadToRelative(0.71f, 0.29f)
            reflectiveQuadTo(4f, 16f)
            verticalLineToRelative(2f)
            lineTo(6f, 18f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(7f, 19f)
            reflectiveQuadTo(6.71f, 19.71f)
            reflectiveQuadTo(6f, 20f)
            lineTo(4f, 20f)
            close()
            moveTo(6f, 14f)
            lineTo(6f, 10f)
            quadTo(6f, 9.17f, 6.59f, 8.59f)
            reflectiveQuadTo(8f, 8f)
            horizontalLineToRelative(8f)
            quadToRelative(0.82f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(18f, 10f)
            verticalLineToRelative(4f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(16f, 16f)
            lineTo(8f, 16f)
            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
            reflectiveQuadTo(6f, 14f)
            close()
        }
    }.build()
}