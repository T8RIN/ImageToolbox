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

val Icons.Outlined.BatchPrediction: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.BatchPrediction",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 800f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 320f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 400f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 880f)
            lineTo(280f, 880f)
            close()
            moveTo(280f, 800f)
            horizontalLineToRelative(400f)
            verticalLineToRelative(-400f)
            lineTo(280f, 400f)
            verticalLineToRelative(400f)
            close()
            moveTo(460f, 760f)
            horizontalLineToRelative(40f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            horizontalLineToRelative(-40f)
            quadToRelative(-8f, 0f, -14f, 6f)
            reflectiveQuadToRelative(-6f, 14f)
            quadToRelative(0f, 8f, 6f, 14f)
            reflectiveQuadToRelative(14f, 6f)
            close()
            moveTo(453f, 680f)
            horizontalLineToRelative(54f)
            quadToRelative(5f, 0f, 9f, -4f)
            reflectiveQuadToRelative(5f, -9f)
            quadToRelative(3f, -14f, 12f, -26.5f)
            reflectiveQuadToRelative(17f, -25.5f)
            quadToRelative(12f, -17f, 21f, -36.5f)
            reflectiveQuadToRelative(9f, -40.5f)
            quadToRelative(0f, -41f, -29f, -69.5f)
            reflectiveQuadTo(480f, 440f)
            quadToRelative(-42f, 0f, -71f, 28.5f)
            reflectiveQuadTo(380f, 538f)
            quadToRelative(0f, 21f, 9.5f, 40f)
            reflectiveQuadToRelative(20.5f, 36f)
            quadToRelative(8f, 13f, 16.5f, 26f)
            reflectiveQuadToRelative(11.5f, 27f)
            quadToRelative(2f, 5f, 6f, 9f)
            reflectiveQuadToRelative(9f, 4f)
            close()
            moveTo(480f, 600f)
            close()
            moveTo(270f, 260f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            reflectiveQuadTo(240f, 230f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(270f, 200f)
            horizontalLineToRelative(420f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(720f, 230f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(690f, 260f)
            lineTo(270f, 260f)
            close()
            moveTo(310f, 140f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            reflectiveQuadTo(280f, 110f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(310f, 80f)
            horizontalLineToRelative(340f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(680f, 110f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(650f, 140f)
            lineTo(310f, 140f)
            close()
        }
    }.build()
}