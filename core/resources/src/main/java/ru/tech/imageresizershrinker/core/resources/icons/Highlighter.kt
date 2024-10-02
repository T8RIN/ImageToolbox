/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Highlighter: ImageVector by lazy {
    Builder(
        name = "Highlighter", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(544f, 560f)
            lineTo(440f, 456f)
            lineTo(240f, 656f)
            quadTo(240f, 656f, 240f, 656f)
            quadTo(240f, 656f, 240f, 656f)
            lineTo(344f, 760f)
            quadTo(344f, 760f, 344f, 760f)
            quadTo(344f, 760f, 344f, 760f)
            lineTo(544f, 560f)
            close()
            moveTo(497f, 399f)
            lineTo(601f, 503f)
            lineTo(800f, 304f)
            quadTo(800f, 304f, 800f, 304f)
            quadTo(800f, 304f, 800f, 304f)
            lineTo(696f, 200f)
            quadTo(696f, 200f, 696f, 200f)
            quadTo(696f, 200f, 696f, 200f)
            lineTo(497f, 399f)
            close()
            moveTo(413f, 371f)
            lineTo(629f, 587f)
            lineTo(400f, 816f)
            quadTo(376f, 840f, 344f, 840f)
            quadTo(312f, 840f, 288f, 816f)
            lineTo(286f, 814f)
            lineTo(283f, 817f)
            quadTo(272f, 828f, 257.5f, 834f)
            quadTo(243f, 840f, 227f, 840f)
            lineTo(108f, 840f)
            quadTo(94f, 840f, 89f, 828f)
            quadTo(84f, 816f, 94f, 806f)
            lineTo(186f, 714f)
            lineTo(184f, 712f)
            quadTo(160f, 688f, 160f, 656f)
            quadTo(160f, 624f, 184f, 600f)
            lineTo(413f, 371f)
            close()
            moveTo(413f, 371f)
            lineTo(640f, 144f)
            quadTo(664f, 120f, 696f, 120f)
            quadTo(728f, 120f, 752f, 144f)
            lineTo(856f, 248f)
            quadTo(880f, 272f, 880f, 304f)
            quadTo(880f, 336f, 856f, 360f)
            lineTo(629f, 587f)
            lineTo(413f, 371f)
            close()
        }
    }.build()
}