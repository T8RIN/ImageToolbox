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

val Icons.Rounded.FilterHdr: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FilterHdr",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(88f, 656f)
            lineToRelative(160f, -213f)
            quadToRelative(6f, -8f, 14.5f, -12f)
            reflectiveQuadToRelative(17.5f, -4f)
            quadToRelative(9f, 0f, 17.5f, 4f)
            reflectiveQuadToRelative(14.5f, 12f)
            lineToRelative(136f, 181f)
            quadToRelative(6f, 8f, 14f, 12f)
            reflectiveQuadToRelative(18f, 4f)
            quadToRelative(25f, 0f, 36f, -22.5f)
            reflectiveQuadToRelative(-4f, -42.5f)
            lineToRelative(-84f, -111f)
            quadToRelative(-8f, -11f, -8f, -24f)
            reflectiveQuadToRelative(8f, -24f)
            lineToRelative(100f, -133f)
            quadToRelative(6f, -8f, 14.5f, -12f)
            reflectiveQuadToRelative(17.5f, -4f)
            quadToRelative(9f, 0f, 17.5f, 4f)
            reflectiveQuadToRelative(14.5f, 12f)
            lineToRelative(280f, 373f)
            quadToRelative(15f, 20f, 4f, 42f)
            reflectiveQuadToRelative(-36f, 22f)
            lineTo(120f, 720f)
            quadToRelative(-25f, 0f, -36f, -22f)
            reflectiveQuadToRelative(4f, -42f)
            close()
        }
    }.build()
}
