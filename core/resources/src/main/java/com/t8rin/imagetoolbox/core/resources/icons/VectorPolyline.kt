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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.VectorPolyline: ImageVector by lazy {
    ImageVector.Builder(
        name = "VectorPolyline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(600f, 880f)
            lineTo(600f, 780f)
            lineTo(320f, 640f)
            lineTo(120f, 640f)
            lineTo(120f, 400f)
            lineTo(292f, 400f)
            lineTo(400f, 276f)
            lineTo(400f, 80f)
            lineTo(640f, 80f)
            lineTo(640f, 320f)
            lineTo(468f, 320f)
            lineTo(360f, 444f)
            lineTo(360f, 570f)
            lineTo(600f, 690f)
            lineTo(600f, 640f)
            lineTo(840f, 640f)
            lineTo(840f, 880f)
            lineTo(600f, 880f)
            close()
            moveTo(480f, 240f)
            lineTo(560f, 240f)
            lineTo(560f, 160f)
            lineTo(480f, 160f)
            lineTo(480f, 240f)
            close()
            moveTo(200f, 560f)
            lineTo(280f, 560f)
            lineTo(280f, 480f)
            lineTo(200f, 480f)
            lineTo(200f, 560f)
            close()
            moveTo(680f, 800f)
            lineTo(760f, 800f)
            lineTo(760f, 720f)
            lineTo(680f, 720f)
            lineTo(680f, 800f)
            close()
            moveTo(520f, 200f)
            lineTo(520f, 200f)
            lineTo(520f, 200f)
            lineTo(520f, 200f)
            close()
            moveTo(240f, 520f)
            lineTo(240f, 520f)
            lineTo(240f, 520f)
            lineTo(240f, 520f)
            close()
            moveTo(720f, 760f)
            lineTo(720f, 760f)
            lineTo(720f, 760f)
            lineTo(720f, 760f)
            close()
        }
    }.build()
}