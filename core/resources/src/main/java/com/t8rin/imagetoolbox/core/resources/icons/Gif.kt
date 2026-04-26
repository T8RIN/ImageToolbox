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

val Icons.Rounded.Gif: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Gif",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(468.5f, 591.5f)
            quadTo(460f, 583f, 460f, 570f)
            verticalLineToRelative(-180f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(490f, 360f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(520f, 390f)
            verticalLineToRelative(180f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(490f, 600f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            close()
            moveTo(240f, 600f)
            quadToRelative(-18f, 0f, -29f, -12.5f)
            reflectiveQuadTo(200f, 560f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -15f, 11f, -27.5f)
            reflectiveQuadToRelative(29f, -12.5f)
            horizontalLineToRelative(130f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(400f, 390f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(370f, 420f)
            lineTo(260f, 420f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-30f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(370f, 480f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(400f, 510f)
            verticalLineToRelative(50f)
            quadToRelative(0f, 15f, -11f, 27.5f)
            reflectiveQuadTo(360f, 600f)
            lineTo(240f, 600f)
            close()
            moveTo(588.5f, 591.5f)
            quadTo(580f, 583f, 580f, 570f)
            verticalLineToRelative(-180f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(610f, 360f)
            horizontalLineToRelative(120f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(760f, 390f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(730f, 420f)
            horizontalLineToRelative(-90f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(50f)
            quadToRelative(13f, 0f, 21.5f, 8.5f)
            reflectiveQuadTo(720f, 490f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(690f, 520f)
            horizontalLineToRelative(-50f)
            verticalLineToRelative(50f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(610f, 600f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            close()
        }
    }.build()
}
