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

val Icons.AutoMirrored.Rounded.ArrowForwardIos: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "AutoMirrored.Rounded.ArrowForwardIos",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(579f, 480f)
            lineTo(285f, 186f)
            quadToRelative(-15f, -15f, -14.5f, -35.5f)
            reflectiveQuadTo(286f, 115f)
            quadToRelative(15f, -15f, 35.5f, -15f)
            reflectiveQuadToRelative(35.5f, 15f)
            lineToRelative(307f, 308f)
            quadToRelative(12f, 12f, 18f, 27f)
            reflectiveQuadToRelative(6f, 30f)
            quadToRelative(0f, 15f, -6f, 30f)
            reflectiveQuadToRelative(-18f, 27f)
            lineTo(356f, 845f)
            quadToRelative(-15f, 15f, -35f, 14.5f)
            reflectiveQuadTo(286f, 844f)
            quadToRelative(-15f, -15f, -15f, -35.5f)
            reflectiveQuadToRelative(15f, -35.5f)
            lineToRelative(293f, -293f)
            close()
        }
    }.build()
}
