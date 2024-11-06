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

val Icons.Outlined.PictureInPictureCenter: ImageVector by lazy {
    ImageVector.Builder(
        name = "PictureInPictureCenter",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(160f, 800f)
            quadTo(127f, 800f, 103.5f, 776.5f)
            quadTo(80f, 753f, 80f, 720f)
            lineTo(80f, 240f)
            quadTo(80f, 207f, 103.5f, 183.5f)
            quadTo(127f, 160f, 160f, 160f)
            lineTo(800f, 160f)
            quadTo(833f, 160f, 856.5f, 183.5f)
            quadTo(880f, 207f, 880f, 240f)
            lineTo(880f, 720f)
            quadTo(880f, 753f, 856.5f, 776.5f)
            quadTo(833f, 800f, 800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 720f)
            lineTo(800f, 720f)
            quadTo(800f, 720f, 800f, 720f)
            quadTo(800f, 720f, 800f, 720f)
            lineTo(800f, 240f)
            quadTo(800f, 240f, 800f, 240f)
            quadTo(800f, 240f, 800f, 240f)
            lineTo(160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            lineTo(160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            close()
            moveTo(320f, 600f)
            lineTo(640f, 600f)
            lineTo(640f, 360f)
            lineTo(320f, 360f)
            lineTo(320f, 600f)
            close()
            moveTo(160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            lineTo(160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            lineTo(160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            quadTo(160f, 240f, 160f, 240f)
            lineTo(160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            quadTo(160f, 720f, 160f, 720f)
            close()
        }
    }.build()
}
