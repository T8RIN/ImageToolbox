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

val Icons.Outlined.BorderColor: ImageVector by lazy {
    ImageVector.Builder(
        name = "BorderColor",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(160f, 960f)
            quadTo(127f, 960f, 103.5f, 936.5f)
            quadTo(80f, 913f, 80f, 880f)
            quadTo(80f, 847f, 103.5f, 823.5f)
            quadTo(127f, 800f, 160f, 800f)
            lineTo(800f, 800f)
            quadTo(833f, 800f, 856.5f, 823.5f)
            quadTo(880f, 847f, 880f, 880f)
            quadTo(880f, 913f, 856.5f, 936.5f)
            quadTo(833f, 960f, 800f, 960f)
            lineTo(160f, 960f)
            close()
            moveTo(240f, 640f)
            lineTo(296f, 640f)
            lineTo(608f, 329f)
            lineTo(579f, 300f)
            lineTo(551f, 272f)
            lineTo(240f, 584f)
            lineTo(240f, 640f)
            close()
            moveTo(160f, 680f)
            lineTo(160f, 567f)
            quadTo(160f, 559f, 163f, 551.5f)
            quadTo(166f, 544f, 172f, 538f)
            lineTo(608f, 103f)
            quadTo(619f, 92f, 633.5f, 86f)
            quadTo(648f, 80f, 664f, 80f)
            quadTo(680f, 80f, 695f, 86f)
            quadTo(710f, 92f, 722f, 104f)
            lineTo(777f, 160f)
            quadTo(789f, 171f, 794.5f, 186f)
            quadTo(800f, 201f, 800f, 217f)
            quadTo(800f, 232f, 794.5f, 246.5f)
            quadTo(789f, 261f, 777f, 273f)
            lineTo(342f, 708f)
            quadTo(336f, 714f, 328.5f, 717f)
            quadTo(321f, 720f, 313f, 720f)
            lineTo(200f, 720f)
            quadTo(183f, 720f, 171.5f, 708.5f)
            quadTo(160f, 697f, 160f, 680f)
            close()
            moveTo(720f, 216f)
            lineTo(720f, 216f)
            lineTo(664f, 160f)
            lineTo(664f, 160f)
            lineTo(720f, 216f)
            close()
            moveTo(608f, 329f)
            lineTo(579f, 300f)
            lineTo(551f, 272f)
            lineTo(551f, 272f)
            lineTo(608f, 329f)
            close()
        }
    }.build()
}
