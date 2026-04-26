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

val Icons.Rounded.Cloud: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Cloud",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(260f, 800f)
            quadToRelative(-91f, 0f, -155.5f, -63f)
            reflectiveQuadTo(40f, 583f)
            quadToRelative(0f, -78f, 47f, -139f)
            reflectiveQuadToRelative(123f, -78f)
            quadToRelative(25f, -92f, 100f, -149f)
            reflectiveQuadToRelative(170f, -57f)
            quadToRelative(117f, 0f, 198.5f, 81.5f)
            reflectiveQuadTo(760f, 440f)
            quadToRelative(69f, 8f, 114.5f, 59.5f)
            reflectiveQuadTo(920f, 620f)
            quadToRelative(0f, 75f, -52.5f, 127.5f)
            reflectiveQuadTo(740f, 800f)
            lineTo(260f, 800f)
            close()
        }
    }.build()
}
