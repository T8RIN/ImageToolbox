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

val Icons.Rounded.Mop: ImageVector by lazy {
    ImageVector.Builder(
        name = "Mop",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(142f, 840f)
            lineTo(240f, 840f)
            lineTo(240f, 760f)
            quadTo(240f, 743f, 251.5f, 731.5f)
            quadTo(263f, 720f, 280f, 720f)
            quadTo(297f, 720f, 308.5f, 731.5f)
            quadTo(320f, 743f, 320f, 760f)
            lineTo(320f, 840f)
            lineTo(440f, 840f)
            lineTo(440f, 760f)
            quadTo(440f, 743f, 451.5f, 731.5f)
            quadTo(463f, 720f, 480f, 720f)
            quadTo(497f, 720f, 508.5f, 731.5f)
            quadTo(520f, 743f, 520f, 760f)
            lineTo(520f, 840f)
            lineTo(640f, 840f)
            lineTo(640f, 760f)
            quadTo(640f, 743f, 651.5f, 731.5f)
            quadTo(663f, 720f, 680f, 720f)
            quadTo(697f, 720f, 708.5f, 731.5f)
            quadTo(720f, 743f, 720f, 760f)
            lineTo(720f, 840f)
            lineTo(818f, 840f)
            quadTo(818f, 840f, 818f, 840f)
            quadTo(818f, 840f, 818f, 840f)
            lineTo(778f, 680f)
            lineTo(182f, 680f)
            lineTo(142f, 840f)
            quadTo(142f, 840f, 142f, 840f)
            quadTo(142f, 840f, 142f, 840f)
            close()
            moveTo(818f, 920f)
            lineTo(142f, 920f)
            quadTo(103f, 920f, 79f, 889f)
            quadTo(55f, 858f, 65f, 820f)
            lineTo(120f, 600f)
            lineTo(120f, 520f)
            quadTo(120f, 487f, 143.5f, 463.5f)
            quadTo(167f, 440f, 200f, 440f)
            lineTo(360f, 440f)
            lineTo(360f, 160f)
            quadTo(360f, 110f, 395f, 75f)
            quadTo(430f, 40f, 480f, 40f)
            quadTo(530f, 40f, 565f, 75f)
            quadTo(600f, 110f, 600f, 160f)
            lineTo(600f, 440f)
            lineTo(760f, 440f)
            quadTo(793f, 440f, 816.5f, 463.5f)
            quadTo(840f, 487f, 840f, 520f)
            lineTo(840f, 600f)
            lineTo(895f, 820f)
            quadTo(908f, 858f, 883.5f, 889f)
            quadTo(859f, 920f, 818f, 920f)
            close()
        }
    }.build()
}
