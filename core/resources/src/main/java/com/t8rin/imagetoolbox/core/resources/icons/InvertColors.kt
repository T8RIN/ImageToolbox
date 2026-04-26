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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.InvertColors: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.InvertColors",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 840f)
            quadToRelative(-133f, 0f, -226.5f, -92f)
            reflectiveQuadTo(160f, 525f)
            quadToRelative(0f, -66f, 25f, -122.5f)
            reflectiveQuadTo(254f, 302f)
            lineToRelative(184f, -181f)
            quadToRelative(9f, -8f, 20f, -12.5f)
            reflectiveQuadToRelative(22f, -4.5f)
            quadToRelative(11f, 0f, 22f, 4.5f)
            reflectiveQuadToRelative(20f, 12.5f)
            lineToRelative(184f, 181f)
            quadToRelative(44f, 44f, 69f, 100.5f)
            reflectiveQuadTo(800f, 525f)
            quadToRelative(0f, 131f, -93.5f, 223f)
            reflectiveQuadTo(480f, 840f)
            close()
            moveTo(480f, 760f)
            verticalLineToRelative(-568f)
            lineTo(310f, 360f)
            quadToRelative(-35f, 33f, -52.5f, 75f)
            reflectiveQuadTo(240f, 525f)
            quadToRelative(0f, 97f, 70f, 166f)
            reflectiveQuadToRelative(170f, 69f)
            close()
        }
    }.build()
}
