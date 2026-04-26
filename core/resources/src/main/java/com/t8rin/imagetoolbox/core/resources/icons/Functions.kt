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

val Icons.Rounded.Functions: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Functions",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(500f, 480f)
            lineTo(253f, 252f)
            quadToRelative(-6f, -6f, -9.5f, -13.5f)
            reflectiveQuadTo(240f, 223f)
            verticalLineToRelative(-23f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(280f, 160f)
            horizontalLineToRelative(380f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(720f, 220f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(660f, 280f)
            lineTo(431f, 280f)
            lineToRelative(184f, 171f)
            quadToRelative(13f, 12f, 13f, 29f)
            reflectiveQuadToRelative(-13f, 29f)
            lineTo(431f, 680f)
            horizontalLineToRelative(229f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(720f, 740f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(660f, 800f)
            lineTo(269f, 800f)
            quadToRelative(-12f, 0f, -20.5f, -8.5f)
            reflectiveQuadTo(240f, 771f)
            verticalLineToRelative(-38f)
            quadToRelative(0f, -6f, 2f, -11.5f)
            reflectiveQuadToRelative(7f, -10.5f)
            lineToRelative(251f, -231f)
            close()
        }
    }.build()
}
