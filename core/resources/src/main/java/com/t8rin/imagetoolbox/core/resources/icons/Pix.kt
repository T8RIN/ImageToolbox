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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Rounded.Pix: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Api",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(480f, 560f)
            lineToRelative(-80f, -80f)
            lineToRelative(80f, -80f)
            lineToRelative(80f, 80f)
            lineToRelative(-80f, 80f)
            close()
            moveTo(395f, 325f)
            lineTo(295f, 225f)
            lineToRelative(128f, -128f)
            quadToRelative(12f, -12f, 27f, -18f)
            reflectiveQuadToRelative(30f, -6f)
            quadToRelative(15f, 0f, 30f, 6f)
            reflectiveQuadToRelative(27f, 18f)
            lineToRelative(128f, 128f)
            lineToRelative(-100f, 100f)
            lineToRelative(-85f, -85f)
            lineToRelative(-85f, 85f)
            close()
            moveTo(225f, 665f)
            lineTo(97f, 537f)
            quadToRelative(-12f, -12f, -18f, -27f)
            reflectiveQuadToRelative(-6f, -30f)
            quadToRelative(0f, -15f, 6f, -30f)
            reflectiveQuadToRelative(18f, -27f)
            lineToRelative(128f, -128f)
            lineToRelative(100f, 100f)
            lineToRelative(-85f, 85f)
            lineToRelative(85f, 85f)
            lineToRelative(-100f, 100f)
            close()
            moveTo(735f, 665f)
            lineTo(635f, 565f)
            lineToRelative(85f, -85f)
            lineToRelative(-85f, -85f)
            lineToRelative(100f, -100f)
            lineToRelative(128f, 128f)
            quadToRelative(12f, 12f, 18f, 27f)
            reflectiveQuadToRelative(6f, 30f)
            quadToRelative(0f, 15f, -6f, 30f)
            reflectiveQuadToRelative(-18f, 27f)
            lineTo(735f, 665f)
            close()
            moveTo(423f, 863f)
            lineTo(295f, 735f)
            lineToRelative(100f, -100f)
            lineToRelative(85f, 85f)
            lineToRelative(85f, -85f)
            lineToRelative(100f, 100f)
            lineTo(537f, 863f)
            quadToRelative(-12f, 12f, -27f, 18f)
            reflectiveQuadToRelative(-30f, 6f)
            quadToRelative(-15f, 0f, -30f, -6f)
            reflectiveQuadToRelative(-27f, -18f)
            close()
        }
    }.build()
}
