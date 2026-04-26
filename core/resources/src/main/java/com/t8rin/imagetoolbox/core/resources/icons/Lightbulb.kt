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

val Icons.Rounded.Lightbulb: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Lightbulb",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(400f, 800f)
            horizontalLineToRelative(160f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(360f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(320f, 720f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(360f, 680f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(640f, 720f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(600f, 760f)
            lineTo(360f, 760f)
            close()
            moveTo(330f, 640f)
            quadToRelative(-69f, -41f, -109.5f, -110f)
            reflectiveQuadTo(180f, 380f)
            quadToRelative(0f, -125f, 87.5f, -212.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(125f, 0f, 212.5f, 87.5f)
            reflectiveQuadTo(780f, 380f)
            quadToRelative(0f, 81f, -40.5f, 150f)
            reflectiveQuadTo(630f, 640f)
            lineTo(330f, 640f)
            close()
        }
    }.build()
}
