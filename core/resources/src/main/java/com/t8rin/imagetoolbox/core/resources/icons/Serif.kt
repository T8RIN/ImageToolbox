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

val Icons.Outlined.Serif: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Serif",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 707f)
            lineTo(254f, 707f)
            lineTo(248f, 714f)
            quadTo(248f, 716f, 254f, 720f)
            lineTo(346f, 720f)
            lineTo(352f, 714f)
            quadTo(352f, 711f, 350.5f, 709f)
            quadTo(349f, 707f, 346f, 707f)
            lineTo(301f, 707f)
            lineTo(354f, 560f)
            lineTo(488f, 560f)
            lineTo(538f, 707f)
            lineTo(496f, 707f)
            lineTo(490f, 714f)
            quadTo(490f, 716f, 496f, 720f)
            lineTo(706f, 720f)
            lineTo(712f, 714f)
            quadTo(712f, 711f, 710.5f, 709f)
            quadTo(709f, 707f, 706f, 707f)
            lineTo(681f, 707f)
            lineTo(515f, 257f)
            quadTo(512f, 249f, 505.5f, 244.5f)
            quadTo(499f, 240f, 491f, 240f)
            lineTo(472f, 240f)
            quadTo(464f, 240f, 457f, 244.5f)
            quadTo(450f, 249f, 447f, 257f)
            lineTo(280f, 707f)
            close()
            moveTo(360f, 545f)
            lineTo(424f, 367f)
            lineTo(483f, 545f)
            lineTo(360f, 545f)
            close()
            moveTo(160f, 880f)
            quadTo(127f, 880f, 103.5f, 856.5f)
            quadTo(80f, 833f, 80f, 800f)
            lineTo(80f, 160f)
            quadTo(80f, 127f, 103.5f, 103.5f)
            quadTo(127f, 80f, 160f, 80f)
            lineTo(800f, 80f)
            quadTo(833f, 80f, 856.5f, 103.5f)
            quadTo(880f, 127f, 880f, 160f)
            lineTo(880f, 800f)
            quadTo(880f, 833f, 856.5f, 856.5f)
            quadTo(833f, 880f, 800f, 880f)
            lineTo(160f, 880f)
            close()
            moveTo(160f, 800f)
            lineTo(800f, 800f)
            lineTo(800f, 160f)
            lineTo(160f, 160f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 800f)
            lineTo(160f, 160f)
            lineTo(160f, 800f)
            close()
        }
    }.build()
}
