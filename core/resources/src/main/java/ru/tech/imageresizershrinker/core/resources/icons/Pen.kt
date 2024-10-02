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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Pen: ImageVector by lazy {
    ImageVector.Builder(
        name = "Pen",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(490f, 433f)
            lineTo(527f, 470f)
            lineTo(744f, 253f)
            lineTo(707f, 216f)
            lineTo(490f, 433f)
            close()
            moveTo(200f, 760f)
            lineTo(237f, 760f)
            lineTo(470f, 527f)
            lineTo(433f, 490f)
            lineTo(200f, 723f)
            lineTo(200f, 760f)
            close()
            moveTo(555f, 555f)
            lineTo(405f, 405f)
            lineTo(572f, 238f)
            lineTo(543f, 209f)
            quadTo(543f, 209f, 543f, 209f)
            quadTo(543f, 209f, 543f, 209f)
            lineTo(352f, 400f)
            quadTo(340f, 412f, 324f, 412f)
            quadTo(308f, 412f, 296f, 400f)
            quadTo(284f, 388f, 284f, 371.5f)
            quadTo(284f, 355f, 296f, 343f)
            lineTo(486f, 153f)
            quadTo(510f, 129f, 542.5f, 129f)
            quadTo(575f, 129f, 599f, 153f)
            lineTo(628f, 182f)
            lineTo(678f, 132f)
            quadTo(690f, 120f, 706.5f, 120f)
            quadTo(723f, 120f, 735f, 132f)
            lineTo(828f, 225f)
            quadTo(840f, 237f, 840f, 253.5f)
            quadTo(840f, 270f, 828f, 282f)
            lineTo(555f, 555f)
            close()
            moveTo(160f, 840f)
            quadTo(143f, 840f, 131.5f, 828.5f)
            quadTo(120f, 817f, 120f, 800f)
            lineTo(120f, 723f)
            quadTo(120f, 707f, 126f, 692.5f)
            quadTo(132f, 678f, 143f, 667f)
            lineTo(405f, 405f)
            lineTo(555f, 555f)
            lineTo(293f, 817f)
            quadTo(282f, 828f, 267.5f, 834f)
            quadTo(253f, 840f, 237f, 840f)
            lineTo(160f, 840f)
            close()
        }
    }.build()
}
