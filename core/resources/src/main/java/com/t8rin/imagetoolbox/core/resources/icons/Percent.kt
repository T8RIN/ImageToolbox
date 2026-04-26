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

val Icons.Rounded.Percent: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Percent",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(300f, 440f)
            quadToRelative(-58f, 0f, -99f, -41f)
            reflectiveQuadToRelative(-41f, -99f)
            quadToRelative(0f, -58f, 41f, -99f)
            reflectiveQuadToRelative(99f, -41f)
            quadToRelative(58f, 0f, 99f, 41f)
            reflectiveQuadToRelative(41f, 99f)
            quadToRelative(0f, 58f, -41f, 99f)
            reflectiveQuadToRelative(-99f, 41f)
            close()
            moveTo(300f, 360f)
            quadToRelative(25f, 0f, 42.5f, -17.5f)
            reflectiveQuadTo(360f, 300f)
            quadToRelative(0f, -25f, -17.5f, -42.5f)
            reflectiveQuadTo(300f, 240f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(240f, 300f)
            quadToRelative(0f, 25f, 17.5f, 42.5f)
            reflectiveQuadTo(300f, 360f)
            close()
            moveTo(660f, 800f)
            quadToRelative(-58f, 0f, -99f, -41f)
            reflectiveQuadToRelative(-41f, -99f)
            quadToRelative(0f, -58f, 41f, -99f)
            reflectiveQuadToRelative(99f, -41f)
            quadToRelative(58f, 0f, 99f, 41f)
            reflectiveQuadToRelative(41f, 99f)
            quadToRelative(0f, 58f, -41f, 99f)
            reflectiveQuadToRelative(-99f, 41f)
            close()
            moveTo(660f, 720f)
            quadToRelative(25f, 0f, 42.5f, -17.5f)
            reflectiveQuadTo(720f, 660f)
            quadToRelative(0f, -25f, -17.5f, -42.5f)
            reflectiveQuadTo(660f, 600f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(600f, 660f)
            quadToRelative(0f, 25f, 17.5f, 42.5f)
            reflectiveQuadTo(660f, 720f)
            close()
            moveTo(188f, 772f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(528f, -528f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineTo(244f, 772f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            close()
        }
    }.build()
}
