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

val Icons.Rounded.DownloadDone: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.DownloadDone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(382f, 526f)
            lineToRelative(338f, -338f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            quadToRelative(12f, 12f, 12f, 28.5f)
            reflectiveQuadTo(777f, 245f)
            lineTo(410f, 612f)
            quadToRelative(-12f, 12f, -28f, 12f)
            reflectiveQuadToRelative(-28f, -12f)
            lineTo(183f, 441f)
            quadToRelative(-12f, -12f, -11.5f, -28.5f)
            reflectiveQuadTo(184f, 384f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            lineToRelative(141f, 142f)
            close()
            moveTo(240f, 800f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 760f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 720f)
            horizontalLineToRelative(480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 760f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 800f)
            lineTo(240f, 800f)
            close()
        }
    }.build()
}
